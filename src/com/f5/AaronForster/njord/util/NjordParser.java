/**
 * 
 */
package com.f5.AaronForster.njord.util;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.text.Element;

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.parser.AbstractParser;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParserNotice;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;
import org.fife.ui.rsyntaxtextarea.parser.Parser;
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice;

import com.f5.AaronForster.njord.MainGuiWindow;

/**
 * Currently just a copy and pase from taskTagparser
 * TaskTagParserNotes
 * Parser that identifies "task tags," such as "<code>TODO</code>",
 * "<code>FIXME</code>", etc. in source code comments.
 *
 * 
 * @author forster
 *
 */
public class NjordParser extends AbstractParser {
	/**
	 * I'm not entirely sure but I believe this holds the matches we've found.
	 */
	private DefaultParseResult result;
	/**
	 * The default pattern which defines what a task is.
	 */
	private String DEFAULT_TASK_PATTERN	= "TODO|FIXME|HACK";
	/**
	 * The actual place we will read the pattern from in case it has been overriden from the default.
	 */
	private Pattern taskPattern;
	/**
	 * What have we found, a task, a command, other? Takes values defined in NjordConstants.
	 */
	private int tokenType;
	/**
	 * I believe this is the color it sets the toolTip box to. maybe I should change it from this ugly grey to the ugly orange used in java editors.
	 */
	private static final Color COLOR = new Color(48, 150, 252);
	/**
	 * The path to the tool tips file.
	 */
	public String toolTipTextspath = "/resources/toolTipTexts.txt";
	/**
	 * A hashMap of tokens and the toolTips associated with them.
	 */
	public HashMap<String, String> toolTipsMap = new HashMap<String, String>();
	/**
	 * Holds the line separator string for the os we're running on.
	 */
	public String nl = System.getProperty("line.separator");
	// Make Linkable items
		/**
		 * The user's desktop environment
		 */
		public Desktop desktop = java.awt.Desktop.getDesktop();
		/**
		 * The beginning of an HTML link. <a href="
		 */
		private static final String A_HREF = "<a href=\"";
		/**
		 * The first closing bracked of an HTML link. \>.
		 */
		private static final String HREF_CLOSED = "\">";
		/**
		 * The closing item for an HTML link. </a>.
		 */
		private static final String HREF_END = "</a>";
		/**
		 * An open HTML tag.
		 */
		private static final String HTML = "<html>";
		/**
		 * The closing HTML tag.
		 */
		private static final String HTML_END = "</html>";
		
		
	/**
	 * Creates a new task parser.  The default parser treats the following
	 * identifiers in comments as task definitions:  "<code>TODO</code>",
	 * "<code>FIXME</code>", and "<code>HACK</code>".
	 */
	public NjordParser() {
		buildToolTipTexts();
		result = new DefaultParseResult(this);
		setTaskPattern(DEFAULT_TASK_PATTERN);
	}
	
	/**
	 * Returns the regular expression used to search for tasks.
	 *
	 * @return The regular expression.  This may be <code>null</code> if no
	 *         regular expression was specified (or an empty string was
	 *         specified).
	 * @see #setTaskPattern(String)
	 */
	public String getTaskPattern() {
		return taskPattern==null ? null : taskPattern.pattern();
	}
	
	/**
	 * This is the main code which actually finds the tokens that we care to mark.
	 */
	public ParseResult parse(RSyntaxDocument doc, String style) {

		Element root = doc.getDefaultRootElement();
		int lineCount = root.getElementCount();

		if (taskPattern==null ||
				style==null || SyntaxConstants.SYNTAX_STYLE_NONE.equals(style)){
			result.clearNotices();
			result.setParsedLines(0, lineCount-1);
			return result;
		}

		// TODO: Pass in parsed line range and just do that
		result.clearNotices();
		result.setParsedLines(0, lineCount-1);

		for (int line=0; line<lineCount; line++) {
			//This grabs all the distinct 'words' for a line as defined in the syntax. Essentially every hunk of printable text separated by a hunk of non-printable text.
			//t will currently be set to just the first token and will be set to the next token at the bottom of this section.
			Token t = doc.getTokenListForLine(line);
			int offs = -1;
			int start = -1;
			String text = null;

			while (t!=null && t.isPaintable()) {
				//First let's check to see if it's a TODO:
				//Though I might ditch this part if I can't figure out an easy way to stick it into a separate tasks list.
				text = t.getLexeme();
				if (t.type==Token.COMMENT_EOL ||
						t.type==Token.COMMENT_MULTILINE ||
						t.type==Token.COMMENT_DOCUMENTATION) {

					offs = t.offset;
					text = t.getLexeme();

					Matcher m = taskPattern.matcher(text);
					if (m.find()) {
						start = m.start();
						offs += start; //I think this actually is just so that the match starts at the word TODO: not at the whole comment.
						tokenType = NjordConstants.PARSER_TYPE_TASK;
						break;
					}
				} else if (toolTipsMap.containsKey(text)) {
//				} else if (t.type==Token.FUNCTION || t.type==Token.IDENTIFIER || t.type==Token.RESERVED_WORD || t.type==Token.RESERVED_WORD_2) {
					offs = t.offset;
					start = offs;
					
					
					if (text.matches("when")) {
						start = offs;
						tokenType = NjordConstants.PARSER_TYPE_COMMAND;
						break;
					}
					int len = text.length();
					toolTipNotice toolTip = new toolTipNotice(this, toolTipsMap.get(text), line, offs, len);
					toolTip.setLevel(ParserNotice.INFO);
					toolTip.setShowInEditor(false);
					toolTip.setColor(COLOR);
					result.addNotice(toolTip);
				}
				
				
				//Set t to the next word
				t = t.getNextToken();
			}
			
			if (start>-1 && tokenType == NjordConstants.PARSER_TYPE_TASK) {
				text = text.substring(start);
				// TODO: Strip off end of MLC's if they're there.
				int len = text.length();
				TaskNotice taskNotice = new TaskNotice(this, text, line, offs, len);
				taskNotice.setLevel(ParserNotice.INFO);
				taskNotice.setShowInEditor(false);
				taskNotice.setColor(COLOR);
				result.addNotice(taskNotice);
			} //else if (start>-1 && tokenType == NjordConstants.PARSER_TYPE_COMMAND) {
////				text = text.substring(start);
//				
//				
//				//DO SOMETHING DIFFERENT HERE
//
//
//				// Master page for iRules https://devcentral.f5.com/wiki/iRules.Commands.ashx
//				// Holycrap iRules commands by version https://devcentral.f5.com/wiki/iRules.BIGIP_Commands_by_Version.ashx
//				//None of these mechanisms work for linking.
////				String message = "If you are seeing this then the parser tooltip thingy is working neither of these work: this " +
////						"http://devcentral.f5.com or this {@link devcentral.f5.com}" + 
////						" so maybe it's this <a href=\'http://devcentral.f5.com'>Dev Central</a>, maybe.";
//				int len = text.length();
//				toolTipNotice toolTip = new toolTipNotice(this, toolTipsMap.get(t), line, offs, len);
//				toolTip.setLevel(ParserNotice.INFO);
//				toolTip.setShowInEditor(false);
//				toolTip.setColor(COLOR);
//				result.addNotice(toolTip);
//			}

		}
		return result;
	}
	
	/**
	 * Pulls the tool tips in from the resources/toolTipTexts.txt file and populates the toolTipTextsArray with them.
	 */
	public void buildToolTipTexts() {
//		HashMap hm = new HashMap();
//		// Put elements to the map
//		hm.put("John Doe", new Double(3434.34));
		
		BufferedReader reader = null;
		try {
            reader = new BufferedReader(new InputStreamReader(MainGuiWindow.class.getResourceAsStream(toolTipTextspath)));
		    String str;
		    String token = "";
		    String text = "";
		     
		    while ((str = reader.readLine()) != null) {
		    	if (str.startsWith("<tooltip")) {
		    		Pattern pattern = Pattern.compile("<tooltip name=\"", Pattern.DOTALL);
	    			Matcher matcher = pattern.matcher(str);
		    		token = matcher.replaceAll(""); 
		    		pattern = Pattern.compile("\">", Pattern.DOTALL);
	    			matcher = pattern.matcher(token);
	    			token = matcher.replaceAll("");
		    	} else if (str.startsWith("</tooltip") ) {
		    		//This is the end of the tooltip let's assign the txt to the array, clear the temp holders and move on.\
//		    		System.out.println("Adding token [" + token + "] and value [" + text +"]");
		    		toolTipsMap.put(token, text);
		    		token = "";
		    		text = "";
		    	} else {
		    		//If it isn't the start of a tooltip or the end of a tooltip it must be tooltip text so let's append it to text
		    		text = text + str + nl;
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
	
	/**
	 * Sets the pattern of task identifiers.  You will usually want this to be
	 * a list of words "or'ed" together, such as
	 * "<code>TODO|FIXME|HACK|REMIND</code>".
	 *
	 * @param pattern The pattern.  A value of <code>null</code> or an
	 *        empty string effectively disables task parsing.
	 * @throws PatternSyntaxException If <code>pattern</code> is an invalid
	 *         regular expression.
	 * @see #getTaskPattern()
	 */
	public void setTaskPattern(String pattern) throws PatternSyntaxException {
		if (pattern==null || pattern.length()==0) {
			taskPattern = null;
		}
		else {
			taskPattern = Pattern.compile(pattern);
		}
	}
	
	/**
	 * A parser notice that signifies a task.
	 */
	public static class TaskNotice extends DefaultParserNotice {

		public TaskNotice(Parser parser, String message, int line, int offs,
				int length) {
			super(parser, message, line, offs, length);
		}

	}

	/**
	 * A parser notice for toolTip text other than a task. The two notices can probably be combined.
	 */
	public static class toolTipNotice extends DefaultParserNotice {

		public toolTipNotice(Parser parser, String message, int line, int offs,
				int length) {
			super(parser, message, line, offs, length);
		}
	}
	

	/**
	 * Takes a JLabel and converts it's text into a hyperlink using it's tooltipText as the link tarket.
	 * 
	 * @param label The label to link. 
	 * @param ml A mouse listener to notify us when a click event has happened.
	 */
	private static void makeLinkable(JLabel label, MouseListener ml) {
	    assert ml != null;
	    label.setText(htmlIfy(linkIfy(label.getText(), label.getToolTipText())));
	    label.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
	    label.addMouseListener(ml);
	}
	
	/**
	 * Checks to see if the platform we are executing on supports Desktop Browsing.
	 * @return True/False.
	 */
	private static boolean isBrowsingSupported() {
	    if (!Desktop.isDesktopSupported()) {
	        return false;
	    }
	    boolean result = false;
	    Desktop desktop = java.awt.Desktop.getDesktop();
	    if (desktop.isSupported(Desktop.Action.BROWSE)) {
	        result = true;
	    }
	    return result;

	}
	
	/**
	 * The mouse event listener for the desktop browsing interface. Detects the mouse click event and opens a browser.
	 *
	 */
	private static class LinkMouseListener extends MouseAdapter {
	    @Override
	    public void mouseClicked(java.awt.event.MouseEvent evt) {
	        JLabel l = (JLabel) evt.getSource();
	        try {
	            Desktop desktop = java.awt.Desktop.getDesktop();
	            URI uri = new java.net.URI(getPlainLink(l.getText()));
	            desktop.browse(uri);
	        } catch (URISyntaxException use) {
	            throw new AssertionError(use);
	        } catch (IOException ioe) {
	            ioe.printStackTrace();
	            JOptionPane.showMessageDialog(null, "Sorry, a problem occurred while trying to open this link in your system's standard browser.", "A problem occured", JOptionPane.ERROR_MESSAGE);
	        }
	    }
	}
	
	/**
	 * Converts a string into an HTML link. This method requires that s is a plain string that requires no further escaping.
	 * 
	 * @param s A String to be converted into an HTML link.
	 * @return HTML text.
	 */
	private static String getPlainLink(String s) {
	    return s.substring(s.indexOf(A_HREF) + A_HREF.length(), s.indexOf(HREF_CLOSED));
	}
	
	/**
	 * Creates an HTML link from the provided data.
	 * 
	 * @param textString The text to be presented.
	 * @param linkLocation The URL Location to use as the destination of the HREF.
	 * @return
	 */
	private static String linkIfy(String textString, String linkLocation) {
	    return A_HREF.concat(linkLocation).concat(HREF_CLOSED).concat(textString).concat(HREF_END);
	}
	
	//WARNING
	//This method requires that s is a plain string that requires
	//no further escaping
	
	/**
	 * Surrounds a string with HTML open and close HTML tags.
	 * 
	 * @param s Non HTML String text.
	 * @return The same text surrounded with HTML open and close text.
	 */
	private static String htmlIfy(String s) {
	    return HTML.concat(s).concat(HTML_END);
	}
	
}