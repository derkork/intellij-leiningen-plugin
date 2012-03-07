package de.janthomae.leiningenplugin.run;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="janthomae@janthomae.de">Jan Thom&auml;</a>
 * @version $Id:$
 */
@State(
        name = "LeiningenRunnerSettings",
        storages = {@Storage(
                id = "LeiningenRunnerSettings",
                file = "$APP_CONFIG$/leiningen.xml"
        )
        })
public class LeiningenRunnerSettings implements PersistentStateComponent<LeiningenRunnerSettings> {

    public
    @NotNull
    String leiningenPath = "/please/set/me/up/in/settings/leiningen";

    public
    @NotNull
    String leiningenHome = System.getProperty("user.home") + "/.lein";

    public static LeiningenRunnerSettings getInstance() {
        return ServiceManager.getService(LeiningenRunnerSettings.class);
    }

    public LeiningenRunnerSettings getState() {
        return this;
    }

    public void loadState(LeiningenRunnerSettings leiningenRunnerSettings) {
        XmlSerializerUtil.copyBean(leiningenRunnerSettings, this);
    }
}
