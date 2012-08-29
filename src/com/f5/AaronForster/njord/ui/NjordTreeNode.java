/**
 * 
 */
package com.f5.AaronForster.njord.ui;

import javax.swing.tree.DefaultMutableTreeNode;

import org.fife.ui.rsyntaxtextarea.TextEditorPane;

import com.f5.AaronForster.njord.util.NjordConstants;

/**
 * NjordTreeNode is a class to override the DefaultMutableTreeNode in order to give us capabilities such as not building the iRules tree 
 * until we actually expand the iRulesTree.
 * 
 * 
 * @author forster
 *
 */
public class NjordTreeNode extends DefaultMutableTreeNode {
	public String name;// Name of this node
    public boolean populated; // This is for later when we only want to fetch things when you actually expand the tree
    public int nodeType = 0;
    
    /**
     * Creates a new node from an RSTA TextEditorPane.
     * When created with the TextEditorPane constructor the NjordTreeNode will be initialized with a node Type of NjordConstants.NODE_TYPE_IRULE.
     * 
     * @param editorPane
     */
    public NjordTreeNode(TextEditorPane editorPane) {
    	super(editorPane);
    	nodeType = NjordConstants.NODE_TYPE_IRULE;
    }

    /**
     * Creates a new node specifying the type.
     * 
     * @param name
     * @param nodeType
     */
    public NjordTreeNode(String name, int nodeType) {
    	super(name);
    	this.nodeType = nodeType;
    }
    
	/**
     * Sets the node type
     * 
     * @param newType
     */
    public void setNodeType(int newType) {
    	nodeType = newType;
    }
    
    /**
     * Gets the node type
     * 
     * @return NjordConstants
     */
    public int getNodeType() {
    	return nodeType;
    }
}