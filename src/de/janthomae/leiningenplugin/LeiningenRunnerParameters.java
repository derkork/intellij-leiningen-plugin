package de.janthomae.leiningenplugin;

import java.util.List;

/**
 * @author <a href="janthomae@janthomae.de">Jan Thom&auml;</a>
 * @version $Id:$
 */
public class LeiningenRunnerParameters implements Cloneable {
    private List<String> myGoals;

    public LeiningenRunnerParameters(List<String> myGoals, String workingDirectory) {
        this.myGoals = myGoals;
        this.workingDirectory = workingDirectory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LeiningenRunnerParameters that = (LeiningenRunnerParameters) o;

        if (myGoals != null ? !myGoals.equals(that.myGoals) : that.myGoals != null) return false;
        if (workingDirectory != null ? !workingDirectory.equals(that.workingDirectory) : that.workingDirectory != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = myGoals != null ? myGoals.hashCode() : 0;
        result = 31 * result + (workingDirectory != null ? workingDirectory.hashCode() : 0);
        return result;
    }

    private String workingDirectory;

    public List<String> getMyGoals() {
        return myGoals;
    }

    public void setMyGoals(List<String> myGoals) {
        this.myGoals = myGoals;
    }


    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }
}
