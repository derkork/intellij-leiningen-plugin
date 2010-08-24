package de.janthomae.leiningenplugin.project;

import com.intellij.ide.highlighter.ModuleFileType;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.StdModuleTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import de.janthomae.leiningenplugin.leiningen.LeiningenProjectFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="janthomae@janthomae.de">Jan Thom&auml;</a>
 * @version $Id:$
 */
public class LeiningenProject {

    private String myWorkingDir;
    private final VirtualFile myProjectFile;
    private final Project myProject;
    private static final Pattern ProjectNamePattern = Pattern.compile("defproject\\s+([^\\s]+)\\s+\"([^\"]+)\"");
    private String myName;
    private String myVersion;
    private Module myModule;

    public LeiningenProject(VirtualFile projectFile, Project project) {
        myProjectFile = projectFile;
        myProject = project;
        this.myWorkingDir = projectFile.getParent().getPath();
        refreshDataFromFile();
    }

    public String getWorkingDir() {
        return myWorkingDir;
    }


    public VirtualFile getVirtualFile() {
        return myProjectFile;
    }


    @NotNull
    public String getName() {
        return myName;
    }

    @Nullable
    public String getVersion() {
        return myVersion;
    }

    private void refreshDataFromFile() {
        String[] values = nameAndVersionFromProjectFile(myProjectFile);
        myName = values[0];
        myVersion = values[1];
    }

    // TODO: this is quite a hack...

    public static String[] nameAndVersionFromProjectFile(VirtualFile projectFile) {
        LeiningenProjectFile lpf = new LeiningenProjectFile();
        String myName = projectFile.getParent().getPath();
        String myVersion = null;
        try {
            String string = new String(projectFile.contentsToByteArray());

            final Matcher matcher = ProjectNamePattern.matcher(string);
            if (matcher.find()) {
                myName = matcher.group(1);
                myVersion = matcher.group(2);
                
            }

        } catch (IOException e) {
            // ok.
        }
        return new String[]{myName, myVersion};
    }

    public String getDisplayName() {
        return myName + (myVersion != null ? ":" + myVersion : "");
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof LeiningenProject &&
                ((LeiningenProject) obj).myProjectFile.equals(myProjectFile);
    }

    @Override
    public int hashCode() {
        return myProjectFile.getPath().hashCode();
    }

    public Module getManagedModule() {
        return myModule;
    }

    public Module reimport() {
        refreshDataFromFile();
        myModule = null;
        // find a matching module
        final ModifiableModuleModel moduleModel = doGetModuleModel();
        for (Module module : moduleModel.getModules()) {
            ModifiableRootModel rootModel = doGetRootModel(module);
            final VirtualFile[] contentRoots = rootModel.getContentRoots();
            for (VirtualFile contentRoot : contentRoots) {
                if (contentRoot.getPath().equals(myWorkingDir)) {
                    myModule = module;
                    break;
                }
            }
            if (myModule != null) {
                break;
            }
        }

        if (myModule == null) {
            // oh-kay we don't have a module yet.
            myModule = moduleModel.newModule(myWorkingDir + File.separator + FileUtil.sanitizeFileName(myName) +
                    ModuleFileType.DOT_DEFAULT_EXTENSION, StdModuleTypes.JAVA);
        }

        // now set up the source paths
        final ModifiableRootModel rootModel = doGetRootModel(myModule);
        rootModel.inheritSdk();
        final ContentEntry contentEntry = rootModel.addContentEntry(myProjectFile.getParent());


        final VirtualFile sourcePath = getSourcePath();
        if (sourcePath != null) {
            contentEntry.addSourceFolder(sourcePath, false);
        }
        final VirtualFile testSourcePath = getTestSourcePath();
        if (testSourcePath != null) {
            contentEntry.addSourceFolder(testSourcePath, true);
        }
        final VirtualFile resourcesPath = getResourcesPath();

        if (resourcesPath != null) {
            contentEntry.addSourceFolder(resourcesPath, false);
        }
        VirtualFile outputPath = createOrGetOutputPath();

        if (outputPath != null) {
            final CompilerModuleExtension extension = rootModel.getModuleExtension(CompilerModuleExtension.class);

            extension.inheritCompilerOutputPath(false);
            extension.setCompilerOutputPath(outputPath);
            extension.setCompilerOutputPathForTests(outputPath);
        }
        new WriteAction() {
            @Override
            protected void run(Result result) throws Throwable {
                rootModel.commit();
                moduleModel.commit();
            }
        }.execute();
        return myModule;
    }

    @Nullable
    private VirtualFile createOrGetOutputPath() {
        final String classes = "classes";
        final VirtualFile outputPath = myProjectFile.getParent().findChild(classes);
        if (outputPath != null) {
            return outputPath;
        }
        return new WriteAction<VirtualFile>() {
            @Override
            public void run(Result<VirtualFile> result) {
                try {
                    result.setResult(myProjectFile.getParent().createChildDirectory(this, classes));
                } catch (IOException e) {
                    result.setResult(null);
                }

            }

        }.execute().getResultObject();
    }

    @Nullable
    private VirtualFile getResourcesPath() {
        return myProjectFile.getParent().findChild("resources");
    }

    @Nullable
    private VirtualFile getTestSourcePath() {
        return myProjectFile.getParent().findChild("test");
    }

    @Nullable
    private VirtualFile getSourcePath() {
        return myProjectFile.getParent().findChild("src");
    }

    protected ModifiableModuleModel doGetModuleModel() {
        return new ReadAction<ModifiableModuleModel>() {
            protected void run(Result<ModifiableModuleModel> result) throws Throwable {
                result.setResult(ModuleManager.getInstance(myProject).getModifiableModel());
            }
        }.execute().getResultObject();
    }

    protected ModifiableRootModel doGetRootModel(final Module module) {
        return new ReadAction<ModifiableRootModel>() {
            protected void run(Result<ModifiableRootModel> result) throws Throwable {
                result.setResult(ModuleRootManager.getInstance(module).getModifiableModel());
            }
        }.execute().getResultObject();
    }

}
