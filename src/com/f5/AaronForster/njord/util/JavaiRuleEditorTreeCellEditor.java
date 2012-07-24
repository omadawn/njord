package com.f5.AaronForster.njord.util;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;

public class JavaiRuleEditorTreeCellEditor extends AbstractCellEditor implements
		TreeCellEditor {
/*##############################################################################################################
 * NOT CURRENTLY IN USE:
 * TreeCellEditor is a method that you implement in order to edit the name of a tree cell. AKA the name of 
 * something in the expandable jTree.
 * It will need to be implemented later when we want to be able to rename an iRule(or other object) in the
 * jTree itself.
 *##############################################################################################################
 */
	
//	@Override
//	public Object getCellEditorValue() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Component getTreeCellEditorComponent(JTree arg0, Object arg1,
//			boolean arg2, boolean arg3, boolean arg4, int arg5) {
//		// TODO Auto-generated method stub
//		return null;
//	}

//}
//static class TreeNodeEditor extends AbstractCellEditor implements TreeCellEditor {
    private static final long serialVersionUID = 1L;
    private JButton button1;
    private JButton button2;
    private JPanel panel1;
    // JW: do not modify the node inside the editor 
    //        private DefaultMutableTreeNode node = null;
    private DefaultTreeCellRenderer defaultRenderer;

    private Object editorValue;

    public JavaiRuleEditorTreeCellEditor() {
        super();
        panel1 = new JPanel();
        defaultRenderer = new DefaultTreeCellRenderer();
//        button1 = new JButton("DELETE");
//        button1.setOpaque(true);
//        button1.setIcon(new ImageIcon("trash.png"));
//        button1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//        button2 = new JButton("UPLOAD");
//        button2.setOpaque(true);
//        button2.setIcon(new ImageIcon("app_up.png"));
//        button2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//        button2.setAction(createAction("upload", "UPLOAD"));
//        button1.setAction(createAction("delete", "DELETE"));
        panel1.add(defaultRenderer);
//        panel1.add(button1);
//        panel1.add(button2);
    }

    private Action createAction(final String actionCommand, String display) {
        Action action = new AbstractAction(display) {

            @Override
            public void actionPerformed(ActionEvent e) {
                stopEditing(actionCommand);
            }

        };
        return action;

    }
    /**
     * @param actionCommand
     */
    protected void stopEditing(String actionCommand) {
        editorValue = actionCommand;
        stopCellEditing();
    }

    @Override
    public Component getTreeCellEditorComponent(JTree tree, Object value,
            boolean isSelected, boolean expanded, boolean leaf, int row) {
        // in order to do some actions on a node
        //            if (value instanceof DefaultMutableTreeNode) {
        //                node = (DefaultMutableTreeNode) value;
        //            }

        defaultRenderer.getTreeCellRendererComponent(tree, value,
                isSelected, expanded, leaf, row, true);

        return panel1;
    }

    /**
     * 
     */
    private void reset() {
        editorValue = null;
    }

    /**
     * At this point in time the component is added to the tree (not documented!) but
     * tree's internal cleanup might not yet be ready
     */ 
    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        reset();
        if (anEvent instanceof MouseEvent) {
            redirect((MouseEvent) anEvent);
        }
        return false;
    }

    private void redirect(final MouseEvent anEvent) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MouseEvent ev = SwingUtilities.convertMouseEvent(anEvent.getComponent(), anEvent, panel1);
                panel1.dispatchEvent(ev);

            }
        });
    }

    @Override
    public Object getCellEditorValue() {
        return editorValue;
    }
}
