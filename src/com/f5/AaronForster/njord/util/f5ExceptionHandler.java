package com.f5.AaronForster.njord.util;


import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.rpc.ServiceException;

import org.apache.axis.AxisFault;
import org.hamcrest.core.IsNull;
import org.slf4j.Logger;

import com.f5.AaronForster.njord.MainGuiWindow;
import com.f5.AaronForster.njord.test.SwingMainGuiWindowTests;
import com.f5.AaronForster.njord.test.Uispec4jMainGuiWindowTest;

public class f5ExceptionHandler {
	//TODO: This sounds a little silly but take all of the actual action out of the ifelse block that we're using to determine what exception type this is. Create a separate method for each exception type. It will make the code much cleaner, easier to read and easier to document.
	//TODO: figure out how to log where the exception occured
	//TODO: Figure out how to log properly in this code.
	//TODO: Decide how to handle provided messages. Do I want to replace my text with the provided text or do I want to append?
	//TODO: Create a logger if one isn't provided to me.
	//TODO: Fix this so I can have owner instead of needing both of these
	private MainGuiWindow owner; // Actually I think this should be something more Generic and then I should figure out what it is and set it to that type.
	private String message = "";
	private Exception e;
	private Logger log;
	
	//I think I might either need to have multiple constructors, one for each exception type.
	// Or b use getClass() or whatever it is instead of instanceOf. I think that everything is going to be of type Exception because of the constructor casting it.
	public f5ExceptionHandler(Exception e) {
		this.e = e;
	}
	
	public f5ExceptionHandler(Exception e, Logger log) {
		this.e = e;
		this.log = log;
	}
	
	public f5ExceptionHandler(Exception e, MainGuiWindow owner, Logger log) {
		this.e = e;
		this.log = log;
		this.owner = owner;
	}
	
	public f5ExceptionHandler(String Message, Exception e) {
		this.message = Message;
		this.e = e;
	}
	
	public f5ExceptionHandler(String Message, Exception e, Logger log) {
		this.message = Message;
		this.e = e;
		this.log = log;
	}
	
	//TODO: this shouldn't be Class it should be some other object type.
	public f5ExceptionHandler(String Message, MainGuiWindow owner, Exception e) {
		this.message = Message;
		this.owner = owner;
		this.e = e;
	}
	
	public f5ExceptionHandler(String Message, MainGuiWindow owner, Exception e, Logger log) {
		this.message = Message;
		this.owner = owner;
		this.e = e;
		this.log = log;
	}
	
	//Use this when you throw an exception
//	f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e, this, log);
//	exceptionHandler.processException();
	
	public void processException() {
		if (log != null) {
			log.debug("Error is an instance of " + e.getClass().toString());
		} else {
			System.out.println("Error is an instance of " + e.getClass().toString());
		}
		if (e instanceof ServiceException) {
			// Log ServiceException
			// What is the difference between ServiceException and RemoteExeption?
			// Log a generic vulture exception message
			if ( message != "" ) {
				message = "ServiceException: " + e.getMessage();
			} else {
				message = message + ": " + e.getMessage();
			}
			log.error(message);
			//log(message) //somehow
		} else if (e instanceof RemoteException) {
			log.debug("Processing Remote Exception");
			// Log RemoteException
			// Try and reconenct?

			String errorContents = e.getMessage();
			Pattern pattern = Pattern.compile(".*error_string         :", Pattern.DOTALL);
			//This only works if we get a syntax error modifying an iRule.
//			Pattern pattern = Pattern.compile(".*error_string         :.*error:", Pattern.DOTALL);
			Matcher matcher = pattern.matcher(errorContents);
			//TODO: Modify the pattern and matcher so we get rid of this crap at the beginning as well. 
			//Error:  01070151:3: Rule [/Common/myIrulesOutputTest] error:
			// But to do that I'm going to have to 
			//TODO: Figure out what sort of error I'm getting. So far I have
			//Modify iRule error iRule already exists. Show everything from error_string
			//Modify iRule syntax error in iRule, cut out all the error number and rule [rulename] error: crap
			

			//Uncomment if working on the regex. The commented code shows what we are matching.
//			while (matcher.find()) {
//				log.info("Start index: " + matcher.start());
//				log.info(" End index: " + matcher.end() + " ");
//				log.info(matcher.group());
//				log.info("End matcher section ##############");
//			}
			
			//TODO: Replace this println with something that either pops up an error or sets the contents of a status box in the main gui. I prefer the latter.
			String errorMessage = matcher.replaceAll("");
			log.info("Error: " + errorMessage);
			
//			editorNoticesBox.setText(errorMessage);
			
			//If the error is that we are trying to create a rule on top of an existing rule then it doesn't have the <stuff> Rule [<rule_name>] error: part
			//This is what getMessage returns in the case of extra text. I need to pull out the last part error_string:
//			Exception caught in LocalLB::urn:iControl:LocalLB/Rule::modify_rule()
//			Exception: Common::OperationFailed
//				primary_error_code   : 17236305 (0x01070151)
//				secondary_error_code : 0
//				error_string         : 01070151:3: Rule [/Common/http_responder] error: 
//			line 15: [parse error: extra characters after close-brace] [ffff]
			
			
			log.error(errorMessage);
			if (owner != null) {
				owner.setNoticesText(errorMessage);
			} 
			
			if ( message != "" ) {
			message = "RemoteException: " + message;
			} else {
			message = message + ": " + message;
			}
			log.error(message);
			
			//log(message) //somehow
		} else if (e instanceof MalformedURLException) {
			// Log RemoteException
			// Try and reconenct?
			// Log a generic vulture exception message
			if ( message != "" ) {
				message = "MalformedURLException: " + e.getMessage();
			} else {
				message = message + ": " + e.getMessage();
			}
			log.error(message);
			//log(message) //somehow	
		} else if (e instanceof AxisFault) {
			//This might be where we end up if we get an error in the irule saving.
			if ( message != "" ) {
				message = "AxisFault: " + e.getMessage();
			} else {
				message = message + ": " + e.getMessage();
			}
			log.error(message);
		} else {
			// Log some new exception we were unnaware of happened here.
			// Perhaps now we stack trace but likely only if in debug
			// Log a generic vulture exception message
			if ( message != "" ) {
				message = "Un-known Exception of type: " + e.getClass() + " encountered sent message: " + e.getMessage();
			} else {
				message = "Un-known Exception of type: " + e.getClass() + " encountered sent message: " + e.getMessage();
//				message = message + ": " + e.getMessage();
			}
			System.out.println("Unknown exception caught of type " + e.getClass());
			System.out.println(e.getStackTrace());
//			log.error(message);
			//log(message) //somehow
		}
		
		
//		If I don't need to do anything specific for specific exceptions I might be able to just
//		message = e.getClass() + " Encountered with message: " + e.getMessage();
		
//		Has this code in public void removeCertificate(final LoadBalancer loadBalancer, final CertificateIdentifier certificateIdentifier)
//		LoadBalancerNotFoundException lnfe = new LoadBalancerNotFoundException(loadBalancer.getLoadBalancerIdentifier());
//		lnfe.setStackTrace(e.getStackTrace());
//		throw lnfe;
		// LBNFE does the below and extends VultureException
//	    public LoadBalancerNotFoundException(final LoadBalancerIdentifier loadBalancerIdentifier) {
//	        super("Load balancer [%s] not found", loadBalancerIdentifier.getValue());
//
//	        this.loadBalancerIdentifier = loadBalancerIdentifier;
//	    }

		
		//TODO: create processRemoteException(RemoteException e) here is it's javaDoc
		/*
		 * RemoteException is so common I wrote a whole separate method to deal with it. RemoteException is what all response messages 
		 * from the BIGIP will come back as such as errors that the object you are trying to create already exists or that the syntax 
		 * of the iRule you are trying to save is incorrect.
		 *  
		 * @param e A RemoteException
		 */
	
		
	}
}
