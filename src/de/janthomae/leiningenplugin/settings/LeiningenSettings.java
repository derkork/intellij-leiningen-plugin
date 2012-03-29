package de.janthomae.leiningenplugin.settings;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.UserActivityListener;
import com.intellij.ui.UserActivityWatcher;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import de.janthomae.leiningenplugin.LeiningenIcons;
import de.janthomae.leiningenplugin.run.LeiningenRunnerSettings;
import org.jetbrains.annotations.Nls;

import javax.swing.*;
import java.awt.*;


/**
 * @author <a href="janthomae@janthomae.de">Jan Thom&auml;</a>
 * @version $Id:$
 */
public class LeiningenSettings implements Configurable {
    private TextFieldWithBrowseButton leinBinSelectorField;
    private TextFieldWithBrowseButton leinHomeSelectorField;
    private JBCheckBox overrideLeinHome;
    private TextFieldWithBrowseButton leinJarSelectorField;
    private JBCheckBox overrideLeinJar;
    private UserActivityWatcher myWatcher;
    private boolean changed = false;

    @Nls
    public String getDisplayName() {
        return "Leiningen";
    }

    public Icon getIcon() {
        return LeiningenIcons.PROJECT_ICON;
    }

    public String getHelpTopic() {
        return null;
    }

    public JComponent createComponent() {
        JPanel outerPanel = new JPanel(new BorderLayout());
        
        JPanel leinPanel = new JPanel(new FormLayout("80dlu, fill:80dlu:grow, 20dlu, 80dlu", "p,p,p"));

        CellConstraints c = new CellConstraints();
        int row = 1;
        leinPanel.add(new JBLabel("Leiningen executable:"), c.xy(1,row));
        this.leinBinSelectorField = new TextFieldWithBrowseButton();
        leinBinSelectorField
                .addBrowseFolderListener("Select the Leiningen executable", "'lein' on Linux/MacOS, 'lein.bat' on Windows. ", null,
                        new FileChooserDescriptor(true, false, false, false, false, false));
        leinPanel.add(leinBinSelectorField, c.xy(2,row));

        row++;

        leinPanel.add(new JBLabel("Leiningen Home:"), c.xy(1,row));
        this.leinHomeSelectorField = new TextFieldWithBrowseButton();
        leinHomeSelectorField
                .addBrowseFolderListener("Select the Leiningen home directory", "usually at $USER_HOME/.lein", null,
                        new FileChooserDescriptor(false, true, false, false, false, false));

        leinPanel.add(leinHomeSelectorField, c.xy(2,row));
        this.overrideLeinHome = new JBCheckBox();
        leinPanel.add(overrideLeinHome, c.xy(3,row));
        leinPanel.add(new JBLabel("Override"), c.xy(4,row));

        row++;

        leinPanel.add(new JBLabel("Leiningen Jar:"), c.xy(1,row));
        this.leinJarSelectorField = new TextFieldWithBrowseButton();
        leinJarSelectorField
                .addBrowseFolderListener("Select the Leiningen Jar", "usually at $USER_HOME/.lein/self-installs/leinigen-VERSION-standalone.jar", null,
                        new FileChooserDescriptor(true, false, true, true, false, false));

        leinPanel.add(leinJarSelectorField, c.xy(2,row));
        this.overrideLeinJar = new JBCheckBox();
        leinPanel.add(overrideLeinJar, c.xy(3,row));
        leinPanel.add(new JBLabel("Override"), c.xy(4,row));

        outerPanel.add(leinPanel, BorderLayout.NORTH);

        myWatcher = new UserActivityWatcher();
        myWatcher.register(outerPanel);
        myWatcher.addUserActivityListener(new UserActivityListener() {
            public void stateChanged() {
                changed = true;
                leinJarSelectorField.setEnabled(overrideLeinJar.isSelected());
                leinHomeSelectorField.setEnabled(overrideLeinHome.isSelected());
            }
        });
        return outerPanel;
    }

    public boolean isModified() {
        return changed;
    }

    public void apply() throws ConfigurationException {
        LeiningenRunnerSettings settings = LeiningenRunnerSettings.getInstance();
        settings.leiningenPath = leinBinSelectorField.getText();
        settings.leiningenHome = leinHomeSelectorField.getText();
        settings.leiningenJar = leinJarSelectorField.getText();
        settings.overrideLeiningenHome = overrideLeinHome.isSelected();
        settings.overrideLeiningenJar = overrideLeinJar.isSelected();
        changed = false;
    }

    public void reset() {
        changed = false;
        LeiningenRunnerSettings settings = LeiningenRunnerSettings.getInstance();
        leinBinSelectorField.setText(settings.leiningenPath);
        leinHomeSelectorField.setText(settings.getRealLeiningenHome());
        leinJarSelectorField.setText(settings.getRealLeiningenJar());
        overrideLeinHome.setSelected(settings.overrideLeiningenHome);
        overrideLeinJar.setSelected((settings.overrideLeiningenJar));
    }

    public void disposeUIResources() {
        myWatcher = null;
    }
}
