package com.f5.AaronForster.njord;

import iControl.LocalLBRuleRuleDefinition;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.xml.rpc.ServiceException;

import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.ShorthandCompletion;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.TextEditorPane;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.modes.TclTokenMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.f5.AaronForster.njord.util.f5ExceptionHandler;
import com.f5.AaronForster.njord.util.f5JavaGuiTree;
//import java.util.prefs;


//A tutorial on how to write a text editor with syntax highlighting. Seems like it could be a tiny bit sketchy but worth a look
// http://ostermiller.org/syntax/editor.html
// An older tutorial from sun (98)
// http://java.sun.com/products/jfc/tsc/articles/text/editor_kit/index.html
// Some source code called 'simple text editor'
// http://www.picksourcecode.com/articles/explan.php?id=6c9882bbac1c7093bd25041881277658&ems=9a0fa83125d48ab7258eab27754dd23e&lg=10
// https://github.com/jgruber/iControlExamples/tree/master/src/main/java/com/f5se/examples

/*
 * Changelog
 * 201200719/20 (major changes finished at about 2:30 and 3:30 AM.) base iRule editor and then TCL syntax highlighting functionality completed.
 * 		Calling this 0.5 now as I figure I'm about 1/2 way to something I wouldn't be embarrased to look at.
 * 		Renaming to Njord, norse god of wind. it's 'controlling wind' and it's got a J in it. for the Java reference. it's perfect.
 * 20120802 0.6 lot's of code cleanup some bugs fixed. 
 *  Saving the connection settings to the preferences file if you change them.
 *  major GUI clean up.
 * 20120803  
 *  added syntax highlighting for iRules but it's not fully functional support for words with colons or underscores in them doesn't seem to work.
 *  Added basic auto complete for iRules and TCL functions
 *  Added a notifications box that persists in the GUI
 * 20120803 0.7
 *  Added new iRule creation
 */


/*
 * NOTES SECTION
 * 
 * TODO SECTION
 * TODO: "Reconnect" IE rebuild the iRules list if you update your connection settings.
 * TODO: Deal with having the iRule name include the partition if we're on an older version. IE V10 doesn't include the full path in the name but V11 does.
 * TODO: Fix a bug where if you hit new irule while a rule is selected that rule's contents will be replaced with the default template.
 * TODO: Working on syntax highlighting this is the java token scanner http://svn.fifesoft.com/viewvc-1.0.5/bin/cgi/viewvc.cgi/RSTALanguageSupport/trunk/src/org/fife/rsta/ac/common/TokenScanner.java?view=markup&revision=588&root=RSyntaxTextArea
 * 			I believe that's the part that pulls out tokens from the document which the syntax then decides if they should be highlighted or not. I can probably extend the TCL one.
 * 		And this is the java parser. Dunno what entirely the difference is http://svn.fifesoft.com/viewvc-1.0.5/bin/cgi/viewvc.cgi/RSTALanguageSupport/trunk/src/org/fife/rsta/ac/java/JavaParser.java?view=markup&revision=279&root=RSyntaxTextArea 
 * 			I think they are used in conjunction.
 * TODO: Move the notices box out of the editor so that it can permanently be on the screen Perhaps I need a vertical splitpane on the RHS Then all messages can update the same one.
 * TODO: Prioritise To Dos - pretty much done, terminal work in progress.
 *
 * TODO: Start logging instead of printing to stdout - started this one.
 * TODO: Fix the doesn't ignore sys irules on v10 bug below
 * TODO: Fix the prefs file so we save it with line breaks. Hrm... might need to do proper line breaks per OS.
 * TODO: Figure out if there is a built in way to have the editor highlight variables that you've assigned.
 * 
 * TODO: Add make EOL visible and probably make whitespace visible setEOLMarkersVisible(boolean visible) 	setWhitespaceVisible(boolean visible) 
 * TODO: add a preference to paint tab lines. See if this setPaintTabLines(boolean paint)  does what I think it does.
 * TODO: Get rid of the progress bar and add a red/green light to the BigIP in the list
 * TODO: Handle some differences between v10 and v11 Some examples: v10 doesn't return the full path of the iRule where V10 does  
 * TODO: Confirm differences and add support for v9
 * TODO: Implement set tab width (built into ntextarea)
 * TODO: Implement some of the other fecking awesome features of rsyntax and ntext area like the built in search/replace/highlight all functionality. 
 * 
 * TODO: Figure out what the minium version of TMOS I can support is and error out if it's that. iRuler has 9.something, maybe 9.4. Is that an iControl thing or just his own. IE do I want to set my bar at 10 or can I support v9 as well. I'd like to support stuff as far back as possible.
 * TODO: Record the version long term and take different actions based on the version?
 * TODO: Add a 'connection verified' variable that I can check at stages.
 * TODO: Don't populate the iRules tree until it has been expanded.
 * 		TODO: Should I have a list iRules button or only do it when I expand the tree. Let's only do it on expand for now.  
 * TODO: Figure out if I want iControl Assembly or JGruber's method. Using assembly for now for safety
 * TODO: Get rid of the 'edit iRule' button and add a 'new iRule' button instead
 * TODO: Figure out how to handle a 'New iRule' Do I need an 'offline iRules' section or can I just label them differently in the tree
 * 
 * TODO: Add keyword highlighting for F5 specific keywords. See if I can get these automatically somewhere, maybe I can download them from Devcentral if I have to
 * 		TODO: on that point I should probably only download them via my own build process so that the code is up to date but that the client one doesn't have to connect to devcentral all the time.
 * 		Some resources for this. http://fifesoft.com/forum/viewtopic.php?f=10&t=268&sid=fe6ab2cd301cc56e2bde4e9b3088b69d
 * 					Quote:
 * 						Just a quick hint for people with the need for simple syntax colouring that does not need the complexity of a full parser.
 *
 *						Start with the *TokenMaker.java implementations that are not created by JFlex. WindowsBatchTokenMaker is a very simple one, UnixShellTokenMaker is slightly more advanced. It is very easy to start from there and "roll your own". Just change the list of reserved words and change the comment handling, and - hey presto! - here's your own syntax colouring scheme."
 * TODO: Maybe later in $userHome/.f5/njord/preferences.settings if that exists it overrides the one in apphome? I will need to store individual bigip connection info there.  
 * 			TODO: Perhaps a preferences file in $appHome/preferences.settings could hold generic items
 * TODO: Perhaps I will place the cursor in the name box and have to implement the tree editor or perhaps I will be a wimp and use a dialog box. Maybe that's why iruler does it that way.
 * TODO: Figure out if I can have the bigip do a syntax check without saving to bigip.
 * TODO: For way future I can read the statistics info on an irule. Before enabling (maybe a "profile button?" Check and make sure the rule actually has statistics enabled.
 * TODO: Add a reload from bigip/reload from disk button. Using whichever is correct.
 * 
 * TODO: Go through all the 'Auto-generated catch block' sections
 * TODO: Go through and fix the case of objects I have some which start with lower case and some which start with upper.
 * TODO: Move some code out of the actions in the actionListener and treeSelectionListeners and into subroutines.
 * 
 * TODO: Add autocomplete http://fifesoft.com/rsyntaxtextarea/examples/example5.php - this item may move down from here
 * 			Here's a perl completion provider http://svn.fifesoft.com/viewvc-1.0.5/bin/cgi/viewvc.cgi/RSTALanguageSupport/trunk/src/org/fife/rsta/ac/perl/PerlCompletionProvider.java?view=markup&revision=269&root=RSyntaxTextArea
 * 				Since I will need to create my own completion provider.
 * 			Download location for this http://sourceforge.net/projects/rsyntaxtextarea/files/
 * TODO: Create an 'Add BigIP' button because I want to:
 * TODO: Have multiple BigIPs in the tree. Each one will hold it's own connection settings. Get the name from the first connection and save it or make it editable?)
 * 
 * TODO: Start saving things like expanded state of a branch node
 * TODO: Create an interface preferences section for things like:
 * 			Always expand folders/partitions when expanding major sections
 * TODO: Clean up the tree. Some options:
 * 			Make the full path folders either git rid of the path in the list and only display on a hover
 * TODO: Change the display of the tree for an item that we are editing. Maybe put an asterisk in front of it or have an icon on each thing and change it if we're editing it.
 * TODO: Add a document listener (See rSyntaxTextArea notes below on how) So that I can tell when a rule has been edited so I can tell that it has been edited and needs to be saved. Update the tree to show.
 *  
 * TODO: Let's try and be sure not to allow a user to edit a built in iRule so they don't have to get pissed off when it won't let them save their changes.
 * TODO: Related, figure out how to identify them.
 * TODO: Add find and replace functionality. rsyntaxtextarea again to the rescue with built in find and replace including 'highlight all occurences' functionality. http://fifesoft.com/rsyntaxtextarea/examples/example4.php
 * TODO: Get rid of the top of the jtree unless I'm planning on having it be the BigIP's host name and having sections for virtuals and iRules and such.

 * TODO: See if it's possible to have multiple action listeners or if there's some other way to make that portion of it simpler.
 * TODO: Add a heirarchy for things. Use http://docs.oracle.com/javase/tutorial/uiswing/events/treeexpansionlistener.html to not load the contents of a branch node until you expand it.
 * 
 * TODO: Get rid of 'DestkopBigIPManager and replace it with this as the base.
 * TODO: Warn the user if they have some lame things like default password.
 * 
 * TODO: Add some of the fricking awesome features of rsyntaxtextarea such as automatically closing curly braces setCloseCurlyBraces(boolean close) a parser (see below,) Focusable tool tips setUseFocusableTips(boolean use) , visible white space
 * TODO: Add a parser to underline things? http://javadoc.fifesoft.com/rsyntaxtextarea/org/fife/ui/rsyntaxtextarea/parser/Parser.html
 * 
 * PUT OFF
 * 
 * TODO: Add code template functionality. rsyntaxtextarea 'code templates' are like vi 'maps' sout would turn into System.out.println("Stuff"). http://fifesoft.com/rsyntaxtextarea/examples/example2.php I would add an interface to create them and then at startup could just read in from the list and loop through creating them like the example. Perhaps another reason to force a directory for prefs store would be that a user could have multiple templates? 
 * TODO: Add iApp editing functionality? 
 * TODO: Make this generic and plugin oriented.
 * TODO: Add links to devcentral pages for keywords and functions and such the way iRuler does.
 * TODO: Come up with some way to switch back and forth between jgruber and icontrol assembly mechanisms?
 * TODO: Investigate the way 'Java Simple Text Editor' builds it's GUI from an XML config file. http://javatxteditor.sourceforge.net/ maybe not too closely since it's gui is crap but perhaps.... * 
 * TODO: Figure out what 'marked occurrences' is IE getMarkedOccurrences() I think it's like when you do a find all and it highlights them.
 * TODO: Add an option to select what version of iRules to use highlighting on. I'll need some fancy way to crawl the iRules reference pages or some better reference from corporate.
 * TODO: Include JMeter and be able to generate real traffic to test said iRules.
 * 			TODO: MUCH later support remote jmeter server functionality so that not only can I generate a little traffic but it could be a full on test bench. 
 * TODO: Implement some sort of code beautifier. There's one for java called jtidy http://sourceforge.net/projects/jtidy which is a port of something called htmltidy
 * 
 * 
 *
 * TODO: Figure out how to implement the ActionPanel in such a way that I can have an ActionPanelByType
 *   ActionPanelByType will initially be an abstract class I will write a default implementation of that generic class as a reference/starting point but I will use an abstract class to define the requirements for that pane.
 *   ActionPanelByType will be the portion of the gui that displays an item which you have selected from the list. There will need to be a 'default' type which will likely just show a welcome message or something simple. Perhaps a couple of important actions like connect and edit preferences.
 *   The Second one I will write will be the Virtual Server pane which will only have enable/disable and a couple of text items. The first of course is the default
 * 
 * TODO: Implement a default user preferences file. At startup check for a file in say $HOME/.DesktopBigIPManager/preferences.txt If it exists pull from and write to it for the saved prefs. Otherwise use a default value. This however will not only be cool for users but
 * TODO: MainGuiWindow class has a Main() in it. Apparently we start the app from there not from a separate class.
 * TODO: Get rid of any auto generated stack traces. I wonder if there's a code style setting or maybe a plugin that would make it write an error instead of stupidly dumping the stack.
 * TODO: Implement a logging mechanism.
 * 
 * 
 * JTree/editor items:
 * JTree has a TreeCellEditor in addition to it's TreeCellRenderer. This is for changind the name and such of the items in the tree itself
 *         	//TODO: Whoa, holycrapwow I think I found what I need. http://fifesoft.com/rsyntaxtextarea/ rsyntax text area is specifically a swing component for formatting CODE. you can do color highlighting stuff with the jeditorpane I'm already using but I would have to do it with things like appendToPane("hi", color.RED,paneToAppendTo); But this thing has rules like that already. It says it supports 30 languages. OOOH I hope one of them is TCL.
 *        	//Editor pane tutorial here: http://docs.oracle.com/javase/tutorial/uiswing/components/editorpane.html
 *          // and API def here: http://docs.oracle.com/javase/6/docs/api/javax/swing/JEditorPane.html
 *
 * 
 * 
 * PROGRESS BAR ITEMS:
 * TODO: Get rid of the progress bar after we have verified our connection and replace it with an icon that says 'Connection Verified' or something.
 * 
 * 
 * TODO: Peep this http://www.ibm.com/developerworks/opensource/tutorials/os-eclipse-code-templates/os-eclipse-code-templates-pdf.pdf for some in depth dialog on template customization.
 * 
 * 
 * BUGS
 * BUG: rsyntaxtextarea's built in undo/redo function has an odd quirk. If you hit undo enough times it removes all the code in the screen. I think that might be 'undoing' the part where I stick the code into the text area. Make it not do that.
 * BUG: Some times I make a change and the app no longer quits when I close the window.
 * BUG: Currently borked on Georges' mac. Not sure if it's borked on mac or some other issue. I'm definately not handling errors in building the tree correctly.
 * BUG: Potentially crashes on building the tree if there aren't any iRules.
 * BUG: Potentially crashes on building the tree if the virtual server you're connecting to is unlicenced.
 * BUG: Sys supplied iRules aren't ignored on V10 bigips
 * 
 * GENERAL NOTES ON RSYNTAXTEXTAREA:
 * Example 3 covers how to customize what exists in the right click menu. http://fifesoft.com/rsyntaxtextarea/examples/example3.php
 * <whatevertokenmaker>.getLineCommentStartAndEnd() a handy way to figure out the languages comment chars and programatically insert a comment.
 * You can add a document listener which would get called when something is changed in the document. textArea.getDocument().addDocumentListener(new MyDocumentListener());   
 */

/**
 * Currently a java based iRule editor which needs some work.
 * The Norse god of winds, sea and fire. He brings good fortune at sea and in the hunt. He is married to the giantess Skadi. His children are Freya and Freyr, whom he fathered on his own sister.
 * 
 * Originally, Njord was one of the Vanir but when they made peace with the Aesir, he and his children were given to them as hostages. The Aesir appointed both Njord and Freyr as high priests to preside over sacrifices. Freya was consecrated as sacrificial priestess. She taught the Aesir witchcraft, an art that was common knowledge among the Vanir.
 * http://en.wikipedia.org/wiki/Nj%C3%B6r%C3%B0r
 *  
 * Njord will begin it's life solely as an iRule editor but I'd like it to become a more complete bigip manager thus the name.
 * At minimum there will be functionality to enable/disable pool members and virtual servers and manage what virtual servers iRules are attached to.
 * 
 * @author Aaron Forster @date 20120601
 * @version 0.7
 */
public class MainGuiWindow implements ActionListener, TreeSelectionListener, TreeExpansionListener {
	private String logPrefix = "MainGuiWindow: ";
	private JFrame frame;
	private String newline = "\n";
	private boolean connectionInitialized = false;
	private String njordVersion = "0.7";

	
	private NjordiRuleDefinition currentSelectedRule = null;
	
	
	JTextArea output;
    JScrollPane scrollPane;
    DefaultMutableTreeNode category = null; // This is part of the navigation tree and here from an initial attempt to decouple building the tree and connecting to the BIGIP. I will probably remove it.
    JToolBar actionBar = new JToolBar(); // Will hold buttons for things like save
    JPanel NavPanel = new JPanel(); // This is the main panel for the LHS navigation tree
    JScrollPane navScrollPane = new JScrollPane();
    JTextPane defaultResultsPanelTextPane = new JTextPane();
    JLabel lblStatusLabel = new JLabel();
    PreferencesDialog preferencesDialog;
    iRuleDialog newiRuleDialong;
    
	//Connection information stuff. Ultimately we get this from the preferences file. We set the defaults here and write this to the prefs if there isn't one otherwise we use that.
	String iIPAddress = "v11ltm1.localdomain";
	long iPort = 443;
	String iUserName = "admin";
	String iPassword = "admin";
	
    // Let's not initialize it yet.
	iControl.Interfaces ic = new iControl.Interfaces(); //From Assembly
	
	//iControl.BigIP from the maven wsdl thingy is reasonably equivalent to iControl.Interfaces from the iControl 
	//	'Assembly' available on DevCentral.
    
	// Create a new TCL Token Maker so I can add some keywords to it.
	TclTokenMaker tokenMaker = null;
	
	NjordiRuleDefinition[] myRules = null;
    List<NjordiRuleDefinition> NjordiRuleList = new ArrayList<NjordiRuleDefinition>();  
	//For the progress bar
	JProgressBar progressBar;
	Timer timer;
	
	//for the text editor to work.
	JPanel resultsPanel = null;
	JSplitPane splitPane = new JSplitPane();
	JSplitPane resultsSPlitPane = new JSplitPane();
	JEditorPane iRuleEditorPane = null;
	RSyntaxTextArea rSyntaxTextArea = null;
	TextEditorPane nTextEditorPane = null;
	JMenuBar menuBar = null;
	JPanel EditorPanel = null;
	//This is where we put messages
	JTextArea resultsPanelNoticesBox = new JTextArea();
	JTextArea editorNoticesBox = new JTextArea();

	
	//Will be the top node in the jTree
	DefaultMutableTreeNode top = null;
	// JTree tree = null; // Initializing this with null so we don't create an object with the sample data which is what new JTree() would do.
	f5JavaGuiTree tree = null;
	
	String userHome = null; 
	
	final Logger log = LoggerFactory.getLogger(MainGuiWindow.class);
	// This would be how I would configure log4j to use a properties file for configuration but I'm not sure how I should do it with the whole slf4j thing.
//	PropertyConfigurator.configure(args[0]);
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		//slf4j logger factory
//		Logger log = LoggerFactory.getLogger(MainGuiWindow.class);
	    //TODO: Go to http://www.slf4j.org/manual.html and see what sort of config file I need to use to make this work.
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					MainGuiWindow window = new MainGuiWindow();
					window.frame.setVisible(true);
				} catch (UnsupportedLookAndFeelException e) {
					//TODO: handle exception
				} catch (ClassNotFoundException e) {
					//TODO:  handle exception
				} catch (InstantiationException e) {
					//TODO:  handle exception
				} catch (IllegalAccessException e) {
					//TODO:  handle exception
				}			

			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainGuiWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame. It seems a little poorly named. We're actually building the whole gui in here.
	 */
	private void initialize() {
		log.info("Starting Up");
		//TODO: Store this in preferences these are the bounderies of the window and where on the screen it starts
		//TODO: Figure out how to get bounds. It's frame.getBounds(); figure out when to save them.
		frame = new JFrame();
		frame.setBounds(100, 100, 1118, 710);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//TODO: Change this to DO_NOTHING_ON_CLOSE and define a window close operation that saves the window state before closing. Or perhaps detect if the window is resized or moved then save then.
		//DO_NOTHING_ON_CLOSE (defined in WindowConstants): Don't do anything; require the program to handle the operation in the windowClosing method of a registered WindowListener object. 
		
	
		
		//TODO: This returns true/false. Deal with it and bail if it's false.
		loadPreferences();

		// Preferences Dialog stuff
		preferencesDialog = new PreferencesDialog(frame, this);
		preferencesDialog.pack();
		
		newiRuleDialong = new iRuleDialog(frame, this);
		newiRuleDialong.pack();
		
		
		// Create the editor panel. We will use it later
		EditorPanel = createEditorPanel();
		
		
		
		// NavPanel starts here  ********************************************************************
//		JPanel NavPanel = new JPanel();
		Dimension navMinimumSize = new Dimension(200, 30);
	
		editorNoticesBox.setEditable(false);
		editorNoticesBox.setText("");
		Dimension resultsPanelNoticesDimension = new Dimension(500,20);
		
		Dimension resultsPanelDimension = new Dimension(500,500);
		
		//TODO: This is currently hidden. Make it show up when we have an iRule selected
		JButton btnSave1 = new JButton("Save");
		btnSave1.setToolTipText("Save the currently selected Object.");
    	btnSave1.addActionListener(this);
		
		// The menu definition	
		menuBar = new JMenuBar();
//		frame.getContentPane().add(menuBar);
		
		JMenu mnConnMenu = new JMenu("Connection");
		menuBar.add(mnConnMenu);
		
		// This one might be my template for how to set action menu items. It also includes keyboard shortcut settings
		JMenuItem mntmEditSettings = new JMenuItem("Edit Settings");
		mntmEditSettings.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_2, ActionEvent.ALT_MASK)); // I think this is the part that adds the hotkey
		mntmEditSettings.addActionListener(this); // This adds an action listener so if you click this it will trigger actionListener() method defined below
		mntmEditSettings.setName("editSettingsBtn"); //TODO: Decide if I want to use 'Name' or if I just want to suck it up and use 'Text'
		mnConnMenu.add(mntmEditSettings);
		
		
		JMenuItem mntmConnect = new JMenuItem("Verify Connection");
		mntmConnect.addActionListener(this);
		mnConnMenu.add(mntmConnect);


		//Future actionBar components *********************************************
		//TODO: Move these buttons to the actionBar once I get that worked out.
		JButton btnGetiRules = new JButton("Get iRules");
		btnGetiRules.setHorizontalAlignment(SwingConstants.RIGHT);
		btnGetiRules.setToolTipText("Get the list of editable iRules from the BIGIP and build a navigation tree with them.");
		btnGetiRules.addActionListener(this);
		menuBar.add(btnGetiRules);
		//		menuBar.add(btnSave1);

		//TODO: Hide this and make it show up only when we have an iRule list? Either that or figure out how to create 'offline iRules'
		JButton btnNewiRule1 = new JButton("New iRule");
		btnNewiRule1.setToolTipText("Create a new blank iRule.");
		btnNewiRule1.addActionListener(this);

				
				
//		// I'm going to get rid of the action menu completely and replace it with a secondary toolbar below the main menu bar
//		JMenu mnActMenu = new JMenu("Actions");
//		menuBar.add(mnActMenu);		
//		
//		JMenuItem actMnGetVirtuals = new JMenuItem("List Virtual Servers");
//		actMnGetVirtuals.addActionListener(this); // This adds an action listener so if you click this it will trigger actionListener() method defined below
//		mnActMenu.add(actMnGetVirtuals);
//		
//		JMenuItem actMnGetiRules = new JMenuItem("List iRules");
//		actMnGetiRules.addActionListener(this); // This adds an action listener so if you click this it will trigger actionListener() method defined below
//		mnActMenu.add(actMnGetiRules);
//		
//		JMenuItem actMnEditRule = new JMenuItem("New Rule");
//		actMnEditRule.addActionListener(this); // This adds an action listener so if you click this it will trigger actionListener() method defined below
//		mnActMenu.add(actMnEditRule);
//
//		JMenuItem actMnSave = new JMenuItem("Save");
//		actMnSave.addActionListener(this); // This adds an action listener so if you click this it will trigger actionListener() method defined below
//		mnActMenu.add(actMnSave);
//		// Action menu ends here
				
				

		//JToolBar actionBar = new JToolBar(); actionBar is now created in a global scope
//
//		JButton btnSavePlaceholder = new JButton("Save Placeholder");
//		actionBar.add(btnSavePlaceholder);
//		GridBagConstraints gbc_actionBar = new GridBagConstraints();
//		gbc_actionBar.anchor = GridBagConstraints.NORTHWEST;
//		gbc_actionBar.insets = new Insets(0, 0, 5, 0);
//		gbc_actionBar.gridx = 0;
//		gbc_actionBar.gridy = 0;
//		frame.getContentPane().add(actionBar, gbc_actionBar);
		
		
		menuBar.add(btnNewiRule1);
		//DEFAULT RESULTS PANE CONTENT ENDS HERE

		JToolBar toolBar = new JToolBar();

		// Make this update between connect and disconnect
		JButton connectToBigIPButton = new JButton("Verify Connection");
		toolBar.add(connectToBigIPButton);
		connectToBigIPButton.addActionListener(this);
		NavPanel.setMinimumSize(navMinimumSize);
		splitPane.setLeftComponent(NavPanel);

		//		JScrollPane navScrollPane = new JScrollPane();
		//TODO Make this work and switch it so that building the nav tree updates navScrollPane not NavPanel
		NavPanel.add(navScrollPane);
		
		//DEFAULT RESULTS PANE CONTENT STARTS HERE		
		JLabel lblDefaultResultspanelContent = new JLabel("Welcome to Njord");
		lblDefaultResultspanelContent.setVerticalAlignment(SwingConstants.TOP);
		
		JTextPane defaultResultsDevCentralURLTextPane = new JTextPane();
		defaultResultsDevCentralURLTextPane.setText("iRules Wiki https://devcentral.f5.com/wiki/iRules.HomePage.ashx");
		defaultResultsPanelTextPane.setText("Welcome to Njord the java iRules editor. Please be forgiving as this is still very much a pre-beta version this application but at this point all of the buttons should actually do something. Please note that Njord is not an official F5 product it is just written by someone who happens to work there. It comes with no warranty or guarantee. I can't even promise it won't break your BIGIP config at this point. Any comments or questions about Njord can be directed to it's author Aaron Forster a.forster@f5.com.\r\n\r\nConnections settings will be saved to ${home}/.f5/njord/settings.properties you can edit this file or change your settings with the connections menu.");
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		
		//Figure out where this is used and rename it to something helpful
		JPanel panel = new JPanel();		
		JButton defaultTextPaneConnectButton = new JButton("Verify Connection");
		defaultTextPaneConnectButton.addActionListener(this);
		
		resultsPanelNoticesBox.setEditable(false);
		resultsPanelNoticesBox.setText("Welcome to Njord ");
		//		resultsPanelNoticesBox.setText("Welcome to Njord " + njordVersion);
				
		resultsPanel = new JPanel();
		GroupLayout gl_resultsPanel = new GroupLayout(resultsPanel);
		gl_resultsPanel.setHorizontalGroup(
				gl_resultsPanel.createParallelGroup(Alignment.LEADING)
				.addComponent(horizontalStrut_1, GroupLayout.PREFERRED_SIZE, 0, GroupLayout.PREFERRED_SIZE)
				.addGroup(gl_resultsPanel.createSequentialGroup()
						.addGap(475)
						.addComponent(lblDefaultResultspanelContent))
						.addGroup(gl_resultsPanel.createSequentialGroup()
								.addGap(40)
								.addComponent(defaultResultsDevCentralURLTextPane, GroupLayout.PREFERRED_SIZE, 500, Short.MAX_VALUE)
								.addGap(40))
								.addGroup(gl_resultsPanel.createSequentialGroup()
										.addGap(40)
										.addComponent(defaultResultsPanelTextPane, GroupLayout.PREFERRED_SIZE, 500, Short.MAX_VALUE)
										.addGap(40))
										.addGroup(gl_resultsPanel.createSequentialGroup()
												.addGap(40)
												.addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addGap(40))
												.addGroup(gl_resultsPanel.createSequentialGroup()
														.addGap(40,300,900)
														.addComponent(defaultTextPaneConnectButton)
														.addGap(40,300,900))
														//				.addComponent(resultsPanelNoticesBox, GroupLayout.PREFERRED_SIZE, 894, GroupLayout.PREFERRED_SIZE)
				);
		gl_resultsPanel.setVerticalGroup(
				gl_resultsPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_resultsPanel.createSequentialGroup()
						.addComponent(horizontalStrut_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGap(30)
						.addComponent(lblDefaultResultspanelContent)
						.addGap(5)
						.addComponent(defaultResultsDevCentralURLTextPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGap(5)
						.addComponent(defaultResultsPanelTextPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGap(5)
						.addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGap(23)
						.addComponent(defaultTextPaneConnectButton)
						//					.addPreferredGap(ComponentPlacement.RELATED, 412, Short.MAX_VALUE)
						//					.addComponent(resultsPanelNoticesBox, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
						)
				);
		resultsPanel.setLayout(gl_resultsPanel);


		resultsSPlitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, resultsPanel, resultsPanelNoticesBox);

		splitPane.setRightComponent(resultsSPlitPane);


		resultsSPlitPane.setResizeWeight(0.95); // This says that the bottom component will keep most of it's size when the split pane is automatically rezized. IE the botton will stay smaller.
		resultsPanelNoticesBox.setPreferredSize(resultsPanelNoticesDimension);
		resultsPanelNoticesBox.setMaximumSize(resultsPanelNoticesDimension);
		resultsPanelNoticesBox.setMinimumSize(resultsPanelNoticesDimension);
		resultsPanel.setMinimumSize(resultsPanelDimension);

		progressBar = new JProgressBar();
		toolBar.add(progressBar);

		//TODO: make this value be the same source as counter below and wherever the timout reads it from.
		//        progressBar.setValue(20); 
		// This turns out to be a percentage. For progress bar flow we probably want to start it at zero, jump it to ten when we hit 'connect' and then move it up instead of down as the timer progresses then jump it to 100% once connected.
		progressBar.setValue(0);

		//		JLabel lblStatusLabel = new JLabel("Status Info ");
		lblStatusLabel.setText("Status Info");
		toolBar.add(lblStatusLabel);
		
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(menuBar, javax.swing.GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 1102, Short.MAX_VALUE)
				.addComponent(splitPane, javax.swing.GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 1102, Short.MAX_VALUE)
				.addComponent(toolBar, javax.swing.GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 1102, Short.MAX_VALUE)
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(menuBar, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
					.addGap(5)
					.addComponent(splitPane, GroupLayout.PREFERRED_SIZE, 623, Short.MAX_VALUE)
					.addGap(5)
					.addComponent(toolBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		);
		frame.getContentPane().setLayout(groupLayout);
		
		
		//This is the start of the group layout for the main window. It currently doesn't do anything because I need to fix some other issues first.
//		GroupLayout gl_MainWindow = new GroupLayout(frame);
//		gl_MainWindow.setHorizontalGroup(
//				gl_MainWindow.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
////				Horizontal Group Here
//		);
//		gl_MainWindow.setVerticalGroup(
//				gl_MainWindow.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
////				Vertical Group Here
//		);
//		

		// Group layout from the editor panel to be used as reference get rid of it.
		
//    	GroupLayout gl_EditorPanel = new GroupLayout(editorPanel);
//    	gl_EditorPanel.setHorizontalGroup(
//    			gl_EditorPanel.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//                .addGroup(gl_EditorPanel.createSequentialGroup()
//                    .addContainerGap()
//                    .addGroup(gl_EditorPanel.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//                        .addComponent(scrollPane_1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE)
//                        .addGroup(gl_EditorPanel.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
//                        		.addGroup(javax.swing.GroupLayout.Alignment.LEADING, gl_EditorPanel.createSequentialGroup()
//                        				.addComponent(btnSave)
//                        				)
//                        		)
//                    .addGroup(gl_EditorPanel.createSequentialGroup()
////					.addGap(187)
////					.addComponent(editorNoticesBox, GroupLayout.PREFERRED_SIZE, 894, GroupLayout.PREFERRED_SIZE)
//                    		)
////					.addComponent(resultsPanelNoticesBox, GroupLayout.PREFERRED_SIZE, 894, GroupLayout.PREFERRED_SIZE
////                        .addComponent(noticesPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
//                    		)
//                    .addContainerGap())
//            );
//    	gl_EditorPanel.setVerticalGroup(
//    			gl_EditorPanel.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, gl_EditorPanel.createSequentialGroup()
//                    .addContainerGap()
//                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
//                    .addComponent(scrollPane_1, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
//                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
//                    .addGroup(gl_EditorPanel.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
//                    		.addComponent(btnSave)
////                    		.addGap(10)
////                    		.addComponent(editorNoticesBox, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE)
//                    		)
//                    .addContainerGap())
//            );

		
		//Group Layout by the designer before I started screwing with it.
//		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
//		groupLayout.setHorizontalGroup(
//			groupLayout.createParallelGroup(Alignment.LEADING)
//				.addComponent(menuBar, GroupLayout.PREFERRED_SIZE, 1102, GroupLayout.PREFERRED_SIZE)
//				.addComponent(splitPane, GroupLayout.PREFERRED_SIZE, 1102, GroupLayout.PREFERRED_SIZE)
//				.addComponent(toolBar, GroupLayout.PREFERRED_SIZE, 1102, GroupLayout.PREFERRED_SIZE)
//		);
//		groupLayout.setVerticalGroup(
//			groupLayout.createParallelGroup(Alignment.LEADING)
//				.addGroup(groupLayout.createSequentialGroup()
//					.addComponent(menuBar, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
//					.addGap(5)
//					.addComponent(splitPane, GroupLayout.PREFERRED_SIZE, 623, GroupLayout.PREFERRED_SIZE)
//					.addGap(5)
//					.addComponent(toolBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
//		);
//		frame.getContentPane().setLayout(groupLayout);
		
		
		
	}
	
	/**
	 * Build the nodes of the navigation tree. Takes the tree to add the nodes to as an argument.
	 * @param tree
	 */
	private void createNodes(DefaultMutableTreeNode tree) {
//	    DefaultMutableTreeNode category = null;
	    DefaultMutableTreeNode addRule = null;
	    
	    category = new DefaultMutableTreeNode("iRules");
	    tree.add(category);
	    
	    //TODO: move the try/catch block to getiRulesList()?
	    //TODO: Create a generic getSomethingList?
	    //TODO: Figure out how to handle the long path of the iRule's full name. IE do I pull off the path and just show the name, do I have expandable folders for the folders in the path? I like that idea but only if I automatically have 'common' be expanded.
	    //TODO: If I do the above I will need a way to remember what folders have been openened if the client does so.
	    
	    // A great discussion of Lazy node expansion http://www.cs.oswego.edu/~lqiu/class/csc520-projectexamples/email/JTree_As_Directory.htm
	    // Which only builds the tree for the visible stuff. But it has issues, reading the article now.
	    // For rebuilding the tree I will need to use treeNode.removeAllChildren() before repopulating
	    // Ultimately rebuilding the tree on an expansion event will involve extending defaultTreeNode or defaultMutableTreeNode to hold some
	    //      state information like hasBeenExpanded to decide if I need to get the iRules or whatever.
	    // For now it'll probably just be easier to not build the tree at all. I can stick an empty jPanel in that space and replace it with the tree once I've built it.
	    
	    

	    for (NjordiRuleDefinition rule : NjordiRuleList){
	    	//TODO: Have to figure out how I want to handle the diff between V10 and V11. Decide ahead of time and do different actions OR just do things like check both ways in sections like this.
	    	//Let's not put the sys iRules in the list
	    	if ( rule.getName().matches("/Common/_sys_.*") ) { // V11 syntax
	    		log.debug("Sys iRule, Skipping");
	    		continue;
	    	} else if ( rule.getName().matches("_sys_.*") ) { // V10 and maybe V9
	    		log.debug("Sys iRule, Skipping");
	    		continue;
	    	}

	    	addRule = new DefaultMutableTreeNode(rule);
	    	category.add(addRule);
	    }
	}
	
	//This is the tree selection listener method.
	public void valueChanged(TreeSelectionEvent e) {
		String s;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                           tree.getLastSelectedPathComponent();

        if (node == null) return;
        resultsPanelNoticesBox.setText(""); //Clear out the notices box.
     
//        Object nodeInfo = node.getUserObject();
        //TODO: Use the above syntax to load nodeInfo as a generic type Object then figure out what type it is and act accordingly.
        //			This avoids random stupid errors such as the null pointer exception we currently generate if you click on a branch node
//        LocalLBRuleRuleDefinition nodeInfo = (LocalLBRuleRuleDefinition) node.getUserObject();
        NjordiRuleDefinition nodeInfo = (NjordiRuleDefinition) node.getUserObject();
//        NjordiRuleDefinition nodeInfo = (NjordiRuleDefinition) node.getUserObject();
        
        
        // This might be part of how to do the hyperlinks so I can send you to devcentral
//        addHyperlinkListener(javax.swing.event.HyperlinkListener l)


        
        // We do this the whole nodeInfo way because we have to check and see if it's a leaf node or not.
        if (node.isLeaf()) {
        	log.info("Rule " + nodeInfo + " Selected");
        	s = "iRule Selection Event Detected";
    		
            //TODO: Check currentSelectedRule and see if it is non-empty. If it is non empty update that rule with the contents of the editor before filling the editor with the new contents.
            //		Then set tree.getLastSelectedPathComponent() into currentSelectedRule.
        	//TODO: also mark the iRule as unsaved at the same time.
        	
        	//Whoohoo! This fixed it so I don't loose the contents of a rule when I click off it. Now to make a visual indication of some sort.
        	if ( currentSelectedRule != null ) {
        		currentSelectedRule.setRule_definition(nTextEditorPane.getText());
        	}
        	
        	currentSelectedRule = nodeInfo;
        	
//        	rSyntaxTextArea.setText(nodeInfo.getRule_definition());
        	nTextEditorPane.setText(currentSelectedRule.getRule_definition());
            //TODO: Set isModified once this has been edited
            
        	Dimension editorPanelDimension = new Dimension(500,500);
        	EditorPanel.setMinimumSize(editorPanelDimension);
//        	EditorPanel.setPreferredSize(editorPanelDimension);
            //TODO: Figure out howo tmake this remove whatever is currently there instead of statically coding 'resultsPanel' There will be a point when it's not 'resultsPanel' Quite often in fact. Like when it's now editPanelScrolePane1
            //I'm not sure if I need to do this but it seems like the right thing to do. Remove the existing contents of the right panel before setting a new one.
//            splitPane.remove(resultsPanel);
//            splitPane.setRightComponent(EditorPanel);
            resultsSPlitPane.remove(resultsPanel);
            resultsSPlitPane.setTopComponent(EditorPanel);
        } else {
//            displayURL(helpURL); 
        }
    }
	
	/**
	 * @see javax.swing.event.TreeExpansionListener#treeCollapsed(javax.swing.event.TreeExpansionEvent)
	 */
	@Override
	public void treeCollapsed(TreeExpansionEvent arg0) {
		//TODO: Get the iRule list.
		//Don't forget to add TreeExpansionListener  to the interfaces implemented
		//Do stuff	
	}

	/**
	 * @see javax.swing.event.TreeExpansionListener#treeExpanded(javax.swing.event.TreeExpansionEvent)
	 */
	@Override
	public void treeExpanded(TreeExpansionEvent arg0) {
		log.debug("Tree Expansion Event");
		createNodes(top);
	}

	//This is the action listener implementation. This handles when you click buttons or menu items.
	/**
	 * actionPerformed is the method that listens for events like clicking a button or a menu item.
	 */
	public void actionPerformed(ActionEvent e) {
        //This will get me the text of the source action without having to instantiate the source
        String actionCommand = e.getActionCommand();
        //This would allow me to identify the class first before getting it with e.getSource() if I needed to instantiate the whole class for something. 
        Class sourceClass = e.getSource().getClass();
     
		//The old way to get the source type
//		JMenuItem source = (JMenuItem)(e.getSource());
//        String SourceType = source.getText();
        
        String statusText;
        //TODO: Can I use a switch here or is java where switch is numbers only? Ahh... there would be a reason to use id or to set a name or something on all buttons/menu items/etc.
        if (actionCommand == "Edit Settings") {
        	//TODO get rid of s and replace it with a meaningful name. Also it's been used for two things one for setting the status text as well as for setting a log message. Separate those usages.
        	log.debug("Edit Settings Event Detected.");
        	resultsPanelNoticesBox.setText("Editing Connection Settings");
        	int result = JOptionPane.showConfirmDialog(null, preferencesDialog.panel_1, 
        			"Connection Preferences", JOptionPane.OK_CANCEL_OPTION);
        	if (result == JOptionPane.OK_OPTION) { // Doesn't seem like we're actually triggering this
		        iIPAddress = preferencesDialog.ConnPreffsHostTextField.getText();
		        iPort = Integer.parseInt( preferencesDialog.ConnPreffsPortTextField.getText());
		        iUserName = preferencesDialog.ConnPreffsUserTextField.getText();
		        iPassword = preferencesDialog.ConnPreffsPasswordTextField.getText();
		        log.debug("Host: " + iIPAddress);
		        log.debug("Port: " + iPort);
		        log.debug("User: " + iUserName);
		        log.debug("Pass: " + iPassword);
		        writePreferences();
		        //TODO: Set connected state to false if connection settings have been edited.
	        }
        	resultsPanelNoticesBox.setText("Connection Settings Saved");
        } else if (actionCommand == "Verify Connection") {
        	log.debug("Connect Event Detected.");
        	//TODO: Figure out why setting progressBar and lblStatusLabel doesn't work here but works below.
        	progressBar.setValue(50);
        	lblStatusLabel.setText("Connecting");
        	resultsPanelNoticesBox.setText("");
        	
        	//jgruber syntax
//        	ic = new iControl.BigIP(iIPAddress,iUserName,iPassword); 
//        	ic.setIgnoreInvalidCert(true); // otherwise java will bail on the invalid cert
        	
        	//TODO: Move this somewhere else
        	boolean animateCountdownTimer = false;
        	
        	if (animateCountdownTimer == true) {
    			//TODO: I should probably pick some minimum length of time in between testing to see if we actually have a valid connection. Like three seconds or one or something.
    			//TODO: Wrap the below in an if statement so I can have a setting that makes the countdown timer go or not. Just so I can enable it when I'm actually working on the countdown indicator code.
    			//TODO: For some reason the progress bar only fills up a little bit of the way. it's probably based on counter value below.
    			ActionListener listener = new ActionListener() {
    				int counter = 20; // TODO: Set this as well as the setValue() above to a variable and make it be read from the same place that the connection subroutine reads it's connection time out from

    				public void actionPerformed(ActionEvent ae) {
    					counter--;
    					progressBar.setValue(counter);
    					//TODO Get rid of this loop/ActionListener() if the connection is successful.
    					if (counter < 1) {
    						//TODO: Figure out how to replace this showMessageDialog with one that cancels our attempt at a connection if it finishes.
    						JOptionPane.showMessageDialog(null, "Kaboom!");
    						timer.stop();
    					}
    				}
    			};
    			//TODO: Make this interval shorter for smoother flow. Note that if it's less than 1 sec we need to modify counter so that it's a multiple of the actual counter variable so that ultimately we end the counter when we time out and give up on the connection.
    			timer = new Timer(1000, listener); //So 1000 here is the delay between times that we trigger the above Action Listener.
    			timer.start();
    			//TODO: add 'BUG:' to the todo filter for the tasks list. Or add a 'BUGS' list which is a tasks list with a different filter.
    			//BUG: There's a bug in here somewhere that if you hit connect multiple times you can get into a state where the 'kaboom!' dialog just repeatedly pops up.
    		}
        	
        	boolean successfullConnection = initializeConnection(); 	
    		//TODO: Move this part, the setting of the progress bar and displaying of connection validity out of this section and maybe back into the listener. We should only be returning true/false here.
    		if (successfullConnection == true) {
            	// TODO: Make this a check rather than printing to the console
    			log.info("Connected");
            	lblStatusLabel.setText("Connected");
            	progressBar.setValue(100);
            	connectionInitialized = true;
    		} else {
    			// scream, run and cry
    			progressBar.setValue(0);
    			log.error("Connection settings invalid");
    			lblStatusLabel.setText("Connection settings invalid");
    			connectionInitialized = false;
    		}
    		resultsPanelNoticesBox.setText("Connection Verified");
//        } else if (actionCommand == "List Virtual Servers"){
//        	//TODO: Make 'List Virtual Servers' actually populate the jtree on the side
//        	//TODO: Populate the gui with info and buttons which have actions for the virtual like and enable/disable (only one should be there at a time.)
//        	//TODO: Then add some actions like enable/disable
//        	log.debug("List Virtuals Detected");
//    		try {
//    			String[] meVirtuals = getVirtualsList();
//    		} catch (Exception f) {
//    			log.error("Whoa, caught an error: " + f);
//    		}
//        } else if (actionCommand == "List iRules"){
//        	//TODO: Make 'List iRules' actually populate the jtree on the side
//        	log.debug("List iRules Detected");
//        	resultsPanelNoticesBox.setText("");
//    		try {
//    			String[] meRules = getiRuleList();
//    		} catch (Exception g) {
//    			log.error("Whoa, caught an error: " + g);
//    		}
        } else if (actionCommand == "New iRule"){
        	//TODO: This is working as far as creating a new text edior and letting you start typing but I still need to figure out how to add it to the list of rules
        	
        	//TODO: For some reason this actually causes the jTree to get all screwed up. Make that not happen
        	//TODO: Make 'Edit Rule' Be the default action for a double click on an iRule in the list.
        	//TODO: Create a 'New Rule' function. This will probably go hand in hand with:
        	//TODO: Figure out how to handle renaming of iRules.
        	//TODO: Figure out how to handle 'offline iRules' IE one you have created but isn't uploaded yet.
        	log.debug("New Rule Detected");
        	resultsPanelNoticesBox.setText("New iRule functionality is still a tiny bit sketchy. It requires that you have a connection to a BIGIP and It's likely to only work on V11 systems. Sorry for the inconvenience.");
        	
        	String newiRuleName = "New_rule";
	        String newiRulePartition = "/Common";
	        
        	int result = JOptionPane.showConfirmDialog(null, newiRuleDialong.panel_1, 
        			"Connection Preferences", JOptionPane.OK_CANCEL_OPTION);
        	if (result == JOptionPane.OK_OPTION) { // Doesn't seem like we're actually triggering this
		        newiRuleName = newiRuleDialong.txtNewiRuleName.getText();
		        newiRulePartition = newiRuleDialong.txtNewiRulePartition.getText();
        	}
        	
        	String newiRuleFullName = newiRulePartition + "/" + newiRuleName;
            NjordiRuleDefinition newiRule = new NjordiRuleDefinition(newiRuleFullName);
        	
            
            if ( NjordiRuleList.isEmpty() ) {
            	initializeConnection();
    			try {
    				NjordiRuleList = new ArrayList<NjordiRuleDefinition>(Arrays.asList(getNjordiRules()));
    			} catch (Exception e1) {
    				f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e1);
    				exceptionHandler.processException();
    			}
    		}
            
            NjordiRuleList.add(newiRule);
            buildNavTree();
            nTextEditorPane.setText(newiRule.getRule_definition());
//            //This doesn't work. I need a better way to add the new iRule to the list of iRules.
//            // I need to throw up a dialog box to start the rule out with and then I need to rebuilt the whole left hand tree I think.
//            DefaultMutableTreeNode addRule = new DefaultMutableTreeNode(newiRule);
//    	    category.add(addRule);
//        	
//
//    		JPanel resultsPanel = new JPanel();
////    		splitPane.setRightComponent(resultsPanel);
//
//    		JScrollPane editPaneScrollPane1 = new javax.swing.JScrollPane();
//            JEditorPane editPane = new javax.swing.JEditorPane();
//    		
//    		//Let's create an 'edit rule' action and replace the above with this:
//            editPaneScrollPane1.setHorizontalScrollBar(null);
//
//            //TODO: WTH is wrong w/ someone to make the background grey? Fix this.
//            editPane.setBackground(new java.awt.Color(233, 228, 242));
//            editPane.setMargin(new java.awt.Insets(3, 20, 3, 20));
//            editPaneScrollPane1.setViewportView(editPane);

            //TODO: Figure out howo tmake this remove whatever is currently there instead of statically coding 'resultsPanel' There will be a point when it's not 'resultsPanel' Quite often in fact. Like when it's now editPanelScrolePane1
            //I'm not sure if I need to do this but it seems like the right thing to do. Remove the existing contents of the right panel before setting a new one.
//            splitPane.remove(resultsPanel);
//            splitPane.setRightComponent(editPaneScrollPane1);
            
            //TODO: put nTextEditorPane into the results panel if it isn't already there
//            resultsSPlitPane.remove(resultsPanel);
//            resultsSPlitPane.setTopComponent(nTextEditorPane);
        } else if (actionCommand == "Save"){
        	//TODO: Apparently blank is a valid iRule as far as mcpd is concerned. Check to ensure the rule isn't completely blank and handle that.
        	//This will tell me what iRule is selected. Then I need to set the definition. tree.getLastSelectedPathComponent()
        	log.info("Save detected");
        	// Clear the contents of the notices box
        	resultsPanelNoticesBox.setText("Saving...");
        	
            // A way to add a parser to validate the code. I don't know how to work this out since we have the bigip do it.
            //addParser(Parser parser) 
        	
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                    tree.getLastSelectedPathComponent();
            if (node == null) return;
            
            
            NjordiRuleDefinition nodeInfo = (NjordiRuleDefinition) node.getUserObject();
            nodeInfo.setRule_definition(nTextEditorPane.getText());

            LocalLBRuleRuleDefinition[] saveRules = new LocalLBRuleRuleDefinition[1]; // Create a list of iRules in order to write them back. We only have one so we only need a tiny list
            saveRules[0] = nodeInfo.getiRule(); // Stick the iRule object into said list

            //TODO: Decide if I should modify the above to use the njordIruleObjects or not
            try {
                if (nodeInfo.isNew) {
                	//Create the rule instead of modifying it.
                	ic.getLocalLBRule().create(saveRules);
                } else {
    				ic.getLocalLBRule().modify_rule(saveRules); // Then write said rule back to the BigIP This could be easily modified to allow the saving of multiple rules.
                }
			} catch (RemoteException e1) {
				//If we caught a remote exception the code was wrong. Let's report it to the user
				String errorContents = e1.getMessage(); //This gets the full message	

				Pattern pattern = Pattern.compile(".*error_string         :.*error:", Pattern.DOTALL);
				Matcher matcher = pattern.matcher(errorContents);
				//TODO: Modify the pattern and matcher so we get rid of this crap at the beginning as well.
				//Error:  01070151:3: Rule [/Common/myIrulesOutputTest] error:
				
				//Uncomment if working on the regex. The commented code shows what we are matching.
//				while (matcher.find()) {
//					log.info("Start index: " + matcher.start());
//					log.info(" End index: " + matcher.end() + " ");
//					log.info(matcher.group());
//					log.info("End matcher section ##############");
//				}
				
				//TODO: Replace this println with something that either pops up an error or sets the contents of a status box in the main gui. I prefer the latter.
				String errorMessage = matcher.replaceAll("Error saving Rule: ");
				log.info("Error: " + errorMessage);
				
				//TODO: Figure out how to make the text box scrollable or resize it if the message is too large.
//				editorNoticesBox.setText(errorMessage);
				resultsPanelNoticesBox.setText(errorMessage);
				return;
				
				//This is what getMessage returns. I need to pull out the last part error_string:
//				Exception caught in LocalLB::urn:iControl:LocalLB/Rule::modify_rule()
//				Exception: Common::OperationFailed
//					primary_error_code   : 17236305 (0x01070151)
//					secondary_error_code : 0
//					error_string         : 01070151:3: Rule [/Common/http_responder] error: 
//				line 15: [parse error: extra characters after close-brace] [ffff]
				
//				f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e1);
//				exceptionHandler.processException();
			} catch (Exception e1) {
				f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e1);
				exceptionHandler.processException();
			}
            
            //TODO: Handle an exception here. This is how the BigIP will let you know if you screwed up.
            //Here's a sample one.
//            : Exception caught in LocalLB::urn:iControl:LocalLB/Rule::modify_rule()
//            Exception: Common::OperationFailed
//            	primary_error_code   : 17236305 (0x01070151)
//            	secondary_error_code : 0
//            	error_string         : 01070151:3: Rule [/Common/http_responder] error: 
//            line 8: [parse error: extra characters after close-brace] [ggg]
//            editorNoticesBox.setText("Save Successful");
            resultsPanelNoticesBox.setText("Save Successful");
        } else if (actionCommand == "Get iRules"){
        	//TODO: Apparently blank is a valid iRule as far as mcpd is concerned. Check to ensure the rule isn't completely blank and handle that.
        	//This will tell me what iRule is selected. Then I need to set the definition. tree.getLastSelectedPathComponent()
        	log.info("Get iRules detected");
        	
        	if (connectionInitialized == true) {
        		buildNavTree();	
        	} else {
        		//TODO: Check that initializeConnection() was successful before attempting to build the tree. There's a separate TODO to add a return type to initializeConnection()
        		initializeConnection();
        		buildNavTree();
        		//        		resultsPanelNoticesBox.setText("Please confirm connection information before attempting");
        	}
        	
        
        	//This isn't working for some reason
        
        } else  {
            statusText = "Unknown";
        	log.debug("Un-Known Action Event Detected." 
                    + newline
                    + "    Event source: " + actionCommand
                    + " (an instance of " + getClassName(sourceClass) + ")");
           
        } 

//        defaultResultsPanelTextPane.setText(s);
    }
	
	/**
	 * Runs the initialize method on the library which sets up the connection object. Then do a get version. If it works we've got good connection settings.
	 * @return
	 */
	private boolean initializeConnection() {
    	ic.initialize(iIPAddress, iPort, iUserName, iPassword); // Initialize the interface to the BigIP

		String version = null;
		try {
			// This one doesn't work either. Probably there's too little time between the setting and the ic.Sys.... maybe a sleep of like 5ms?hrm..
//        	progressBar.setValue(50);
//        	lblStatusLabel.setText("Connecting");
//			version = ic.SystemSystemInfo().get_version();
			version = ic.getSystemSystemInfo().get_version();
//			return true;
		} catch (RemoteException e1) {
			//TODO: Move this stuff into f5ExceptionHandler
			String errorContents = e1.getMessage();
			
			Pattern pattern = Pattern.compile(".*error_string         :.*error:", Pattern.DOTALL);
			Matcher matcher = pattern.matcher(errorContents);
			//TODO: Modify the pattern and matcher so we get rid of this crap at the beginning as well.
			//Error:  01070151:3: Rule [/Common/myIrulesOutputTest] error:
			
			//Uncomment if working on the regex. The commented code shows what we are matching.
			while (matcher.find()) {
				log.info("Start index: " + matcher.start());
				log.info(" End index: " + matcher.end() + " ");
				log.info(matcher.group());
				log.info("End matcher section ##############");
			}
			
			//TODO: Replace this println with something that either pops up an error or sets the contents of a status box in the main gui. I prefer the latter.
			String errorMessage = matcher.replaceAll("");
			log.info("Error: " + errorMessage);
			
			//TODO: Figure out how to make the text box scrollable or resize it if the message is too large.
//			editorNoticesBox.setText(errorMessage);
			resultsPanelNoticesBox.setText(errorMessage);
			
			//TODO: Add a return type so I can do a return here.
//			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e1);
//			exceptionHandler.processException();
		} catch (ServiceException e1) {
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e1);
			exceptionHandler.processException();
		} catch (Exception e1) {
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e1);
			exceptionHandler.processException();
		}
		
		//TODO: Move this part, the setting of the progress bar and displaying of connection validity out of this section and maybe back into the listener. We should only be returning true/false here.
		if (version != null) {
        	log.debug("My Big-IP is version:"+version);
        	return true; //It worked
		} else {
			// scream, run and cry
			log.error("Connection settings invalid");
			return false; //We are failz
		}
	}
	
	/**
	 * Returns the class name minus the leading package portion.
	 * I'll probably want to get rid of this after testing
	 * @param o
	 * @return
	 */
    protected String getClassName(Object o) {
        String classString = o.getClass().getName();
        int dotIndex = classString.lastIndexOf(".");
        return classString.substring(dotIndex+1);
    }

    /**
     * I will delete this in a while after I update actionPerformed()
     */
    private Container createContentPane() {
        //Create the content-pane-to-be.
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setOpaque(true);
 
        //Create a scrolled text area.
        output = new JTextArea(5, 30);
        output.setEditable(false);
        scrollPane = new JScrollPane(output);
 
        //Add the text area to the content pane.
        contentPane.add(scrollPane, BorderLayout.CENTER);
 
        return contentPane;
    }
    
    /**
     * Sets the text displayed at the bottom of the window. Public allows us to send back update messages from other things.
     * @param newText
     */
    public void setLabel(String newText) {
    	lblStatusLabel.setText(newText);
    }
    
    /**
     * returns only a string list of the names of the iRules instead of actual virtual server objects. I might move these to a sub package
     * @return
     * @throws Exception
     */
	public String [] getVirtualsList() throws Exception {
		//Do I want to throw this here or do I wanto to just handle this here. I think I'm throwing it because getLocalLBVirtualServer().get_list() Throws and exception and I was pushing it up.
		//TODO: Handle exception here instead of elevating it.
		String [] virtual_list = ic.getLocalLBVirtualServer().get_list(); // From Assembly
//		String [] virtual_list = ic.LocalLBVirtualServer().get_list(); // From wsdl
		System.out.println("Available Virtuals");
		for (String string : virtual_list) {
			System.out.println("   " + string);
		}
		return virtual_list;
	}
	
	/**
	 * getiRuleList returns only a string list of the names of the iRules instead of actual iRule objects 
	 * 
	 * @return
	 * @throws Exception
	 */
	public String [] getiRuleList() throws Exception {
		String [] rule_list = ic.getLocalLBRule().get_list();
		System.out.println("Available Rules");
		for (String string : rule_list) {
			System.out.println("   " + string);
		}
		return rule_list;
	}
	
		
	/**
	 * Get the iRules from the BigIP. 
	 * Currently returns objects of type LocalLBRuleRuleDefinition. I need to change this to a custom object. At that point I won't have to override the way I create the tree anymore.
	 * 
	 * @return
	 */
	public LocalLBRuleRuleDefinition[] getiRules() {
		LocalLBRuleRuleDefinition[] iRules = null;
		try {
			iRules = ic.getLocalLBRule().query_all_rules();
			System.out.println("Available Rules");
			for (LocalLBRuleRuleDefinition rule : iRules) {
				System.out.println("   " + rule); //wonder if LocalLBRuleRuleDefinition has a toString() it should.
			}
		} catch (RemoteException e) {
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e);
			exceptionHandler.processException();
			//Not:
			//e.printStackTrace();
		} catch (Exception e) {
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e);
			exceptionHandler.processException();
			//Not:
			//e.printStackTrace();
		}
		return iRules;
	}
    
	/**
	 * Get the iRules from the BigIP and create a list of NjordRules for them. 
	 * 
	 * 
	 * @return
	 */
	public NjordiRuleDefinition[] getNjordiRules() {
		LocalLBRuleRuleDefinition[] iRules = null;
		NjordiRuleDefinition[] NjordRules = null;
		try {
			iRules = ic.getLocalLBRule().query_all_rules();
			log.debug("Available Rules");
			NjordRules = new NjordiRuleDefinition[iRules.length];
			for (int i = 0; i < iRules.length ; i++) {
//			for (LocalLBRuleRuleDefinition rule : iRules) {
				NjordRules[i] = new NjordiRuleDefinition(iRules[i]);
				log.debug("   " + iRules[i]); //wonder if LocalLBRuleRuleDefinition has a toString() it should.
			}
		} catch (RemoteException e) {
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e);
			exceptionHandler.processException();
			//Not:
			//e.printStackTrace();
		} catch (Exception e) {
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e);
			exceptionHandler.processException();
			//Not:
			//e.printStackTrace();
		}
		return NjordRules;
	}
	
	private JPanel createEditorPanel() {
		//TODO: Instead of creating a new text editor every time we select an iRule I should perhaps create one earlier or do a use if exists, create if doesn't. Then when someone clicks on an iRule I can just populate the text editor with the contents of said iRule. Perhaps even save the contents of said text editor and then populate it with the new iRule.
    	JPanel editorPanel = new JPanel();
//    	GridBagLayout gbl_editorPanel = new GridBagLayout();
//    	GroupLayout gl_EditorPanel = new GroupLayout();
    	
//    	GroupLayout gl_EditorPanel = new GroupLayout(editorPanel);
//    	gl_EditorPanel.setHorizontalGroup(
//    			gl_EditorPanel.createParallelGroup(Alignment.LEADING) 			
//				.addComponent(horizontalStrut_1, GroupLayout.PREFERRED_SIZE, 0, GroupLayout.PREFERRED_SIZE)
//				.addGroup(gl_EditorPanel.createSequentialGroup()
//					.addGap(475)
//					.addComponent(lblDefaultResultspanelContent))
//				.addGroup(gl_EditorPanel.createSequentialGroup()
//					.addGap(173)
//					.addComponent(defaultResultsDevCentralURLTextPane, GroupLayout.PREFERRED_SIZE, 743, GroupLayout.PREFERRED_SIZE))
//				.addGroup(gl_EditorPanel.createSequentialGroup()
//					.addGap(173)
//					.addComponent(defaultResultsPanelTextPane, GroupLayout.PREFERRED_SIZE, 743, GroupLayout.PREFERRED_SIZE))
//				.addGroup(gl_EditorPanel.createSequentialGroup()
//					.addGap(173)
//					.addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
//				.addGroup(gl_EditorPanel.createSequentialGroup()
//					.addGap(508)
//					.addComponent(defaultTextPaneConnectButton))
//		);
    	
    	
//    	gbl_editorPanel.columnWidths = new int[] {100, 100, 100, 100, 100, 100, 100, 100, 100, 100};
//    	gbl_editorPanel.rowHeights = new int[] {100, 100, 100, 100, 100, 100};
//    	gbl_editorPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
//    	gbl_editorPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
//    	editorPanel.setLayout(gbl_editorPanel);
    	
    	// This builds a tokenmaker factory based on the iRules tokenmaker which is extended from the TCL token maker.
		AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance();
		atmf.putMapping("SYNTAX_STYLE_IRULES", "com.f5.AaronForster.njord.util.iRulesTokenMaker");
		TokenMakerFactory.setDefaultInstance(atmf); //Don't know if I need this line or not
//		textArea.setSyntaxEditingStyle("SyntaxConstants.SYNTAX_STYLE_TCL");
    	
    	nTextEditorPane = new TextEditorPane();
//    	nTextEditorPane.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_TCL);
    	nTextEditorPane.setSyntaxEditingStyle("SYNTAX_STYLE_IRULES");
    	nTextEditorPane.setCodeFoldingEnabled(true);
    	nTextEditorPane.setAntiAliasingEnabled(true);
    	
    	
//    	nTextEditorPane.setSize(textPaneDimension);
    	
    	//TODO: Update the color scheme. Figure out what items I will be using Function, reserved_word, etc for what components and how they should be colorized.
    	SyntaxScheme scheme = nTextEditorPane.getSyntaxScheme();
//        scheme.getStyle(Token.RESERVED_WORD).foreground = Color.pink;
        scheme.getStyle(Token.FUNCTION).foreground = Color.MAGENTA;
        Color operatorColor = new Color(880080);
//        scheme.getStyle(Token.OPERATOR).foreground = Color.GREEN;
        scheme.getStyle(Token.OPERATOR).foreground = operatorColor;
        
//        		purple(16 SVG) #800080
    	
//    	rSyntaxTextArea = new RSyntaxTextArea(20, 60); //This is numrows and numcols
//        rSyntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_TCL); 
//        rSyntaxTextArea.setCodeFoldingEnabled(true);
//        rSyntaxTextArea.setAntiAliasingEnabled(true);
    	
    	
    	JScrollPane scrollPane_1 = new JScrollPane();
//    	scrollPane_1.setViewportView(rSyntaxTextArea);
    	//Let's try this with TextEditorPane;
    	scrollPane_1.setViewportView(nTextEditorPane);
    	GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
    	gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
    	gbc_scrollPane_1.gridwidth = 8;
    	gbc_scrollPane_1.gridheight = 8;
    	gbc_scrollPane_1.anchor = GridBagConstraints.NORTHWEST;
    	gbc_scrollPane_1.insets = new Insets(0, 0, 0, 5);
    	gbc_scrollPane_1.gridx = 1;
    	gbc_scrollPane_1.gridy = 0;
    	editorPanel.add(scrollPane_1, gbc_scrollPane_1);

    	JButton btnSave = new JButton("Save");
    	GridBagConstraints gbc_btnSave = new GridBagConstraints();
//    	gbc_btnSave.anchor = GridBagConstraints.NORTHWEST;
//    	gbc_btnSave.gridx = 2;
//    	gbc_btnSave.gridy = 8;
    	btnSave.addActionListener(this);

    	GroupLayout gl_EditorPanel = new GroupLayout(editorPanel);
    	gl_EditorPanel.setHorizontalGroup(
    			gl_EditorPanel.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(gl_EditorPanel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_EditorPanel.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(scrollPane_1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE)
                        .addGroup(gl_EditorPanel.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        		.addGroup(javax.swing.GroupLayout.Alignment.LEADING, gl_EditorPanel.createSequentialGroup()
                        				.addComponent(btnSave)
                        				)
                        		)
                    .addGroup(gl_EditorPanel.createSequentialGroup()
//					.addGap(187)
//					.addComponent(editorNoticesBox, GroupLayout.PREFERRED_SIZE, 894, GroupLayout.PREFERRED_SIZE)
                    		)
//					.addComponent(resultsPanelNoticesBox, GroupLayout.PREFERRED_SIZE, 894, GroupLayout.PREFERRED_SIZE
//                        .addComponent(noticesPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    		)
                    .addContainerGap())
            );
    	gl_EditorPanel.setVerticalGroup(
    			gl_EditorPanel.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, gl_EditorPanel.createSequentialGroup()
                    .addContainerGap()
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(scrollPane_1, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(gl_EditorPanel.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    		.addComponent(btnSave)
//                    		.addGap(10)
//                    		.addComponent(editorNoticesBox, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE)
                    		)
                    .addContainerGap())
            );
    	
//    	gl_EditorPanel.setHorizontalGroup(
//    			gl_EditorPanel.createParallelGroup(Alignment.LEADING)
//    			.addGroup(arg0)
//				.addComponent(horizontalStrut_1, GroupLayout.PREFERRED_SIZE, 0, GroupLayout.PREFERRED_SIZE)
//				.addGroup(gl_EditorPanel.createSequentialGroup()
//					.addGap(475)
//					.addComponent(lblDefaultResultspanelContent))
//				.addGroup(gl_EditorPanel.createSequentialGroup()
//					.addGap(173)
//					.addComponent(defaultResultsDevCentralURLTextPane, GroupLayout.PREFERRED_SIZE, 743, GroupLayout.PREFERRED_SIZE))
//				.addGroup(gl_EditorPanel.createSequentialGroup()
//					.addGap(173)
//					.addComponent(defaultResultsPanelTextPane, GroupLayout.PREFERRED_SIZE, 743, GroupLayout.PREFERRED_SIZE))
//				.addGroup(gl_EditorPanel.createSequentialGroup()
//					.addGap(173)
//					.addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
//				.addGroup(gl_EditorPanel.createSequentialGroup()
//					.addGap(508)
//					.addComponent(defaultTextPaneConnectButton))
//    	);
    	

    	editorPanel.setLayout(gl_EditorPanel);

    	 // A CompletionProvider is what knows of all possible completions, and
        // analyzes the contents of the text area at the caret position to
        // determine what completion choices should be presented. Most
        // instances of CompletionProvider (such as DefaultCompletionProvider)
        // are designed so that they can be shared among multiple text
        // components.
        CompletionProvider provider = createCompletionProvider();

        // An AutoCompletion acts as a "middle-man" between a text component
        // and a CompletionProvider. It manages any options associated with
        // the auto-completion (the popup trigger key, whether to display a
        // documentation window along with completion choices, etc.). Unlike
        // CompletionProviders, instances of AutoCompletion cannot be shared
        // among multiple text components.
        AutoCompletion ac = new AutoCompletion(provider);
        ac.install(nTextEditorPane);
    	
    	
    	return editorPanel;

	}

	/**
	 * Create a simple provider that adds some Java-related completions.
	 * 
	 * @return The completion provider.
	 */
	private CompletionProvider createCompletionProvider() {

		// A DefaultCompletionProvider is the simplest concrete implementation
		// of CompletionProvider. This provider has no understanding of
		// language semantics. It simply checks the text entered up to the
		// caret position for a match against known completions. This is all
		// that is needed in the majority of cases.
		DefaultCompletionProvider provider = new DefaultCompletionProvider();

		String operatorsFilePath = "resources/iRulesOperatorsUncategorized.txt"; // contains, matches_glob, etc
		String statementsFilePath = "resources/iRulesStatementsUncategorized.txt"; // drop, pool and more
		String functionsFilePath = "resources/iRulesFunctionsUncategorized.txt"; // findstr, class and others
		String commandsFilePath = "resources/iRulesCommandsUncategorized.txt"; // HTTP::return etc etc
		String tclCommandsFilePath = "resources/tclCommandsUncategorized.txt"; // Built in tcl commands

		//		for (String filePath : (operatorsFilePath, statementsFilePath)) {
		//			
		//		}
		provider.addCompletion(new BasicCompletion(provider, "abstract"));
	    provider.addCompletion(new BasicCompletion(provider, "assert"));
	      
		try {
			BufferedReader in = new BufferedReader(new FileReader(operatorsFilePath));
			String str;
			while ((str = in.readLine()) != null) {
				provider.addCompletion(new BasicCompletion(provider, str));
//				String[] words = str.split(","); // split on commas
//				for (String word : words) {
//					provider.addCompletion(new BasicCompletion(provider, word));
//				}

			}
			in.close();
		} catch (IOException e) {
		}

		try {
			BufferedReader in = new BufferedReader(new FileReader(statementsFilePath));
			String str;
			while ((str = in.readLine()) != null) {
				String[] words = str.split(","); // split on commas
				for (String word : words) {
					provider.addCompletion(new BasicCompletion(provider, word));
				}

			}
			in.close();
		} catch (IOException e) {
		}

		try {
			BufferedReader in = new BufferedReader(new FileReader(functionsFilePath));
			String str;
			while ((str = in.readLine()) != null) {
				String[] words = str.split(","); // split on commas
				for (String word : words) {
					provider.addCompletion(new BasicCompletion(provider, word));
				}

			}
			in.close();
		} catch (IOException e) {
		}

		try {
			BufferedReader in = new BufferedReader(new FileReader(commandsFilePath));
			String str;
			while ((str = in.readLine()) != null) {
				String[] words = str.split(","); // split on commas
				for (String word : words) {
					provider.addCompletion(new BasicCompletion(provider, word));
				}

			}
			in.close();
		} catch (IOException e) {
		}
		
		try {
			BufferedReader in = new BufferedReader(new FileReader(tclCommandsFilePath));
			String str;
			while ((str = in.readLine()) != null) {
				String[] words = str.split(","); // split on commas
				for (String word : words) {
					provider.addCompletion(new BasicCompletion(provider, word));
				}

			}
			in.close();
		} catch (IOException e) {
		}




		// Add completions for all Java keywords. A BasicCompletion is just
		// a straightforward word completion.
		//	      provider.addCompletion(new BasicCompletion(provider, "abstract"));
		// Add a couple of "shorthand" completions. These completions don't
		// require the input text to be the same thing as the replacement text.
		provider.addCompletion(new ShorthandCompletion(provider, "sysout",
				"System.out.println(", "System.out.println("));
		provider.addCompletion(new ShorthandCompletion(provider, "syserr",
				"System.err.println(", "System.err.println("));

		return provider;

	}

	
	/**
	 * buildNavTreeFromBIGIP because later i will build it from some local which may or may not include stuff from the BigIP.
	 * 
	 */
	private void buildNavTree() {
		int navScrollPaneComponentsCount = NavPanel.getComponentCount();
		if ( navScrollPaneComponentsCount > 1) {
			NavPanel.remove(tree); 
		}
		//TODO: need to change this to buildNaveTree and have it pull in the list only if the list is empty.
//		Need to populate the list here

//	    NjordiRuleDefinition[] myRules = null;

		if ( NjordiRuleList.isEmpty() ) {
			try {
				NjordiRuleList = new ArrayList<NjordiRuleDefinition>(Arrays.asList(getNjordiRules()));
			} catch (Exception e) {
				f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e);
				exceptionHandler.processException();
			}
		}
	
		
		top = new DefaultMutableTreeNode("BigIP");
		createNodes(top);
//		tree = new JTree(top); // Now that TOP exists let's initialize the tree.

		tree = new f5JavaGuiTree(top);
//	    category = new DefaultMutableTreeNode("iRules");
//	    tree.add(category);
	
		//Where the tree is initialized:
	    tree.getSelectionModel().setSelectionMode
	            (TreeSelectionModel.SINGLE_TREE_SELECTION);

	    //Listen for when the selection changes.
	    tree.addTreeSelectionListener(this);
	    //Listen for when non-leave nodes are expanded
	    tree.addTreeExpansionListener(this);
		
		//This was the wrong way to go about it. TreeCellRenderer and TreeCellEditor are actually the editor and renderer for the tree itself. IE You can edit IN THE tree. It doesn't effect what happens in some other panel.
//		tree.setCellEditor(new JavaiRuleEditorTreeCellEditor());
//        tree.setEditable(true);
//	    NavPanel.add(scrollPane);
//	    navScrollPane.remove(tree);
//	    navScrollPane.add(tree);
	    JPanel bogusPanel = new JPanel();
	    
	    
//	    navScrollPane.remove(tree);
//	    navScrollPane.add(tree);
	    
	    //I want to do it with navScrollPane but that's not currently working.
//	    navScrollPane.add(tree);	
	    
	    NavPanel.add(tree);
//		NavPanel.repaint();
	    NavPanel.updateUI(); // Forces NavPanel to re-evaluate it's contents
//		frame.pack();
		
		
	}
	
	/**
	 * loadPreferences will handle reading in connections preferences and creating the prefs file if need be. 
	 * That is if I use the whole file thing at all instead of changing to java.util.prefs
	 * @return
	 */
	private boolean loadPreferences() {
		userHome = System.getProperty("user.home");
		
		//http://www.particle.kth.se/~lindsey/JavaCourse/Book/Part1/Java/Chapter10/Preferences.html
		//TODO: Replace this whole file thing with java.util.prefs
		//Check and see if the prefs and bigips directory exists and create it if it doesn't
		String settingsDirPath = userHome + "/.f5/njord/";
		File settingsDirectory = new File(settingsDirPath);
		if ( !settingsDirectory.exists() ) {
			log.info("Preferences directory doesn't exist. Creating.");
			// Create multiple directories
			writePreferences();
		}
		
		//Same for each individual file.
		File propsFile = new File(settingsDirectory + "/settings.properties");

		if ( !propsFile.exists() ) {
			log.info("Settings file doesn't exist");
			
			writePreferences();
		} else {
			log.info("Settings file exists");
		}

		// Get the preferences from a file
		Properties properties = new Properties();
		try {
		    properties.load(new FileInputStream(propsFile));
		} catch (IOException e) {
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e);
			exceptionHandler.processException();
		}
		
		iIPAddress = properties.getProperty("connection.host");
		iPort = Long.valueOf(properties.getProperty("connection.port"));
		iUserName = properties.getProperty("connection.usernameame");
		iPassword = properties.getProperty("connection.password");
		return true;
	}
	
	private boolean writePreferences() {
		String settingsDirPath = userHome + "/.f5/njord/";
		File settingsDirectory = new File(settingsDirPath);
		boolean success = false;
		if ( !settingsDirectory.exists() ) {
		success = (new File(settingsDirPath)).mkdirs();
		  if (success) {
		  log.info("Directories: " 
		   + settingsDirPath + " created");
		  }
		}
		
		File propsFile = new File(settingsDirectory + "/settings.properties");
		success = false;
		try {
			success = propsFile.createNewFile();
		    if (success) {
		    	log.info( propsFile + " created");
			}
	    	FileWriter fstream = new FileWriter(propsFile);
	    	BufferedWriter out = new BufferedWriter(fstream);
	    	out.write("#Njord the java iRule editor settings");
	    	out.newLine();
	    	out.write("connection.host = " + iIPAddress); //TODO: replace this with our default settings
	    	out.newLine();
	    	out.write("connection.port = " + iPort);
	    	out.newLine();
	    	out.write("connection.usernameame = " + iUserName);
	    	out.newLine();
	    	out.write("connection.password = " + iPassword);
	    	out.newLine();
	    	//Close the output stream
	    	out.close();
		} catch (IOException e) {
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e);
			exceptionHandler.processException();
		}
		return true;
	}
}
