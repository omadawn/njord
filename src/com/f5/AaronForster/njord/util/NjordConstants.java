package com.f5.AaronForster.njord.util;


/**
 * NjordConstants provides a single place to track various types of constants for <code>Njord</code>.
 * Currently only implemented for things to send to {@link Njord}
 * 
 * @author Aaron Forster a.forster@f5.com
 * @since 0.8.3
 * @version 1.2
 *
 */
public interface NjordConstants {
	/*
	 * Because I'll need it at some point
	 */
	public static final int NULL							= 0;
	
	//Tree node types
	/**
	 * For the actual top node of a jTree.
	 */
	public static final int NODE_TYPE_TOP					= 50;
	
	/**
	 * For Local or Remote primary containers.
	 */
	public static final int NODE_TYPE_LOCAL_REMOTE			= 51;
	
	/**
	 * For 'iRules' top level container and iRules types.
	 */
	public static final int NODE_TYPE_CATEGORY				= 52; 
	
	/**
	 * For iRules themselves.
	 */
	public static final int NODE_TYPE_IRULE					= 53;
	
	//###################################################################
	//                 iRule types
	//###################################################################
	
	/**
	 * Local Traffic iRule type
	 */
	public static final int IRULE_TYPE_LTM					= 191;
	
	/**
	 * Global Traffic iRule type
	 */
	public static final int IRULE_TYPE_GTM					= 192;
	
	//###################################################################
	//                 Token Types
	//###################################################################
	
	/**
	 * Events Token type
	 */
	public static final int TOKEN_TYPE_EVENT						= 101;

	/**
	 * Operators Token type
	 */
	public static final int TOKEN_TYPE_OPERATOR						= 102;
	
	/**
	 * Statements Token type
	 */
	public static final int TOKEN_TYPE_STATEMENT					= 103;
	
	/**
	 * Functions Token type
	 */
	public static final int TOKEN_TYPE_FUNCTION						= 104;
	
	/**
	 * Commands Token type
	 */
	public static final int TOKEN_TYPE_COMMAND						= 105;
	
	/**
	 * TCLCommands Token type
	 */
	public static final int TOKEN_TYPE_TCL_COMMAND					= 106;
	
	//###################################################################
	//                 Parser Notice Types
	//###################################################################
	//Used in NjordParser to determine what we've found.
	
	/**
	 * Parser type for task labels such as #TODO: #HACK: or #FIXME:
	 */
	public static final int PARSER_TYPE_TASK					= 501;
	
	/**
	 * Parser type for commands such as HTTP::header or info
	 */
	public static final int PARSER_TYPE_COMMAND					= 502;
	
	
}
//Constants that define the different programming languages understood by
//* <code>RSyntaxTextArea</code>.  These constants are the values you can pass
//* to {@link RSyntaxTextArea#setSyntaxEditingStyle(String)} to get syntax
//* highlighting.<p>
//*
//* By default, all <code>RSyntaxTextArea</code>s can render all of these
//* languages, but this can be changed (the list can be augmented or completely
//* overwritten) on a per-text area basis.  What languages can be rendered is
//* actually managed by the {@link TokenMakerFactory} installed on the text
//* area's {@link RSyntaxDocument}.  By default, all
//* <code>RSyntaxDocumenet</code>s have a factory installed capable of handling
//* all of these languages.