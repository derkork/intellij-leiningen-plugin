package de.janthomae.leiningenplugin.navigator;

import com.intellij.ui.treeStructure.SimpleNode;

import java.util.ArrayList;
import java.util.List;

/**
* @author <a href="janthomae@janthomae.de">Jan Thom&auml;</a>
* @version $Id:$
*/
public class RootNode extends LeiningenNode {

    private List<LeiningenProjectNode>
            children = new ArrayList<LeiningenProjectNode>();

    public RootNode(SimpleNode parent) {
        super(parent);
    }

    @Override
    public String getName() {
        return "Leiningen Projects";
    }

    public void clear() {
        children.clear();
    }

    public void addProjectNode(LeiningenProjectNode node) {
        children.add(node);
    }

    @Override
    public boolean isAutoExpandNode() {
        return true;
    }

    @Override
    public boolean isAlwaysShowPlus() {
        return true;
    }

    @Override
    public SimpleNode[] getChildren() {
        return children.toArray(new SimpleNode[children.size()]);
    }

}
