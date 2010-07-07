package de.janthomae.leiningenplugin;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;

/**
 * @author <a href="janthomae@janthomae.de">Jan Thom&auml;</a>
 * @version $Id:$
 */
@State(name = "LeiningenRunner", storages = {@Storage(id = "default", file = "$WORKSPACE_FILE$")})
public class LeiningenRunner extends SimpleProjectComponent implements PersistentStateComponent<LeiningenRunnerSettings> {
    private LeiningenRunnerSettings mySettings = new LeiningenRunnerSettings();

    protected LeiningenRunner(Project project) {
        super(project);
    }

    public LeiningenRunnerSettings getState() {
        return mySettings;
    }

    public void loadState(LeiningenRunnerSettings leiningenRunnerSettings) {
        mySettings = leiningenRunnerSettings;
    }


    public static LeiningenRunner getInstance(Project project) {
        return project.getComponent(LeiningenRunner.class);
    }
}
