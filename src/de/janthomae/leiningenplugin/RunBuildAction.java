package de.janthomae.leiningenplugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;

import java.util.List;

/**
 * @author <a href="janthomae@janthomae.de">Jan Thom&auml;</a>
 * @version $Id:$
 */
public class RunBuildAction extends AnAction implements DumbAware {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getData(PlatformDataKeys.PROJECT);
        List<String> goals = anActionEvent.getData(LeiningenDataKeys.LEININGEN_GOALS);
        if (goals == null || goals.isEmpty()) {
            return;
        }
        LeiningenProject leiningenProject = anActionEvent.getData(LeiningenDataKeys.LEININGEN_PROJECT);
        if (leiningenProject == null) {
            return;
        }

        LeiningenRunnerParameters params = new LeiningenRunnerParameters(goals, leiningenProject.getWorkingDir());
        LeiningenRunConfigurationType.runConfiguration(project, params, new LeiningenRunnerSettings(),
                anActionEvent.getDataContext(), null);

    }
}
