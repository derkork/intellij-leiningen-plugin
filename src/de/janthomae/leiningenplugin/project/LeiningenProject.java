package de.janthomae.leiningenplugin.project;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ui.update.MergingUpdateQueue;
import com.intellij.util.ui.update.Update;
import de.janthomae.leiningenplugin.leiningen.LeiningenAPI;
import de.janthomae.leiningenplugin.module.ModuleCreationUtils;
import de.janthomae.leiningenplugin.utils.ClassPathUtils;

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
    private MergingUpdateQueue mergingUpdateQueue;

    public static LeiningenProject create(VirtualFile leinProjectFile) {
        return new LeiningenProject(leinProjectFile);
    }

    private LeiningenProject(VirtualFile leinProjectFile) {
        this.leinProjectFile = leinProjectFile;

        //Update in the background - based loosely on org.jetbrains.idea.maven.utils.MavenImportNotifier
        mergingUpdateQueue = new MergingUpdateQueue("Leiningen Project Update Queue",500,true,MergingUpdateQueue.ANY_COMPONENT);
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
        return new String[]{(String)map.get(ModuleCreationUtils.LEIN_PROJECT_NAME), (String) map.get(ModuleCreationUtils.LEIN_PROJECT_VERSION)};
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
     * Reimport the leiningen project.
     *
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
        // TODO Leaving this for future development.
        mergingUpdateQueue.queue(new Update(mergingUpdateQueue) {
            @Override
            public void run() {
                //Reload the lein project file
                ModuleCreationUtils mcu = new ModuleCreationUtils();

                //Update the module - eventually we can have multiple modules here that the project maintains.
                Map result = mcu.importModule(ideaProject, leinProjectFile);

                name = (String) result.get(ModuleCreationUtils.LEIN_PROJECT_NAME);
                namespace = (String) result.get(ModuleCreationUtils.LEIN_PROJECT_GROUP);
                version = (String) result.get(ModuleCreationUtils.LEIN_PROJECT_VERSION);
            }
        });

    }
}
