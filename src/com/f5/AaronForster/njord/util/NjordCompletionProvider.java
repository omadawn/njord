/**
 * 
 */
package com.f5.AaronForster.njord.util;

import org.fife.ui.autocomplete.DefaultCompletionProvider;

/**
 * @author forster
 *
 */
public class NjordCompletionProvider extends DefaultCompletionProvider {
	
	
	/**
	 * Extended to add :: and _ as valid characters for the completion provider so they complete our words.
	 * @see org.fife.ui.autocomplete.DefaultCompletionProvider#isValidChar(char)
	 */
	protected boolean isValidChar(char ch) {
		   return ch==':' || ch=='_' || super.isValidChar(ch);
		}
}
