package de.janthomae.leiningenplugin.navigator;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDialog;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import de.janthomae.leiningenplugin.LeiningenProject;
import de.janthomae.leiningenplugin.LeiningenProjectsManager;

/**
 * @author <a href="janthomae@janthomae.de">Jan Thom&auml;</a>
 * @version $Id:$
 */
public class AddManagedFilesAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project theProject = e.getData(PlatformDataKeys.PROJECT);
        final LeiningenProjectsManager manager =
                LeiningenProjectsManager.getInstance(theProject);

        FileChooserDescriptor singlePomSelection = new FileChooserDescriptor(true, false, false, false, false, true) {
            @Override
            public boolean isFileSelectable(VirtualFile file) {
                return super.isFileSelectable(file) && !manager.isManagedFile(file);
            }

            @Override
            public boolean isFileVisible(VirtualFile file, boolean showHiddenFiles) {
                if (!file.isDirectory() && !LeiningenProjectsManager.isProjectFile(file)) return false;
                return super.isFileVisible(file, showHiddenFiles);
            }
        };

        VirtualFile fileToSelect = e.getData(PlatformDataKeys.VIRTUAL_FILE);

        FileChooserDialog dialog = FileChooserFactory.getInstance().createFileChooser(singlePomSelection, theProject);
        VirtualFile[] files = dialog.choose(fileToSelect,theProject);
        if (files.length == 0) return;

        for (VirtualFile file : files) {
            LeiningenProject leiningenProject = new LeiningenProject(file);
            manager.addLeiningenProject(leiningenProject);
        }
    }
}
