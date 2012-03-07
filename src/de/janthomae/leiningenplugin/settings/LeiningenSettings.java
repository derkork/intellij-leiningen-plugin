package de.janthomae.leiningenplugin.settings;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.UserActivityListener;
import com.intellij.ui.UserActivityWatcher;
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
        
        JPanel leinPanel = new JPanel(new GridLayout(0,1));

        JPanel leinHomePanel = new JPanel(new BorderLayout());
        leinHomePanel.add(new JLabel("Path to Leiningen home directory:"), BorderLayout.WEST);
        this.leinHomeSelectorField = new TextFieldWithBrowseButton();
        leinHomeSelectorField
                .addBrowseFolderListener("Select the Leiningen home directory", "~/.lein on Linux/MacOS.", null,
                        new FileChooserDescriptor(false, true, false, false, false, false));
        leinHomePanel.add(this.leinHomeSelectorField, BorderLayout.CENTER);
        leinPanel.add(leinHomePanel);

        JPanel leinBinPanel = new JPanel(new BorderLayout());
        leinBinPanel.add(new JLabel("Path to Leiningen executable:"), BorderLayout.WEST);
        this.leinBinSelectorField = new TextFieldWithBrowseButton();
        leinBinSelectorField
                .addBrowseFolderListener("Select the Leiningen executable", "'lein' on Linux/MacOS, 'lein.bat' on Windows. ", null,
                        new FileChooserDescriptor(true, false, false, false, false, false));
        leinBinPanel.add(this.leinBinSelectorField, BorderLayout.CENTER);
        leinPanel.add(leinBinPanel);

        outerPanel.add(leinPanel, BorderLayout.NORTH);

        myWatcher = new UserActivityWatcher();
        myWatcher.register(outerPanel);
        myWatcher.addUserActivityListener(new UserActivityListener() {
            public void stateChanged() {
                changed = true;
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
        changed = false;
    }

    public void reset() {
        changed = false;
        LeiningenRunnerSettings settings = LeiningenRunnerSettings.getInstance();
        leinBinSelectorField.setText(settings.leiningenPath);
        leinHomeSelectorField.setText(settings.leiningenHome);
    }

    public void disposeUIResources() {
        myWatcher = null;
    }
}
