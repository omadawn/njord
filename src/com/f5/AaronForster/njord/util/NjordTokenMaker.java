package com.f5.AaronForster.njord.util;

import javax.swing.text.Segment;

import org.fife.ui.rsyntaxtextarea.AbstractTokenMaker;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;


/*
 * NOTES
 * 
 * OK, I am unlikely to use this class. AbstractTokenMaker actually is used to make your own syntax highlighter. The full thing. Parens, line breaks, keywords, comments, all of it.
 * There is another method to simply add a word to hightlight
 * public void addHighlightedIdentifier(String word, int tokenType);
 *    public boolean removeHighlightedIdentifier(String word);
 *   public void clearAddedHighlightedIdentifiers();
 *
 */


// Use it like this
//SyntaxTextArea editor = new RSyntaxTextArea();
//((AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance()).putMapping(WikiTokenMaker.StyleName, WikiTokenMaker.class.getName());
//editor.setSyntaxEditingStyle(WikiTokenMaker.StyleName);      
//editor.setSyntaxScheme(getWikiSyntaxScheme(editor));
//editor.setWrapStyleWord(true);
//editor.setLineWrap(true);

//THIS CODE ISN'T MINE IT'S FROM HERE http://fifesoft.com/forum/viewtopic.php?f=10&t=275
public class NjordTokenMaker extends AbstractTokenMaker {

	public static final String StyleName = "Njord";

	// These values must be valid Token values.
	// I redefined them to clarify the code.
	public static final int NULL      = 0;  // Token.NULL
	public static final int HEADLINE   = 1;  // Token.COMMENT_EOL
	public static final int TICKS      = 2;  // Token.COMMENT_DOCUMENTATION
	public static final int TAGS      = 4;  // Token.RESERVED_WORD
	public static final int TEXT      = 15; // Token.IDENTIFIER
	public static final int WHITESPACE   = 16; // Token.WHITESPACE
	public static final int TABLE      = 17; // Token.SEPARATOR
	public static final int GRAMMAR      = 18; // Token.OPERATOR
	public static final int WIKILINK    = 19; // Token.PREPROCESSOR
	public static final int EXTLINK       = 20; // Token.ERROR_IDENTIFIER

	protected final String grammar = "*:;";

	protected final String table = "{}|!-";

	protected final char HeadlineChar = '=';

	protected final char TickChar = '\'';

	protected final char OpenLinkChar = '[';

	protected final char CloseLinkChar = ']';

	private int currentTokenStart;
	private int currentTokenType;

	public NjordTokenMaker() {
		super();
	}


	/**
	 * Checks the token to give it the exact ID it deserves before
	 * being passed up to the super method.
	 *
	 * @param segment <code>Segment</code> to get text from.
	 * @param start Start offset in <code>segment</code> of token.
	 * @param end End offset in <code>segment</code> of token.
	 * @param tokenType The token's type.
	 * @param startOffset The offset in the document at which the token occurs.
	 */
	public void addToken(Segment segment, int start, int end, int tokenType, int startOffset) {
		switch (tokenType) {
		// Since reserved words, functions, and data types are all passed
		// into here as "identifiers," we have to see what the token
		// really is...
		case TEXT:
			try {
				int value = wordsToHighlight.get(segment, start,end);
				if (value!=-1)
					tokenType = value;
			} catch (Throwable t) {
			}
			break;
		case WHITESPACE:
		case GRAMMAR:
		case TABLE:
		case HEADLINE:
		case TICKS:
		case WIKILINK:
		case EXTLINK:
			break;

		default:
			System.err.println(getClass().getSimpleName() + ".addToken: Unknown token type " + tokenType);
			tokenType = TEXT;
			break;

		}
		try {
			super.addToken(segment, start, end, tokenType, startOffset);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}


	/**
	 * Returns the text to place at the beginning and end of a
	 * line to "comment" it in a this programming language.
	 *
	 * @return The start and end strings to add to a line to "comment"
	 *         it out.
	 */
	public String[] getLineCommentStartAndEnd() {
		return new String[] { null, null };
	}


	/**
	 * Returns whether tokens of the specified type should have "mark
	 * occurrences" enabled for the current programming language.
	 *
	 * @param type The token type.
	 * @return Whether tokens of this type should have "mark occurrences"
	 *         enabled.
	 */
	public boolean getMarkOccurrencesOfTokenType(int type) {
		return false;
	}


	/**
	 * Returns the words to highlight for Windows batch files.
	 *
	 * @return A <code>TokenMap</code> containing the words to highlight for
	 *         Windows batch files.
	 * @see org.fife.ui.rsyntaxtextarea.AbstractTokenMaker#getWordsToHighlight
	 */
	public TokenMap getWordsToHighlight() {
		TokenMap tokenMap = new TokenMap(true); // Ignore case.
		addTag(tokenMap, "pre");
		addTag(tokenMap, "strike");
		addTag(tokenMap, "nowiki");
		addTag(tokenMap, "b");
		addTag(tokenMap, "p");
		addTag(tokenMap, "br");
		addTag(tokenMap, "u");
		return tokenMap;
	}

	private void addTag(TokenMap tokenMap, String tag) {
		tokenMap.put("<" + tag + ">", TAGS);
		tokenMap.put("</" + tag + ">", TAGS);
	}


	/**
	 * Returns a list of tokens representing the given text.
	 *
	 * @param text The text to break into tokens.
	 * @param startTokenType The token with which to start tokenizing.
	 * @param startOffset The offset at which the line of tokens begins.
	 * @return A linked list of tokens representing <code>text</code>.
	 */
	public Token getTokenList(Segment text, int startTokenType, final int startOffset) {

		resetTokenList();
		try {
			char[] array = text.array;
			int offset = text.offset;
			int count = text.count;
			int end = offset + count;

			// See, when we find a token, its starting position is always of the form:
			// 'startOffset + (currentTokenStart-offset)'; but since startOffset and
			// offset are constant, tokens' starting positions become:
			// 'newStartOffset+currentTokenStart' for one less subraction operation.
			int newStartOffset = startOffset - offset;

			currentTokenStart = offset;
			currentTokenType  = startTokenType;

			int openingTickCount = 0;

			//beginning:
			for (int i=offset; i<end; i++) {

				char c = array[i];

				switch (currentTokenType) {

				case NULL:
					currentTokenStart = i;   // Starting a new token here.
					switch (c) {
					// Headline
					case HeadlineChar:
						if (array[i+1] == HeadlineChar) {
							i = end - 1;
							addToken(text, currentTokenStart,i, HEADLINE, newStartOffset+currentTokenStart);
							//    We need to set token type to null so at the bottom we don't add one more token.
							currentTokenType = NULL;
							break;
						}
					case ' ':
					case '\t':
						currentTokenType = WHITESPACE;
						break;

						// Ticks: Bold and/or Italics
					case TickChar:
						if (array[i+1] == TickChar) {
							int lastTick = i;
							while (lastTick < end && array[lastTick] == TickChar)
								lastTick++;
							i = lastTick;
							openingTickCount = lastTick - currentTokenStart;                        
							currentTokenType = TICKS;
						} else
							currentTokenType = TEXT;
						break;

						// WikiLink or ExtLink
					case OpenLinkChar:
						if (array[currentTokenStart+1] == OpenLinkChar)
							currentTokenType = WIKILINK;
						else
							currentTokenType = EXTLINK;
						break;

					default:

						// Just to speed things up a tad, as this will usually be the case (if spaces above failed).
						if (RSyntaxUtilities.isLetterOrDigit(c) || c=='\\') {
							currentTokenType = TEXT;
							break;
						}

						// Lists, Definitions, Indentations
						int indexOf = grammar.indexOf(c,0);
						if (indexOf>-1) {
							addToken(text, currentTokenStart,i, GRAMMAR, newStartOffset+currentTokenStart);
							currentTokenType = Token.NULL;
							break;
						}

						// Table chars
						indexOf = table.indexOf(c,0);
						if (indexOf>-1) {
							addToken(text, currentTokenStart,i, TABLE, newStartOffset+currentTokenStart);
							currentTokenType = Token.NULL;
							break;
						}
						currentTokenType = TEXT;
						break;

					} // End of NULL switch

					break;

				case WHITESPACE:
					switch (c) {
					case ' ':
					case '\t':
						break;   // Still whitespace.

						// Bold and/or Italics
					case TickChar:
						if ((i + 1 < end) && array[i+1] == TickChar) {
							addToken(text, currentTokenStart,i-1, WHITESPACE, newStartOffset+currentTokenStart);
							currentTokenStart = i;
							// set 'i' to first non-tick character
							int lastTick = i;
							while (lastTick < end && array[lastTick] == TickChar)
								lastTick++;
							i = lastTick;
							openingTickCount = i - currentTokenStart;
							currentTokenType = TICKS;
						}
						break;

						// WikiLink or ExtLink
					case OpenLinkChar:
						addToken(text, currentTokenStart,i-1, WHITESPACE, newStartOffset+currentTokenStart);
						currentTokenStart = i;                     
						if (array[i+1] == OpenLinkChar)
							currentTokenType = WIKILINK;
						else
							currentTokenType = EXTLINK;
						break;

					default:
						// Add the whitespace token and start anew.
						addToken(text, currentTokenStart,i-1, WHITESPACE, newStartOffset+currentTokenStart);
						currentTokenStart = i;
						currentTokenType = TEXT;

					} // End of WHITESPACE switch

					break;

				default:
				case TEXT:
					switch (c) {
					case ' ':
					case '\t':
						addToken(text, currentTokenStart,i-1, TEXT, newStartOffset+currentTokenStart);
						currentTokenStart = i;
						currentTokenType = WHITESPACE;
						break;

						// Bold and/or Italics
					case TickChar:
						if ((i + 1 < end) && array[i+1] == TickChar) {
							addToken(text, currentTokenStart,i-1, TEXT, newStartOffset+currentTokenStart);
							currentTokenStart = i;
							// set 'i' to first non-tick character
							int lastTick = i;
							while (lastTick < end && array[lastTick] == TickChar)
								lastTick++;
							i = lastTick;
							openingTickCount = i - currentTokenStart;
							currentTokenType = TICKS;
						}
						break;

						// WikiLink or ExtLink
					case OpenLinkChar:
						addToken(text, currentTokenStart,i-1, TEXT, newStartOffset+currentTokenStart);
						currentTokenStart = i;                     
						if (array[i+1] == OpenLinkChar)
							currentTokenType = WIKILINK;
						else
							currentTokenType = EXTLINK;                     
						break;

					default:
						// Otherwise, fall through and assume we're still okay as TEXT...
					}
					// End of TEXT switch
					break;

				case TICKS:
					if (c == TickChar) {
						int lastTick = i;
						while (lastTick < end && array[lastTick] == TickChar)
							lastTick++;
						int closingTickCount = lastTick - i;

						if (openingTickCount == 0 || openingTickCount == closingTickCount) {
							// Only syntax color chunks with matching opening & closing tick counts
							// OR if this is a multiline section
							i = lastTick;
							addToken(text, currentTokenStart,i - 1, TICKS, newStartOffset+currentTokenStart);
						} else {
							// Count mismatch... look for matching tick count
							while ((lastTick + 1) < end && (openingTickCount != closingTickCount)) {
								closingTickCount = 0;
								int nextTick = text.toString().indexOf(TickChar, lastTick - offset + 1);
								if (nextTick == -1)
									// break while
										lastTick = end;
								else
									// Got a next tick
									lastTick = offset + nextTick;
								while (lastTick < end && array[lastTick] == TickChar) {
									closingTickCount++;
									lastTick++;
								}
							}
							i = lastTick;
							if (openingTickCount == closingTickCount)
								// Have matching tick count... syntax color
								addToken(text, currentTokenStart,i - 1, TICKS, newStartOffset+currentTokenStart);
							else
								// Missing matching tick count... plain text
								addToken(text, currentTokenStart,i - 1, TEXT, newStartOffset+currentTokenStart);
						}
						currentTokenStart = i;
						currentTokenType = TEXT;
					}
					break;

				case WIKILINK:
					if (c == CloseLinkChar) {
						if (i + 1 < end && array[i + 1] == CloseLinkChar) {
							addToken(text, currentTokenStart,i, WIKILINK, newStartOffset+currentTokenStart);
							i = i + 1;
							currentTokenStart = i;
							currentTokenType = TEXT;                     
						}
					}
					break;

				case EXTLINK:
					if (c == CloseLinkChar) {
						addToken(text, currentTokenStart,i - 1, EXTLINK, newStartOffset+currentTokenStart);
						currentTokenStart = i;
						currentTokenType = TEXT;                     
					}
					break;

				} // End of switch (currentTokenType).

			} // End of for (int i=offset; i<end; i++).

			// Deal with the (possibly there) last token.
			if (currentTokenType > NULL) {
				// Unclosed tokens are considered plain text
				addToken(text, currentTokenStart,end-1, TEXT, newStartOffset+currentTokenStart);
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		addNullToken();
		// Return the first token in our linked list.
		return firstToken;
	}
}
