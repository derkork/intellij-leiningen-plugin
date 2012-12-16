package de.janthomae.leiningenplugin.project;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import de.janthomae.leiningenplugin.leiningen.LeiningenAPI;
import de.janthomae.leiningenplugin.module.ModuleCreationUtils;
import de.janthomae.leiningenplugin.utils.ClassPathUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Representation of Leiningen project in this plugin.
 *
 * @author <a href="janthomae@janthomae.de">Jan Thom&auml;</a>
 * @author Vladimir Matveev
 * @version $Id:$
 */
public class LeiningenProject {
    private final VirtualFile leinProjectFile;

    private String name;
    private String namespace;
    private String version;

    public static LeiningenProject create(VirtualFile leinProjectFile) {
        return new LeiningenProject(leinProjectFile);
    }

    private LeiningenProject(VirtualFile leinProjectFile) {
        this.leinProjectFile = leinProjectFile;
    }

    public String getWorkingDir() {
        return leinProjectFile.getParent().getPath();
    }


    public VirtualFile getVirtualFile() {
        return leinProjectFile;
    }

    public static String[] nameAndVersionFromProjectFile(VirtualFile projectFile) {
        ClassPathUtils.getInstance().switchToPluginClassLoader();
        Map map = LeiningenAPI.loadProject(projectFile.getPath());
        return new String[]{(String) map.get(ModuleCreationUtils.LEIN_PROJECT_NAME),
                (String) map.get(ModuleCreationUtils.LEIN_PROJECT_VERSION)};
    }

    public String getDisplayName() {
        return (namespace != null ? namespace + "/" : "") + name + (version != null ? ":" + version : "");
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof LeiningenProject &&
                ((LeiningenProject) obj).leinProjectFile.equals(leinProjectFile);
    }

    @Override
    public int hashCode() {
        return leinProjectFile.getPath().hashCode();
    }

    /**
     * Re-import the leiningen project.
     * <p/>
     * This will refresh the leiningen module associated with this project.
     *
     * @param ideaProject The idea project
     * @throws LeiningenProjectException
     */
    public void reimport(final Project ideaProject) throws LeiningenProjectException {

        // This puts the downloading and refreshing on a background queue.  Now that lein can download
        // dependencies it will lock the UI unless it's put on a background thread.
        // This makes it so the ui is responsive, however we need to put some sort of feedback to the user
        // so that he knows when it's complete - like the Maven plugin does.

        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                new Task.Backgroundable(ideaProject, "Synchronizing Leiningen project", false) {
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        indicator.setIndeterminate(true);
                        //Reload the lein project file
                        ModuleCreationUtils mcu = new ModuleCreationUtils();

                        //Update the module - eventually we can have multiple modules here that the project maintains.
                        Map result = mcu.importModule(ideaProject, leinProjectFile);

                        name = (String) result.get(ModuleCreationUtils.LEIN_PROJECT_NAME);
                        namespace = (String) result.get(ModuleCreationUtils.LEIN_PROJECT_GROUP);
                        version = (String) result.get(ModuleCreationUtils.LEIN_PROJECT_VERSION);
                    }
                }.queue();
            }
        }, ideaProject.getDisposed());
        // the second parameter makes sure that the task will not be executed if the project is disposed in the mean
        // time. this can happen if the user closes the project quickly after re-opening it.

    }
}
