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
	
	public NjordFileLocation(MainGuiWindow mainWindow, String ruleName, iControl.Interfaces ic, boolean local) {
		owner = mainWindow;
		this.ruleName = ruleName;
		this.ic = ic;
		this.local = local;
		getRule(ruleName);
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
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.fife.ui.rsyntaxtextarea.FileLocation#isLocalAndExists()
	 */
	@Override
	public boolean isLocalAndExists() {
		// TODO Auto-generated method stub
		return false;
	}
		
	@Override
	public boolean isRemote() {
		return true;
	}

	public boolean save() {
		
		return false;
	}
}
