package com.f5.AaronForster.njord.util;


import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.rpc.ServiceException;

import org.apache.axis.AxisFault;
import org.slf4j.Logger;

import com.f5.AaronForster.njord.MainGuiWindow;
import com.f5.AaronForster.njord.test.MainGuiWindowTest;
import com.f5.AaronForster.njord.test.Uispec4jMainGuiWindowTest;

public class f5ExceptionHandler {
	//TODO: figure out how to log where the exception occured
	//TODO: Figure out how to log properly in this code.
	//TODO: Decide how to handle provided messages. Do I want to replace my text with the provided text or do I want to append?
	//TODO: Create a logger if one isn't provided to me.
	//TODO: Fix this so I can have owner instead of needing both of these
	private MainGuiWindow ownerAsMainGuiWindow; // Actually I think this should be something more Generic and then I should figure out what it is and set it to that type.
	private MainGuiWindowTest ownerAsMainGuiWindowTest;
	private Uispec4jMainGuiWindowTest ownerAsUispec4jMainGuiWindowTest;
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
		this.ownerAsMainGuiWindow = owner;
	}
	
	public f5ExceptionHandler(Exception e, MainGuiWindowTest owner, Logger log) {
		this.e = e;
		this.log = log;
		this.ownerAsMainGuiWindowTest = owner;
	}
	
	public f5ExceptionHandler(Exception e, Uispec4jMainGuiWindowTest owner, Logger log) {
		this.e = e;
		this.log = log;
		this.ownerAsUispec4jMainGuiWindowTest = owner;
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
		this.ownerAsMainGuiWindow = owner;
		this.e = e;
	}
	
	public f5ExceptionHandler(String Message, MainGuiWindow owner, Exception e, Logger log) {
		this.message = Message;
		this.ownerAsMainGuiWindow = owner;
		this.e = e;
		this.log = log;
	}
	
	//Use this when you throw an exception
//	f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e, this, log);
//	exceptionHandler.processException();
	
	public void processException() {
		log.debug("Error is an instance of " + e.getClass().toString());
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
			//This is going to break if I generate this exception from within my jUnit tests.
			ownerAsMainGuiWindow.setNoticesText(errorMessage);
			
			
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
			log.error(message);
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

		
		
//		// Change this
//	} catch (RemoteException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	} catch (ServiceException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//	// to this (Not Real Code)
//	} catch (RemoteException e) {
//		log.stuff("Caught RemoteException " + e + " at " whereExceptionCaught);)
//	} catch (ServiceException e) {
//		log.stuff("Caught RemoteException " + e + " at " whereExceptionCaught);)
//	}
	
	
	
		
	}
}
