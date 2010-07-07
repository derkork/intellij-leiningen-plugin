package de.janthomae.leiningenplugin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.treeStructure.SimpleNode;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.ui.treeStructure.SimpleTreeBuilder;
import com.intellij.ui.treeStructure.SimpleTreeStructure;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="janthomae@janthomae.de">Jan Thom&auml;</a>
 * @version $Id:$
 */
public class LeiningenProjectStructure extends SimpleTreeStructure {

    private SimpleTreeBuilder myTreeBuilder;
    private RootNode myRoot = new RootNode();
    private Project myProject;
    private final LeiningenProjectsManager myProjectsManager;

    public LeiningenProjectStructure(Project project, LeiningenProjectsManager projectsManager, SimpleTree tree) {
        super();
        myProject = project;
        myProjectsManager = projectsManager;
        myTreeBuilder = new SimpleTreeBuilder(tree, (DefaultTreeModel) tree.getModel(), this, null);
        Disposer.register(myProject, myTreeBuilder);

        myTreeBuilder.initRoot();
        myTreeBuilder.expand(myRoot, null);

    }

    @Override
    public Object getRootElement() {
        return myRoot;
    }

    public void update() {
        myRoot.clear();
        final List<LeiningenProject> projects = myProjectsManager.getLeiningenProjects();
        for (LeiningenProject project : projects) {
            LeiningenProjectNode lpn = new LeiningenProjectNode(myRoot,project);
            myRoot.addProjectNode(lpn);
        }
        myTreeBuilder.updateFromRoot();
        myTreeBuilder.expand(myRoot, null);
    }

    public abstract static class LeiningenSimpleNode extends SimpleNode {

        protected LeiningenSimpleNode(SimpleNode parent) {
            super(parent);
        }

        public VirtualFile getVirtualFile() {
            return null;
        }
    }


    public static class LeiningenProjectNode extends LeiningenSimpleNode {
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
                new GoalNode(this, "pom"),
                new GoalNode(this, "help"),
                new GoalNode(this, "upgrade"),
                new GoalNode(this, "install"),
                new GoalNode(this, "jar"),
                new GoalNode(this, "deps"),
                new GoalNode(this, "uberjar"),
                new GoalNode(this, "clean"),
                new GoalNode(this, "compile"),
                new GoalNode(this, "version")
        };


        @Override
        public SimpleNode[] getChildren() {
            return myChildren;
        }

        @Override
        public VirtualFile getVirtualFile() {
            return myLeiningenProject.getVirtualFile();
        }

        public LeiningenProject getLeiningenProject() {
            return myLeiningenProject;
        }
    }

    public static class RootNode extends SimpleNode {

        private List<LeiningenProjectNode> children = new ArrayList<LeiningenProjectNode>();

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
        public SimpleNode[] getChildren() {
            return children.toArray(new SimpleNode[children.size()]);
        }

    }

    public static class GoalNode extends SimpleNode {

        private final String myGoal;

        public GoalNode(LeiningenProjectNode parent, String goal) {
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
            return ((LeiningenProjectNode) getParent()).myLeiningenProject;
        }

        @Override
        public SimpleNode[] getChildren() {
            return SimpleNode.NO_CHILDREN;
        }
    }

    public static <T extends SimpleNode> List<T> getSelectedNodes(SimpleTree tree, Class<T> nodeClass) {
        final List<T> filtered = new ArrayList<T>();
        for (SimpleNode node : getSelectedNodes(tree)) {
            if ((nodeClass != null) && (!nodeClass.isInstance(node))) {
                filtered.clear();
                break;
            }
            //noinspection unchecked
            filtered.add((T) node);
        }
        return filtered;
    }

    private static List<SimpleNode> getSelectedNodes(SimpleTree tree) {
        List<SimpleNode> nodes = new ArrayList<SimpleNode>();
        TreePath[] treePaths = tree.getSelectionPaths();
        if (treePaths != null) {
            for (TreePath treePath : treePaths) {
                nodes.add(tree.getNodeFor(treePath));
            }
        }
        return nodes;
    }

}
