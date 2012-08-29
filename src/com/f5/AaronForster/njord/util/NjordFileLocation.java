/**
 * 
 */
package com.f5.AaronForster.njord.util;

import iControl.LocalLBRuleRuleDefinition;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;

import org.eclipse.osgi.framework.adaptor.FilePath;
import org.fife.ui.rsyntaxtextarea.FileLocation;
import org.fife.ui.rsyntaxtextarea.TextEditorPane;

import com.f5.AaronForster.njord.MainGuiWindow;

/**
 * @author forster
 *
 */
public class NjordFileLocation extends FileLocation {
	public String ruleName = null;
	public String ruleDefinition = null;
	public iControl.Interfaces ic = null;
	public LocalLBRuleRuleDefinition rule = null;
	public boolean exists = false; // Used to determine if the file needs to be created. IE is this a new file we've created or one we have loaded.
	public boolean local = false;
	public MainGuiWindow owner = null;
	public File fileHandle = null;	
	
	/**
	 * FileLocation for an iRule on a BIGIP.
	 * 
	 * @param mainWindow A handle for the main window in case we need to send notices/etc.
	 * @param ruleName The name of the iRule.
	 * @param ic An initialized iControl object to fetch rules/etc.
	 */
	public NjordFileLocation(MainGuiWindow mainWindow, String ruleName, iControl.Interfaces ic) {
		owner = mainWindow;
		this.ruleName = ruleName;
		this.ic = ic;
		local = false;
		getRuleFromBIGIP(ruleName);
	}
	
	/**
	 * Soon to be deprecated
	 * FileLocation for a locally created (And thus not on a BIGIP iRule. Giving us a Rule Definition tells us to set the 
	 * local variable which will cause us to create the iRule on the server when we save it instead of saving it.
	 * 
	 * @param mainWindow A handle for the main window in case we need to send notices/etc.
	 * @param ruleName The name of the iRule.
	 * @param ic An initialized iControl object to fetch rules/etc.
	 * @param ruleDefinition The text of the iRule.
	 */
	public NjordFileLocation(MainGuiWindow mainWindow, String ruleName, iControl.Interfaces ic, String ruleDefinition) {
		owner = mainWindow;
		this.ruleName = ruleName;
		this.ic = ic;
		local = true;
		this.ruleDefinition = ruleDefinition;
	}

	/**
	 * A constructor for creating local iRules which will be saved on the local file system instead of the BIGIP.
	 * 
	 * @param mainWindow A handle for the main window in case we need to send notices/etc.
	 * @param ruleName The name of the iRule.
	 * @param localPath A java IO file.
	 */
	public NjordFileLocation(MainGuiWindow mainWindow, String ruleName, File localPath) {
		owner = mainWindow;
		this.ruleName = ruleName;
		local = true;
		fileHandle = localPath;
		if (fileHandle.exists()) {
			this.exists = true;
		}
	}
	
	/**
	 * Fetches the actual rule content from the BIGIP.
	 * 
	 * @param ruleName
	 */
	public void getRuleFromBIGIP(String ruleName) {
		String[] ruleNames = { ruleName };
		
		try {
			rule = ic.getLocalLBRule().query_rule(ruleNames)[0];
		} catch (RemoteException e) {
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e);
			exceptionHandler.processException();
		} catch (Exception e) {
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e);
			exceptionHandler.processException();
		}
		
		ruleDefinition = rule.getRule_definition();
		
	}
	
	/* (non-Javadoc)
	 * @see org.fife.ui.rsyntaxtextarea.FileLocation#getActualLastModified()
	 */
	@Override
	protected long getActualLastModified() {
		return TextEditorPane.LAST_MODIFIED_UNKNOWN;
	}

	/* (non-Javadoc)
	 * @see org.fife.ui.rsyntaxtextarea.FileLocation#getFileFullPath()
	 */
	@Override
	public String getFileFullPath() {
		// TODO Auto-generated method stub
		if (local) {
			return fileHandle.getAbsolutePath();
		} else {
			//Do something better here for remote.
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see org.fife.ui.rsyntaxtextarea.FileLocation#getFileName()
	 */
	@Override
	public String getFileName() {
		return ruleName;
	}

	/* (non-Javadoc)
	 * @see org.fife.ui.rsyntaxtextarea.FileLocation#getInputStream()
	 */
	@Override
	public InputStream getInputStream() throws IOException {
		if (!local) {
			InputStream is = new ByteArrayInputStream( ruleDefinition.getBytes("UTF-8") );
			return is;
		} else {
			InputStream is = new BufferedInputStream(new FileInputStream(fileHandle));
			return is;
		}
		
	}

	/* (non-Javadoc)
	 * @see org.fife.ui.rsyntaxtextarea.FileLocation#getOutputStream()
	 */
	@Override
	public OutputStream getOutputStream() throws IOException {
		if (!local) {
			OutputStream os = new NjordOutputStream(owner, ruleName, ic, local);
			return os;			
		} else {
			if (!fileHandle.exists()) {
				fileHandle.createNewFile();
			}
			OutputStream os = new FileOutputStream(fileHandle);
			return os;
		}
	}

	/* (non-Javadoc)
	 * @see org.fife.ui.rsyntaxtextarea.FileLocation#isLocal()
	 */
	@Override
	public boolean isLocal() {
		return local;
	}
	
	//TODO: I think I can get rid of this.
	public boolean isLocal(boolean local) {
		this.local = local;
		return this.local;
	}
	
	/* (non-Javadoc)
	 * @see org.fife.ui.rsyntaxtextarea.FileLocation#isLocalAndExists()
	 */
	@Override
	public boolean isLocalAndExists() {
		if (local && exists) {
			return true;
		} else {
			return false;
		}
	}
		
	@Override
	public boolean isRemote() {
		return !local;
	}
//
//	public boolean save() {
//		
//		return false;
//	}
}
