package de.janthomae.leiningenplugin;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.JDOMExternalizable;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="janthomae@janthomae.de">Jan Thom&auml;</a>
 * @version $Id:$
 */
public class LeiningenRunConfiguration extends RunConfigurationBase
        implements LocatableConfiguration, ModuleRunProfile {

    private LeiningenRunnerParameters myRunnerParams;

    public LeiningenRunConfiguration(Project project, ConfigurationFactory factory, String name) {
        super(project, factory, name);
    }


    public boolean isGeneratedName() {
        return false;
    }

    public String suggestedName() {
        return null;
    }

    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new LeiningenRunConfigurationSettings(getProject());
    }

    public JDOMExternalizable createRunnerSettings(ConfigurationInfoProvider configurationInfoProvider) {
        return null;
    }

    public SettingsEditor<JDOMExternalizable> getRunnerSettingsEditor(ProgramRunner programRunner) {
        return null;
    }

    @NotNull
    public Module[] getModules() {
        return Module.EMPTY_ARRAY;
    }

    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment executionEnvironment)
            throws ExecutionException {
        LeiningenCommandLineState state =
                new LeiningenCommandLineState(LeiningenRunner.getInstance(getProject()).getState(), myRunnerParams,
                        executionEnvironment);
        state.setConsoleBuilder(TextConsoleBuilderFactory.getInstance().createBuilder(getProject()));
        return state;
    }

    public void checkConfiguration() throws RuntimeConfigurationException {

    }

    public void setRunnerParams(LeiningenRunnerParameters myRunnerParams) {
        this.myRunnerParams = myRunnerParams;
    }
}
