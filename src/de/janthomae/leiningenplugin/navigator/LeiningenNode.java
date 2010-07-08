package de.janthomae.leiningenplugin.navigator;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.ui.treeStructure.SimpleNode;
import com.intellij.ui.treeStructure.SimpleTree;
import com.sun.istack.internal.Nullable;
import org.jetbrains.annotations.NonNls;

import java.awt.event.InputEvent;

/**
 * @author <a href="janthomae@janthomae.de">Jan Thom&auml;</a>
 * @version $Id:$
 */
public abstract class LeiningenNode extends SimpleNode {


    public LeiningenNode(SimpleNode parent) {
        super(parent);
    }

    public void handleDoubleClickOrEnter(SimpleTree tree, InputEvent inputEvent) {
        String actionId = getActionId();
        if (actionId != null) {
            executeAction(actionId, inputEvent);
        }
    }

    @Nullable
    @NonNls
    public String getActionId() {
        return null;
    }

    public static void executeAction(final String actionId, final InputEvent e) {
        final ActionManager actionManager = ActionManager.getInstance();
        final AnAction action = actionManager.getAction(actionId);
        if (action != null) {
            final Presentation presentation = new Presentation();
            final AnActionEvent
                    event =
                    new AnActionEvent(e, DataManager.getInstance().getDataContext(e.getComponent()), "", presentation,
                            actionManager, 0);
            action.update(event);
            if (presentation.isEnabled()) {
                action.actionPerformed(event);
            }
        }
    }
}
