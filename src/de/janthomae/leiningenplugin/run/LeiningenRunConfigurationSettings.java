package de.janthomae.leiningenplugin.run;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.TextFieldWithAutoCompletion;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import de.janthomae.leiningenplugin.LeiningenProjectsManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author <a href="janthomae@janthomae.de">Jan Thom&auml;</a>
 * @version $Id:$
 */
public class LeiningenRunConfigurationSettings extends SettingsEditor<LeiningenRunConfiguration> {
    private final Project myProject;
    private TextFieldWithBrowseButton wdSelector;
    private TextFieldWithAutoCompletion goals;

    public LeiningenRunConfigurationSettings(final Project p) {
        myProject = p;
    }

    protected void resetEditorFrom(LeiningenRunConfiguration configuration) {
        wdSelector.setText(configuration.getRunnerParams().getWorkingDirectory());
        goals.setText(StringUtil.join(configuration.getRunnerParams().getGoals(), " "));
    }

    protected void applyEditorTo(LeiningenRunConfiguration configuration) throws ConfigurationException {
        LeiningenRunnerParameters runnerParams =
                new LeiningenRunnerParameters(StringUtil.split(goals.getText(), " "), wdSelector.getText());
        configuration.setRunnerParams(runnerParams);
    }

    @NotNull
    protected JComponent createEditor() {

        JPanel configurationPanel = new JPanel(new FormLayout("fill:50dlu:grow", "p,p,5dlu,p,p,fill:50dlu:grow"));

        CellConstraints c = new CellConstraints();
        configurationPanel.add(new JLabel("Working directory"), c.xy(1, 1));
        this.wdSelector = new TextFieldWithBrowseButton();
        this.wdSelector
                .addBrowseFolderListener("Select working directory", "This directory must contain a project.clj file. ",
                        null, new FileChooserDescriptor(false, true, false, false, false, false));
        configurationPanel.add(this.wdSelector, c.xy(1, 2));
        configurationPanel.add(new JLabel("Goals to run (separate with space)"), c.xy(1, 4));
        this.goals = new TextFieldWithAutoCompletion(myProject);
        this.goals.setVariants(LeiningenProjectsManager.GOALS);
        configurationPanel.add(this.goals, c.xy(1, 5));
        return configurationPanel;
    }

    protected void disposeEditor() {
    }
}
