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
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
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
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.xml.rpc.ServiceException;

import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.ShorthandCompletion;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.TextEditorPane;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.f5.AaronForster.njord.util.NjordDocumentListener;
import com.f5.AaronForster.njord.util.NjordFileLocation;
import com.f5.AaronForster.njord.util.f5ExceptionHandler;
import com.f5.AaronForster.njord.util.f5JavaGuiTree;
import com.f5.AaronForster.njord.util.njordTreeRenderer;


// https://github.com/jgruber/iControlExamples/tree/master/src/main/java/com/f5se/examples

/*
 * Changelog
 * 201200719/20 (major changes finished at about 2:30 and 3:30 AM.) base iRule editor and then TCL syntax highlighting functionality completed.
 * 		Calling this 0.5 now as I figure I'm about 1/2 way to something I wouldn't be embarrased to look at.
 * 		Renaming to Njord, norse god of wind. it's 'controlling wind' and it's got a J in it. for the Java reference. it's perfect.
 * 20120802 0.6 lot's of code cleanup some bugs fixed. 
 *  Saving the connection settings to the preferences file if you change them.
 *  major GUI clean up.
 *  fixed line breaks in prefs file
 *  prevent the display of sys built in iRules.
 * 20120803  
 *  added syntax highlighting for iRules but it's not fully functional support for words with colons or underscores in them doesn't seem to work.
 *  Added basic auto complete for iRules and TCL functions
 *  Added a notifications box that persists in the GUI
 * 20120803 0.7
 *  Added new iRule creation
 *  separated building the nav tree from pulling the iRules off the BIGIP
 * 20120804 0.8.0
 *  Added actionBar and moved buttons from the main menu bar to it.
 *  fixed an issue where you could no longer save a change to an existing iRule
 *  Wrote several junit tests but am not near done.
 * 20120807
 *  A bunch more JUnit tests. I still have a bunch of kinks in that to work out but it's a start.
 *  Got rid of all the System.out.println's in the main code
 *  Created my own tree renderer. We're now showing if an iRule is dirty though our mechanism for marking it dirty is out of whack.
 */


/*
 * NOTES SECTION
 * 
 *  
 * TODO SECTION
 * 0.8
 * TODO: Get rid of 'DestkopBigIPManager and replace it with this as the base.
 * 
 * 
 * I could use java.awt.Robot to automate clicking on buttons but apparently you have to specify the co-ordinates of every object to click.
 * A decent article on junit testing of swing http://www.javaworld.com/javaworld/jw-11-2004/jw-1115-swing.html?page=1
 * A sort of interesting book chapter about junit. It's actually from an ant book but the chapter is about junit http://java.sun.com/developer/Books/javaprogramming/ant/ant_chap04.pdf
 * AHA, I think I found it http://www.uispec4j.org/ uispec4j gui test code designed so that you can write the test code before you write the real code even.
 * I should be able to use something like this to select an element of a tree   window.getTree().select("friends")
 * GAH! uispec4j doesn't seem to be compatible w/ java 7. UNG..... 
 * Maybe I should install java 6 and write to that I'd rather be backwards compatible anyway.
 * http://www.fitnesse.org/ a standalone wiki and testing framework. IE Jira Jr.
 * 
 * 0.9
 * TODO: Set the get irules button to disabled unless the connection is verified
 * TODO: Create a makeVerified (or something slightly different) Function as well as a makeUnVerified that does a number of things neccessary for that state. Such as disabling buttons/etc.
 * TODO: Working on syntax highlighting this is the java token scanner http://svn.fifesoft.com/viewvc-1.0.5/bin/cgi/viewvc.cgi/RSTALanguageSupport/trunk/src/org/fife/rsta/ac/common/TokenScanner.java?view=markup&revision=588&root=RSyntaxTextArea
 * 			I believe that's the part that pulls out tokens from the document which the syntax then decides if they should be highlighted or not. I can probably extend the TCL one.
 * 		And this is the java parser. Dunno what entirely the difference is http://svn.fifesoft.com/viewvc-1.0.5/bin/cgi/viewvc.cgi/RSTALanguageSupport/trunk/src/org/fife/rsta/ac/java/JavaParser.java?view=markup&revision=279&root=RSyntaxTextArea 
 * 			I think they are used in conjunction.
 * TODO: Get rid of the progress bar and add a red/green light to the BigIP in the list
 * TODO: Update the new rule template so that you can select from multiple events
 * TODO: Store the window geometry in preferences these are the bounderies of the window and where on the screen it starts
 * TODO: Figure out how to get bounds. It's frame.getBounds(); figure out when to save them.
 * 
 * 1.0 
 *  * TODO: Figure out if there is a built in way to have the editor highlight variables that you've assigned.
 * TODO: Implement set tab width (built into ntextarea)
 * TODO: Build the nav tree but don't populate the iRules tree until it has been expanded.
 * TODO: Add some more reasonable sample templates
 * TODO: Add a reload from bigip/reload from disk button. Using whichever is correct.
 * later
 * TODO: Figure out my problem with logging in my other methods
 * TODO: Add make EOL visible and probably make whitespace visible setEOLMarkersVisible(boolean visible) 	setWhitespaceVisible(boolean visible) 
 * TODO: add a preference to paint tab lines. See if this setPaintTabLines(boolean paint)  does what I think it does.
 * TODO: Implement some of the other fecking awesome features of rsyntax and ntext area like the built in search/replace/highlight all functionality. 
 * TODO: Add custom code templates both for new rule creation and for shortcut expansion the way sysout works
 * TODO: Maybe later in $userHome/.f5/njord/preferences.settings if that exists it overrides the one in apphome? I will need to store individual bigip connection info there.  
 * 			TODO: Perhaps a preferences file in $appHome/preferences.settings could hold generic items
 * TODO: Figure out if I can have the bigip do a syntax check without saving to bigip.
 * TODO: Create an 'Add BigIP' button because I want to:
 * TODO: Have multiple BigIPs in the tree. Each one will hold it's own connection settings. Get the name from the first connection and save it or make it editable?)
 * TODO: Start saving things like expanded state of a branch node
 * TODO: Create an interface preferences section for things like:
 * 			Always expand folders/partitions when expanding major sections
 * TODO: Clean up the tree. Some options:
 * 			Make the full path folders either git rid of the path in the list and only display on a hover 
 * TODO: Add find and replace functionality. rsyntaxtextarea again to the rescue with built in find and replace including 'highlight all occurences' functionality. http://fifesoft.com/rsyntaxtextarea/examples/example4.php
 * TODO: Get rid of the top of the jtree unless I'm planning on having it be the BigIP's host name and having sections for virtuals and iRules and such.
 * TODO: See if it's possible to have multiple action listeners or if there's some other way to make that portion of it simpler.
 * TODO: Add a heirarchy for things. Use http://docs.oracle.com/javase/tutorial/uiswing/events/treeexpansionlistener.html to not load the contents of a branch node until you expand it.
 * TODO: Warn the user if they have some lame things like default password.
 * TODO: Add some of the fricking awesome features of rsyntaxtextarea such as automatically closing curly braces setCloseCurlyBraces(boolean close) a parser (see below,) Focusable tool tips setUseFocusableTips(boolean use) , visible white space
 * TODO: Add a parser to underline things? http://javadoc.fifesoft.com/rsyntaxtextarea/org/fife/ui/rsyntaxtextarea/parser/Parser.html
 * TODO: Make it so you can copy/paste names out of the nav tree
 * 
 * PUT OFF
 * TODO: For way future I can read the statistics info on an irule. Before enabling (maybe a "profile button?" Check and make sure the rule actually has statistics enabled.
 * TODO: Add iApp editing functionality? For the iApp editor have multiple "tabs" which really just are buttons which save the code into the iApp object and reload the editor with new codd and update the syntax style.
 * TODO: Make this generic and plugin oriented.
 * TODO: Add links to devcentral pages for keywords and functions and such the way iRuler does.
 * TODO: Come up with some way to switch back and forth between jgruber and icontrol assembly mechanisms?
 * TODO: Investigate the way 'Java Simple Text Editor' builds it's GUI from an XML config file. http://javatxteditor.sourceforge.net/ maybe not too closely since it's gui is crap but perhaps.... * 
 * TODO: Figure out what 'marked occurrences' is IE getMarkedOccurrences() I think it's like when you do a find all and it highlights them.
 * TODO: Add an option to select what version of iRules to use highlighting on. I'll need some fancy way to crawl the iRules reference pages or some better reference from corporate.
 * TODO: Include JMeter and be able to generate real traffic to test said iRules.
 * 			TODO: MUCH later support remote jmeter server functionality so that not only can I generate a little traffic but it could be a full on test bench. 
 * TODO: Implement some sort of code beautifier. There's one for java called jtidy http://sourceforge.net/projects/jtidy which is a port of something called htmltidy

 * TODO: Figure out how to implement the ActionPanel in such a way that I can have an ActionPanelByType
 *   ActionPanelByType will initially be an abstract class I will write a default implementation of that generic class as a reference/starting point but I will use an abstract class to define the requirements for that pane.
 *   ActionPanelByType will be the portion of the gui that displays an item which you have selected from the list. There will need to be a 'default' type which will likely just show a welcome message or something simple. Perhaps a couple of important actions like connect and edit preferences.
 *   The Second one I will write will be the Virtual Server pane which will only have enable/disable and a couple of text items. The first of course is the default
 * 
 * 
 * PROGRESS BAR ITEMS:
 * TODO: Get rid of the progress bar after we have verified our connection and replace it with an icon that says 'Connection Verified' or something.
 * 
 * 
 * TODO: Peep this http://www.ibm.com/developerworks/opensource/tutorials/os-eclipse-code-templates/os-eclipse-code-templates-pdf.pdf for some in depth dialog on template customization.
 * 
 * 
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
 * @version 0.8.2
 */
public class MainGuiWindow implements ActionListener, TreeSelectionListener, TreeExpansionListener {
	private String logPrefix = "MainGuiWindow: ";
	private String newline = "\n";
	private boolean connectionInitialized = false;
	private String njordVersion = "0.8.2";
	public String bigIPVersion = "unknown"; // Version number has multiple dots in it so I can't do it as a number type.
//	public float bigIPVersion = 0;

	//Connection information stuff. Ultimately we get this from the preferences file. We set the defaults here and write this to the prefs if there isn't one otherwise we use that.
	public String iIPAddress = "v11ltm1.localdomain";
	public long iPort = 443;
	public String iUserName = "admin";
	public String iPassword = "admin";
	
	private String userHome = null; //Stores the user's home directory path. Will probably get rid of this when we switch to using the right java preferences module
	
    // Let's not initialize it yet.
	private iControl.Interfaces ic = new iControl.Interfaces(); //From Assembly
	
	//iControl.BigIP from the maven wsdl thingy is reasonably equivalent to iControl.Interfaces from the iControl 
	//	'Assembly' available on DevCentral.
    
	private String initialNotificationsBoxText = "Welcome to Njord the java iRules editor.\nPlease be forgiving as this is still very much a pre-beta version this application but at this point all of the buttons should actually do something. Please note that Njord is not an official F5 product it is just written by someone who happens to work there. It comes with no warranty or guarantee. I can't even promise it won't break your BIGIP config at this point. Any comments or questions about Njord can be directed to it's author Aaron Forster a.forster@f5.com.\r\n\r\nConnections settings will be saved to ${home}/.f5/njord/settings.properties you can edit this file or change your settings with the connections menu.\r\n\r\nIf autocompletion and syntax highlighting don't seem to be working properly ensure that the resources directory is in the same directory as the njord jar file.";
	private String initialNoticesTextboxText = "Welcome to Njord v" + njordVersion;
	
	private TextEditorPane currentSelectedRule = null;
	public List<String> iRuleList = new ArrayList<String>();
//	public List<NjordiRuleDefinition> iRulesAsNjordObjects = new ArrayList<NjordiRuleDefinition>();
	
	
	//ALL THE SWING ITEMS SHOULD GO IN HERE:
	public JFrame frame;
	//private For the progress bar
	private JProgressBar progressBar;
	
	public JMenuBar menuBar = null;
	public JMenuItem mntmEditSettings = null;
	private JToolBar actionBar = new JToolBar(); // Will hold buttons for things like save
	public JTextPane defaultResultsDevCentralURLTextPane = null;
	public JTextPane txtNotificationsTextBox = new JTextPane();
	public JButton defaultTextPaneConnectButton = null;
	public JMenuItem mntmConnect = null;
	public JTextArea resultsPanelNoticesBox = new JTextArea();	//This is where we put messages
	public JButton btnGetiRules = null;
	public JButton btnNewiRule = null;
	public DefaultMutableTreeNode top = null; //Top node in the jtree
	public f5JavaGuiTree tree = null;
	public JButton btnActionSave = null;
	public JButton btnDeleteiRule = null;
	
	private JPanel resultsPanel = null;
	private JSplitPane splitPane = new JSplitPane();
	private JSplitPane resultsSPlitPane = new JSplitPane();
	private TextEditorPane nTextEditorPane = null;
	
	private JPanel EditorPanel = null;
	public JScrollPane codeEditorScrollPane = null;
	
	private JTextArea output;
	private JScrollPane scrollPane;
	private DefaultMutableTreeNode category = null; // This is part of the navigation tree and here from an initial attempt to decouple building the tree and connecting to the BIGIP. I will probably remove it.
	private JPanel NavPanel = new JPanel(); // This is the main panel for the LHS navigation tree
	private JScrollPane navScrollPane = new JScrollPane();

	private JLabel lblStatusLabel = new JLabel();
	public PreferencesDialog preferencesDialog;
	private iRuleDialog newiRuleDialong;
	
	//## Editor colors
	//These are good enough for now.
	//TODO: Figure out how to define the regex scheme so that it catches those.
	public Color annotationColor = Color.decode("#646464");
	public Color variableColor = Color.decode("#7F0055");
//	public Color identifyerColor = Color.decode("#880080");
	public Color commentColor = Color.decode("#3F7F5F"); // Leave this the same green for now but may override with this green from eclipse
	public Color functionColor = Color.decode("#880080");
	public Color regexColor = Color.decode("#2A00FF");
	public Color reservedWordColor = Color.decode("#0033FF"); // TCL built in functions (not entirely accurate)
	public Color reservedWord2Color = Color.decode("#000099"); // Irules stuff
	public Color operatorColor = new Color(880080);
	public Color doublequoteColor = Color.decode("#1a5a9a");
	public Color backquoteColor = Color.decode("#4a2a3a");
	public Color bracketColor = Color.decode("#990099");
    
	//slf4j logger factory
	final Logger log = LoggerFactory.getLogger(MainGuiWindow.class);
    //TODO: Go to http://www.slf4j.org/manual.html and see what sort of config file I need to use to make this work.
	
	// This would be how I would configure log4j to use a properties file for configuration but I'm not sure how I should do it with the whole slf4j thing.
//	PropertyConfigurator.configure(args[0]);
	
	/*
	 * Launch the application.
	 */
	public static void main(String[] args) {
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

	/*
	 * The default constructor just runs initialize() which builds most of the GUI.
	 */
	public MainGuiWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame. It seems a little poorly named. We're actually building the whole gui in here.
	 */
	private void initialize() {
		log.info(logPrefix + "Starting Up");
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
	
//		editorNoticesBox.setEditable(false);
//		editorNoticesBox.setText("");
		Dimension resultsPanelNoticesDimension = new Dimension(500,20);
		
		Dimension resultsPanelDimension = new Dimension(500,500);
		
		//TODO: This is currently hidden. Make it show up when we have an iRule selected
		JButton btnSave1 = new JButton("Save");
		btnSave1.setToolTipText("Save the currently selected Object.");
    	btnSave1.addActionListener(this);
		
		// The menu definition	
		menuBar = new JMenuBar();
		menuBar.setName("menuBar");
//		frame.getContentPane().add(menuBar);
		
		JMenu mnConnMenu = new JMenu("Connection");
		mnConnMenu.setName("ConnectionMenu");
		menuBar.add(mnConnMenu);
		
		// This one might be my template for how to set action menu items. It also includes keyboard shortcut settings
		mntmEditSettings = new JMenuItem("Edit Settings");
		mntmEditSettings.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_2, ActionEvent.ALT_MASK)); // I think this is the part that adds the hotkey
		mntmEditSettings.addActionListener(this); // This adds an action listener so if you click this it will trigger actionListener() method defined below
		mntmEditSettings.setName("editSettingsBtn"); 
		mnConnMenu.add(mntmEditSettings);
		
		mntmConnect = new JMenuItem("Verify Connection");
		mntmConnect.addActionListener(this);
		mnConnMenu.add(mntmConnect);

		//ActionBar components *********************************************				
		btnGetiRules = new JButton("Get iRules");
		btnGetiRules.setHorizontalAlignment(SwingConstants.RIGHT);
		btnGetiRules.setToolTipText("Get the list of editable iRules from the BIGIP and build a navigation tree with them.");
		btnGetiRules.addActionListener(this);
		btnGetiRules.setName("listiRulesButton");
		actionBar.add(btnGetiRules);
		
		//TODO: Hide this and make it show up only when we have an iRule list? Either that or figure out how to create 'offline iRules'
		btnNewiRule = new JButton("New iRule");
		btnNewiRule.setToolTipText("Create a new iRule.");
		btnNewiRule.addActionListener(this);
		btnNewiRule.setName("newiRuleButton");
		btnNewiRule.setEnabled(false); //We will re-Enable this once we have built the iRUle nav tree.
		actionBar.add(btnNewiRule);
		
		btnActionSave = new JButton("Save"); // Hide this until we are editing an iRule
		btnActionSave.setEnabled(false);
		btnActionSave.addActionListener(this);
		actionBar.add(btnActionSave);
		
		//DEFAULT RESULTS PANE CONTENT ENDS HERE

		JToolBar toolBar = new JToolBar();  //TODO: Figure out what this is used for and rename it to something more descriptive

		JButton connectToBigIPButton = new JButton("Connect");
		toolBar.add(connectToBigIPButton);
		connectToBigIPButton.addActionListener(this);
		NavPanel.setMinimumSize(navMinimumSize);
		splitPane.setLeftComponent(NavPanel);

		//TODO Make this work and switch it so that building the nav tree updates navScrollPane not NavPanel
		NavPanel.add(navScrollPane);
		
		//DEFAULT RESULTS PANE CONTENT STARTS HERE		
		JLabel lblDefaultResultspanelContent = new JLabel("Welcome to Njord");
		lblDefaultResultspanelContent.setVerticalAlignment(SwingConstants.TOP);
		
		defaultResultsDevCentralURLTextPane = new JTextPane();
		defaultResultsDevCentralURLTextPane.setName("DevCentralURLBox");
		defaultResultsDevCentralURLTextPane.setText("iRules Wiki https://devcentral.f5.com/wiki/iRules.HomePage.ashx");
		txtNotificationsTextBox.setText(initialNotificationsBoxText);
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		
		//Figure out where this is used and rename it to something helpful
		JPanel panel = new JPanel();		
		defaultTextPaneConnectButton = new JButton("Verify Connection");
		defaultTextPaneConnectButton.addActionListener(this);
		defaultTextPaneConnectButton.setName("defaultTxtPnConnectButton");
		
		resultsPanelNoticesBox.setEditable(false);
		resultsPanelNoticesBox.setText(initialNoticesTextboxText);
		resultsPanelNoticesBox.setName("noticesTextBox");
		
		resultsPanel = new JPanel();
		GroupLayout gl_resultsPanel = new GroupLayout(resultsPanel);
		gl_resultsPanel.setHorizontalGroup(
			gl_resultsPanel.createParallelGroup(Alignment.LEADING)
				.addComponent(horizontalStrut_1, GroupLayout.PREFERRED_SIZE, 0, GroupLayout.PREFERRED_SIZE)
					.addGroup(gl_resultsPanel.createSequentialGroup()
					.addGap(40, 388, 900)
					.addComponent(lblDefaultResultspanelContent)
					.addGap(40, 388, 900))
				.addGroup(gl_resultsPanel.createSequentialGroup()
					.addGap(40)
					.addComponent(defaultResultsDevCentralURLTextPane, GroupLayout.PREFERRED_SIZE, 813, Short.MAX_VALUE)
					.addGap(40))
				.addGroup(gl_resultsPanel.createSequentialGroup()
					.addGap(40)
					.addComponent(txtNotificationsTextBox, GroupLayout.PREFERRED_SIZE, 813, Short.MAX_VALUE)
					.addGap(40))
				.addGroup(gl_resultsPanel.createSequentialGroup()
					.addGap(40)
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 813, Short.MAX_VALUE)
					.addGap(40))
				.addGroup(gl_resultsPanel.createSequentialGroup()
					.addGap(40, 388, 900)
					.addComponent(defaultTextPaneConnectButton)
					.addGap(40, 388, 900))
				.addGroup(gl_resultsPanel.createSequentialGroup()
					.addGap(391)
					.addComponent(lblDefaultResultspanelContent)
					.addContainerGap(417, Short.MAX_VALUE))
		);
		gl_resultsPanel.setVerticalGroup(
			gl_resultsPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_resultsPanel.createSequentialGroup()
					.addComponent(horizontalStrut_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(29)
					.addComponent(lblDefaultResultspanelContent)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(defaultResultsDevCentralURLTextPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(5)
					.addComponent(txtNotificationsTextBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(5)
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(23)
					.addComponent(defaultTextPaneConnectButton))
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
		lblStatusLabel.setText("Unverified");
		toolBar.add(lblStatusLabel);
		
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(menuBar, javax.swing.GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 1102, Short.MAX_VALUE)
				.addComponent(actionBar, javax.swing.GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 1102, Short.MAX_VALUE)
//				actionBar
				.addComponent(splitPane, javax.swing.GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 1102, Short.MAX_VALUE)
				.addComponent(toolBar, javax.swing.GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 1102, Short.MAX_VALUE)
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(menuBar, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
					.addGap(5)
					.addComponent(actionBar, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
					.addGap(5)
					.addComponent(splitPane, GroupLayout.PREFERRED_SIZE, 623, Short.MAX_VALUE)
					.addGap(5)
					.addComponent(toolBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		);
		
		btnDeleteiRule = new JButton("Delete iRule");
		btnDeleteiRule.setEnabled(false);
		btnDeleteiRule.setName("DeleteiRuleButton");
		btnDeleteiRule.addActionListener(this);
		actionBar.add(btnDeleteiRule);
		frame.getContentPane().setLayout(groupLayout);	
	}
	
	/**
	 * Build the nodes of the iRule navigation tree. Takes the tree to add the nodes to as an argument.
	 * @param tree
	 */
	private void createNodes(DefaultMutableTreeNode tree) {
	    DefaultMutableTreeNode addRule = null;
	    
	    category = new DefaultMutableTreeNode("iRules");
	    tree.add(category);
	    
	    //TODO: Create a generic getSomethingList?
	    //TODO: Figure out how to handle the long path of the iRule's full name. IE do I pull off the path and just show the name, do I have expandable folders for the folders in the path? I like that idea but only if I automatically have 'common' be expanded.
	    //TODO: If I do the above I will need a way to remember what folders have been openened if the client does so.
	    for (Iterator<String> it = iRuleList.iterator() ; it.hasNext(); ) {
	    	String thisRule = it.next();

	    	if ( thisRule.matches("/Common/_sys_.*") ) { // V11 syntax
	    		log.debug("Sys iRule, Skipping");
	    		it.remove();
	    		continue;
	    	} else if ( thisRule.matches("_sys_.*") ) { // V10 and maybe V9
	    		log.debug("Sys iRule, Skipping");
	    		it.remove();
	    		continue;
	    	}

	    	NjordFileLocation ruleLocation = new NjordFileLocation(this, thisRule, ic); 
	    	TextEditorPane ruleEditor = null;
	    	try {
	    		ruleEditor = new TextEditorPane(0, true, ruleLocation);
	    	} catch (IOException e) {
	    		f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e);
	    		exceptionHandler.processException();
	    	}

	    	ruleEditor.setName(thisRule);
	    	ruleEditor.discardAllEdits(); //This prevents undo from 'undoing' the loading of the editor with an iRule
	    	ruleEditor.getDocument().addDocumentListener(new NjordDocumentListener(this));

	    	//Syntax Highlighting items
	    	ruleEditor.setSyntaxEditingStyle("SYNTAX_STYLE_IRULES");
	    	ruleEditor.setCodeFoldingEnabled(true);
	    	ruleEditor.setAntiAliasingEnabled(true);
	    	
	    	//Colors pulled out of the eclipse gui
//	    	Variables blue             00 00 192 0000C0
//	    	keywords  purpleish        127 00 85  7F0055
//	    	Operators solid black 	   00 00 00  000000
//	    	Strings  also blue         42 00 255  2A00FF
//	    	Annotations grey           100 100 100 646464   (I think this is stuff like @Test)
//	    	comments greenish          63  127  95  3F7F5F
//	    	todo tag blue grey         127 159 191  7F9FBF
	    	
	    	//Some of my own
	    	// A couple of slightly different but good blues
	    	//0000CC 0000FF    how about 000066 and  0033ff
	    	//Some good purples
	    	// 660066 990099
	    	
	    	SyntaxScheme scheme = ruleEditor.getSyntaxScheme();
	    	scheme.getStyle(Token.ANNOTATION).foreground = annotationColor;
	    	scheme.getStyle(Token.VARIABLE).foreground = variableColor;
//	    	scheme.getStyle(Token.IDENTIFIER).foreground = identifyerColor; //I think 'identifyer' is all the other words.
	    	scheme.getStyle(Token.FUNCTION).foreground = functionColor;
	    	scheme.getStyle(Token.REGEX).foreground = regexColor;
	    	scheme.getStyle(Token.COMMENT_EOL).foreground = commentColor;
//	    	scheme.getStyle(Token.COMMENT_MULTILINE).foreground = commentColor; // I don't think there's a multi-line comment defined yet
	    	scheme.getStyle(Token.RESERVED_WORD_2).foreground = reservedWord2Color;
	    	scheme.getStyle(Token.RESERVED_WORD).foreground = reservedWordColor;
	    	scheme.getStyle(Token.OPERATOR).foreground = operatorColor;
//	    	scheme.getStyle(Token.LITERAL_STRING_DOUBLE_QUOTE).foreground = doublequoteColor;
	    	scheme.getStyle(Token.LITERAL_BACKQUOTE).foreground = backquoteColor;
	    	scheme.getStyle(Token.SEPARATOR).foreground = bracketColor;
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
	        ac.install(ruleEditor);
	    		    	
	    	addRule = new DefaultMutableTreeNode(ruleEditor);


	    	category.add(addRule);
	    }
	    
	    if ( iRuleList.isEmpty() ) {
	    	resultsPanelNoticesBox.setText("No valid iRules found on BIGIP.");
	    	TextEditorPane ruleEditor = null;
    	
    		ruleEditor = new TextEditorPane();

    		ruleEditor.setName("No iRules Found On BIGIP");
    		ruleEditor.discardAllEdits(); //This prevents undo from 'undoing' the loading of the editor with an iRule
    		ruleEditor.getDocument().addDocumentListener(new NjordDocumentListener(this));

    		//Syntax Highlighting items
    		ruleEditor.setSyntaxEditingStyle("SYNTAX_STYLE_IRULES");
    		ruleEditor.setCodeFoldingEnabled(true);
    		ruleEditor.setAntiAliasingEnabled(true);


    		//TODO: Update the color scheme. Figure out what items I will be using Function, reserved_word, etc for what components and how they should be colorized.
    		SyntaxScheme scheme = nTextEditorPane.getSyntaxScheme();
    		//	        scheme.getStyle(Token.RESERVED_WORD).foreground = Color.pink;
    		scheme.getStyle(Token.FUNCTION).foreground = Color.MAGENTA;
    		Color operatorColor = new Color(880080);
    		//	        scheme.getStyle(Token.OPERATOR).foreground = Color.GREEN;
    		scheme.getStyle(Token.OPERATOR).foreground = operatorColor;

    		addRule = new DefaultMutableTreeNode(ruleEditor);
    		category.add(addRule);
	    }
	}
	
	//This is the tree selection listener method.
	public void valueChanged(TreeSelectionEvent e) {
		//Hrm... on the one hand I could try and define node and then call valueChanged(TreeSelectionEvent) But on the other I'd have to figure out how to create a TreeSelectionEvent.
		//		And honestly once I figure out how to create that I will probalby have selected a node in the tree. Hrm...
		// Perhaps I will make valueChanged(TreeSelectionEvent do some minimal work assigning a node and such and then call some OTHER method and that will be one I can write a JUnit test for?
		// Perhaps my best bet would be to spend some time on the oracle looking for recommendations on writing junit tests for swing code.
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                           tree.getLastSelectedPathComponent();

        if (node == null) return;
        resultsPanelNoticesBox.setText(""); //Clear out the notices box.
     
//        Object nodeInfo = node.getUserObject();
        //TODO: Use the above syntax to load nodeInfo as a generic type Object then figure out what type it is and act accordingly.
        //			This avoids random stupid errors such as the null pointer exception we currently generate if you click on a branch node
//        LocalLBRuleRuleDefinition nodeInfo = (LocalLBRuleRuleDefinition) node.getUserObject();
        TextEditorPane nodeInfo = (TextEditorPane) node.getUserObject();
//        NjordiRuleDefinition nodeInfo = (NjordiRuleDefinition) node.getUserObject();
        
        
        // This might be part of how to do the hyperlinks so I can send you to devcentral
//        addHyperlinkListener(javax.swing.event.HyperlinkListener l)
   
        // We do this the whole nodeInfo way because we have to check and see if it's a leaf node or not.
        if (node.isLeaf()) {
        	log.info("Rule " + nodeInfo + " Selected");
    		
            //TODO: Check currentSelectedRule and see if it is non-empty. If it is non empty update that rule with the contents of the editor before filling the editor with the new contents.
            //		Then set tree.getLastSelectedPathComponent() into currentSelectedRule.
        	//TODO: also mark the iRule as unsaved at the same time.
        	
//        	if ( currentSelectedRule != null ) {
//        		currentSelectedRule.setRule_definition(nTextEditorPane.getText());
//        	}
        	
        	currentSelectedRule = nodeInfo;
        	btnDeleteiRule.setEnabled(true);
        	
        	if ( currentSelectedRule.isDirty() ) {
        		btnActionSave.setEnabled(true);
        	} else {
        		btnActionSave.setEnabled(false);
        	}
//        	rSyntaxTextArea.setText(nodeInfo.getRule_definition());
//        	nTextEditorPane.setText(currentSelectedRule.getRule_definition());
            
        	Dimension editorPanelDimension = new Dimension(500,500);
        	EditorPanel.setMinimumSize(editorPanelDimension);
//        	EditorPanel.setPreferredSize(editorPanelDimension);
            //TODO: Figure out howo tmake this remove whatever is currently there instead of statically coding 'resultsPanel' There will be a point when it's not 'resultsPanel' Quite often in fact. Like when it's now editPanelScrolePane1
            //I'm not sure if I need to do this but it seems like the right thing to do. Remove the existing contents of the right panel before setting a new one.
//            splitPane.remove(resultsPanel);
//            splitPane.setRightComponent(EditorPanel);
        	
        	//TODO: This seems like a very inneficient way to do this.
//        	codeEditorScrollPane.remove(nTextEditorPane);
//        	codeEditorScrollPane.add(currentSelectedRule);
        	codeEditorScrollPane.setViewportView(currentSelectedRule);
            resultsSPlitPane.remove(resultsPanel);
//            resultsSPlitPane.setTopComponent(EditorPanel);
//            resultsSPlitPane.setTopComponent(currentSelectedRule);
            resultsSPlitPane.setTopComponent(codeEditorScrollPane);
        } else {
//            displayURL(helpURL);
        	btnDeleteiRule.setEnabled(false);
        	btnActionSave.setEnabled(false);
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

	/**
	 * actionPerformed is the method that listens for events like clicking a button or a menu item.
	 */
	public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand(); //This will get me the text of the source action without having to instantiate the source
        Class sourceClass = e.getSource().getClass(); //This would allow me to identify the class first before getting it with e.getSource() if I needed to instantiate the whole class for something.

        String statusText;
        //TODO: Can I use a switch here or is java where switch is numbers only? Ahh... there would be a reason to use id or to set a name or something on all buttons/menu items/etc.
        if (actionCommand == "Edit Settings") {
        	log.debug("Edit Settings Event Detected.");
        	resultsPanelNoticesBox.setText("Editing Connection Settings");
        	
        	final PreferencesDialog settingsPane = new PreferencesDialog(frame, this);
        	settingsPane.ConnPreffsHostTextField.setText(iIPAddress);
        	settingsPane.ConnPreffsPortTextField.setText(String.valueOf(iPort));
        	settingsPane.ConnPreffsUserTextField.setText(iUserName);
        	settingsPane.ConnPreffsPasswordTextField.setText(iPassword);
//        	settingsPane.setEnabled(true);


//        	final JDialog dialog = new JDialog(frame, 
//                             "Connection Settings",
//                             true);
//        	dialog.setContentPane(settingsPane);
//        	dialog.pack();
//        	dialog.setVisible(true);


//        	int value = ((Integer)settingsPane.getValue()).intValue();
//          	int result = JOptionPane.showConfirmDialog(null, preferencesDialog.panel_1,
        	int result = JOptionPane.showConfirmDialog(null, settingsPane.panel_1, 
        			"Connection Preferences", JOptionPane.OK_CANCEL_OPTION);
        	if (result == JOptionPane.OK_OPTION) { 
		        iIPAddress = settingsPane.ConnPreffsHostTextField.getText();
		        iPort = Integer.parseInt( settingsPane.ConnPreffsPortTextField.getText());
		        iUserName = settingsPane.ConnPreffsUserTextField.getText();
		        iPassword = settingsPane.ConnPreffsPasswordTextField.getText();
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
        	boolean successfullConnection = initializeConnection();
        } else if (actionCommand == "New iRule"){
        	//TODO: This is working as far as creating a new text edior and letting you start typing but I still need to figure out how to add it to the list of rules
        	
        	//TODO: For some reason this actually causes the jTree to get all screwed up. Make that not happen
        	//TODO: Make 'Edit Rule' Be the default action for a double click on an iRule in the list.
        	//TODO: Create a 'New Rule' function. This will probably go hand in hand with:
        	//TODO: Figure out how to handle renaming of iRules.
        	//TODO: Figure out how to handle 'offline iRules' IE one you have created but isn't uploaded yet.
        	log.debug("New Rule Detected");
        	resultsPanelNoticesBox.setText("New iRule functionality is still a tiny bit sketchy. It requires that you have a connection to a BIGIP and It's likely to only work on V11 systems. Sorry for the inconvenience.");
        	
        	String newiRuleName = "new_rule";
	        String newiRulePartition = "/Common";
	        
	        int result = JOptionPane.showConfirmDialog(null, newiRuleDialong.panel_1, 
	        		"New iRule", JOptionPane.OK_CANCEL_OPTION);
	        if (result == JOptionPane.OK_OPTION) { 
	        	newiRuleName = newiRuleDialong.txtNewiRuleName.getText();
	        	newiRulePartition = newiRuleDialong.txtNewiRulePartition.getText();

	        	String newiRuleFullName = newiRulePartition + "/" + newiRuleName;

	        	if ( iRuleList.isEmpty() ) {
	        		try {
	        			resultsPanelNoticesBox.append("\nFetching list of rules from the BIGIP");
	        			iRuleList = new ArrayList<String>(Arrays.asList(getiRuleList()));
	        			resultsPanelNoticesBox.append("\nList fetched");
	        		} catch (Exception e1) {
	        			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e1, this, log);
	        			exceptionHandler.processException();
	        		}
	        		String[] myNameAsAList = { newiRuleFullName };
	        		List<String> thisRuleAsList = new ArrayList<String>(Arrays.asList(myNameAsAList));
	        		
	        		if (iRuleList.contains(newiRuleFullName)) { // This isn't working
	        			//There is already a rule with this name
	        			resultsPanelNoticesBox.setText("Unable to create rule a rule with this name already exists on the BIGIP. Please choose another");
	        			return;
	        		}
	        		iRuleList.add(newiRuleFullName);
	        		buildNavTree();
	        	} else {
	        		if (iRuleList.contains(newiRuleFullName)) { 
	        			//There is already a rule with this name
	        			resultsPanelNoticesBox.setText("Unable to create rule a rule with this name already exists on the BIGIP. Please choose another");
	        			return;
	        		}
	        		iRuleList.add(newiRuleFullName);
	        	}

	        	String newRuleDefinition = "when CLIENT_ACCEPTED {\n\t\n\t\n}";
	        			
	        	DefaultMutableTreeNode addRule = null;
	        	
	        	TextEditorPane ruleEditor = new TextEditorPane();
	        	ruleEditor.setName(newiRuleFullName);
	        	ruleEditor.setText(newRuleDefinition);
	        	ruleEditor.discardAllEdits(); //This prevents undo from 'undoing' the loading of the editor with an iRule
	        	ruleEditor.getDocument().addDocumentListener(new NjordDocumentListener(this));

	        	//Syntax Highlighting items
	        	ruleEditor.setSyntaxEditingStyle("SYNTAX_STYLE_IRULES");
	        	ruleEditor.setCodeFoldingEnabled(true);
	        	ruleEditor.setAntiAliasingEnabled(true);

		    	SyntaxScheme scheme = ruleEditor.getSyntaxScheme();
		    	scheme.getStyle(Token.ANNOTATION).foreground = annotationColor;
		    	scheme.getStyle(Token.VARIABLE).foreground = variableColor;
//		    	scheme.getStyle(Token.IDENTIFIER).foreground = identifyerColor; //I think 'identifyer' is all the other words.
		    	scheme.getStyle(Token.FUNCTION).foreground = functionColor;
		    	scheme.getStyle(Token.REGEX).foreground = regexColor;
		    	scheme.getStyle(Token.COMMENT_EOL).foreground = commentColor;
//		    	scheme.getStyle(Token.COMMENT_MULTILINE).foreground = commentColor; // I don't think there's a multi-line comment defined yet
		    	scheme.getStyle(Token.RESERVED_WORD_2).foreground = reservedWord2Color;
		    	scheme.getStyle(Token.RESERVED_WORD).foreground = reservedWordColor;
		    	scheme.getStyle(Token.OPERATOR).foreground = operatorColor;
//		    	scheme.getStyle(Token.LITERAL_STRING_DOUBLE_QUOTE).foreground = doublequoteColor;
		    	scheme.getStyle(Token.LITERAL_BACKQUOTE).foreground = backquoteColor;
		    	scheme.getStyle(Token.SEPARATOR).foreground = bracketColor;
		    	
	        	// Not this but adding them by hand.
	        	//	        	buildNavTree();
	        	addRule = new DefaultMutableTreeNode(ruleEditor);

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
		        ac.install(ruleEditor);
		    		    	
	        	DefaultTreeModel model = (DefaultTreeModel)tree.getModel();

	        	// Find node to which new node is to be added
	        	int startRow = 0;
	        	String prefix = "iRules";
	        	TreePath path = tree.getNextMatch(prefix, startRow, Position.Bias.Forward);
	        	MutableTreeNode node = (MutableTreeNode)path.getLastPathComponent();

	        	// Insert new node as last child of node
	        	model.insertNodeInto(addRule, node, node.getChildCount());
	        }
        } else if (actionCommand == "Save"){
        	//TODO: Apparently blank is a valid iRule as far as mcpd is concerned. Check to ensure the rule isn't completely blank and handle that.
        	//This will tell me what iRule is selected. Then I need to set the definition. tree.getLastSelectedPathComponent()
        	log.info("Save detected");
        	// Clear the contents of the notices box
        	String resultsText = "Saving...";
        	resultsPanelNoticesBox.setText(resultsText);
        	
            // A way to add a parser to validate the code. I don't know how to work this out since we have the bigip do it.
            //addParser(Parser parser) 
        	
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                    tree.getLastSelectedPathComponent();
            if (node == null) return;
            
            TextEditorPane nodeInfo = (TextEditorPane) node.getUserObject();

            if (nodeInfo.isLocal()) {
            	NjordFileLocation newLoc = new NjordFileLocation(this, nodeInfo.getName(), ic, nodeInfo.getText()); 
            	try {
            		nodeInfo.saveAs(newLoc);
            		newLoc.isLocal(false);
            		if (resultsPanelNoticesBox.getText().startsWith("Saving")) { //TODO: Check and see that these if blocks are even neccesary
            			//If the results panel still says "Saving..." then we were successfull. If it failed then the text will have an error in it now.
            			resultsPanelNoticesBox.setText("Save successful");
            		}
            		tree.repaint(); // Force the navigation tree to redraw itself. It will notice that the editor for this rule is now clean and display it properly.
            		btnActionSave.setEnabled(false); // Then disable the save button to show that we are saved on the server one more way.
            	} catch (IOException e1) {
            		f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e1, this, log);
            		exceptionHandler.processException();
            	}
            } else {
            	try {
            		nodeInfo.save();	
            		if (resultsPanelNoticesBox.getText().startsWith("Saving")) {
            			//If the results panel still says "Saving..." then we were successfull. If it failed then the text will have an error in it now.
            			resultsPanelNoticesBox.setText("Save successful");
            		}
            		tree.repaint(); // Force the navigation tree to redraw itself. It will notice that the editor for this rule is now clean and display it properly.
            		btnActionSave.setEnabled(false); // Then disable the save button to show that we are saved on the server one more way.
            	} catch (IOException e2) {
            		f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e2);
            		exceptionHandler.processException(); 
            	}
            }
        } else if (actionCommand == "Get iRules"){
        	//TODO: Apparently blank is a valid iRule as far as mcpd is concerned. Check to ensure the rule isn't completely blank and handle that.
        	//This will tell me what iRule is selected. Then I need to set the definition. tree.getLastSelectedPathComponent()
        	log.info("Get iRules detected");
        	
        	if (connectionInitialized == true) {
        		buildNavTree();	
        	} else {
        		boolean success = initializeConnection();
        		if (success) {
        			buildNavTree();
        		} 
        	}
        } else if (actionCommand == "Delete iRule"){
        	log.info("Delete iRule detected");
        	String resultsText = "Deleting...";
        	resultsPanelNoticesBox.setText(resultsText);

        	DefaultMutableTreeNode node = (DefaultMutableTreeNode)
    				tree.getLastSelectedPathComponent();
    		if (node == null) return;

    		TextEditorPane nodeInfo = (TextEditorPane) node.getUserObject();
    		String ruleName = nodeInfo.getName();
    		String[] ruleNames = { ruleName };
        	//TODO: Change this so it shows the rule name.
        	int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the selected rule [" + ruleName + "] from the BIGIP?", 
        			"Delete iRule", JOptionPane.OK_CANCEL_OPTION);

        	if (result == JOptionPane.OK_OPTION) { 
        		try {
        			ic.getLocalLBRule().delete_rule(ruleNames);
        			resultsPanelNoticesBox.setText("Rule [" + ruleName + "] successfully deleted.");
//        			node.removeFromParent();
//        			tree.updateUI(); // This does the duplicate thingy.
        			
        			DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
        			
        			// Find node to remove
//        			int startRow = 0;
//        			String prefix = ruleName;
//        			TreePath path = tree.getNextMatch(prefix, startRow, Position.Bias.Forward);
//        			MutableTreeNode deleteNode = (MutableTreeNode)path.getLastPathComponent();

        			// Remove node; if node has descendants, all descendants are removed as well
//        			model.removeNodeFromParent(deleteNode);
        			model.removeNodeFromParent(node);		
        		} catch (RemoteException e1) {
        			resultsPanelNoticesBox.setText("Delete failed"); // This is actually just a cheat to blank out the results panel. It's actual text will probably be set by the exception handler.
        			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e1, this, log);
        			exceptionHandler.processException();
        		} catch (Exception e1) {
        			resultsPanelNoticesBox.setText("Delete failed"); // This is actually just a cheat to blank out the results panel. It's actual text will probably be set by the exception handler.
        			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e1, this, log);
        			exceptionHandler.processException();
        		}
        	}        
        } else  {
        	//If we have entered this section we have screwed something up 
            statusText = "Unknown";
        	log.error("Un-Known Action Event Detected." 
                    + newline
                    + "    Event source: " + actionCommand
                    + " (an instance of " + getClassName(sourceClass) + ")");
           
        } 

    }
	
	/**
	 * A wrapper for initializeConnection() this part handles updating the notices box and stuff like that.
	 */
	private void connectToBigIP() {
		//TODO: Get rid of this method
		
    	
    	//jgruber syntax
//    	ic = new iControl.BigIP(iIPAddress,iUserName,iPassword); 
//    	ic.setIgnoreInvalidCert(true); // otherwise java will bail on the invalid cert
    	
    	boolean successfullConnection = initializeConnection();
    	//TODO: Tighten connections down so that there's only one place where I do this part. IE I should move the connection validation into the connect method.
    	//TODO: Do a better check on the connection. Right now I'm just checking for a version and saying failed if i don't get it. I need to know if I'm getting an error from the device, etc.

		if (successfullConnection == true) {
        	// TODO: Make this a check rather than printing to the console
			log.info("Connected");
//        	lblStatusLabel.setText("Connected");
//        	resultsPanelNoticesBox.setText("Connection Verified");
//        	progressBar.setValue(100);
			//TODO: Replace the progress bar with a red/green indicator light
        	connectionInitialized = true;
		} else {
			// scream, run and cry
//			progressBar.setValue(0);
//			log.error("Connection settings invalid");
//			resultsPanelNoticesBox.setText("Connection Failed");
			lblStatusLabel.setText("Disconnected");
			connectionInitialized = false;
		}
		resultsPanelNoticesBox.setText("Connection Verified");
	}
	
	/**
	 * Runs the initialize method on the library which sets up the connection object. Then do a get version. If it works we've got good connection settings.
	 * @return
	 */
	private boolean initializeConnection() {
		lblStatusLabel.setText("Connecting");
    	resultsPanelNoticesBox.setText("");
    	
    	ic.initialize(iIPAddress, iPort, iUserName, iPassword); // Initialize the interface to the BigIP

		String version = null;
		try {
			//TODO: There has to be a better way to do this
			version = ic.getSystemSystemInfo().get_version();
			Pattern versionPrefix = Pattern.compile("BIG-IP_v");
			Matcher versionMatcher = versionPrefix.matcher(version);
			bigIPVersion = versionMatcher.replaceAll(""); //Kinda redundant, I know.

		} catch (RemoteException e1) {	
			//TODO: Add a return type so I can do a return here.
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e1, this, log);
			exceptionHandler.processException();
		} catch (ServiceException e1) {
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e1, this, log);
			exceptionHandler.processException();
		} catch (Exception e1) {
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e1, this, log);
			exceptionHandler.processException();
		}
		
		//TODO: Move this part, the setting of the progress bar and displaying of connection validity out of this section and maybe back into the listener. We should only be returning true/false here.
		if (version != null) {
        	log.debug("My Big-IP is version:" + bigIPVersion);
        	if ( bigIPVersion.startsWith("10") ) {
        		log.info("v" + bigIPVersion + ", success");
        		resultsPanelNoticesBox.setText("Connected to BIG-IP version: " + bigIPVersion);
        		connectionInitialized = true;
        		return true; //It worked
        	} else if (bigIPVersion.startsWith("11")) {
        		log.info("v" + bigIPVersion + ", success");
        		resultsPanelNoticesBox.setText("Connected to BIG-IP version: " + bigIPVersion);
        		connectionInitialized = true;
        		return true; //It worked;
        	} else if (bigIPVersion.startsWith("9")) {
        		log.info("v" + bigIPVersion + ", unclear");
        		resultsPanelNoticesBox.setText("njord is currently untested on BIG-IP version 9 systems. Proceed at your own risk.");
        		connectionInitialized = true;
        		return true; 
        	} else {
        		log.info("v" + bigIPVersion + ", fail");
        		resultsPanelNoticesBox.setText("BIG-IP version: " + bigIPVersion + "Is unsupported. You must connect to a BIG-IP version 9.4 or higher.");
        		connectionInitialized = false;
        		return false;
        	}
        	
		} else {
			// scream, run and cry
			//TODO: Check the return code of the exception to see what the cause of failure is.
			log.error("Connection settings invalid");
			connectionInitialized = false;
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
		log.debug("Available Virtuals");
		for (String string : virtual_list) {
			log.debug("   " + string);
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
		log.debug("Available Rules");
		for (String string : rule_list) {
			log.debug("   " + string);
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
			log.debug("Available Rules");
			for (LocalLBRuleRuleDefinition rule : iRules) {
				log.debug("   " + rule); //wonder if LocalLBRuleRuleDefinition has a toString() it should.
			}
		} catch (RemoteException e) {
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e, this, log);
			exceptionHandler.processException();
			//Not:
			//e.printStackTrace();
		} catch (Exception e) {
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e, this, log);
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
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e, this, log);
			exceptionHandler.processException();
			//Not:
			//e.printStackTrace();
		} catch (Exception e) {
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e, this, log);
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
    	// I will probably still set this here but it is used somwehere else.
		AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance();
		atmf.putMapping("SYNTAX_STYLE_IRULES", "com.f5.AaronForster.njord.util.iRulesTokenMaker");
		TokenMakerFactory.setDefaultInstance(atmf); //Don't know if I need this line or not
//		textArea.setSyntaxEditingStyle("SyntaxConstants.SYNTAX_STYLE_TCL");
    	
		//This text editor pane is no longer in use but I'm leaving it here for reference.
    	nTextEditorPane = new TextEditorPane();
//    	nTextEditorPane.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_TCL);
    	nTextEditorPane.setSyntaxEditingStyle("SYNTAX_STYLE_IRULES");
    	nTextEditorPane.setCodeFoldingEnabled(true);
    	nTextEditorPane.setAntiAliasingEnabled(true);
    	//This isn't going to work since eventually I'll have a separate text editor for ever rule in order to have a separate undo history for each iRule but it's a start.
    	nTextEditorPane.setName("editorPane");
    	
    	
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
    	
    	//TODO: change the name of this scrollpane to something more intelligent.
    	codeEditorScrollPane = new JScrollPane();
//    	codeEditorScrollPane.setViewportView(rSyntaxTextArea);
    	//Let's try this with TextEditorPane;
    	codeEditorScrollPane.setViewportView(nTextEditorPane);
    	GridBagConstraints gbc_codeEditorScrollPane = new GridBagConstraints();
    	gbc_codeEditorScrollPane.fill = GridBagConstraints.BOTH;
    	gbc_codeEditorScrollPane.gridwidth = 8;
    	gbc_codeEditorScrollPane.gridheight = 8;
    	gbc_codeEditorScrollPane.anchor = GridBagConstraints.NORTHWEST;
    	gbc_codeEditorScrollPane.insets = new Insets(0, 0, 0, 5);
    	gbc_codeEditorScrollPane.gridx = 1;
    	gbc_codeEditorScrollPane.gridy = 0;
    	editorPanel.add(codeEditorScrollPane, gbc_codeEditorScrollPane);

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
                        .addComponent(codeEditorScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE)
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
                    .addComponent(codeEditorScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
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


		// These are some sample completions. They are java but this can be used to do code templates for iRules
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
		
//		Need to populate the list here
		if ( iRuleList.isEmpty() ) {
			try {
				iRuleList = new ArrayList<String>(Arrays.asList(getiRuleList()));
			} catch (Exception e) {
				f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e, this, log);
				exceptionHandler.processException();
			}
		}
		
		top = new DefaultMutableTreeNode("BigIP");
		
		//Started this here because I was going to create a 'config' section and get the bigip.conf but w/ the "No iRules functionality it isn't Required. Putting this feature off to a later version.
//		DefaultMutableTreeNode configCategory = new DefaultMutableTreeNode("Config");
//	    top.add(configCategory);
		
		createNodes(top);

		tree = new f5JavaGuiTree(top);
		ToolTipManager.sharedInstance().registerComponent(tree);
		tree.setCellRenderer(new njordTreeRenderer());
		
	    tree.getSelectionModel().setSelectionMode
	            (TreeSelectionModel.SINGLE_TREE_SELECTION);
	    
	    //Listen for when the selection changes.
	    tree.addTreeSelectionListener(this);
	    //Listen for when non-leave nodes are expanded
	    tree.addTreeExpansionListener(this);

	    NavPanel.add(tree);
	    NavPanel.updateUI(); // Forces NavPanel to re-evaluate it's contents
	    btnNewiRule.setEnabled(true); //Now that we have iRules we can create a new one.
	}
	
	/**
	 * Sets the contents of the notices box. For sub classes to be able to send back a message to us to set.
	 * @param message
	 */
	public void setNoticesText(String message) {
		resultsPanelNoticesBox.setText(message);	
	}
	
	/**
	 * Creates a list of editors for all of the iRules in iRulesList and returns it
	 */
	public List buildiRuleListOfEditors(List<String> iRules) {
		//I don't think I'm going to end up using this method.
		List<NjordiRuleDefinition> njordRules = new ArrayList<NjordiRuleDefinition>();
		
	    for (String thisRule : iRules ) {
	    	if ( thisRule.matches("/Common/_sys_.*") ) { // V11 syntax
	    		log.debug("Sys iRule, Skipping");
	    		continue;
	    	} else if ( thisRule.matches("_sys_.*") ) { // V10 and maybe V9
	    		log.debug("Sys iRule, Skipping");
	    		continue;
	    	}
	    	
	    	NjordFileLocation ruleLocation = new NjordFileLocation(this, thisRule, ic);
	    	TextEditorPane ruleEditor = null;
			try {
				ruleEditor = new TextEditorPane(0, true, ruleLocation);
			} catch (IOException e) {
				f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e);
				exceptionHandler.processException();
			}
	    	
			ruleEditor.setName(thisRule);
			ruleEditor.discardAllEdits(); //This prevents undo from 'undoing' the loading of the editor with an iRule
			ruleEditor.getDocument().addDocumentListener(new NjordDocumentListener(this));
			
			//Syntax Highlighting items
			ruleEditor.setSyntaxEditingStyle("SYNTAX_STYLE_IRULES");
			ruleEditor.setCodeFoldingEnabled(true);
			ruleEditor.setAntiAliasingEnabled(true);

	    	
	    	//TODO: Update the color scheme. Figure out what items I will be using Function, reserved_word, etc for what components and how they should be colorized.
	    	SyntaxScheme scheme = ruleEditor.getSyntaxScheme();
//	        scheme.getStyle(Token.RESERVED_WORD).foreground = Color.pink;
	        scheme.getStyle(Token.FUNCTION).foreground = Color.MAGENTA;
	        Color operatorColor = new Color(880080);
//	        scheme.getStyle(Token.OPERATOR).foreground = Color.GREEN;
	        scheme.getStyle(Token.OPERATOR).foreground = operatorColor;
	        
//	        		purple(16 SVG) #800080
			
//			textField.getDocument().addDocumentListener(new MyDocumentListener());
	        NjordiRuleDefinition thisRuleObject = new NjordiRuleDefinition(ruleEditor);
	        njordRules.add(thisRuleObject);
	        
	    }
	    return njordRules;
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
			log.debug("Settings file exists");
		}

		// Get the preferences from a file
		Properties properties = new Properties();
		try {
		    properties.load(new FileInputStream(propsFile));
		} catch (IOException e) {
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e, this, log);
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
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e, this, log);
			exceptionHandler.processException();
		}
		return true;
	}
}
