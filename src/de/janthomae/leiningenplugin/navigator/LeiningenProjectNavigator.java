package de.janthomae.leiningenplugin.navigator;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ex.ToolWindowManagerAdapter;
import com.intellij.openapi.wm.ex.ToolWindowManagerEx;
import com.intellij.ui.treeStructure.SimpleTree;
import de.janthomae.leiningenplugin.LeiningenProjectsManager;
import de.janthomae.leiningenplugin.LeiningenProjectsManagerListener;
import de.janthomae.leiningenplugin.SimpleProjectComponent;

import javax.swing.*;
import java.awt.*;

/**
 * @author <a href="janthomae@janthomae.de">Jan Thom&auml;</a>
 * @version $Id:$
 */
public class LeiningenProjectNavigator extends SimpleProjectComponent {

    public static final String TOOL_WINDOW_ID = "Leiningen";
    private ToolWindow myToolWindow;
    private SimpleTree myTree;
    private LeiningenProjectStructure myStructure;
    private final LeiningenProjectsManager myProjectsManager;


    public LeiningenProjectNavigator(Project project, LeiningenProjectsManager projectsManager) {
        super(project);
        myProjectsManager = projectsManager;
    }

    @Override
    public void initComponent() {
        if (!isNormalProject()) return;
        runWhenInitialized(myProject, new Runnable() {
            public void run() {
                addPanel();
                myProjectsManager.addProjectsManagerListener(new LeiningenProjectsManagerListener() {
                    public void projectsChanged() {
                        scheduleStructureUpdate();
                    }
                });
            }
        });

    }


    private void addPanel() {
        initTree();
        myStructure = new LeiningenProjectStructure(myProject, myProjectsManager, myTree);
        JPanel panel = new LeiningenProjectNavigatorPanel(myProject, myTree);
        final ToolWindowManagerEx manager = ToolWindowManagerEx.getInstanceEx(myProject);
        myToolWindow = manager.registerToolWindow(TOOL_WINDOW_ID, panel, ToolWindowAnchor.RIGHT, myProject, true);
        final ToolWindowManagerAdapter listener = new ToolWindowManagerAdapter() {
            boolean wasVisible = false;

            @Override
            public void stateChanged() {
                if (myToolWindow.isDisposed()) return;
                boolean visible = myToolWindow.isVisible();
                if (!visible || visible == wasVisible) return;
                scheduleStructureUpdate();
                wasVisible = visible;
            }
        };
        manager.addToolWindowManagerListener(listener);
        scheduleStructureUpdate();
    }

    private void scheduleStructureUpdate() {
        invokeLater(myProject, new Runnable() {
            public void run() {
                myStructure.update();
            }
        });
    }

    private void initTree() {
        myTree = new SimpleTree() {
            final JLabel myLabel = new JLabel("There are no Leiningen projects to display.");

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (myProjectsManager.hasProjects()) return;

                myLabel.setFont(getFont());
                myLabel.setBackground(getBackground());
                myLabel.setForeground(getForeground());
                Rectangle bounds = getBounds();
                Dimension size = myLabel.getPreferredSize();
                myLabel.setBounds(0, 0, size.width, size.height);

                int x = (bounds.width - size.width) / 2;
                Graphics g2 = g.create(bounds.x + x, bounds.y + 20, bounds.width, bounds.height);
                try {
                    myLabel.paint(g2);
                } finally {
                    g2.dispose();
                }
            }
        };
    }


}
