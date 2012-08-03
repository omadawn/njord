package com.f5.AaronForster.njord;

import iControl.LocalLBRuleRuleDefinition;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
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
 */

/*
 * NOTES SECTION
 * //Hrmm... interesting thought. If I make it too fancy it can compete w/ EM. Maybe not connect to multiple bigips? No, let's connect to multiple but definately make it plugin oriented and if it comes up I can pitch a free and commercial version.
 * 
 * 
 * TODO SECTION
 * TODO: Catch and report a syntax error from bigip. Also report success/fail of save.
 * TODO: How about this. Instead of creating the text editor when we click on a rule create it when the app starts the 
 * 			way the dialog box is created. Then load it into the frame and stick the iRule code into it when a rule is selected.
 * 			Then when a rule is edited I can stick it's current contents back into the njordiRule object every time I edit it maybe?
 * 			Then write it to the BigIP when I save it.
 * 		OR another alternative is to create a njord editor for every iRule I click on. Then stick that active editor into the frame when 
 * 		I click on one. How do I keep track of which is which though without creating an editor for every iRule?
 * TODO: Prioritise To Dos - pretty much done, terminal work in progress.
 * TODO: Start logging instead of printing to stdout - started this one.
 * TODO: Add a save on action selected. I'll have to have a 'current iRule or something like that so that I always know what iRule is currently being edited and then update the njord irule object when I've clicked on something and update the editor with the contents of whatever iRule object is selected if you've selected something else.
 * TODO: Add a link to the iRules reference https://devcentral.f5.com/wiki/iRules.HomePage.ashx
 *
 * TODO: Oooh, switch to textEditor http://javadoc.fifesoft.com/rsyntaxtextarea/org/fife/ui/rsyntaxtextarea/TextEditorPane.html a sub class of rsyntaxTextEditor (and part of that project) which has built in support for loading/saving and also tracks things like is it dirty/etc.
 * TODO: Add make EOL visible and probably make whitespace visible setEOLMarkersVisible(boolean visible) 	setWhitespaceVisible(boolean visible) 
 * TODO: add a preference to paint tab lines. See if this setPaintTabLines(boolean paint)  does what I think it does.
 * TODO: The below has changed slightly and I might even ditch the connect button in the main window.
 * TODO: Catch connect from main window 
 * 		TODO: Figure out why the connect button in the middle of the screen doesn't work but the menu item one does. It's due to the action listener issue
 * TODO: Add a 'connection verified' variable that I can check at stages.
 * TODO: Don't populate the iRules tree until it has been expanded.
 * 		TODO: Should I have a list iRules button or only do it when I expand the tree. Let's only do it on expand for now.  
 * TODO: Figure out if I want iControl Assembly or JGruber's method. Using assembly for now for safety
 * TODO: Get rid of the 'edit iRule' button and add a 'new iRule' button instead
 * TODO: Figure out how to handle a 'New iRule' Do I need an 'offline iRules' section or can I just label them differently in the tree
 * TODO: Add keyword highlighting for F5 specific keywords. See if I can get these automatically somewhere, maybe I can download them from Devcentral if I have to
 * 		TODO: on that point I should probably only download them via my own build process so that the code is up to date but that the client one doesn't have to connect to devcentral all the time.
 * 		Some resources for this. http://fifesoft.com/forum/viewtopic.php?f=10&t=268&sid=fe6ab2cd301cc56e2bde4e9b3088b69d
 * 					Quote:
 * 						Just a quick hint for people with the need for simple syntax colouring that does not need the complexity of a full parser.
 *
 *						Start with the *TokenMaker.java implementations that are not created by JFlex. WindowsBatchTokenMaker is a very simple one, UnixShellTokenMaker is slightly more advanced. It is very easy to start from there and "roll your own". Just change the list of reserved words and change the comment handling, and - hey presto! - here's your own syntax colouring scheme."
 * TODO: Maybe later in $userHome/.f5/njord/preferences.settings if that exists it overrides the one in apphome? I will need to store individual bigip connection info there.  
 * 			TODO: Perhaps a preferences file in $appHome/preferences.settings could hold generic items
 * TODO: Get rid of the connect/disconnect verbage. It's a web UI and there's really no 'connected' ever. Have a 'verify connection' button instead.
 * TODO: Start writing edited settings back out to a file
 * TODO:  Perhaps I will place the cursor in the name box and have to implement the tree editor or perhaps I will be a wimp and use a dialog box. Maybe that's why iruler does it that way.
 * TODO: Figure out if I can have the bigip do a syntax check without saving to bigip.
 * TODO: For way future I can read the statistics info on an irule. Before enabling (maybe a "profile button?" Check and make sure the rule actually has statistics enabled.
 * TODO: Add a reload from bigip/reload from disk button. Using whichever is correct.
 * 
 * TODO: go through all the 'Auto-generated catch block' sections
 * TODO: It seems like now adays the app stays running even after closing w/ x in corner. Make it quit when you stop running it. It stopped doing this but I will watch it
 * TODO: Move some code out of the actions in the actionListener and treeSelectionListeners and into subroutines.
 * 
 * TODO: Add autocomplete http://fifesoft.com/rsyntaxtextarea/examples/example5.php - this item may move down from here
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
 * TODO: Figure out what 'marked occurrences' is IE getMarkedOccurrences() 
 * TODO: Add an option to select what version of iRules to use highlighting on. I'll need some fancy way to crawl the iRules reference pages or some better reference from corporate.
 * 
 * 
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
 * Njord will begin it's life solely as an iRule editor but I'd like it to become a full on bigip manager thus the name.
 * 
 * @author Aaron Forster @date 20120601
 * @version 0.5
 */
public class MainGuiWindow implements ActionListener, TreeSelectionListener, TreeExpansionListener {
	private String logPrefix = "MainGuiWindow: ";
	private JFrame frame;
	private String newline = "\n";

	JTextArea output;
    JScrollPane scrollPane;
    
    JTextPane defaultResultsPanelTextPane = new JTextPane();
    JLabel lblStatusLabel = new JLabel();
    PreferencesDialog preferencesDialog;
    
	//Connection information stuff. Ultimately we get this from the preferences file
	String iIPAddress;
	long iPort;
	String iUserName;
	String iPassword;
	
    // Let's not initialize it yet.
	iControl.Interfaces ic = new iControl.Interfaces(); //From Assembly
	
	//iControl.BigIP from the maven wsdl thingy is reasonably equivalent to iControl.Interfaces from the iControl 
	//	'Assembly' available on DevCentral.
    
	// Create a new TCL Token Maker so I can add some keywords to it.
	TclTokenMaker tokenMaker = null;
	
	//For the progress bar
	JProgressBar progressBar;
	Timer timer;
	
	//for the text editor to work.
	JPanel resultsPanel = null;
	JSplitPane splitPane = new JSplitPane();
	JEditorPane iRuleEditorPane = null;
	RSyntaxTextArea rSyntaxTextArea = null;
	TextEditorPane nTextEditorPane = null;
	JMenuBar menuBar = null;
	JPanel EditorPanel = null;
	
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
		
		// Add our own keywords to the TCL Token Maker This doesn't work yet
        addF5ReservedWords();

		// Preferences Dialog stuff
		preferencesDialog = new PreferencesDialog(frame, this);
		preferencesDialog.pack();
		
		// Create the editor panel. We will use it later
		EditorPanel = createEditorPanel();
		
		// The menu definition	
		menuBar = new JMenuBar();
		frame.getContentPane().add(menuBar, BorderLayout.NORTH);
		
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
		
		JMenu mnActMenu = new JMenu("Actions");
		menuBar.add(mnActMenu);		
		
		JMenuItem actMnGetVirtuals = new JMenuItem("List Virtual Servers");
		actMnGetVirtuals.addActionListener(this); // This adds an action listener so if you click this it will trigger actionListener() method defined below
		mnActMenu.add(actMnGetVirtuals);
		
		JMenuItem actMnGetiRules = new JMenuItem("List iRules");
		actMnGetiRules.addActionListener(this); // This adds an action listener so if you click this it will trigger actionListener() method defined below
		mnActMenu.add(actMnGetiRules);
		
		JMenuItem actMnEditRule = new JMenuItem("New Rule");
		actMnEditRule.addActionListener(this); // This adds an action listener so if you click this it will trigger actionListener() method defined below
		mnActMenu.add(actMnEditRule);

		JMenuItem actMnSave = new JMenuItem("Save");
		actMnSave.addActionListener(this); // This adds an action listener so if you click this it will trigger actionListener() method defined below
		mnActMenu.add(actMnSave);
			
		// The rest of the gui def
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);
		
		JPanel NavPanel = new JPanel();
		Dimension navMinimumSize = new Dimension(200, 30);
		NavPanel.setMinimumSize(navMinimumSize);
		splitPane.setLeftComponent(NavPanel);
		
		JScrollPane scrollPane = new JScrollPane();
		NavPanel.add(scrollPane);
		
		Component horizontalStrut = Box.createHorizontalStrut(54);
		NavPanel.add(horizontalStrut);
	
		//I can get rid of this here once I make it so we don't automatically populate the iRules.
		initializeConnection();
		//TODO: Figure out how to only display the list once I've clicked a 'display' button.
		//TODO: I'm pretty sure this is causing an infinite loop. Review code
		//TODO: Build this from a preferences file.
		top = new DefaultMutableTreeNode("BigIP");
		createNodes(top);
//		tree = new JTree(top); // Now that TOP exists let's initialize the tree.

		tree = new f5JavaGuiTree(top);
//	    category = new DefaultMutableTreeNode("iRules");
//	    tree.add(category);
	
		//This was the wrong way to go about it. TreeCellRenderer and TreeCellEditor are actually the editor and renderer for the tree itself. IE You can edit IN THE tree. It doesn't effect what happens in some other panel.
//		tree.setCellEditor(new JavaiRuleEditorTreeCellEditor());
//        tree.setEditable(true);

//		JTree tree = new JTree();
		NavPanel.add(tree);
		
		//Where the tree is initialized:
	    tree.getSelectionModel().setSelectionMode
	            (TreeSelectionModel.SINGLE_TREE_SELECTION);

	    //Listen for when the selection changes.
	    tree.addTreeSelectionListener(this);
	    //Listen for when non-leave nodes are expanded
	    tree.addTreeExpansionListener(this);
		
		
		//DEFAULT RESULTS PANE CONTENT STARTS HERE

		
		resultsPanel = new JPanel();

		Dimension resultsPanelMinimumSize = new Dimension(500, 700);
		resultsPanel.setMinimumSize(resultsPanelMinimumSize);
		
		JTextPane notificationsPanel = new JTextPane();
//		Dimension notificationsPanelMaximumSize = new Dimension(50, 300);
//		notificationsPanel.setMaximumSize(notificationsPanelMaximumSize);
		
//		JSplitPane ResultsPanelSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, resultsPanel, notificationsPanel);
//		frame.getContentPane().add(ResultsPanelSplitPane, BorderLayout.EAST);
//		splitPane.setRightComponent(ResultsPanelSplitPane);
		splitPane.setRightComponent(resultsPanel);
		
		JLabel lblDefaultResultspanelContent = new JLabel("Default resultsPanel Content");
		lblDefaultResultspanelContent.setVerticalAlignment(SwingConstants.TOP);
		
		JTextPane defaultResultsDevCentralURLTextPane = new JTextPane();
		defaultResultsDevCentralURLTextPane.setText("This will contain a link to dev central or something");
		defaultResultsPanelTextPane.setText("Should have some welcome text here that perhaps includes a little help message and maybe some copywrite information. I would prefer if it was pinned to the bottom of the containing pane.");
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		
		JPanel panel = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel.getLayout();
		
		JButton defaultTextPaneConnectButton = new JButton("Connect");
		GroupLayout gl_resultsPanel = new GroupLayout(resultsPanel);
		gl_resultsPanel.setHorizontalGroup(
			gl_resultsPanel.createParallelGroup(Alignment.LEADING)
				.addComponent(horizontalStrut_1, GroupLayout.PREFERRED_SIZE, 0, GroupLayout.PREFERRED_SIZE)
				.addGroup(gl_resultsPanel.createSequentialGroup()
					.addGap(475)
					.addComponent(lblDefaultResultspanelContent))
				.addGroup(gl_resultsPanel.createSequentialGroup()
					.addGap(173)
					.addComponent(defaultResultsDevCentralURLTextPane, GroupLayout.PREFERRED_SIZE, 743, GroupLayout.PREFERRED_SIZE))
				.addGroup(gl_resultsPanel.createSequentialGroup()
					.addGap(173)
					.addComponent(defaultResultsPanelTextPane, GroupLayout.PREFERRED_SIZE, 743, GroupLayout.PREFERRED_SIZE))
				.addGroup(gl_resultsPanel.createSequentialGroup()
					.addGap(173)
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(gl_resultsPanel.createSequentialGroup()
					.addGap(508)
					.addComponent(defaultTextPaneConnectButton))
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
					.addComponent(defaultTextPaneConnectButton))
		);
		resultsPanel.setLayout(gl_resultsPanel);
		
		//DEFAULT RESULTS PANE CONTENT ENDS HERE
		
		
		JToolBar toolBar = new JToolBar();
		frame.getContentPane().add(toolBar, BorderLayout.SOUTH);
		
		// Make this update between connect and disconnect
		JButton connectToBigIPButton = new JButton("Connect");
		toolBar.add(connectToBigIPButton);
		
		progressBar = new JProgressBar();
		toolBar.add(progressBar);
		
		//TODO: make this value be the same source as counter below and wherever the timout reads it from.
//        progressBar.setValue(20); 
		// This turns out to be a percentage. For progress bar flow we probably want to start it at zero, jump it to ten when we hit 'connect' and then move it up instead of down as the timer progresses then jump it to 100% once connected.
        progressBar.setValue(0);

//		JLabel lblStatusLabel = new JLabel("Status Info ");
		lblStatusLabel.setText("Status Info");
		toolBar.add(lblStatusLabel);
		
	}
	
	/**
	 * Build the nodes of the navigation tree. Takes the tree to add the nodes to as an argument.
	 * @param tree
	 */
	private void createNodes(DefaultMutableTreeNode tree) {
	    DefaultMutableTreeNode category = null;
	    DefaultMutableTreeNode addRule = null;
	    
	    category = new DefaultMutableTreeNode("iRules");
	    tree.add(category);
	    
	    //TODO: move the try/catch block to getiRulesList()?
	    //TODO: Create a generic getSomethingList?
	    //TODO: Figure out how to handle the long path of the iRule's full name. IE do I pull off the path and just show the name, do I have expandable folders for the folders in the path? I like that idea but only if I automatically have 'common' be expanded.
	    //TODO: If I do the above I will need a way to remember what folders have been openened if the client does so.
	    
	    NjordiRuleDefinition[] myRules = null;
//	    LocalLBRuleRuleDefinition[] myRules = null;
		try {
			myRules = getNjordiRules();
		} catch (Exception e) {
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e);
			exceptionHandler.processException();
		}
//	    for (LocalLBRuleRuleDefinition rule : myRules){		
	    for (NjordiRuleDefinition rule : myRules){
	    	//TODO: Figure out how to add the actual RULE but have it display the rule name instead of the default results from rule.toString() which produces output not fit for humans.
	    	addRule = new DefaultMutableTreeNode(rule);
	    	    category.add(addRule);
	    }
	}
	
	//This is the tree selection listener method.
	public void valueChanged(TreeSelectionEvent e) {
		//TODO: Create an editor with the contents of the rule.
		//TODO: Figure out how to not change unless it's a double click. And/Or write the contents of the editor into a temp cache so that we don't loose it when we switch iRules. Then ask to save them when you close or something.
		String s;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                           tree.getLastSelectedPathComponent();

        if (node == null) return;

//        Object nodeInfo = node.getUserObject();
        //TODO: Use the above syntax to load nodeInfo as a generic type Object then figure out what type it is and act accordingly.
//        LocalLBRuleRuleDefinition nodeInfo = (LocalLBRuleRuleDefinition) node.getUserObject();
        NjordiRuleDefinition nodeInfo = (NjordiRuleDefinition) node.getUserObject();
//        NjordiRuleDefinition nodeInfo = (NjordiRuleDefinition) node.getUserObject();
        
        // Hrm.... ok to use I might be getting closer. 
        // So it seems like maybe I have to create a token maker to add the tokens, then create a syntax scheme and associate the tokens with that somehow. I thought the scheme just defined what colors things like 'reserved words' used but somehoe it has to identify what a reserved word is.
        // 		I am unclear about how.
        // Maybe there is some more documentation on how in the docs for TokenMaker, the APP http://fifesoft.com/blog/?cat=7
//        rSyntaxTextArea.setSyntaxScheme(scheme);
        
        
        // This might be part of how to do the hyperlinks so I can send you to devcentral
//        addHyperlinkListener(javax.swing.event.HyperlinkListener l)

        // A way to add a parser to validate the code. I don't know how to work this out since we have the bigip do it.
        //addParser(Parser parser) 
        
        // We do this the whole nodeInfo way because we have to check and see if it's a leaf node or not.
        if (node.isLeaf()) {
        	log.info("Rule " + nodeInfo + " Selected");
        	s = "iRule Selection Event Detected";
    		
//    		JMenuItem actMnSave = new JMenuItem("Save");
//    		actMnSave.addActionListener(this); // This adds an action listener so if you click this it will trigger actionListener() method defined below
//    		menuBar.add(actMnSave);
    		
        	//Try this for layout
        	
        	
//        	rSyntaxTextArea.setText(nodeInfo.getRule_definition());
        	nTextEditorPane.setText(nodeInfo.getRule_definition());
            //TODO: Set isModified once this has been edited
            
            //TODO: Figure out howo tmake this remove whatever is currently there instead of statically coding 'resultsPanel' There will be a point when it's not 'resultsPanel' Quite often in fact. Like when it's now editPanelScrolePane1
            //I'm not sure if I need to do this but it seems like the right thing to do. Remove the existing contents of the right panel before setting a new one.
            splitPane.remove(resultsPanel);
//            splitPane.setRightComponent(editPaneScrollPane1);
//            splitPane.setRightComponent(editorPanel);
            splitPane.setRightComponent(EditorPanel);
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
        	int result = JOptionPane.showConfirmDialog(null, preferencesDialog.panel_1, 
        			"Connection Preferences", JOptionPane.OK_CANCEL_OPTION);
        	if (result == JOptionPane.OK_OPTION) {
		        iIPAddress = preferencesDialog.ConnPreffsHostTextField.getText();
		        iPort = Integer.parseInt( preferencesDialog.ConnPreffsPortTextField.getText());
		        iUserName = preferencesDialog.ConnPreffsUserTextField.getText();
		        iPassword = preferencesDialog.ConnPreffsPasswordTextField.getText();
		        log.debug("Host: " + iIPAddress);
		        log.debug("Port: " + iPort);
		        log.debug("User: " + iUserName);
		        log.debug("Pass: " + iPassword);
		        //TODO: Set connected state to false if connection settings have been edited.
	        }
        } else if (actionCommand == "Connect") {
        	log.debug("Connect Event Detected.");
        	//TODO: Figure out why setting progressBar and lblStatusLabel doesn't work here but works below.
        	progressBar.setValue(50);
        	lblStatusLabel.setText("Connecting");
        	
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
    		} else {
    			// scream, run and cry
    			progressBar.setValue(0);
    			log.error("Connection settings invalid");
    			lblStatusLabel.setText("Connection settings invalid");
    		}
        } else if (actionCommand == "List Virtual Servers"){
        	//TODO: Make 'List Virtual Servers' actually populate the jtree on the side
        	//TODO: Populate the gui with info and buttons which have actions for the virtual like and enable/disable (only one should be there at a time.)
        	//TODO: Then add some actions like enable/disable
        	log.debug("List Virtuals Detected");
    		try {
    			String[] meVirtuals = getVirtualsList();
    		} catch (Exception f) {
    			log.error("Whoa, caught an error: " + f);
    		}
        } else if (actionCommand == "List iRules"){
        	//TODO: Make 'List iRules' actually populate the jtree on the side
        	log.debug("List iRules Detected");
    		try {
    			String[] meRules = getiRuleList();
    		} catch (Exception g) {
    			log.error("Whoa, caught an error: " + g);
    		}
        } else if (actionCommand == "New iRule"){
        	//TODO: I'll have to figure out how to pass an argument to this action
        	//TODO: Also 'edit rule' is a flat lie. Right now it's just openning a blank text editor.
        	//TODO: For some reason this actually causes the jTree to get all screwed up. Make that not happen
        	//TODO: Make 'Edit Rule' Be the default action for a double click on an iRule in the list.
        	//TODO: Create a 'New Rule' function. This will probably go hand in hand with:
        	//TODO: Figure out how to handle renaming of iRules.
        	//TODO: Figure out how to handle 'offline iRules' IE one you have created but isn't uploaded yet.
        	log.debug("New Rule Detected");
        	
    		JPanel resultsPanel = new JPanel();
    		splitPane.setRightComponent(resultsPanel);

    		JScrollPane editPaneScrollPane1 = new javax.swing.JScrollPane();
            JEditorPane editPane = new javax.swing.JEditorPane();
    		
    		//Let's create an 'edit rule' action and replace the above with this:
            editPaneScrollPane1.setHorizontalScrollBar(null);

            //TODO: WTH is wrong w/ someone to make the background grey? Fix this.
            editPane.setBackground(new java.awt.Color(233, 228, 242));
            editPane.setMargin(new java.awt.Insets(3, 20, 3, 20));
            editPaneScrollPane1.setViewportView(editPane);

            //TODO: Figure out howo tmake this remove whatever is currently there instead of statically coding 'resultsPanel' There will be a point when it's not 'resultsPanel' Quite often in fact. Like when it's now editPanelScrolePane1
            //I'm not sure if I need to do this but it seems like the right thing to do. Remove the existing contents of the right panel before setting a new one.
            splitPane.remove(resultsPanel);
            splitPane.setRightComponent(editPaneScrollPane1);
        } else if (actionCommand == "Save"){
        	//TODO: Apparently blank is a valid iRule as far as mcpd is concerned. Check to ensure the rule isn't completely blank and handle that.
        	//This will tell me what iRule is selected. Then I need to set the definition. tree.getLastSelectedPathComponent()
        	log.info("Save detected");
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                    tree.getLastSelectedPathComponent();
            if (node == null) return;
//            LocalLBRuleRuleDefinition nodeInfo = (LocalLBRuleRuleDefinition) node.getUserObject();
            NjordiRuleDefinition nodeInfo = (NjordiRuleDefinition) node.getUserObject();
//            nodeInfo.setRule_definition(rSyntaxTextArea.getText()); // Stick the text from the text area back into the iRule object
//            nodeInfo.setRule_definition(rSyntaxTextArea.getText()); // Stick the text from the text area back into the iRule object
            //TODO: Get rid of this. Save the data to the NjordiRuleDefinition somwehere else like when the rule looses focus or when they stop typing or every 3 seconds or something. Then just use that. instead of the contents of the editor? no maybe the contents of the editor will always be the MOST up to date. Hrm....
            nodeInfo.setRule_definition(nTextEditorPane.getText());
            LocalLBRuleRuleDefinition[] saveRules = new LocalLBRuleRuleDefinition[1]; // Create a list of iRules in order to write them back. We only have one so we only need a tiny list
            saveRules[0] = nodeInfo.getiRule(); // Stick the iRule object into said list
            //TODO: Decide if I should modify the above to use the njordIruleObjects or not
            try {
				ic.getLocalLBRule().modify_rule(saveRules); // Then write said rule back to the BigIP This could be easily modified to allow the saving of multiple rules.
			} catch (RemoteException e1) {
//				If we caught a remote exception the code was wrong. Let's try something here
				//Can't break this up on new lines I need all the new lines after error_string    : 
				// Let's try making the grep multi line.
				String errorContents = e1.getMessage(); //This gets the full message	

				Pattern pattern = Pattern.compile(".*error_string         :", Pattern.DOTALL);
				Matcher matcher = pattern.matcher(errorContents);
				//Uncomment if working on the regex. The commented code shows what we are matching.
//				while (matcher.find()) {
//					System.out.print("Start index: " + matcher.start());
//					System.out.print(" End index: " + matcher.end() + " ");
//					System.out.println(matcher.group());
//				}
				//TODO: Replace this println with something that either pops up an error or sets the contents of a status box in the main gui. I prefer the latter.
				String errorMessage = matcher.replaceAll("");
				System.out.println("Error: " + errorMessage);
				
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
            
        } else {
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
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e1);
			exceptionHandler.processException();
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
	
	
	public void addF5ReservedWords() {

		
		tokenMaker = new TclTokenMaker();
		
		String reservedWord = "thisword";
        int len = reservedWord.length();
        char[] charArray = new char[len];
//        char[] charArray = new char[len];
        
        // put original string in an 
        // array of chars
        for (int i = 0; i < len; i++) {
            charArray[i] = 
            		reservedWord.charAt(i);
        } 
        
        String charArrayMessage = "Char array is holding :";
        for (int i =0; i < len; i++) {
            charArrayMessage = charArrayMessage + " [" + charArray[i] + "] ";      	
        }
  
		log.debug(charArrayMessage);
		
		//I _think_ I get it start and end are within the chararray itself so I could have all the words in a single array. To save mem I guess.
		
		tokenMaker.addToken(charArray, 0, len, Token.RESERVED_WORD, 0);
		
//		tokenMaker.addToken(arg0, arg1, arg2, arg3, arg4)
		// Flex syntax for adding a reserved word
//		"case"						{ addToken(Token.RESERVED_WORD); }
		
//		more detail on this
		/*
		 * Adds the token specified to the current linked list of tokens.
		 *
		 * @param array The character array.
		 * @param start The starting offset in the array.
		 * @param end The ending offset in the array.
		 * @param tokenType The token's type.
		 * @param startOffset The offset in the document at which this token
		 *                    occurs.
		 */
		// Man! what does this mean? What are the ints for?
//		addToken(char[] array, int start, int end, int tokenType, int startOffset)
//		   Adds the token specified to the current linked list of tokens.
		// ok ok, um try three let's see if I can extend the java tokenmaker
//		addHighlightedIdentifier("thisword", Token.RESERVED_WORD);
		
		
		
		//Crap, kept going in the thread this feature was never implemented like this. gah!		
		//These are the methds that apply to adding 'Highlight Identifiers'
//        public void addHighlightedIdentifier(String word, int tokenType);
//        public boolean removeHighlightedIdentifier(String word);
//        public void clearAddedHighlightedIdentifiers();
		//TODO: Decide if I really should be doing this or just return void
//		return true;
	}
	
	private JPanel createEditorPanel() {
		//TODO: Instead of creating a new text editor every time we select an iRule I should perhaps create one earlier or do a use if exists, create if doesn't. Then when someone clicks on an iRule I can just populate the text editor with the contents of said iRule. Perhaps even save the contents of said text editor and then populate it with the new iRule.
    	JPanel editorPanel = new JPanel();
    	GridBagLayout gbl_editorPanel = new GridBagLayout();
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
    	
    	
    	gbl_editorPanel.columnWidths = new int[] {100, 100, 100, 100, 100, 100, 100, 100, 100, 100};
    	gbl_editorPanel.rowHeights = new int[] {100, 100, 100, 100, 100, 100};
    	gbl_editorPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    	gbl_editorPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
//    	editorPanel.setLayout(gbl_editorPanel);
    	
    	
		AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance();
		atmf.putMapping("SYNTAX_STYLE_IRULES", "com.f5.AaronForster.njord.util.iRulesTokenMaker");
		TokenMakerFactory.setDefaultInstance(atmf); //Don't know if I need this line or not
//		textArea.setSyntaxEditingStyle("SyntaxConstants.SYNTAX_STYLE_TCL");
    	
    	nTextEditorPane = new TextEditorPane();
//    	nTextEditorPane.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_TCL);
    	//This didn't work because setting it above doesn't work quite that way.
    	nTextEditorPane.setSyntaxEditingStyle("SYNTAX_STYLE_IRULES");
    	nTextEditorPane.setCodeFoldingEnabled(true);
    	nTextEditorPane.setAntiAliasingEnabled(true);
    	
//    	nTextEditorPane.setSize(textPaneDimension);
    	
    	SyntaxScheme scheme = nTextEditorPane.getSyntaxScheme();
//        scheme.getStyle(Token.RESERVED_WORD).foreground = Color.pink;
        scheme.getStyle(Token.FUNCTION).foreground = Color.MAGENTA;
    	
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
    	gbc_btnSave.anchor = GridBagConstraints.NORTHWEST;
    	gbc_btnSave.gridx = 2;
    	gbc_btnSave.gridy = 8;
    	btnSave.addActionListener(this);
    	editorPanel.add(btnSave, gbc_btnSave);
		

    	GroupLayout gl_EditorPanel = new GroupLayout(editorPanel);
    	gl_EditorPanel.setHorizontalGroup(
    			gl_EditorPanel.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(gl_EditorPanel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_EditorPanel.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(scrollPane_1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE)
                        .addGroup(gl_EditorPanel.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
//                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, gl_EditorPanel.createSequentialGroup()
//                                .addComponent(wwwButton)
//                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
//                                .addComponent(urlTextField))
//                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, gl_EditorPanel.createSequentialGroup()
//                                .addComponent(copyButton)
//                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
//                                .addComponent(cutButton)
//                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
//                                .addComponent(pasteButton)
//                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
//                                .addComponent(selectAllButton)
//                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
//                                .addComponent(clearButton))))
                        		))
                    .addContainerGap())
            );
    	gl_EditorPanel.setVerticalGroup(
    			gl_EditorPanel.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, gl_EditorPanel.createSequentialGroup()
                    .addContainerGap()
//                    .addGroup(gl_EditorPanel.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
//                        .addComponent(nTextEditorPane)
//                        .addComponent(cutButton)
//                        .addComponent(pasteButton)
//                        .addComponent(selectAllButton)
//                        .addComponent(clearButton))
//                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
//                    .addGroup(gl_EditorPanel.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
//                        .addComponent(wwwButton)
//                        .addComponent(urlTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(scrollPane_1, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
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
    	
    	return editorPanel;
    	
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
			  Boolean success = (new File(settingsDirPath)).mkdirs();
			  if (success) {
			  log.info("Directories: " 
			   + settingsDirPath + " created");
			  }
		}
		
		//Same for each individual file.
		File propsFile = new File(settingsDirectory + "/settings.properties");
		
		if ( !propsFile.exists() ) {
			log.info("BigIPS file doesn't exist");
			boolean success = false;
			try {
				success = propsFile.createNewFile();
			    if (success) {
			    	log.info( propsFile + " created");
			    	FileWriter fstream = new FileWriter(propsFile);
			    	BufferedWriter out = new BufferedWriter(fstream);
			    	out.write("#Njord the java iRule editor settings");
			    	out.write("connection.host = v11ltm1.localdomain\n"); //TODO: replace this with our default settings
			    	out.write("connection.port = 443\n");
			    	out.write("connection.usernameame = admin\n");
			    	out.write("connection.password = admin\n");
			    	//Close the output stream
			    	out.close();
				}
			} catch (IOException e) {
				f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e);
				exceptionHandler.processException();
			}
		} else {
			log.info("BigIPS file exists");
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
    
}
