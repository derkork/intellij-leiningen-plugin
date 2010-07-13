package de.janthomae.leiningenplugin.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.xmlb.XmlSerializer;
import de.janthomae.leiningenplugin.LeiningenConstants;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * @author <a href="janthomae@janthomae.de">Jan Thom&auml;</a>
 * @version $Id:$
 */
public class LeiningenRunConfiguration extends RunConfigurationBase
        implements LocatableConfiguration, ModuleRunProfile {

    private LeiningenRunnerParameters myRunnerParams = new LeiningenRunnerParameters(new ArrayList<String>(), "");

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
                new LeiningenCommandLineState(LeiningenRunnerSettings.getInstance(), myRunnerParams,
                        executionEnvironment);
        state.setConsoleBuilder(TextConsoleBuilderFactory.getInstance().createBuilder(getProject()));
        return state;
    }

    public void checkConfiguration() throws RuntimeConfigurationException {
        String wd = myRunnerParams.getWorkingDirectory();
        if (wd.isEmpty()) {
            throw new RuntimeConfigurationError("You need to specify a working directory.");
        }
        VirtualFile vf = LocalFileSystem.getInstance().findFileByPath(wd);
        if (vf != null && vf.exists()) {
            VirtualFile vf2 = vf.findChild(LeiningenConstants.PROJECT_CLJ);
            if (vf2 == null || !vf2.isValid()) {
                throw new RuntimeConfigurationError(
                        "There is no Leiningen project file in the selected working directory.");
            }
        } else {
            throw new RuntimeConfigurationError("The selected working directory does not exist.");
        }

        if (myRunnerParams.getGoals().isEmpty()) {
            throw new RuntimeConfigurationError("You need to specify at least one goal.");
        }

    }

    public void setRunnerParams(@NotNull LeiningenRunnerParameters myRunnerParams) {
        this.myRunnerParams = myRunnerParams;
    }

    @NotNull
    public LeiningenRunnerParameters getRunnerParams() {
        return myRunnerParams;
    }

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        super.readExternal(element);
        final Element child = element.getChild("LeiningenRunnerParameters");
        if (child != null) {
            myRunnerParams = XmlSerializer.deserialize(child, LeiningenRunnerParameters.class);

        }
    }
    
    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);
        element.addContent(XmlSerializer.serialize(myRunnerParams));
    }
}
