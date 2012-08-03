package com.f5.AaronForster.njord.util;

import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;
import org.fife.ui.rsyntaxtextarea.modes.TclTokenMaker;

//Here's the thread that I used to figure this stuff out http://fifesoft.com/forum/viewtopic.php?f=10&t=268&p=1749#p1749
public class iRulesTokenMaker extends TclTokenMaker {
//	org.fife.ui.rsyntaxtextarea.TokenMap
	TokenMap myWordsToHighlight = new TokenMap();
	
	
	public iRulesTokenMaker() {
//		TokenMap myWordsToHighlight = new TokenMap();
		//TODO: I need to decide what to set my words to. I like the idea of setting the irules commands to FUNCTION and then just making function not be FECKING UGLY like it is in the default. Maybe I'll set it to purple.
		myWordsToHighlight.put("thisword", Token.IDENTIFIER);
		myWordsToHighlight.put("thatword", Token.FUNCTION);
		myWordsToHighlight.put("oneword", Token.RESERVED_WORD);
		myWordsToHighlight.put("twoword", Token.RESERVED_WORD_2);
		//TODO: For some reason all the stuff with :: in it doesn't get highlighted. Which sorta sucks. I've put in a comment to the thread.
		myWordsToHighlight.put("HTTP::respond", Token.FUNCTION);
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
}
