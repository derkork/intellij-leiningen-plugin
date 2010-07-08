package de.janthomae.leiningenplugin.run;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author <a href="janthomae@janthomae.de">Jan Thom&auml;</a>
 * @version $Id:$
 */
public class LeiningenRunConfigurationSettings extends SettingsEditor<LeiningenRunConfiguration> {
  private LeiningenRunConfiguration configuration;
  Configurable myCompositeConfigurable;

  public LeiningenRunConfigurationSettings(final Project p) {
    myCompositeConfigurable = new LeiningenRunConfigurationConfigurable();
  }

  protected void resetEditorFrom(LeiningenRunConfiguration configuration) {
    this.configuration = configuration;
    myCompositeConfigurable.reset();
  }

  protected void applyEditorTo(LeiningenRunConfiguration configuration) throws ConfigurationException {
    this.configuration = configuration;
    myCompositeConfigurable.apply();
  }

  @NotNull
  protected JComponent createEditor() {
    return myCompositeConfigurable.createComponent();
  }

  protected void disposeEditor() {
    myCompositeConfigurable.disposeUIResources();
  }
}
