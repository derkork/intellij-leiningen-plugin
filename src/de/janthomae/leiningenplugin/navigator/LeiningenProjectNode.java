package de.janthomae.leiningenplugin.navigator;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.treeStructure.SimpleNode;
import de.janthomae.leiningenplugin.LeiningenIcons;
import de.janthomae.leiningenplugin.LeiningenProject;
import de.janthomae.leiningenplugin.LeiningenProjectsManager;

/**
 * @author <a href="janthomae@janthomae.de">Jan Thom&auml;</a>
 * @version $Id:$
 */
public class LeiningenProjectNode extends LeiningenNode {
    private final LeiningenProject myLeiningenProject;
    private SimpleNode[] myChildren;

    public LeiningenProjectNode(RootNode myRoot, LeiningenProject project) {
        super(myRoot);
        this.myLeiningenProject = project;
        setUniformIcon(LeiningenIcons.PROJECT_ICON);
        myChildren = new SimpleNode[LeiningenProjectsManager.GOALS.length];
        for (int i = 0; i < LeiningenProjectsManager.GOALS.length; i++) {
            String goal = LeiningenProjectsManager.GOALS[i];
            myChildren[i] = new LeiningenGoalNode(this, goal);
        }
    }


    @Override
    public String getName() {
        return myLeiningenProject.getWorkingDir();
    }

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
