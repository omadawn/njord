package com.f5.AaronForster.njord.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;
import org.fife.ui.rsyntaxtextarea.modes.TclTokenMaker;

import com.f5.AaronForster.njord.NjordiRuleDefinition;

//Here's the thread that I used to figure this stuff out http://fifesoft.com/forum/viewtopic.php?f=10&t=268&p=1749#p1749
public class iRulesTokenMaker extends TclTokenMaker {
//	org.fife.ui.rsyntaxtextarea.TokenMap
	TokenMap myWordsToHighlight = new TokenMap();
	String operatorsFilePath = "resources/iRulesOperatorsUncategorized.txt"; // contains, matches_glob, etc
	String statementsFilePath = "resources/iRulesStatementsUncategorized.txt"; // drop, pool and more
	String functionsFilePath = "resources/iRulesFunctionsUncategorized.txt"; // findstr, class and others
	String commandsFilePath = "resources/iRulesCommandsUncategorized.txt"; // HTTP::return etc etc
	
	
	public iRulesTokenMaker() {
		initializeTokens();

		//TODO: I need to decide what to set my words to. I like the idea of setting the irules commands to FUNCTION and then just making function not be FECKING UGLY like it is in the default. Maybe I'll set it to purple.
		myWordsToHighlight.put("thisword", Token.IDENTIFIER);
		myWordsToHighlight.put("thatword", Token.FUNCTION);
		myWordsToHighlight.put("oneword", Token.RESERVED_WORD);
		myWordsToHighlight.put("twoword", Token.RESERVED_WORD_2);
		myWordsToHighlight.put("how-aboutthis", Token.RESERVED_WORD);
		//TODO: For some reason all the stuff with :: in it doesn't get highlighted. Which sorta sucks. I've put in a comment to the thread.
//		myWordsToHighlight.put("HTTP::respond", Token.FUNCTION);
		//TODO: This doesn't work because HTTP : and respond are being fed to the syntax the tokenmaker comes out with in order to determine if they are key words. Somehow I have to get it to process the whole thing instead of splitting it up.
		
		String respondToken = "HTTP::respond";
		myWordsToHighlight.put(respondToken, Token.FUNCTION);
		myWordsToHighlight.put("this:that", Token.FUNCTION);
		myWordsToHighlight.put("HTTP::path", Token.RESERVED_WORD_2);
		myWordsToHighlight.put("HTTP::uri", Token.RESERVED_WORD);
		
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
		File operatorsFile = new File(operatorsFilePath);

//		if ( !operatorsFile.exists() ) {
//			System.out.println("Operators file doesn't exist!");
//			return;
//		} else {
////			System.out.println("Settings file exists");
//		}
		try {
		    BufferedReader in = new BufferedReader(new FileReader(operatorsFilePath));
		    String str;
		    while ((str = in.readLine()) != null) {
		        String[] words = str.split(","); // split on commas
		        for (String word : words) {
		        	myWordsToHighlight.put(word, Token.OPERATOR);
		        }
		        
		    }
		    in.close();
		} catch (IOException e) {
		}
		
	}
	
	private void initializeStatements() {
		File statementsFile = new File(statementsFilePath);

//		if ( !statementsFile.exists() ) {
//			System.out.println("Statements file doesn't exist!");
//			return;
//		} else {
//			System.out.println("Settings file exists");
//		}
		try {
		    BufferedReader in = new BufferedReader(new FileReader(statementsFilePath));
		    String str;
		    while ((str = in.readLine()) != null) {
		        String[] words = str.split(","); // split on commas
		        for (String word : words) {
		        	myWordsToHighlight.put(word, Token.FUNCTION);
		        }
		        
		    }
		    in.close();
		} catch (IOException e) {
		}
	}
	
	private void initializeFunctions() {
		File functionsFile = new File(functionsFilePath);

//		if ( !functionsFile.exists() ) {
//			System.out.println("Functions file doesn't exist!");
//			return;
//		} else {
////			System.out.println("Functions file exists");
//		}
		try {
		    BufferedReader in = new BufferedReader(new FileReader(functionsFilePath));
		    String str;
		    while ((str = in.readLine()) != null) {
		        String[] words = str.split(","); // split on commas
		        for (String word : words) {
		        	myWordsToHighlight.put(word, Token.RESERVED_WORD_2);
		        }
		        
		    }
		    in.close();
		} catch (IOException e) {
		}
	}
	
	private void initializeCommands() {
		File commandsFile = new File(commandsFilePath);

		if ( !commandsFile.exists() ) {
			System.out.println("Commands file doesn't exist!");
			return;
		} else {
//			System.out.println("Commands file exists");
		}
		try {
		    BufferedReader in = new BufferedReader(new FileReader(commandsFilePath));
		    String str;
		    while ((str = in.readLine()) != null) {
		        String[] words = str.split(","); // split on commas
		        for (String word : words) {
		        	myWordsToHighlight.put(word, Token.RESERVED_WORD);
		        }
		        
		    }
		    in.close();
		} catch (IOException e) {
		}
	}
}
