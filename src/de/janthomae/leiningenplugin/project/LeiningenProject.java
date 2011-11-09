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
import com.intellij.openapi.vcs.ui.VcsBalloonProblemNotifier;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.WindowManager;
import de.janthomae.leiningenplugin.LeiningenUtil;
import de.janthomae.leiningenplugin.leiningen.LeiningenProjectFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Representation of Leiningen project in this plugin.
 *
 * @author <a href="janthomae@janthomae.de">Jan Thom&auml;</a>
 * @author Vladimir Matveev
 *
 * @version $Id:$
 */
public class LeiningenProject {
    private final VirtualFile projectFile;
    private final Project project;

    private String workingDir;
    private String name;
    private String namespace;
    private String version;
    private Module module;
    private LeiningenProjectFile leiningenProjectFile;

    public static LeiningenProject createAndLoad(VirtualFile projectFile, Project project) throws LeiningenProjectException {
        LeiningenProject leiningenProject = new LeiningenProject(projectFile, project);
        leiningenProject.refreshDataFromFile();
        return leiningenProject;
    }

    public static LeiningenProject create(VirtualFile projectFile, Project project) {
        return new LeiningenProject(projectFile, project);
    }

    private LeiningenProject(VirtualFile projectFile, Project project) {
        this.projectFile = projectFile;
        this.project = project;
        this.workingDir = projectFile.getParent().getPath();
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

    private void refreshDataFromFile() throws LeiningenProjectException {
        // Dirty hack to make clojure work in plugin environment
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        // Reload project.clj
        leiningenProjectFile = new LeiningenProjectFile(projectFile.getPath());
        if (!leiningenProjectFile.isValid()) {
            LeiningenUtil.notifyError("Leiningen",
                    "Unable to load project file! Please check if it is valid leiningen project!", project);
            throw new LeiningenProjectException("Unable to load project file!", leiningenProjectFile.getError());
        }
        name = leiningenProjectFile.getName();
        namespace = leiningenProjectFile.getNamespace();
        version = leiningenProjectFile.getVersion();
    }

    public static String[] nameAndVersionFromProjectFile(VirtualFile projectFile) {
        LeiningenProjectFile lpf = new LeiningenProjectFile(projectFile.getPath());
        String name = lpf.isValid() ? lpf.getName() : "";
        String version = lpf.isValid() ? lpf.getVersion() : "";
        return new String[]{name, version};
    }

    public String getDisplayName() {
        return (namespace != null ? namespace + "/" : "") + name + (version != null ? ":" + version : "");
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
        return module;
    }

    public Module reimport() throws LeiningenProjectException {
        refreshDataFromFile();
        module = null;

        // find a matching module
        final ModifiableModuleModel moduleModel = getModuleModel();

        for (Module module : moduleModel.getModules()) {
            ModifiableRootModel rootModel = getRootModel(module);
            final VirtualFile[] contentRoots = rootModel.getContentRoots();
            for (VirtualFile contentRoot : contentRoots) {
                if (contentRoot.getPath().equals(workingDir)) {
                    this.module = module;
                    break;
                }
            }
            if (this.module != null) {
                break;
            }
        }

        if (module == null) {
            // oh-kay we don't have a module yet.
            module = moduleModel.newModule(workingDir + File.separator + FileUtil.sanitizeFileName(name) +
                    ModuleFileType.DOT_DEFAULT_EXTENSION, StdModuleTypes.JAVA);
        }

        // now set up different paths
        final ModifiableRootModel rootModel = getRootModel(module);
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
        return module;
    }

    @Nullable
    private VirtualFile createOrGetOutputPath() {
        final String compilePath = leiningenProjectFile.getCompilePath();
        final VirtualFile projectDirectory = projectFile.getParent();
        final VirtualFile outputPath = projectDirectory.findFileByRelativePath(compilePath);
        if (outputPath != null) {
            return outputPath;
        }
        return new WriteAction<VirtualFile>() {
            private List<String> splitDirectories(String directory) {
                LinkedList<String> directories = new LinkedList<String>();
                File current = new File(directory);
                while (current != null) {
                    directories.addFirst(current.getName());
                    current = current.getParentFile();
                }
                return directories;
            }

            @Override
            public void run(Result<VirtualFile> result) {
                try {
                    List<String> directories = splitDirectories(compilePath);
                    VirtualFile lastDirectory = projectDirectory;
                    for (String directory : directories)
                        lastDirectory = lastDirectory.createChildDirectory(this, directory);
                    if (lastDirectory == projectDirectory)
                        lastDirectory = null;
                    result.setResult(lastDirectory);
                } catch (IOException e) {
                    result.setResult(null);
                }

            }

        }.execute().getResultObject();
    }

    @Nullable
    private VirtualFile getResourcesPath() {
        return projectFile.getParent().findChild(leiningenProjectFile.getResourcesPath());
    }

    @Nullable
    private VirtualFile getTestSourcePath() {
        return projectFile.getParent().findChild(leiningenProjectFile.getTestPath());
    }

    @Nullable
    private VirtualFile getSourcePath() {
        return projectFile.getParent().findChild(leiningenProjectFile.getSourcePath());
    }

    protected ModifiableModuleModel getModuleModel() {
        return new ReadAction<ModifiableModuleModel>() {
            protected void run(Result<ModifiableModuleModel> result) throws Throwable {
                result.setResult(ModuleManager.getInstance(project).getModifiableModel());
            }
        }.execute().getResultObject();
    }

    protected ModifiableRootModel getRootModel(final Module module) {
        return new ReadAction<ModifiableRootModel>() {
            protected void run(Result<ModifiableRootModel> result) throws Throwable {
                result.setResult(ModuleRootManager.getInstance(module).getModifiableModel());
            }
        }.execute().getResultObject();
    }

}
