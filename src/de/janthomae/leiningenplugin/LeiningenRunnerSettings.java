package de.janthomae.leiningenplugin;

import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="janthomae@janthomae.de">Jan Thom&auml;</a>
 * @version $Id:$
 */
public class LeiningenRunnerSettings implements Cloneable {

    private @NotNull String leiningenPath = "/usr/share/leiningen/lein";

    @NotNull
    public String getLeiningenPath() {
        return leiningenPath;
    }

    public void setLeiningenPath(@NotNull String leiningenPath) {
        this.leiningenPath = leiningenPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LeiningenRunnerSettings that = (LeiningenRunnerSettings) o;

        return leiningenPath.equals(that.leiningenPath);
    }

    @Override
    public int hashCode() {
        return leiningenPath.hashCode();
    }
}
