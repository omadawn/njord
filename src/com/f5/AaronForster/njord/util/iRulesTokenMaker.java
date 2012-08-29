package com.f5.AaronForster.njord.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;
import org.fife.ui.rsyntaxtextarea.modes.TclTokenMaker;

import com.f5.AaronForster.njord.MainGuiWindow;
import com.f5.AaronForster.njord.NjordiRuleDefinition;

//Here's the thread that I used to figure this stuff out http://fifesoft.com/forum/viewtopic.php?f=10&t=268&p=1749#p1749
public class iRulesTokenMaker extends TclTokenMaker {
//	org.fife.ui.rsyntaxtextarea.TokenMap
	TokenMap myWordsToHighlight = new TokenMap();
	java.net.URL operatorsURL = MainGuiWindow.class.getResource("/resources/iRulesOperatorsUncategorized.txt"); // contains, matches_glob, etc
	java.net.URL statementsURL = MainGuiWindow.class.getResource("/resources/iRulesStatementsUncategorized.txt"); // drop, pool and more
	java.net.URL functionsURL = MainGuiWindow.class.getResource("/resources/iRulesFunctionsUncategorized.txt"); // findstr, class and others
	java.net.URL commandsURL = MainGuiWindow.class.getResource("/resources/iRulesCommandsUncategorized.txt"); // HTTP::return etc etc
	java.net.URL tclCommandsURL = MainGuiWindow.class.getResource("/resources/tclCommandsUncategorized.txt"); // Built in tcl commands

	
	int operatorsClassification = Token.OPERATOR;
	int statementsClassification = Token.RESERVED_WORD_2;
	int functionsClassification = Token.FUNCTION;
	int commandsClassification = Token.RESERVED_WORD_2;
	
	public iRulesTokenMaker() {
		initializeTokens();

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
	
//	myWordsToHighlight.put("thisword", Token.IDENTIFIER);
	
	public void addToken(char[] array, int start, int end, int tokenType, int startOffset) {
//		updateTokenMap();
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
		try {
		    BufferedReader in = new BufferedReader(new FileReader(operatorsURL.getPath()));
		    String str;
		    while ((str = in.readLine()) != null) {
		        String[] words = str.split(","); // split on commas
		        for (String word : words) {
		        	myWordsToHighlight.put(word, operatorsClassification);
		        }
		        
		    }
		    in.close();
		} catch (IOException e) {
		}
		
	}
	
	private void initializeStatements() {
//		if ( !statementsFile.exists() ) {
//			System.out.println("Statements file doesn't exist!");
//			return;
//		} else {
//			System.out.println("Settings file exists");
//		}
		try {
		    BufferedReader in = new BufferedReader(new FileReader(statementsURL.getPath()));
		    String str;
		    while ((str = in.readLine()) != null) {
		        String[] words = str.split(","); // split on commas
		        for (String word : words) {
		        	myWordsToHighlight.put(word, statementsClassification);
		        }
		        
		    }
		    in.close();
		} catch (IOException e) {
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
		try {
		    BufferedReader in = new BufferedReader(new FileReader(functionsURL.getPath()));
		    String str;
		    while ((str = in.readLine()) != null) {
		        String[] words = str.split(","); // split on commas
		        for (String word : words) {
		        	myWordsToHighlight.put(word, functionsClassification);
		        }
		        
		    }
		    in.close();
		} catch (IOException e) {
		}
	}
	
	private void initializeCommands() {
		try {
		    BufferedReader in = new BufferedReader(new FileReader(commandsURL.getPath()));
		    String str;
		    while ((str = in.readLine()) != null) {
		        String[] words = str.split(","); // split on commas
		        for (String word : words) {
		        	myWordsToHighlight.put(word, commandsClassification);
		        }
		        
		    }
		    in.close();
		} catch (IOException e) {
		}
	}
}
