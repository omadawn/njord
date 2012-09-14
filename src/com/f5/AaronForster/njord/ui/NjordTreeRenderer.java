package com.f5.AaronForster.njord.ui;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.fife.ui.rsyntaxtextarea.TextEditorPane;

import com.f5.AaronForster.njord.util.NjordConstants;

/**
 * NjordTreeRenderer is Njord's extension of DefaultTreeCellRenderer. It renders the navigation tree with customizations specific to Njord.
 * 
 * @author Aaron Forster a.forster@f5.com
 *
 */
public class NjordTreeRenderer extends DefaultTreeCellRenderer {
		public String cleanIconPath = "/resources/iRuleIconClean.png";//"resources/iRuleIconClean.png";
		public Icon cleanIcon;
		public String dirtyIconPath = "/resources/iRuleIconDirty.png";                           
		public Icon dirtyIcon;
		public String folderIconPath = "/resources/FolderIcon.png";   
		public Icon folderIcon;
		public String bigipIconPath = "/resources/BigIPIcon.png";                           
		public Icon bigipIcon;
		public int nodeType = 0;
		
	    /**
	     * Instantiates an instance of NjordTreeRenderer
	     */
	    public NjordTreeRenderer() {
	    	cleanIcon = createImageIcon(cleanIconPath);
	    	dirtyIcon = createImageIcon(dirtyIconPath);
	    	folderIcon = createImageIcon(folderIconPath);
	    	bigipIcon = createImageIcon(bigipIconPath);
	    }

	    /**
	     * The primary Rendering component. This is the element which actually defines the display of each component.
	     * 
	     * @see javax.swing.tree.DefaultTreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
	     */
	    public Component getTreeCellRendererComponent(
	                        JTree tree,
	                        Object value,
	                        boolean sel,
	                        boolean expanded,
	                        boolean leaf,
	                        int row,
	                        boolean hasFocus) {
	    	TextEditorPane njordiRuleNode = null; 
	    	
	    	NjordTreeNode node = (NjordTreeNode)value;
	    	nodeType = node.getNodeType();
	    	
	    	if ( nodeType == NjordConstants.NODE_TYPE_IRULE) {
	        	njordiRuleNode = (TextEditorPane) node.getUserObject();
	        }
	        
//	    	System.out.println("Row is " + row + " for selection");
	        super.getTreeCellRendererComponent(
	                        tree, value, sel,
	                        expanded, leaf, row,
	                        hasFocus);
	        if (nodeType == NjordConstants.NODE_TYPE_IRULE && !njordiRuleNode.isDirty()) {
	            setIcon(cleanIcon);
	            setToolTipText("The rule is up to date on the server.");
	            setName(njordiRuleNode.getName());
	        } else if (nodeType == NjordConstants.NODE_TYPE_IRULE && njordiRuleNode.isDirty()) {
	        	setIcon(dirtyIcon);
	            setToolTipText("The rule has been modified locally and must be saved");
	            setText(" *** " + njordiRuleNode.getName());
	        } else if ( nodeType == NjordConstants.NODE_TYPE_LOCAL_REMOTE) {
	        	if (node.toString().matches("BigIP")) {
	        		setIcon(bigipIcon);
	        	}
	        } //else {
//	        	setIcon(folderIcon);
//	            setToolTipText(null);
	      //  }
	        return this;
	    }

	    /** 
	     * Generates an image that can be used in a JTree.
	     * 
	     * @param path a java.net.URL location to an image.
	     * @return An imageIcon suitable for use in a JTree or null if the path was invalid.
	     */
	    protected static ImageIcon createImageIcon(String path) {
	    	java.net.URL imgURL = NjordTreeRenderer.class.getResource(path);
	        if (imgURL != null) {
	            return new ImageIcon(imgURL);
	        } else {
	            System.err.println("Couldn't find file: " + path);
	            return null;
	        }
	    }
	}