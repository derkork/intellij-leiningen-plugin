package de.janthomae.leiningenplugin.navigator;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.treeStructure.SimpleNode;
import de.janthomae.leiningenplugin.LeiningenIcons;
import de.janthomae.leiningenplugin.LeiningenProject;

/**
* @author <a href="janthomae@janthomae.de">Jan Thom&auml;</a>
* @version $Id:$
*/
public class LeiningenProjectNode extends LeiningenNode {
    private final LeiningenProject myLeiningenProject;

    public LeiningenProjectNode(RootNode myRoot, LeiningenProject project) {
        super(myRoot);
        this.myLeiningenProject = project;
        setUniformIcon(LeiningenIcons.PROJECT_ICON);
    }


    @Override
    public String getName() {
        return myLeiningenProject.getWorkingDir();
    }

    final SimpleNode[] myChildren = {
            new LeiningenGoalNode(this, "pom"),
            new LeiningenGoalNode(this, "help"),
            new LeiningenGoalNode(this, "upgrade"),
            new LeiningenGoalNode(this, "install"),
            new LeiningenGoalNode(this, "jar"),
            new LeiningenGoalNode(this, "deps"),
            new LeiningenGoalNode(this, "uberjar"),
            new LeiningenGoalNode(this, "clean"),
            new LeiningenGoalNode(this, "compile"),
            new LeiningenGoalNode(this, "version")
    };


    @Override
    public SimpleNode[] getChildren() {
        return myChildren;
    }

    public VirtualFile getVirtualFile() {
        return myLeiningenProject.getVirtualFile();
    }

    public LeiningenProject getLeiningenProject() {
        return myLeiningenProject;
    }
}
