package de.janthomae.leiningenplugin.navigator;

import com.intellij.execution.application.ApplicationConfigurable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.ShowSettingsUtil;
import de.janthomae.leiningenplugin.settings.LeiningenSettings;

/**
 * @author <a href="janthomae@janthomae.de">Jan Thom&auml;</a>
 * @version $Id:$
 */
public class ShowSettingsAction extends AnAction {


    @Override
    public void actionPerformed(AnActionEvent e) {
        ShowSettingsUtil.getInstance().showSettingsDialog(PlatformDataKeys.PROJECT.getData(e.getDataContext()), LeiningenSettings.DISPLAY_NAME);
    }

}