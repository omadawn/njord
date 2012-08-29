/**
 * 
 */
package com.f5.AaronForster.njord.ui;


//UNG, this is NASTY, I think it will be easier to override LocalLBRuleRuleDefinition
import iControl.LocalLBRuleRuleDefinition;

import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import org.fife.ui.rsyntaxtextarea.TextEditorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.f5.AaronForster.njord.MainGuiWindow;
import com.f5.AaronForster.njord.NjordiRuleDefinition;
import com.f5.AaronForster.njord.util.NjordConstants;

/**
 * @author forster
 *
 */
public class f5JavaGuiTree extends JTree {
	final Logger log = LoggerFactory.getLogger(f5JavaGuiTree.class);
	//TODO: Figure out why log isn't properly being initialized. I get a null pointer exception when I try and use it below.

	/**
	 * 
	 */
	public f5JavaGuiTree() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public f5JavaGuiTree(Object[] arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public f5JavaGuiTree(Vector<?> arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public f5JavaGuiTree(Hashtable<?, ?> arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public f5JavaGuiTree(TreeNode arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public f5JavaGuiTree(TreeModel arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public f5JavaGuiTree(TreeNode arg0, boolean arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Converts the value of a contained object to text.
	 * convertValueToText() is what the tree renderer's use in order to determine what to name an object within a jTree.
	 * 
	 * @see javax.swing.JTree#convertValueToText(java.lang.Object, boolean, boolean, boolean, int, boolean)
	 */
	public String convertValueToText(Object value, boolean selected,
			boolean expanded, boolean leaf, int row, boolean hasFocus) {
		
		NjordTreeNode node = (NjordTreeNode) value;
		System.out.println("Node has: " + node.toString());
		
		if (node.getNodeType() == NjordConstants.NODE_TYPE_IRULE) {
			TextEditorPane rule = (TextEditorPane) node.getUserObject();
			return rule.getName();  
		} else {
			return super.convertValueToText(value, selected, expanded, leaf, row, hasFocus);
		}

	}
	
}
