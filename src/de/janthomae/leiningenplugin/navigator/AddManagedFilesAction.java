package de.janthomae.leiningenplugin.navigator;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDialog;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import de.janthomae.leiningenplugin.project.LeiningenProjectsManager;

/**
 * Action to add a leiningen project file to the IDEA project.
 *
 * @author <a href="janthomae@janthomae.de">Jan Thom&auml;</a>
 */
public class AddManagedFilesAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project ideaProject = e.getData(PlatformDataKeys.PROJECT);
        final LeiningenProjectsManager manager =
                LeiningenProjectsManager.getInstance(ideaProject);

        FileChooserDescriptor leinProjectFileDescriptor = new FileChooserDescriptor(true, false, false, false, false, true) {
            @Override
            public boolean isFileSelectable(VirtualFile file) {
                return super.isFileSelectable(file) && !manager.isManagedFile(file);
            }

            @Override
            public boolean isFileVisible(VirtualFile file, boolean showHiddenFiles) {
                return (file.isDirectory() || LeiningenProjectsManager.isProjectFile(file)) &&
                        super.isFileVisible(file, showHiddenFiles);
            }
        };

        VirtualFile fileToSelect = e.getData(PlatformDataKeys.VIRTUAL_FILE);

        FileChooserDialog dialog = FileChooserFactory.getInstance().createFileChooser(leinProjectFileDescriptor, ideaProject,null);

        VirtualFile[] files = dialog.choose(fileToSelect,ideaProject);
        if (files.length == 0) return;

        for (VirtualFile file : files) {
            manager.importLeiningenProject(file, ideaProject);
        }
    }
}
