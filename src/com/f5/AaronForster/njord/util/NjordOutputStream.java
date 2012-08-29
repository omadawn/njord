/**
 * 
 */
package com.f5.AaronForster.njord.util;

import iControl.LocalLBRuleRuleDefinition;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.rmi.RemoteException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.f5.AaronForster.njord.MainGuiWindow;

/**
 * @author forster
 *
 */
public class NjordOutputStream extends OutputStream  {
	public iControl.Interfaces ic = null;
	public String iRuleName = "";
	public boolean local = false; // True if the iRule does not exist on the server
	public boolean exists = false;
	public StringBuilder buffer;
	public ByteArrayOutputStream baos = new ByteArrayOutputStream();
	public MainGuiWindow owner = null;
	public boolean isInError = false; // This is a stupid hack to try and figure out why TextEditorPane isn't noticing that we're throwing an IOException.
	final Logger log = LoggerFactory.getLogger(NjordOutputStream.class);
	
	
	public NjordOutputStream(String iRuleName, iControl.Interfaces ic) {
		this.iRuleName = iRuleName;
		this.ic = ic;
	}
	
	public NjordOutputStream(MainGuiWindow owner, String iRuleName, iControl.Interfaces ic, boolean local, boolean exists) {
		this.owner = owner;
		this.iRuleName = iRuleName;
		this.ic = ic;
		this.local = local;
	}

	/** 
	 * Takes an input stream of bytes which we will write to the BIGIP
	 */
	@Override
	public void write(int output) {
		baos.write(output);
	}
	
	public void flush() throws IOException {
		write(baos.toString());
		//This did not solve it.
//		write(baos.toString("ISO-8859-1")); //Western (ISO-8859-1)
	}

	public void write(String ruleDefinition) throws IOException{
		LocalLBRuleRuleDefinition[] saveRules = new LocalLBRuleRuleDefinition[1]; // Create a list of iRules in order to write them back. We only have one so we only need a tiny list
		saveRules[0] = new LocalLBRuleRuleDefinition();
		saveRules[0].setRule_definition(ruleDefinition);
		saveRules[0].setRule_name(iRuleName);

		try {
			//          if (nodeInfo.isNew()) {
			if (!exists) {
				//Rule doesn't exist on the server so create it instead of modifying it.
				ic.getLocalLBRule().create(saveRules);
				exists = true;
			} else {
				ic.getLocalLBRule().modify_rule(saveRules);
			}
		} catch (RemoteException e1) {
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e1, owner, log);
			exceptionHandler.processException();
			throw new java.io.IOException(e1.getMessage());
		} catch (Exception e1) {
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e1, owner, log);
			exceptionHandler.processException();
			throw new java.io.IOException(e1.getMessage());
		}
	}
	
}
