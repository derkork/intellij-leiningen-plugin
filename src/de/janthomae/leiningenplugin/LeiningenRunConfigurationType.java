package de.janthomae.leiningenplugin;

import com.intellij.compiler.options.CompileStepBeforeRun;
import com.intellij.execution.*;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.impl.RunnerAndConfigurationSettingsImpl;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author <a href="janthomae@janthomae.de">Jan Thom&auml;</a>
 * @version $Id:$
 */
public class LeiningenRunConfigurationType implements LocatableConfigurationType {
    private ConfigurationFactory myFactory;

    LeiningenRunConfigurationType() {
        myFactory = new ConfigurationFactory(this) {
            public RunConfiguration createTemplateConfiguration(Project project) {
                throw new UnsupportedOperationException();
            }

            public RunConfiguration createTemplateConfiguration(Project project, RunManager runManager) {
                return new LeiningenRunConfiguration(project, this, "");
            }

            @Override
            public void configureBeforeRunTaskDefaults(Key<? extends BeforeRunTask> providerID, BeforeRunTask task) {
                if (providerID == CompileStepBeforeRun.ID) {
                    task.setEnabled(false);
                }
            }
        };
    }

    public RunnerAndConfigurationSettings createConfigurationByLocation(Location location) {
        return createRunnerAndConfigurationSettings(null, null, location.getProject());
    }


    public boolean isConfigurationByLocation(RunConfiguration runConfiguration, Location location) {
        return false;
    }

    public String getDisplayName() {
        return "Leiningen";
    }

    public String getConfigurationTypeDescription() {
        return "Leiningen Build";
    }

    public Icon getIcon() {
        return null;
    }

    @NotNull
    public String getId() {
        return "LeiningenRunConfiguration";
    }

    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{myFactory};
    }

    public static void runConfiguration(Project project,
                                        LeiningenRunnerParameters params,
                                        LeiningenRunnerSettings runnerSettings,
                                        DataContext context,
                                        @Nullable ProgramRunner.Callback callback) {
        RunnerAndConfigurationSettings configSettings = createRunnerAndConfigurationSettings(
                runnerSettings,
                params,
                project);

        ProgramRunner runner = RunnerRegistry.getInstance().findRunnerById(DefaultRunExecutor.EXECUTOR_ID);
        ExecutionEnvironment env = new ExecutionEnvironment(runner, configSettings, context);
        Executor executor = DefaultRunExecutor.getRunExecutorInstance();

        try {
            runner.execute(executor, env, callback);
        } catch (ExecutionException e) {
        }
    }

    private static RunnerAndConfigurationSettings createRunnerAndConfigurationSettings(
            LeiningenRunnerSettings runnerSettings, LeiningenRunnerParameters params, Project project) {
        LeiningenRunConfigurationType type =
                ConfigurationTypeUtil.findConfigurationType(LeiningenRunConfigurationType.class);

        final RunnerAndConfigurationSettingsImpl settings =
                RunManagerEx.getInstanceEx(project).createConfiguration("Leiningen", type.myFactory);
        LeiningenRunConfiguration configuration = (LeiningenRunConfiguration) settings.getConfiguration();
        configuration.setRunnerParams(params);
        return settings;
    }
}
