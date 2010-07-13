package de.janthomae.leiningenplugin.navigator;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.treeStructure.SimpleTree;
import de.janthomae.leiningenplugin.LeiningenDataKeys;
import de.janthomae.leiningenplugin.project.LeiningenProject;
import org.jetbrains.annotations.NonNls;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="janthomae@janthomae.de">Jan Thom&auml;</a>
 * @version $Id:$
 */
public class LeiningenProjectNavigatorPanel extends SimpleToolWindowPanel implements DataProvider {
    private Project myProject;
    private SimpleTree myTree;

    public LeiningenProjectNavigatorPanel(Project project, SimpleTree tree) {
        super(true, false);
        myProject = project;
        myTree = tree;

        final ActionManager actionManager = ActionManager.getInstance();
        ActionToolbar actionToolbar = actionManager.createActionToolbar("Leiningen Navigator Toolbar",
                (DefaultActionGroup) actionManager
                        .getAction("Leiningen.NavigatorActionsToolbar"),
                true);

        actionToolbar.setTargetComponent(tree);
        setToolbar(actionToolbar.getComponent());

        setContent(new JScrollPane(myTree));
    }


    public Object getData(@NonNls String dataId) {
        if (PlatformDataKeys.PROJECT.is(dataId)) return myProject;
        if (LeiningenDataKeys.LEININGEN_GOALS.is(dataId)) return extractGoals();
        if (PlatformDataKeys.VIRTUAL_FILE.is(dataId)) return extractVirtualFile();
        if (LeiningenDataKeys.LEININGEN_PROJECT.is(dataId)) return extractLeiningenProject();
        return null;
    }

    private LeiningenProject extractLeiningenProject() {
        List<LeiningenProjectNode> projectNodes =
                LeiningenProjectStructure
                        .getSelectedNodes(myTree, LeiningenProjectNode.class);
        if (!projectNodes.isEmpty()) {
            return projectNodes.get(0).getLeiningenProject();
        }
        List<LeiningenGoalNode> goalNodes =
                LeiningenProjectStructure.getSelectedNodes(myTree, LeiningenGoalNode.class);
        if (!goalNodes.isEmpty()) {
            return goalNodes.get(0).getLeiningenProject();
        }
        return null;
    }

    private List<String> extractGoals() {
        List<String> result = new ArrayList<String>();
        List<LeiningenGoalNode> v =
                LeiningenProjectStructure.getSelectedNodes(myTree, LeiningenGoalNode.class);
        for (LeiningenGoalNode goalNode : v) {
            result.add(goalNode.getGoalName());
        }
        return result;
    }

    private VirtualFile extractVirtualFile() {
        for (LeiningenProjectNode each : LeiningenProjectStructure
                .getSelectedNodes(myTree, LeiningenProjectNode.class)) {
            VirtualFile file = each.getVirtualFile();
            if (file != null && file.isValid()) return file;
        }
        return null;
    }
}
