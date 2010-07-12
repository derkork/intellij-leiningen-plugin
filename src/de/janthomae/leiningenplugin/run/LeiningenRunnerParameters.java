package de.janthomae.leiningenplugin.run;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="janthomae@janthomae.de">Jan Thom&auml;</a>
 * @version $Id:$
 */
public class LeiningenRunnerParameters implements Cloneable {
    private List<String> myGoals;
    private String myWorkingDirectory;


    // Serialization, only.
    public LeiningenRunnerParameters() {
        myGoals = new ArrayList<String>();
        myWorkingDirectory = "";
    }
    
    public LeiningenRunnerParameters(@NotNull List<String> myGoals, String workingDirectory) {
        this.myGoals = myGoals;
        this.myWorkingDirectory = workingDirectory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LeiningenRunnerParameters that = (LeiningenRunnerParameters) o;

        if (myGoals != null ? !myGoals.equals(that.myGoals) : that.myGoals != null) return false;
        if (myWorkingDirectory != null ? !myWorkingDirectory
                .equals(that.myWorkingDirectory) : that.myWorkingDirectory != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = myGoals != null ? myGoals.hashCode() : 0;
        result = 31 * result + (myWorkingDirectory != null ? myWorkingDirectory.hashCode() : 0);
        return result;
    }

    @NotNull

    public List<String> getGoals() {
        return myGoals;
    }

    public void setGoals(@NotNull List<String> myGoals) {
        this.myGoals = myGoals;
    }


    public String getWorkingDirectory() {
        return myWorkingDirectory;
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.myWorkingDirectory = workingDirectory;
    }
}
