/**
 * 
 */
package com.f5.AaronForster.njord.util;


//UNG, this is NASTY, I think it will be easier to override LocalLBRuleRuleDefinition
import iControl.LocalLBRuleRuleDefinition;

import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.f5.AaronForster.njord.MainGuiWindow;

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

//	Sometimes, it is not feasible to override toString; in such a scenario you can override the convertValueToText of JTree to map the object from the model into a string that gets displayed.
	public String convertValueToText(Object value, boolean selected,
			boolean expanded, boolean leaf, int row, boolean hasFocus) {
		//Value seems to be the 'tree node' object which actually contains the object I care about.
		
//		log.info("Value has: " + value.toString());  
		System.out.println("Value has: " + value.toString());
		
		// At this point I can't think of a scenario where we won't be getting a defaultMutableTreeNode so I feel safe doing this. We'll see.
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
//		log.info("Node has: " + node.toString());
		System.out.println("Node has: " + node.toString());
		
		// Only try and get the object as an LocalLBRuleRuleDefinition if it's a leaf node
		// This might get tricky if I have more than one type of object. Like virtuals or iApps.
		if (node.isLeaf()) {
		// neither instanceof nor getClass() have been very helpful.
		//		if (value instanceof LocalLBRuleRuleDefinition) {
			LocalLBRuleRuleDefinition rule = (LocalLBRuleRuleDefinition) node.getUserObject();
			return rule.getRule_name();
		} else {
			return super.convertValueToText(value, selected, expanded, leaf, row, hasFocus);
		}

	}
	
}
