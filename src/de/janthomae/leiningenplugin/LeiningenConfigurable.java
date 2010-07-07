package de.janthomae.leiningenplugin;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;

import javax.swing.*;

/**
 * @author <a href="janthomae@janthomae.de">Jan Thom&auml;</a>
 * @version $Id:$
 */
public class LeiningenConfigurable implements Configurable {
    @Nls
    public String getDisplayName() {
        return null;
    }

    public Icon getIcon() {
        return null;
    }

    public String getHelpTopic() {
        return null;
    }

    public JComponent createComponent() {
        return null;
    }

    public boolean isModified() {
        return false;
    }

    public void apply() throws ConfigurationException {

    }

    public void reset() {

    }

    public void disposeUIResources() {

    }
}
