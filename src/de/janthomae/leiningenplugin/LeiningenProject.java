package de.janthomae.leiningenplugin;

import com.intellij.openapi.vfs.VirtualFile;

/**
 * @author <a href="janthomae@janthomae.de">Jan Thom&auml;</a>
 * @version $Id:$
 */
public class LeiningenProject {

    private String workingDir;
    private final VirtualFile myProjectFile;

    public LeiningenProject(VirtualFile projectFile) {
        myProjectFile = projectFile;
        this.workingDir = projectFile.getParent().getPath();
    }

    public String getWorkingDir() {
        return workingDir;
    }


    public VirtualFile getVirtualFile() {
        return myProjectFile;
    }
}
