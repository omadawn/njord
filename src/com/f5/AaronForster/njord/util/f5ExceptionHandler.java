package com.f5.AaronForster.njord.util;


import java.net.MalformedURLException;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.axis.AxisFault;

public class f5ExceptionHandler {
	//TODO: figure out how to log where the exception occured
	//TODO: Figure out how to log properly in this code.
	//TODO: Decide how to handle provided messages. Do I want to replace my text with the provided text or do I want to append?
	private Object owner; // Until I figure out something better
	private String message = "";
	private Exception e;
	
	//I think I might either need to have multiple constructors, one for each exception type.
	// Or b use getClass() or whatever it is instead of instanceOf. I think that everything is going to be of type Exception because of the constructor casting it.
	public f5ExceptionHandler(Exception e) {
		this.e = e;
	}
	
	public f5ExceptionHandler(String Message, Exception e) {
		this.message = message;
		this.e = e;
	}
	
	//TODO: this shouldn't be Class it should be some other object type.
	public f5ExceptionHandler(String Message, Object owner, Exception e) {
		this.message = message;
		this.owner = owner;
		this.e = e;
	}
	
	//Use this when you throw an exception
//	f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e1);
//	exceptionHandler.processException();
	
	public void processException() {
		System.out.println("Error is an instance of " + e.getClass().toString());
		if (e instanceof ServiceException) {
			// Log ServiceException
			// What is the difference between ServiceException and RemoteExeption?
			// Log a generic vulture exception message
			if ( message != "" ) {
				message = "ServiceException: " + e.getMessage();
			} else {
				message = message + ": " + e.getMessage();
			}
			System.out.println(message);
			//log(message) //somehow
		} else if (e instanceof RemoteException) {
			// Log RemoteException
			// Try and reconenct?
			// Log a generic vulture exception message
			if ( message != "" ) {
				message = "RemoteException: " + e.getMessage();
			} else {
				message = message + ": " + e.getMessage();
			}
			System.out.println(message);
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
			System.out.println(message);
			//log(message) //somehow	
		} else if (e instanceof AxisFault) {
			//This might be where we end up if we get an error in the irule saving.
			if ( message != "" ) {
				message = "AxisFault: " + e.getMessage();
			} else {
				message = message + ": " + e.getMessage();
			}
			System.out.println(message);
		} else {
			// Log some new exception we were unnaware of happened here.
			// Perhaps now we stack trace but likely only if in debug
			// Log a generic vulture exception message
			if ( message != "" ) {
				message = "Un-known Exception of type: " + e.getClass() + " encountered sent message: " + e.getMessage();
			} else {
				message = message + ": " + e.getMessage();
			}
			System.out.println(message);
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
