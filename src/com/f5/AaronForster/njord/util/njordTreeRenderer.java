package com.f5.AaronForster.njord.util;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.f5.AaronForster.njord.NjordiRuleDefinition;

public class njordTreeRenderer extends DefaultTreeCellRenderer {
	//TODO: Deal with the fact that getPath needs the path to be under the src folder but that other things do not. WTH man?
		String cleanIconPath = "/resources/iRuleIconClean.png";//"resources/iRuleIconClean.png";
	    Icon cleanIcon;
	    String dirtyIconPath = "/resources/iRuleIconDirty.png";                           
	    Icon dirtyIcon;
	    String folderIconPath = "/resources/FolderIcon.png";   
	    Icon folderIcon;
	    String bigipIconPath = "/resources/BigIPIcon.png";                           
	    Icon bigipIcon;
	    
	    public njordTreeRenderer() {
	    	cleanIcon = createImageIcon(cleanIconPath);
	    	dirtyIcon = createImageIcon(dirtyIconPath);
	    	folderIcon = createImageIcon(folderIconPath);
	    	bigipIcon = createImageIcon(bigipIconPath);
	    }
	    
	    public njordTreeRenderer(Icon icon) {
	        cleanIcon = icon;
	    }

	    public Component getTreeCellRendererComponent(
	                        JTree tree,
	                        Object value,
	                        boolean sel,
	                        boolean expanded,
	                        boolean leaf,
	                        int row,
	                        boolean hasFocus) {
	    	NjordiRuleDefinition njordiRuleNode = null; 

	        DefaultMutableTreeNode node =
	                (DefaultMutableTreeNode)value;
	        if ( node.isLeaf() ) {
	        	njordiRuleNode = (NjordiRuleDefinition) node.getUserObject();
	        }
	        
	    	System.out.println("Row is " + row + " for selection");
	        super.getTreeCellRendererComponent(
	                        tree, value, sel,
	                        expanded, leaf, row,
	                        hasFocus);
	        if (leaf && isClean(njordiRuleNode)) {
	            setIcon(cleanIcon);
	            setToolTipText("The rule is up to date on the server.");
//	            setIconTextGap(10);
	            setName(njordiRuleNode.getName());
//	            setName(njordiRuleNode.getShortname()); //getShortName isn't currently implemented
	        } else if (leaf && !isClean(njordiRuleNode)) {
	        	setIcon(dirtyIcon);
	            setToolTipText("The rule has been modified locally and must be saved");
//	            setIconTextGap(20);
//	            setName(" --- " + njordiRuleNode.getName());
	            setText(" *** " + njordiRuleNode.getName());
//	            setName(njordiRuleNode.getShortname());
	            //TODO: figure out why this makes it shorten the rule name and show elipses
	        } else if ( row == 0) {
	        	setIcon(bigipIcon);
	        } else {
	        	setIcon(folderIcon);
	            setToolTipText(null);
	        }

	        return this;
	    }

	    protected boolean isClean(NjordiRuleDefinition njordiRuleNode) {

	        if ( njordiRuleNode.isModified() ) {
	        	return false; 
	        }
	        return true;
	    }
	    
	    /** Returns an ImageIcon, or null if the path was invalid. */
	    protected static ImageIcon createImageIcon(String path) {
	    	java.net.URL imgURL = njordTreeRenderer.class.getResource(path);
	        if (imgURL != null) {
	            return new ImageIcon(imgURL);
	        } else {
	            System.err.println("Couldn't find file: " + path);
	            return null;
	        }
	    }
	}