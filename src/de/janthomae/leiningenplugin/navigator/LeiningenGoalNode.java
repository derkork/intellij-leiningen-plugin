package de.janthomae.leiningenplugin.navigator;

import com.intellij.ui.treeStructure.SimpleNode;
import de.janthomae.leiningenplugin.LeiningenIcons;
import de.janthomae.leiningenplugin.project.LeiningenProject;

/**
* @author <a href="janthomae@janthomae.de">Jan Thom&auml;</a>
* @version $Id:$
*/
public class LeiningenGoalNode extends LeiningenNode {

    private final String myGoal;

    public LeiningenGoalNode(LeiningenProjectNode parent, String goal) {
        super(parent);
        myGoal = goal;
        setUniformIcon(LeiningenIcons.GOAL_ICON);
    }

    @Override
    public String getName() {
        return myGoal;
    }


    public String getGoalName() {
        return myGoal;
    }

    public LeiningenProject getLeiningenProject() {
        return ((LeiningenProjectNode) getParent()).getLeiningenProject();
    }

    @Override
    public SimpleNode[] getChildren() {
        return SimpleNode.NO_CHILDREN;
    }

    @Override
    public String getActionId() {
        return "Leiningen.RunBuild";
    }
}
