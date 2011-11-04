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

/**
 * @author <a href="janthomae@janthomae.de">Jan Thom&auml;</a>
 * @version $Id:$
 */
public class LeiningenProject {

    private String workingDir;
    private final VirtualFile projectFile;
    private final Project project;
    private String name;
    private String version;
    private Module myModule;

    public LeiningenProject(VirtualFile projectFile, Project project) {
        this.projectFile = projectFile;
        this.project = project;
        this.workingDir = projectFile.getParent().getPath();
        refreshDataFromFile();
    }

    public String getWorkingDir() {
        return workingDir;
    }


    public VirtualFile getVirtualFile() {
        return projectFile;
    }


    @NotNull
    public String getName() {
        return name;
    }

    @Nullable
    public String getVersion() {
        return version;
    }

    private void refreshDataFromFile() {
        String[] values = nameAndVersionFromProjectFile(projectFile);
        name = values[0];
        version = values[1];
    }

    public static String[] nameAndVersionFromProjectFile(VirtualFile projectFile) {
        LeiningenProjectFile lpf = new LeiningenProjectFile(projectFile.getName());
        String myName = lpf.getName();
        String myVersion = lpf.getVersion();
        return new String[]{myName, myVersion};
    }

    public String getDisplayName() {
        return name + (version != null ? ":" + version : "");
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof LeiningenProject &&
                ((LeiningenProject) obj).projectFile.equals(projectFile);
    }

    @Override
    public int hashCode() {
        return projectFile.getPath().hashCode();
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
                if (contentRoot.getPath().equals(workingDir)) {
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
            myModule = moduleModel.newModule(workingDir + File.separator + FileUtil.sanitizeFileName(name) +
                    ModuleFileType.DOT_DEFAULT_EXTENSION, StdModuleTypes.JAVA);
        }

        // now set up the source paths
        final ModifiableRootModel rootModel = doGetRootModel(myModule);
        rootModel.inheritSdk();
        final ContentEntry contentEntry = rootModel.addContentEntry(projectFile.getParent());


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
        final VirtualFile outputPath = projectFile.getParent().findChild(classes);
        if (outputPath != null) {
            return outputPath;
        }
        return new WriteAction<VirtualFile>() {
            @Override
            public void run(Result<VirtualFile> result) {
                try {
                    result.setResult(projectFile.getParent().createChildDirectory(this, classes));
                } catch (IOException e) {
                    result.setResult(null);
                }

            }

        }.execute().getResultObject();
    }

    @Nullable
    private VirtualFile getResourcesPath() {
        return projectFile.getParent().findChild("resources");
    }

    @Nullable
    private VirtualFile getTestSourcePath() {
        return projectFile.getParent().findChild("test");
    }

    @Nullable
    private VirtualFile getSourcePath() {
        return projectFile.getParent().findChild("src");
    }

    protected ModifiableModuleModel doGetModuleModel() {
        return new ReadAction<ModifiableModuleModel>() {
            protected void run(Result<ModifiableModuleModel> result) throws Throwable {
                result.setResult(ModuleManager.getInstance(project).getModifiableModel());
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
