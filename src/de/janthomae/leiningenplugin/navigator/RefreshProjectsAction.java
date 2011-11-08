package de.janthomae.leiningenplugin.navigator;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import de.janthomae.leiningenplugin.project.LeiningenProject;
import de.janthomae.leiningenplugin.project.LeiningenProjectException;
import de.janthomae.leiningenplugin.project.LeiningenProjectsManager;

import java.util.List;

/**
 * @author <a href="janthomae@janthomae.de">Jan Thom&auml;</a>
 * @version $Id:$
 */
public class RefreshProjectsAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project theProject = e.getData(PlatformDataKeys.PROJECT);
        final LeiningenProjectsManager manager =
                LeiningenProjectsManager.getInstance(theProject);

        final List<LeiningenProject> projects = manager.getLeiningenProjects();
        for (LeiningenProject project : projects) {
            try {
                project.reimport();
            } catch (LeiningenProjectException e1) {
                // Just ignore it for now
            }
        }
    }
}