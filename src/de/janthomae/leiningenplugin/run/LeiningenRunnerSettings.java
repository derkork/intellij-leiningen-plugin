package de.janthomae.leiningenplugin.run;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

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
    public static final String LeinHomeName = ".lein";

    @NotNull
    public String leiningenPath = "/please/set/me/up/in/settings/leiningen";

    @NotNull
    public String leiningenHome;

    @NotNull
    public String leiningenJar = "";

    public boolean overrideLeiningenHome = false;

    public boolean overrideLeiningenJar = false;



    /**
     * Returns the real leiningen home.
     * @return the real leiningen home.
     */
    @NotNull
    public String getRealLeiningenHome() {
        if (overrideLeiningenHome) {
            return leiningenHome;
        }
        // first check LEIN_HOME env variable
        String env = System.getenv("LEIN_HOME");
        if (!StringUtil.isEmpty(env)) {
             return env;
        }

        File leinExec = new File(leiningenPath);
        if (leinExec.exists()) {
            // check if bundled mode
            File bundledLein = new File(leinExec.getParentFile(), LeinHomeName);
            if (bundledLein.exists()) {
                return bundledLein.getPath();
            }
        }
        // Fallback to user home
        return System.getProperty("user.home") + File.separator + LeinHomeName;
    }

    /**
     * Returns the real leiningen jar.
     * @return the real leiningen jar
     */
    @NotNull
    public String getRealLeiningenJar() {
        if (overrideLeiningenJar) {
            return leiningenJar;
        }
        // first check LEIN_JAR env variable
        String env = System.getenv("LEIN_JAR");
        if (!StringUtil.isEmpty(env)) {
            return env;
        }

        // ok it's not set, look for a file below the lein home
        File leinHome = new File(getRealLeiningenHome());
        if ( leinHome.exists() && leinHome.isDirectory() ) {
            File selfInstalls = new File(leinHome,  "self-installs");
            if ( selfInstalls.exists() && selfInstalls.isDirectory() ) {
                File[] files = selfInstalls.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.startsWith("leiningen-") && name.endsWith("-standalone.jar");
                    }
                });
                if ( files.length > 0 ) {
                    return files[0].getPath();
                }
            }
        }
        // fallback
        return "leiningen-standalone.jar";
    }

    /**
     * Returns the URLs that form the leiningen classpath, that is the leiningen.jar and all plugins.
     * @return an array of urls.
     */
    @NotNull
    public URL[] getLeiningenClasspathUrls() {
        File leinHome = new File(getRealLeiningenHome());
        ArrayList<URL> urls = new ArrayList<URL>();
        File leinJar = new File(getRealLeiningenJar());
        if ( leinJar.exists()) {
            try {
                urls.add(leinJar.toURI().toURL());
            } catch (MalformedURLException e) {
                // ignore.
            }
        }
        if (leinHome.exists() && leinHome.isDirectory()) {
            File plugins = new File(leinHome, "plugins");
            if ( plugins.exists() && plugins.isDirectory() ) {
                File[] files = plugins.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".jar");
                    }
                });
                for (File file : files) {
                    try {
                        urls.add(file.toURI().toURL());
                    } catch (MalformedURLException e) {
                        // ignore
                    }
                }
            }
        }
        return urls.toArray(new URL[urls.size()]);
    }


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
