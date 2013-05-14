package de.janthomae.leiningenplugin.module.forms;

import de.janthomae.leiningenplugin.module.model.ModuleInformation;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: Chris Shellenbarger
 * Date: 4/14/13
 * Time: 6:08 PM
 *
 * This is the java class that backs the LeiningenModuleInformationForm
 */
public class LeiningenModuleInformationForm {
    private JPanel mainPanel;
    private JTextField groupNameTextField;
    private JTextField artifactTextField;
    private JTextField versionTextField;
    private JTextField projectFileTextField;

    private final boolean editable;

    /**
     * Create the Module Information Form.
     *
     * @param editable True if the form should be editable or not.
     */
    public LeiningenModuleInformationForm(boolean editable) {
        this.editable = editable;
    }

    /**
     * Get the main panel created by this form.
     *
     * @return The main panel created by this form.
     */
    public JPanel getMainPanel() {
        groupNameTextField.setEditable(editable);
        artifactTextField.setEditable(editable);
        versionTextField.setEditable(editable);
        projectFileTextField.setEditable(editable);

        return mainPanel;
    }

    public void setData(ModuleInformation data) {
        groupNameTextField.setText(data.getGroupId());
        artifactTextField.setText(data.getArtifactId());
        versionTextField.setText(data.getVersion());
        projectFileTextField.setText(data.getProjectFilePath());
    }

    public void getData(ModuleInformation data) {
        data.setGroupId(groupNameTextField.getText());
        data.setArtifactId(artifactTextField.getText());
        data.setVersion(versionTextField.getText());
        data.setProjectFilePath(projectFileTextField.getText());
    }

    public boolean isModified(ModuleInformation data) {
        if (groupNameTextField.getText() != null ? !groupNameTextField.getText().equals(data.getGroupId()) : data.getGroupId() != null)
            return true;
        if (artifactTextField.getText() != null ? !artifactTextField.getText().equals(data.getArtifactId()) : data.getArtifactId() != null)
            return true;
        if (versionTextField.getText() != null ? !versionTextField.getText().equals(data.getVersion()) : data.getVersion() != null)
            return true;
        if (projectFileTextField.getText() != null ? !projectFileTextField.getText().equals(data.getProjectFilePath()) : data.getProjectFilePath() != null)
            return true;
        return false;
    }
}
