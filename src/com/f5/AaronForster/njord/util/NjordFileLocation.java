/**
 * 
 */
package com.f5.AaronForster.njord.util;

import iControl.LocalLBRuleRuleDefinition;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;

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
	public boolean local = false;
	public MainGuiWindow owner = null;
	
	public NjordFileLocation(String ruleName, iControl.Interfaces ic) {
//		ruleName = rule; //.getName();
//		ruleDefinition = rule.getRule_definition();
		this.ruleName = ruleName;
		this.ic = ic;
		getRule(ruleName);
	}
	
	/**
	 * FileLocation for an iRule on a BIGIP.
	 * 
	 * @param mainWindow
	 * @param ruleName
	 * @param ic
	 */
	public NjordFileLocation(MainGuiWindow mainWindow, String ruleName, iControl.Interfaces ic) {
		owner = mainWindow;
		this.ruleName = ruleName;
		this.ic = ic;
		local = false;
		getRule(ruleName);
	}
	
	/**
	 * FileLocation for a locally created (And thus not on a BIGIP iRule. Giving us a Rule Definition tells us to set the 
	 * local variable which will cause us to create the iRule on the server when we save it instead of saving it.
	 * 
	 * @param mainWindow
	 * @param ruleName
	 * @param ic
	 * @param ruleDefinition
	 */
	public NjordFileLocation(MainGuiWindow mainWindow, String ruleName, iControl.Interfaces ic, String ruleDefinition) {
		owner = mainWindow;
		this.ruleName = ruleName;
		this.ic = ic;
		local = true;
//		getRule(ruleName);
		this.ruleDefinition = ruleDefinition;
	}

	public void getRule(String ruleName) {
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
		return  TextEditorPane.LAST_MODIFIED_UNKNOWN;
	}

	/* (non-Javadoc)
	 * @see org.fife.ui.rsyntaxtextarea.FileLocation#getFileFullPath()
	 */
	@Override
	public String getFileFullPath() {
		// TODO Auto-generated method stub
		return null;
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
	protected InputStream getInputStream() throws IOException {
		InputStream is = new ByteArrayInputStream( ruleDefinition.getBytes("UTF-8") );
		return is;
	}

	/* (non-Javadoc)
	 * @see org.fife.ui.rsyntaxtextarea.FileLocation#getOutputStream()
	 */
	@Override
	protected OutputStream getOutputStream() throws IOException {
//		OutputStream os = new ByteArrayOutputStream();
		OutputStream os = new NjordOutputStream(owner, ruleName, ic, local);
		return os;
	}

	/* (non-Javadoc)
	 * @see org.fife.ui.rsyntaxtextarea.FileLocation#isLocal()
	 */
	@Override
	public boolean isLocal() {
		return local;
	}

	public boolean isLocal(boolean local) {
		this.local = local;
		return this.local;
	}
	
	/* (non-Javadoc)
	 * @see org.fife.ui.rsyntaxtextarea.FileLocation#isLocalAndExists()
	 */
	@Override
	public boolean isLocalAndExists() {
		// Always return false because local only iRules will never exist.
		return false;
	}
		
	@Override
	public boolean isRemote() {
		return local;
	}

	public boolean save() {
		
		return false;
	}
}
