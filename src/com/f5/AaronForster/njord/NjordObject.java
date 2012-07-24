/**
 * 
 */
package com.f5.AaronForster.njord;

/*
 * TODOS
 * TODO: Figure out if I want toString() to return the short name or the long name.
 * TODO: Implement an equals() Which should compare the contents of the object. IE the actual config definition minus the name.
 */
/**
 * NjordObject is the base for all custom Njord related objects
 * 
 *
 * @author Aaron Forster @date 20120601
 * @version 0.5.1
 * @Since 0.5
 */
public class NjordObject {

	/*#################################################################################
	 * ATTRIBUTES
	 * #################################################################################*/
	/**
	 * Is this object new? AKA does not exist on the bigip.
	 */
	public boolean isNew = false;
	
	/**
	 * Has the object been modified and thus needs to be saved on the bigip.
	 */
	public boolean isModified = false;
	/**
	 * The BigIP this object is actually attached to. I might not use this. I will probably have a list of things like iRules, iApps, whatever that we've pulled in.
	 * Currently there is only one BigIP so this attr isn't in use.
	 */
	public String BigIP = null;
	/**
	 * The name of the object. This is the fully qualified (full path) name.
	 */
	public String myName = null;
	

	//For descendent classes implement an object parameter that holds the object we have pulled in from the BigIP
	
	//Set the owner in here?

	
	/*#################################################################################
	 * CONSTRUCTORS
	 *#################################################################################*/	
	public NjordObject() {
		isNew = true;
		isModified = true;
		myName = Integer.toString(this.hashCode()); //We don't have a real name so let's use the hash.
	}
	
	/*#################################################################################
	 * GETTERS AND SETTERS
	 *#################################################################################*/
	/**
	 * Get the full name of the object including path.
	 * 
	 * @return
	 */
	public String getName() {
		return myName;
	}
	
	/**
	 * Get only the last component of the name.
	 * 
	 * @return
	 */
	public String getShortname() {
		String[] FullNameComponents = myName.split("/");
		return FullNameComponents[FullNameComponents.length]; //This _should_ return the very last component. I'll find out later.
	}
	
	/**
	 * Takes a string and sets the new name to that string.
	 * Currently not checking to see if it's a full path or not.
	 * Returns the name you set which can be used to see if it worked the way you want.
	 * 
	 * @param newName
	 * @return
	 */
	public String setName(String newName) {
		//TODO: Check and make sure this has a path and if not add /Common in front of it.
		//TODO: See if there's a way to validate the name without sending back to the BigIP
		myName = newName;
		return myName; 
	}
	
	public String getBigIP() {
		return BigIP;
	}
	
	public void setBigIP(String newBigIP) {
		this.BigIP = newBigIP;
	}
	
	
	/*#################################################################################
	 * OTHER METHODS
	 *#################################################################################*/
	public boolean isNew() {
		return isNew;
	}
	
	public boolean isModified() {
		return isModified;
	}
	
	
	/**
	 * toString() Returns the full path of the object.
	 * @return
	 */
	public String toString() {
		return getName();
	}	
}
