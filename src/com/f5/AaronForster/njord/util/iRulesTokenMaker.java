package com.f5.AaronForster.njord.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;
import org.fife.ui.rsyntaxtextarea.modes.TclTokenMaker;

import com.f5.AaronForster.njord.MainGuiWindow;

//Here's the thread that I used to figure this stuff out http://fifesoft.com/forum/viewtopic.php?f=10&t=268&p=1749#p1749
public class iRulesTokenMaker extends TclTokenMaker {
//	org.fife.ui.rsyntaxtextarea.TokenMap
	TokenMap myWordsToHighlight = new TokenMap();
	
	String operatorsPath = "/resources/iRulesOperatorsUncategorized.txt";
	String statementsPath = "/resources/iRulesStatementsUncategorized.txt"; // drop, pool and more
	String functionsPath = "/resources/iRulesFunctionsUncategorized.txt"; // findstr, class and others
	String commandsPath = "/resources/iRulesCommandsUncategorized.txt"; // HTTP::return etc etc
	String tclCommandsPath = "/resources/tclCommandsUncategorized.txt"; // Built in tcl commands
	
	int operatorsClassification = Token.OPERATOR;
	int statementsClassification = Token.RESERVED_WORD_2;
	int functionsClassification = Token.FUNCTION;
	int commandsClassification = Token.RESERVED_WORD_2;
	
	public iRulesTokenMaker() {
		initializeTokens(); // Now for said files let's build our various lists of tokens.

		// Then let's include a few hand coded others which are used primarily for testing and developing the syntax highlighting scheme.
		//TODO: I need to decide what to set my words to. I like the idea of setting the irules commands to FUNCTION and then just making function not be FECKING UGLY like it is in the default. Maybe I'll set it to purple.
		myWordsToHighlight.put("ANNOTATION", Token.ANNOTATION);
		myWordsToHighlight.put("VARIABLE", Token.VARIABLE);
		myWordsToHighlight.put("OPERATOR", Token.OPERATOR);
		myWordsToHighlight.put("IDENTIFIER", Token.IDENTIFIER);
		myWordsToHighlight.put("FUNCTION", Token.FUNCTION);
		myWordsToHighlight.put("REGEX", Token.REGEX);
		myWordsToHighlight.put("COMMENT_KEYWORD", Token.COMMENT_KEYWORD);
		myWordsToHighlight.put("COMMENT_MARKUP", Token.COMMENT_MARKUP);
		myWordsToHighlight.put("COMMENT_MULTILINE", Token.COMMENT_MULTILINE);
		myWordsToHighlight.put("COMMENT_EOL", Token.COMMENT_EOL);
		myWordsToHighlight.put("RESERVED_WORD", Token.RESERVED_WORD);
		myWordsToHighlight.put("RESERVED_WORD_2", Token.RESERVED_WORD_2);
		
		myWordsToHighlight.put("thisword", Token.IDENTIFIER);
		myWordsToHighlight.put("thatword", Token.FUNCTION);
		myWordsToHighlight.put("oneword", Token.RESERVED_WORD);
		myWordsToHighlight.put("twoword", Token.RESERVED_WORD_2);
		myWordsToHighlight.put("won_derline", Token.RESERVED_WORD);
		myWordsToHighlight.put("two_derline", Token.RESERVED_WORD_2);
	}
	
	public void addToken(char[] array, int start, int end, int tokenType, int startOffset) {
		   if (tokenType==Token.IDENTIFIER) { // I might also want to use FUNCTION. I'll have to decide
		      int value = myWordsToHighlight.get(array, start, end); 
		      if (value != -1) {
		         tokenType = value;
		      }
		   } else if (tokenType==Token.FUNCTION) { 
			   int value = myWordsToHighlight.get(array, start, end);
			   if (value != -1) {
				   tokenType = value;
			   }
		   }
		   super.addToken(array, start,end, tokenType, startOffset);
		}
	
	private void initializeTokens() {
		initializeOperators(); // contains, matches_glob, etc
		initializeStatements(); // drop, pool and more
		initializeFunctions(); // findstr, class and others
		initializeCommands(); // HTTP::return etc etc
		// We're not doing Events
	}
	
	private void initializeOperators() {
//		File functionsFile = new File(operatorsURL.getPath());
//		System.out.println("Looking for " + operatorsURL.getPath());
//		if ( !functionsFile.exists() ) {
//			System.out.println("Operators file doesn't exist!");
//			return;
//		} else {
////			System.out.println("Functions file exists");
//		}
		BufferedReader reader = null;
		try {
//			InputStream stream = MainGuiWindow.class.getResourceAsStream(operatorsPath);
//			InputStreamReader fileReader = new InputStreamReader(stream);
//			BufferedReader reader = new BufferedReader(fileReader);  
       
            reader = new BufferedReader(new InputStreamReader(MainGuiWindow.class.getResourceAsStream(operatorsPath)));
		    String str;

		    while ((str = reader.readLine()) != null) {
		        String[] words = str.split(","); // split on commas
		        for (String word : words) {
		        	myWordsToHighlight.put(word, operatorsClassification);
		        }
		        
		    }
		} catch (IOException e) {
		} finally {
			try {
				if (reader !=null) { 
					reader.close();  
				}
			} catch (IOException e) {
				f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e);
				exceptionHandler.processException();
			}
		}
		
	}
	
	private void initializeStatements() {
//		if ( !statementsFile.exists() ) {
//			System.out.println("Statements file doesn't exist!");
//			return;
//		} else {
//			System.out.println("Settings file exists");
//		}
		BufferedReader reader = null;
		try {
//			InputStream stream = MainGuiWindow.class.getResourceAsStream(statementsPath);
//			InputStreamReader fileReader = new InputStreamReader(stream);
//			reader = new BufferedReader(fileReader);  
            
            reader = new BufferedReader(new InputStreamReader(MainGuiWindow.class.getResourceAsStream(statementsPath)));
		    String str;
		    while ((str = reader.readLine()) != null) {
		        String[] words = str.split(","); // split on commas
		        for (String word : words) {
		        	myWordsToHighlight.put(word, statementsClassification);
		        }
		        
		    }
		} catch (IOException e) {
		} finally {
			try {
				if (reader !=null) { 
					reader.close();  
				}
			} catch (IOException e) {
				f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e);
				exceptionHandler.processException();
			}
		}
	}
	
	private void initializeFunctions() {
//		File functionsFile = new File(functionsFilePath);

//		if ( !functionsFile.exists() ) {
//			System.out.println("Functions file doesn't exist!");
//			return;
//		} else {
////			System.out.println("Functions file exists");
//		}
		BufferedReader reader = null;
		try {
//			InputStream stream = MainGuiWindow.class.getResourceAsStream(functionsPath);
//			InputStreamReader fileReader = new InputStreamReader(stream);
//			reader = new BufferedReader(fileReader);
			
			reader = new BufferedReader(new InputStreamReader(MainGuiWindow.class.getResourceAsStream(functionsPath)));
		    String str;
		    while ((str = reader.readLine()) != null) {
		        String[] words = str.split(","); // split on commas
		        for (String word : words) {
		        	myWordsToHighlight.put(word, functionsClassification);
		        }
		        
		    }
		} catch (IOException e) {
		} finally {
			try {
				if (reader !=null) { 
					reader.close();  
				}
			} catch (IOException e) {
				f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e);
				exceptionHandler.processException();
			}
		}
	}
	
	private void initializeCommands() {
		BufferedReader reader = null;
		try {
//			InputStream stream = MainGuiWindow.class.getResourceAsStream(commandsPath);
//			InputStreamReader fileReader = new InputStreamReader(stream);
//			reader = new BufferedReader(fileReader); 
			
			reader = new BufferedReader(new InputStreamReader(MainGuiWindow.class.getResourceAsStream(commandsPath)));
		    String str;
		    while ((str = reader.readLine()) != null) {
		        String[] words = str.split(","); // split on commas
		        for (String word : words) {
		        	myWordsToHighlight.put(word, commandsClassification);
		        }
		        
		    }
		} catch (IOException e) {
		} finally {
			try {
				if (reader !=null) { 
					reader.close();  
				}
			} catch (IOException e) {
				f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e);
				exceptionHandler.processException();
			}
		}
	}
}
