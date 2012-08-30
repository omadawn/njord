package com.f5.AaronForster.njord;

import iControl.LocalLBRuleRuleDefinition;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
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
import javax.swing.JFileChooser;
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
import javax.swing.event.TreeWillExpandListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.xml.rpc.ServiceException;

import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.ShorthandCompletion;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.FileLocation;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.TextEditorPane;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.f5.AaronForster.njord.ui.NjordTreeNode;
import com.f5.AaronForster.njord.ui.NjordTreeRenderer;
import com.f5.AaronForster.njord.ui.f5JavaGuiTree;
import com.f5.AaronForster.njord.util.NjordConstants;
import com.f5.AaronForster.njord.util.NjordDocumentListener;
import com.f5.AaronForster.njord.util.NjordFileLocation;
import com.f5.AaronForster.njord.util.f5ExceptionHandler;

// https://github.com/jgruber/iControlExamples/tree/master/src/main/java/com/f5se/examples

/*
 * NOTES SECTION
 * 
=======
 * Main iControl reference on DevCentral
 * https://devcentral.f5.com/wiki/iControl.GlobalLB.ashx
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
 * @version 0.8.6
 */
public class MainGuiWindow implements ActionListener, TreeSelectionListener, TreeExpansionListener, TreeWillExpandListener {
	/**
	 * Prepended to log messages to show what portion of code is currently logging.
	 */
	public String logPrefix = "MainGuiWindow: ";
	/**
	 * Holds the line separator string for the os we're running on.
	 */
	public String nl = System.getProperty("line.separator");
	/**
	 * Tells us if our connection to the BIGIP has been successfully initialized and validated.
	 */
	public boolean connectionInitialized = false;
	/**
	 * The version of Njord that we are.
	 */
	public String njordVersion = "0.8.6";
	/**
	 * Holds the version of TMOS running on the BIGIP that we have connected to.
	 */
	public String bigIPVersion = "unknown"; // Version number has multiple dots in it so I can't do it as a number type. I'm sure there's some other type I can use somwhere that I could do a >< compair against.
	
	//Initial default settings which may get overridden later.
	//Default connection information if the user doesn't have a preferences file
	/**
	 * The initial default IP address that will be populated in the connections dialog if a user preference file does not already exist. This is the default for a BIGIP.
	 */
	public String iIPAddress = "192.168.1.245";
	/**
	 * The initial default port number that will be populated in the connections dialog if a user preference file does not already exist. This is the default for a BIGIP.
	 */
	public long iPort = 443;
	/**
	 * The initial default administrative account that will be populated in the connections dialog if a user preference file does not already exist. This is the default for a BIGIP.
	 */
	public String iUserName = "admin";
	/**
	 * The initial default administrative password that will be populated in the connections dialog if a user preference file does not already exist. This is the default for a BIGIP.
	 */
	public String iPassword = "admin";
	
	/**
	 * This populates the 'splash screen' notices box on startup that a user sees.
	 */
	public String initialNotificationsBoxText = "Welcome to Njord the java iRules editor." + nl + nl + "Njord currently will only " +
			"edit LTM iRules. Apologies, this will be resolved soon." + nl + nl + "Please be forgiving as this is still a beta " + 
			"product." + nl + nl + "Use the Get iRules button to fetch your iRules from the server. Please note that Njord is not an " +
					"official F5 product it is just written by someone who happens to work there. It comes with no warranty or guarantee." +
					" Any comments or questions about Njord can be directed to it's author Aaron Forster a.forster@f5.com." + nl + nl + 
					"Connections settings will be saved to ${home}/.f5/njord/settings.properties you can change your settings with the " +
					"connections menu.";
	/**
	 * The initial text in the notices box. The notices box is where Njord Communicates back to the end user.
	 */
	public String initialNoticesTextboxText = "Welcome to Njord v" + njordVersion;
	
	//Environment items
	/**
	 * The user's default home directory path.
	 */
	public String userHome = null;
	
    /**
     * The icontrol interface object for interacting with a BIGIP.
     */
	public iControl.Interfaces ic = new iControl.Interfaces();
    
	//iRule Items.
	/**
	 * A temporary holder for the iRule that is currently selected and being edited.
	 */
	public TextEditorPane currentSelectedRule = null;
	/**
	 * A text list of the iRules found on the BIGIP.
	 */
	public List<String> iRuleList = new ArrayList<String>();	
	
	//ALL THE SWING ITEMS SHOULD BE HERE
	public JFrame frame;
	//private For the progress bar
	/**
	 * The progress bar at the bottom of the screen. This will be removed in future versions.
	 */
	public JProgressBar progressBar; // Get rid of this
	/**
	 * The primary menu bar which holds the connections menu and more.
	 */
	public JMenuBar menuBar = null;
	/**
	 * The swing component that represents the 'Edit Connection Settings' Menu.
	 */
	public JMenuItem mntmEditSettings = null;
	/**
	 * The Connect button with the connections menu.
	 */
	public JMenuItem mntmConnect = null;
	/**
	 * The action bar which holds buttons for common tasks. 'Get iRules,' 'Save,' etc.
	 */
	public JToolBar actionBar = new JToolBar(); // Will hold buttons for things like save
	/**
	 * Above the notifications text box is a link to DevCentral.
	 */
	public JTextPane defaultResultsDevCentralURLTextPane = null;
	/**
	 * A label which sits above the DevCentral URL link.
	 */
	public JLabel defaultResultsDevCentralURLLabel = null;
	/**
	 * The Notifications box holds messages from the developer to users of Njord.
	 */
	public JTextPane txtNotificationsTextBox = new JTextPane();
	/**
	 * The connection button on the initial spash screen.
	 */
	public JButton defaultTextPaneConnectButton = null;
	/**
	 * The notices text box is how Njord comminicates back to the end user.
	 */
	public JTextArea resultsPanelNoticesBox = new JTextArea();	//This is where we put messages
	/**
	 * The 'Get iRUles' button.
	 */
	public JButton btnGetiRules = null;
	/**
	 * The 'New iRule' button.
	 */
	public JButton btnNewiRule = null;
	/**
	 * The 'Save' button.
	 */
	public JButton btnActionSave = null;
	/**
	 * The 'Delete iRule' button.
	 */
	public JButton btnDeleteiRule = null;
	/**
	 * The 'Deploy to BIGIP' button.
	 */
	public JButton btnDeployiRule = null;
	
	//Navigation Tree items
	/**
	 * navigationTree is the JTree that holds the list of iRules and all that which we are going to work on.
	 */
	public f5JavaGuiTree navigationTree = null;
	/**
	 * The true top of the navigation tree which allows us to appear to have two separate trees without screwing up the gui.
	 */
	public NjordTreeNode top = null;
	/**
	 * Holds top of the portion of the JTree derived from the BIGIP.
	 */
	public NjordTreeNode remoteTree = null;
	/**
	 * Holds top of the portion of the JTree derived from local resources.
	 */
	public NjordTreeNode localTree = null;
	/**
	 * Holds the 'iRules' directory which contains remote iRules.
	 */
	public NjordTreeNode remoteiRulesCategory = null;
	/**
	 * Holds the 'iRules' directory which contains local iRules if there are any.
	 */
	public NjordTreeNode localiRulesCategory = null;
	/**
	 * This is a tree path which corresponds to the remote iRules directory. This is needed due to difficulties figuring out the syntax 
	 * to identify a given JTree node.
	 */
	public TreePath remoteiRulesPath = null;
	/**
	 * This is a tree path which corresponds to the local iRules directory. This is needed due to difficulties figuring out the syntax 
	 * to identify a given JTree node.
	 */
	public TreePath localiRulesPath = null;
	/**
	 * A JPanel which occupies the main LHS of the application. Currently only the navScrollPane will really ever get stuck in there but
	 * it gives us the ability to swap out some other navigation items without destroying the gui.
	 */
	public JPanel NavPanel = new JPanel();
	/**
	 * A JScrollPane which contains the navigation tree. This allows us to automatically add scroll bars if the conent of the navigation 
	 * tree becomes either longer or wider than the current size of the pane.
	 */
	public JScrollPane navScrollPane = null; //new JScrollPane();
	/**
	 * The primary user interaction pane for the appliation. Could contain the initial notifications pane or an editor of some sort.
	 */
	public JPanel resultsPanel = null;
	/**
	 * The primary split pane which gives us the LHS/RHS of the application.
	 */
	public JSplitPane splitPane = new JSplitPane();
	/**
	 * The results split pane is the primary RHS of the application which is split top to bottom giving us the primary results panel 
	 * above and the notices pane below.
	 */
	public JSplitPane resultsSPlitPane = new JSplitPane();
	/**
	 * A deprecated RSTA TextEditorPane from when we were only creating one and swapping out the text from within.
	 */
	public TextEditorPane nTextEditorPane = null;
	/**
	 * EditorPanel takes over the top of the resutlts split pane. It will contain a scrol panel which will be the actual contaner for our
	 * editor panes.
	 */
	public JPanel EditorPanel = null;
	/**
	 * A scroll panel which is where we will swap our editors in and out of to give us
	 * the ability to edit more than one iRule at a time without loosing our undo buffer, clean/dirty status or more.
	 */
	public JScrollPane codeEditorScrollPane = null;
	
	//Unclear what these are used for
	/**
	 * I have no idea what this is for.
	 */
	public JTextArea output;
	/**
	 * I have no idea what this is for.
	 */
	public JScrollPane scrollPane;
//	private JScrollPane navScrollPane = new JScrollPane();
	/**
	 * A small label in the bottom RHS of the application which should reflect our connected/disconnected status.
	 */
	public JLabel lblStatusLabel = new JLabel();
	
	//Dialog popups
	/**
	 * Will hold the Connections preferences dialog box. This should be renamed to reflect that it is for connections since there 
	 * will be other application preferences in the future.
	 */
	public PreferencesDialog preferencesDialog;
	/**
	 * The new irule dialog pop up window.
	 */
	public iRuleDialog newiRuleDialog;
	
	//## Editor colors
	//TODO: These should all be moved to the constants class.
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
	
	// Make Linkable items
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
	 * An hpen HTML tag.
	 */
	private static final String HTML = "<html>";
	/**
	 * The closing HTML tag.
	 */
	private static final String HTML_END = "</html>";

	//slf4j logger factory
	/**
	 * Holds our logger factory for SLF4J.
	 */
	final Logger log = LoggerFactory.getLogger(MainGuiWindow.class);
    //TODO: Go to http://www.slf4j.org/manual.html and see what sort of config file I need to use to make this work.
	
	/**
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

	/**
	 * The default constructor just runs initialize() which builds most of the GUI.
	 */
	public MainGuiWindow() {
		buildMainGUI();
	}

	/**
	 * Builds the bulk of the GUI and triggers some other important events.
	 */
	private void buildMainGUI() {
		log.info(logPrefix + "Starting Up");
		frame = new JFrame();
		frame.setBounds(100, 100, 1118, 710);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//TODO: Change this to DO_NOTHING_ON_CLOSE and define a window close operation that saves the window state before closing. Or perhaps detect if the window is resized or moved then save then.
		//DO_NOTHING_ON_CLOSE (defined in WindowConstants): Don't do anything; require the program to handle the operation in the windowClosing method of a registered WindowListener object. 
		
		//TODO: This returns true/false. Deal with it and bail if it's false.
		loadPreferences(); //Load our preferences from the preferences file or create the prefs file if it does not exist.

		// Setup the preference dialog ahead of time.
		preferencesDialog = new PreferencesDialog(frame, this);
		preferencesDialog.pack();
		
		// Setup the new iRule dialog ahead of time. 
		newiRuleDialog = new iRuleDialog(frame, this);
		newiRuleDialog.pack();
		 
		// Creates the editor panel not the actual textEditors themselves.
		EditorPanel = createEditorPanel();

		/*
		 * ############### Menu bar section
		 */
		// The menu definition	
		menuBar = new JMenuBar();
		menuBar.setName("menuBar");
		
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
		
		btnDeleteiRule = new JButton("Delete iRule");
		btnDeleteiRule.setEnabled(false);
		btnDeleteiRule.setName("DeleteiRuleButton");
		btnDeleteiRule.addActionListener(this);
		actionBar.add(btnDeleteiRule);
		
		btnDeployiRule = new JButton("Deploy to BIGIP");
		btnDeployiRule.setEnabled(false);
		btnDeployiRule.setName("DeployiRuleButton");
		btnDeployiRule.addActionListener(this);
		actionBar.add(btnDeployiRule);
		
		/*
		 * ############### End menu bar section.
		 */
		
		JToolBar toolBar = new JToolBar();  //TODO: Figure out what this is used for and rename it to something more descriptive
		JButton connectToBigIPButton = new JButton("Connect");
		toolBar.add(connectToBigIPButton);
		connectToBigIPButton.addActionListener(this);
		
		// NavPanel starts here  ********************************************************************
		Dimension navMinimumSize = new Dimension(200, 30);
		Dimension resultsPanelNoticesDimension = new Dimension(500,20);
		Dimension resultsPanelDimension = new Dimension(500,500);
		NavPanel.setMinimumSize(navMinimumSize);
		splitPane.setLeftComponent(NavPanel);
		// NavPanel ends here  ********************************************************************
		
		JLabel lblDefaultResultspanelContent = new JLabel("Welcome to Njord");
		lblDefaultResultspanelContent.setVerticalAlignment(SwingConstants.TOP);
		
		defaultResultsDevCentralURLLabel = new JLabel();
		defaultResultsDevCentralURLLabel.setName("DevCentralURLLabel");
		defaultResultsDevCentralURLLabel.setText("Stuck on an iRule? Visit Deventral's iRules Wiki");
		defaultResultsDevCentralURLLabel.setToolTipText("https://devcentral.f5.com/wiki/iRules.HomePage.ashx");
		
		if (isBrowsingSupported()) {
//			defaultResultsDevCentralURLLabel.setText(htmlIfy(linkIfy("Stuck on an iRule? Visit Deventral's iRules Wiki", "https://devcentral.f5.com/wiki/iRules.HomePage.ashx")));
//			defaultResultsDevCentralURLLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
//			defaultResultsDevCentralURLLabel.addMouseListener(new LinkMouseListener());
			makeLinkable(defaultResultsDevCentralURLLabel, new LinkMouseListener());
		} else {
			defaultResultsDevCentralURLLabel.setText(defaultResultsDevCentralURLLabel.getText() + ": " + defaultResultsDevCentralURLLabel.getToolTipText());
		}

		txtNotificationsTextBox.setEditable(false);
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
					.addGap(40, 300, 900)
//					.addComponent(defaultResultsDevCentralURLLabel, GroupLayout.PREFERRED_SIZE, 813, Short.MAX_VALUE)
					.addComponent(defaultResultsDevCentralURLLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE,  Short.MAX_VALUE)
					.addGap(40, 300, 900))
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
					.addComponent(defaultResultsDevCentralURLLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
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
		
		//I should get rid of this progress bar
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
		
		frame.getContentPane().setLayout(groupLayout);
		
		buildNavTree();
	}
	
	/**
	 * Build the nodes of the iRule navigation tree.
	 * 
	 * @param tree The tree under which to create the nodes.
	 */
	private void createNodes(NjordTreeNode thisTree) {
		log.debug(logPrefix + "Running createNodes");
	    NjordTreeNode addRule = null;
	    //TODO: Create a generic getSomethingList?
	    //TODO: Figure out how to handle the long path of the iRule's full name. IE do I pull off the path and just show the name, do I have expandable folders for the folders in the path? I like that idea but only if I automatically have 'common' be expanded.
	    //TODO: If I do the above I will need a way to remember what folders have been openened if the client does so.
	   //Here's my no valid iRules list bug. I need to check my connection settings first.
	    if ( iRuleList.isEmpty() ) {
	    	resultsPanelNoticesBox.setText("No valid iRules found on BIGIP.");
	    	TextEditorPane ruleEditor = getEditorForRule(null, "No iRules Found On BIGIP");
    		addRule = new NjordTreeNode(ruleEditor);
    		thisTree.add(addRule);
	    }
	    
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
	    	TextEditorPane ruleEditor = getEditorForRule(ruleLocation, thisRule);
	    	addRule = new NjordTreeNode(ruleEditor);
	    	thisTree.add(addRule);
	    	
	    }
	}
	
	//This is the tree selection listener method.
	public void valueChanged(TreeSelectionEvent e) {
		//Hrm... on the one hand I could try and define node and then call valueChanged(TreeSelectionEvent) But on the other I'd have to figure out how to create a TreeSelectionEvent.
		//		And honestly once I figure out how to create that I will probalby have selected a node in the tree. Hrm...
		// Perhaps I will make valueChanged(TreeSelectionEvent do some minimal work assigning a node and such and then call some OTHER method and that will be one I can write a JUnit test for?
		// Perhaps my best bet would be to spend some time on the oracle looking for recommendations on writing junit tests for swing code.
        NjordTreeNode node = (NjordTreeNode)
        		navigationTree.getLastSelectedPathComponent();
        log.info("Node " + node + " Selected");
        
        if (node == null) return;
        resultsPanelNoticesBox.setText(""); //Clear out the notices box.
        
        if (node.getNodeType() == NjordConstants.NODE_TYPE_IRULE) {
        	TextEditorPane nodeInfo = (TextEditorPane) node.getUserObject();
        	if (nodeInfo.isLocal()) {
        		btnDeployiRule.setEnabled(true);
        	} else {
        		btnDeployiRule.setEnabled(false);
        	}
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
//		createNodes(remoteTree);
	}

	/**
	 * @see javax.swing.event.TreeWillExpandListener#treeWillExpand(javax.swing.event.TreeExpansionEvent)
	 * @param tel
	 */
	public void treeWillExpand(TreeExpansionEvent tel) {
//		TreePath path = tel.getPath();
		log.info(logPrefix + "Node: " + tel.getPath() + " Expanded.");
		//This works to select only this tree node.
//		if ( remoteiRulesPath.equals(path)) {
//			// I still can't get it to add the little plus for this stupid tree so right now this will never be triggered.
//			log.info(logPrefix + "Building Nodes Tree");
//			createNodes(dummyParent);
//		}
	}
	
	/**
	 * @see javax.swing.event.TreeWillExpandListener#treeWillCollapse(javax.swing.event.TreeExpansionEvent)
	 * @param twc
	 */
	public void treeWillCollapse(TreeExpansionEvent twc) {
		
	}
	
	/**
	 * actionPerformed is the method that listens for events like clicking a button or a menu item.
	 */
	public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand(); //This will get me the text of the source action without having to instantiate the source
        Class sourceClass = e.getSource().getClass(); //This would allow me to identify the class first before getting it with e.getSource() if I needed to instantiate the whole class for something.

        //TODO: Can I use a switch here or is java where switch is numbers only? Ahh... there would be a reason to use id or to set a name or something on all buttons/menu items/etc.
        if (actionCommand == "Edit Settings") {
        	log.debug("Edit Settings Event Detected.");
        	resultsPanelNoticesBox.setText("Editing Connection Settings");
        	
        	final PreferencesDialog settingsPane = new PreferencesDialog(frame, this);
        	settingsPane.ConnPreffsHostTextField.setText(iIPAddress);
        	settingsPane.ConnPreffsPortTextField.setText(String.valueOf(iPort));
        	settingsPane.ConnPreffsUserTextField.setText(iUserName);
        	settingsPane.ConnPreffsPasswordTextField.setText(iPassword);
        	
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
        	initializeConnection();
        } else if (actionCommand == "New iRule"){
        	//TODO: Make 'Edit Rule' Be the default action for a double click on an iRule in the list.
        	//TODO: Figure out how to handle renaming of iRules.
        	log.debug("New Rule Detected");
        	resultsPanelNoticesBox.setText("New iRule functionality is still a tiny bit sketchy. It requires that you have a connection to a BIGIP. Sorry for the inconvenience.");
        	log.debug("Version is " + bigIPVersion);
        	
         	String newiRuleName = "new_rule";
	        String newiRulePartition = ""; //Will be blank by default for v9 and v10 servers
	        
	        String newRuleDialogTitle = "New iRule";
	        
        	if (connectionInitialized) {
        		//Assume creation on the BIGIP?
    	        if (bigIPVersion.startsWith("11")) {
    	        	newiRulePartition = "/Common"; //v11 objects will always have a partition so populate this by default.
    	        }
            	newiRuleDialog.txtNewiRuleName.setText(newiRuleName);
    	        newiRuleDialog.txtNewiRulePartition.setText(newiRulePartition);
        	} else {
            	newiRuleDialog.txtNewiRuleName.setText(newiRuleName);
        		//IF we're doing a local file let's not use the partition.
        		newiRuleDialog.panel.remove(newiRuleDialog.lblPartition);
        		newiRuleDialog.panel.remove(newiRuleDialog.txtNewiRulePartition);
        		newRuleDialogTitle = "New Local iRule";
        	}
        	

	        int result = JOptionPane.showConfirmDialog(null, newiRuleDialog.panel_1, 
	        		newRuleDialogTitle, JOptionPane.OK_CANCEL_OPTION);  
	        
	        if (result == JOptionPane.OK_OPTION) {
	        	newiRuleName = newiRuleDialog.txtNewiRuleName.getText();
	        	String newiRuleFullName = "";//Start w/ blank.
	        	NjordFileLocation iRuleLocation = null;
	        	
	        	if (connectionInitialized) {
	        		newiRulePartition = newiRuleDialog.txtNewiRulePartition.getText();
	        		if (newiRulePartition.endsWith("/")) {
	        			//This tells us not only that it ends in a / but also that it is non-empty
	        			// then full name is just combining both
	        			newiRuleFullName = newiRulePartition + newiRuleName;
	        		} else if (newiRulePartition.equals("")) {
	        			//this is v10 or local then.
	        			//Do I need to enforce that it can't be empty on v11?
	        			newiRuleFullName = newiRuleName;
	        		} else {
	        			//We're v11 but partition doesn't end in /
	        			newiRuleFullName = newiRulePartition + "/" + newiRuleName;
	        		}
	        	} else {
	        		newiRuleFullName = newiRuleName;
	        	}
	        	

	        	if (connectionInitialized) {
	        		//Assume creation on the BIGIP?
	        		//Has to be a better way to deal with this.
	        		if ( iRuleList.isEmpty() ) {
	        			try {
	        				resultsPanelNoticesBox.append("\nFetching list of rules from the BIGIP");
	        				iRuleList = new ArrayList<String>(Arrays.asList(getiRuleList()));
	        				resultsPanelNoticesBox.append("\nList fetched");
	        			} catch (Exception e1) {
	        				f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e1, this, log);
	        				exceptionHandler.processException();
	        			}

	        			if (iRuleList.contains(newiRuleFullName)) { 
	        				//There is already a rule with this name
	        				resultsPanelNoticesBox.setText("Unable to create rule a rule with this name already exists on the BIGIP. Please choose another");
	        				return;
	        			}
	        			iRuleList.add(newiRuleFullName);
	        			createNodes(remoteiRulesCategory);
	        		} else {
	        			if (iRuleList.contains(newiRuleFullName)) { 
	        				//There is already a rule with this name
	        				resultsPanelNoticesBox.setText("Unable to create rule a rule with this name already exists on the BIGIP. Please choose another");
	        				return;
	        			}
	        			iRuleList.add(newiRuleFullName);
	        		}
	        		iRuleLocation = null;
	        	} else {
	        		//Create a local only rule
	        		JFileChooser fc = new JFileChooser();  
	        		FileSystemView fv = fc.getFileSystemView();  
	        		File documentsDir = fv.getDefaultDirectory();

	        		String localiRulesDirPath = documentsDir.getAbsolutePath() + "/Njord/Local/iRules/";
	        		File localiRulesDir = new File(localiRulesDirPath);
	        		if (!localiRulesDir.exists()) {
	        			log.info(logPrefix + "Local Files directory doesn't exist. Creating.");
	        			localiRulesDir.mkdirs();
	        		} 
	        		String localFilePath = localiRulesDirPath + newiRuleName;
	        		File localFile = new File(localFilePath);

	        		iRuleLocation = new NjordFileLocation(this, newiRuleFullName, localFile);
	        	}
	        	
	        	String newRuleDefinition = "when CLIENT_ACCEPTED {\n\t\n\t\n}";
	        	
	        	TextEditorPane ruleEditor = getEditorForRule(iRuleLocation, newiRuleFullName);
	        	ruleEditor.setText(newRuleDefinition);
	        	ruleEditor.discardAllEdits(); //This prevents undo from 'undoing' the loading of the editor with an iRule
	        	NjordTreeNode addRule = new NjordTreeNode(ruleEditor);
	        	DefaultTreeModel model = (DefaultTreeModel)navigationTree.getModel();
	        	
//	        	// Find node to which new node is to be added
//	        	int startRow = 0;
//	        	String prefix = "iRules";
//	        	TreePath path = tree.getNextMatch(prefix, startRow, Position.Bias.Forward);
//	        	MutableTreeNode node = (MutableTreeNode)path.getLastPathComponent();
	        	
	        	if (connectionInitialized) {
	        		model.insertNodeInto(addRule, remoteiRulesCategory, remoteiRulesCategory.getChildCount());
	        		resultsPanelNoticesBox.setText("New rule created locally. Save to create on the BIGIP");
	        		navigationTree.expandPath(remoteiRulesPath);
	        	} else {
	        		model.insertNodeInto(addRule, localiRulesCategory, localiRulesCategory.getChildCount());
	        		resultsPanelNoticesBox.setText("New local rule");
	        		navigationTree.expandPath(localiRulesPath);
	        	}
	        	
	        	// Insert new node as last child of node
//	        	model.insertNodeInto(addRule, node, node.getChildCount());
	        	
	        	
	        }
	        
        } else if (actionCommand == "Save"){
        	//TODO: Apparently blank is a valid iRule as far as mcpd is concerned. Check to ensure the rule isn't completely blank and handle that.
        	//This will tell me what iRule is selected. Then I need to set the definition. tree.getLastSelectedPathComponent()
        	log.info("Save detected");
        	// Clear the contents of the notices box
        	String resultsText = "Saving...";
        	resultsPanelNoticesBox.setText(resultsText);
            NjordTreeNode node = (NjordTreeNode)
            		navigationTree.getLastSelectedPathComponent();
            if (node == null) return;
            
            TextEditorPane nodeInfo = (TextEditorPane) node.getUserObject();

            if (nodeInfo.isLocal()) {
//            	NjordFileLocation newLoc = new NjordFileLocation(this, nodeInfo.getName(), ic, nodeInfo.getText()); 
            	try {
//            		nodeInfo.saveAs(newLoc);
//            		newLoc.isLocal(false);
            		nodeInfo.save();
            		if (resultsPanelNoticesBox.getText().startsWith("Saving")) { //TODO: Check and see that these if blocks are even neccesary
            			//If the results panel still says "Saving..." then we were successfull. If it failed then the text will have an error in it now.
            			resultsPanelNoticesBox.setText("Save successful");
            		}
            		navigationTree.repaint(); // Force the navigation tree to redraw itself. It will notice that the editor for this rule is now clean and display it properly.
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
            		navigationTree.repaint(); // Force the navigation tree to redraw itself. It will notice that the editor for this rule is now clean and display it properly.
            		btnActionSave.setEnabled(false); // Then disable the save button to show that we are saved on the server one more way.
            	} catch (IOException e2) {
            		f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e2);
            		exceptionHandler.processException(); 
            	}
            }
            
        } else if (actionCommand == "Get iRules"){
        	log.info("Get iRules detected");
        	if (!connectionInitialized) {
        		initializeConnection();
        	}
    		if ( iRuleList.isEmpty() ) {
    			try {
    				iRuleList = new ArrayList<String>(Arrays.asList(getiRuleList()));
    			} catch (Exception e1) {
    				f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e1, this, log);
    				exceptionHandler.processException();
    			}
    		}
    		createNodes(remoteiRulesCategory);
    		navigationTree.expandPath(remoteiRulesPath);
    		
        } else if (actionCommand == "Delete iRule"){
        	log.info("Delete iRule detected");
        	String resultsText = "Deleting...";
        	resultsPanelNoticesBox.setText(resultsText);

        	NjordTreeNode node = (NjordTreeNode)
        			navigationTree.getLastSelectedPathComponent();
        	if (node == null) return;

        	TextEditorPane nodeInfo = (TextEditorPane) node.getUserObject();

        	String ruleName = nodeInfo.getName();
        	String[] ruleNames = { ruleName };
        	String deleteMessage = "Are you sure you want to delete the selected rule [" + ruleName + "] ";
        	if (nodeInfo.isLocal() ) {
        		deleteMessage = deleteMessage + "From the local File System?";
        	} else {
        		deleteMessage = deleteMessage + "from the BIGIP?";
        	}
        	
        	int result = JOptionPane.showConfirmDialog(null, deleteMessage, 
        			"Delete iRule", JOptionPane.OK_CANCEL_OPTION);

        	if (result == JOptionPane.OK_OPTION) {
        		if (nodeInfo.isLocal()) {
        			//Local file remove from the file system
        			String path = nodeInfo.getFileFullPath();
        			log.info(logPrefix + "Deleting " + path);
        			File deleteFile = new File(path);
        			deleteFile.delete();
    				resultsPanelNoticesBox.setText("Rule [" + ruleName + "] successfully deleted from the local file system.");
    				DefaultTreeModel model = (DefaultTreeModel)navigationTree.getModel();
    				model.removeNodeFromParent(node);
        			//This works now update notices pane and the nav tree
        		} else {
        			//Remote so remove it from the BIGIP
        			
        			try {
        				ic.getLocalLBRule().delete_rule(ruleNames);
        				resultsPanelNoticesBox.setText("Rule [" + ruleName + "] successfully deleted from BIGIP.");
        				DefaultTreeModel model = (DefaultTreeModel)navigationTree.getModel();
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
        	}
        } else if (actionCommand == "Deploy to BIGIP"){
        	//Local rule that needs to be created on the BIGIP.
        	NjordTreeNode node = (NjordTreeNode)
        			navigationTree.getLastSelectedPathComponent();
    		if (node == null) return;
    		deployLocaliRuleToBigIP(node);
//    		ffff
        } else {
        	//If we have entered this section we have screwed something up 
        	log.error("Un-Known Action Event Detected." 
                    + nl
                    + "    Event source: " + actionCommand
                    + " (an instance of " + getClassName(sourceClass) + ")");
        } 
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
	 * 
	 * @param o A class.
	 * @return
	 */
    protected String getClassName(Object o) {
        String classString = o.getClass().getName();
        int dotIndex = classString.lastIndexOf(".");
        return classString.substring(dotIndex+1);
    }
 
    /**
     * Sets the text displayed at the bottom of the window. Public allows us to send back update messages from other things.
     * @param newText
     */
    public void setLabel(String newText) {
    	lblStatusLabel.setText(newText);
    }
    
    /**
     * returns only a string list of the names of the Virtual Servers instead of actual virtual server objects. I might move these to a sub package
     * @return
     * @throws Exception
     */
	public String [] getVirtualsList() {
		String[] virtuals = null;
		try {
			virtuals = ic.getLocalLBVirtualServer().get_list();
		} catch (RemoteException e) {
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e);
			exceptionHandler.processException();
		} catch (Exception e) {
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e);
			exceptionHandler.processException();
		}
		log.debug("Available Virtuals");
		for (String string : virtuals) {
			log.debug("   " + string);
		}
		return virtuals;
	}
	
	/**
	 * getiRuleList returns only a string list of the names of the iRules instead of actual iRule objects 
	 * 
	 * @return
	 * @throws Exception
	 */
	public String[] getiRuleList() {
		String[] iRules = null;
		try {
			iRules = ic.getLocalLBRule().get_list();
		} catch (RemoteException e) {
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e);
			exceptionHandler.processException();
		} catch (Exception e) {
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e);
			exceptionHandler.processException();
		}
		log.debug("Available Rules");
		for (String string : iRules) {
			log.debug("   " + string);
		}
		return iRules;
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
	
	//I can probably get rid of this method completely
	private JPanel createEditorPanel() {
    	JPanel editorPanel = new JPanel();
		AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance();
		atmf.putMapping("SYNTAX_STYLE_IRULES", "com.f5.AaronForster.njord.util.iRulesTokenMaker");
		TokenMakerFactory.setDefaultInstance(atmf); //Don't know if I need this line or not
    	
//		//This text editor pane is no longer in use but I'm leaving it here for reference.
//    	nTextEditorPane = new TextEditorPane();
//    	nTextEditorPane.setSyntaxEditingStyle("SYNTAX_STYLE_IRULES");
//    	nTextEditorPane.setCodeFoldingEnabled(true);
//    	nTextEditorPane.setAntiAliasingEnabled(true);
//    	nTextEditorPane.setName("editorPane");
//    	SyntaxScheme scheme = nTextEditorPane.getSyntaxScheme();
//        scheme.getStyle(Token.FUNCTION).foreground = Color.MAGENTA;
//        Color operatorColor = new Color(880080);
//        scheme.getStyle(Token.OPERATOR).foreground = operatorColor;
    	
    	//TODO: change the name of this scrollpane to something more intelligent.
    	codeEditorScrollPane = new JScrollPane();
    	//Let's try this with TextEditorPane;
//    	codeEditorScrollPane.setViewportView(nTextEditorPane);
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
    	btnSave.addActionListener(this);
    	btnSave.setEnabled(false);

    	GroupLayout gl_EditorPanel = new GroupLayout(editorPanel);
    	gl_EditorPanel.setHorizontalGroup(
    			gl_EditorPanel.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(gl_EditorPanel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_EditorPanel.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(codeEditorScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE)
                        .addGroup(gl_EditorPanel.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        		.addGroup(javax.swing.GroupLayout.Alignment.LEADING, gl_EditorPanel.createSequentialGroup()
                        				.addComponent(btnSave)))
                    .addGroup(gl_EditorPanel.createSequentialGroup()))
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
                    		.addComponent(btnSave))
                    .addContainerGap())
            );

    	editorPanel.setLayout(gl_EditorPanel);
//
//    	// A CompletionProvider is what knows of all possible completions, and
//        // analyzes the contents of the text area at the caret position to
//        // determine what completion choices should be presented. Most
//        // instances of CompletionProvider (such as DefaultCompletionProvider)
//        // are designed so that they can be shared among multiple text
//        // components.
//        CompletionProvider provider = createCompletionProvider();
//
//        // An AutoCompletion acts as a "middle-man" between a text component
//        // and a CompletionProvider. It manages any options associated with
//        // the auto-completion (the popup trigger key, whether to display a
//        // documentation window along with completion choices, etc.). Unlike
//        // CompletionProviders, instances of AutoCompletion cannot be shared
//        // among multiple text components.
//        AutoCompletion ac = new AutoCompletion(provider);
//        ac.install(nTextEditorPane);
    	
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

		String operatorsPath = "/resources/iRulesOperatorsUncategorized.txt";
		String statementsPath = "/resources/iRulesStatementsUncategorized.txt"; // drop, pool and more
		String functionsPath = "/resources/iRulesFunctionsUncategorized.txt"; // findstr, class and others
		String commandsPath = "/resources/iRulesCommandsUncategorized.txt"; // HTTP::return etc etc
		String tclCommandsPath = "/resources/tclCommandsUncategorized.txt"; // Built in tcl commands
		
		provider.addCompletion(new BasicCompletion(provider, "abstract"));
	    provider.addCompletion(new BasicCompletion(provider, "assert"));
	    BufferedReader reader = null;
	    
		try {
			reader = new BufferedReader(new InputStreamReader(MainGuiWindow.class.getResourceAsStream(operatorsPath)));
			String str;
			while ((str = reader.readLine()) != null) {
				provider.addCompletion(new BasicCompletion(provider, str));
			}
		} catch (IOException e) {
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e, this, log);
			exceptionHandler.processException();
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

		try {
			reader = new BufferedReader(new InputStreamReader(MainGuiWindow.class.getResourceAsStream(statementsPath)));;
			String str;
			while ((str = reader.readLine()) != null) {
				String[] words = str.split(","); // split on commas
				for (String word : words) {
					provider.addCompletion(new BasicCompletion(provider, word));
				}

			}
		} catch (IOException e) {
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e, this, log);
			exceptionHandler.processException();
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

		try {
			reader = new BufferedReader(new InputStreamReader(MainGuiWindow.class.getResourceAsStream(functionsPath)));
			String str;
			while ((str = reader.readLine()) != null) {
				String[] words = str.split(","); // split on commas
				for (String word : words) {
					provider.addCompletion(new BasicCompletion(provider, word));
				}

			}
		} catch (IOException e) {
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e, this, log);
			exceptionHandler.processException();
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

		try {
			reader = new BufferedReader(new InputStreamReader(MainGuiWindow.class.getResourceAsStream(commandsPath)));
			String str;
			while ((str = reader.readLine()) != null) {
				String[] words = str.split(","); // split on commas
				for (String word : words) {
					provider.addCompletion(new BasicCompletion(provider, word));
				}

			}
		} catch (IOException e) {
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e, this, log);
			exceptionHandler.processException();
		}
		
		try {
			reader = new BufferedReader(new InputStreamReader(MainGuiWindow.class.getResourceAsStream(tclCommandsPath)));
			String str;
			while ((str = reader.readLine()) != null) {
				String[] words = str.split(","); // split on commas
				for (String word : words) {
					provider.addCompletion(new BasicCompletion(provider, word));
				}

			}
		} catch (IOException e) {
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e, this, log);
			exceptionHandler.processException();
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
		
		//TODO: replace these with some iRules defaults.
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
			NavPanel.remove(navigationTree); 
		}
		
		top = new NjordTreeNode("top", NjordConstants.NODE_TYPE_TOP);
		remoteTree = new NjordTreeNode("BigIP", NjordConstants.NODE_TYPE_LOCAL_REMOTE);
		localTree = new NjordTreeNode("Local", NjordConstants.NODE_TYPE_LOCAL_REMOTE);
		top.add(remoteTree);
		top.add(localTree);
	    remoteiRulesCategory = new NjordTreeNode("iRules", NjordConstants.NODE_TYPE_CATEGORY);
	    remoteTree.add(remoteiRulesCategory);
		localiRulesCategory = new NjordTreeNode("iRules", NjordConstants.NODE_TYPE_CATEGORY);
		
        localTree.add(localiRulesCategory);
        
        //End create local tree portion
        
        navigationTree = new f5JavaGuiTree(top);
        navigationTree.setRootVisible(false); //Hides the top node
		
		ToolTipManager.sharedInstance().registerComponent(navigationTree);
		navigationTree.setCellRenderer(new NjordTreeRenderer());
		
		navigationTree.getSelectionModel().setSelectionMode
	            (TreeSelectionModel.SINGLE_TREE_SELECTION);
	    
	    //Listen for when the selection changes.
		navigationTree.addTreeSelectionListener(this);
	    //Listen for when non-leave nodes are expanded
		navigationTree.addTreeExpansionListener(this);
	    //Not doing this _yet_
//	    tree.addTreeWillExpandListener(this);
	    
	    navScrollPane = new JScrollPane(navigationTree);
        
        //Let's see if we can get it's path this way.
        remoteiRulesPath = new TreePath(remoteiRulesCategory.getPath());
        localiRulesPath = new TreePath(localiRulesCategory.getPath());
        log.info(logPrefix + "Paths are remote [" + remoteiRulesPath + "] and local [" + localiRulesPath + "]");  
        
        navigationTree.expandPath(new TreePath(remoteTree.getPath()));
        navigationTree.expandPath(new TreePath(localTree.getPath()));
        
		GroupLayout gl_NavPanel = new GroupLayout(NavPanel);
		gl_NavPanel.setHorizontalGroup(
			gl_NavPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_NavPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(navScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_NavPanel.setVerticalGroup(
			gl_NavPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_NavPanel.createSequentialGroup()
					.addGap(5)
					.addComponent(navScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
		);
		NavPanel.setLayout(gl_NavPanel);
	    
	    Dimension navScrollPanelMinSize = new Dimension(200,500);
	    Dimension navScrollPanelPreferedSize = new Dimension(200,700);
	    Dimension navScrollPanelMaxSize = new Dimension(200,1000);
	    navScrollPane.setPreferredSize(navScrollPanelPreferedSize);
	    navScrollPane.setMinimumSize(navScrollPanelMinSize);
	    navScrollPane.setMaximumSize(navScrollPanelMaxSize);
 
	    NavPanel.add(navScrollPane);
	    NavPanel.updateUI(); // Forces NavPanel to re-evaluate it's contents
	    btnNewiRule.setEnabled(true); //Now that we have iRules we can create a new one.
	    
	    buildLocalNodes(localiRulesCategory);
	}
	
	
	/**
	 * Builds the local side of the navigation tree. At some point it might be nice to have a single method for local and 
	 * remote or to have them return something but this is separate for now to simplify development and troubleshooting.
	 */
	public void buildLocalNodes(NjordTreeNode tree) {
		// check to see that the local folder exists. 
		//If not return nuthin'
		//if so get the list of the files and create editors for them the way that we do for remote but with a local path.
		JFileChooser fc = new JFileChooser();  
		FileSystemView fv = fc.getFileSystemView();  
		File documentsDir = fv.getDefaultDirectory();//I should move this to something global so I'm not getting it all the time.

		String localiRulesDirPath = documentsDir.getAbsolutePath() + "/Njord/Local/iRules/";
		File localiRulesDir = new File(localiRulesDirPath);
		
		File[] localiRules = null;
		if (localiRulesDir.exists()) {
			localiRules = localiRulesDir.listFiles(); //or perhaps I should use listFiles(fileFilter[])
			if (localiRules != null ) {
				//There are rules in this list let's add them to the tree
				for (File iRule : localiRules) {
					// Interestingly editing and saving are working fine but loading from disk is not
					String iRulename = iRule.getName();
					NjordFileLocation ruleLocation = new NjordFileLocation(this, iRulename, iRule); 
			    	TextEditorPane ruleEditor = getEditorForRule(ruleLocation, iRulename);
			    	NjordTreeNode addRule = new NjordTreeNode(ruleEditor);
			    	tree.add(addRule);
				}
			}
		}
	}
	
	public void deployLocaliRuleToBigIP(NjordTreeNode node) {
		//TODO: COnfirm connection to BIGIP First
		TextEditorPane editorPane = (TextEditorPane) node.getUserObject();
		//Do I want to remove it locally when this happens?
		NjordFileLocation remoteLocation = new NjordFileLocation(this, editorPane.getName(), ic, editorPane.getText());
		
		TextEditorPane newEditorPane = getEditorForRule(remoteLocation, editorPane.getName()); 
				
//				new TextEditorPane(0, true, remoteLocation);
		
		if (iRuleList.contains(editorPane.getName())) { 
			//There is already a rule with this name
			resultsPanelNoticesBox.setText("Unable to create rule a rule with this name already exists on the BIGIP. Please choose another");
			return;
		}
		iRuleList.add(editorPane.getName());
		newEditorPane.setText(editorPane.getText());
//		createNodes(remoteiRulesCategory);
		NjordTreeNode addRule = new NjordTreeNode(newEditorPane);
//		remoteiRulesCategory.add(addRule);
		
		DefaultTreeModel model = (DefaultTreeModel)navigationTree.getModel();
    	model.insertNodeInto(addRule, remoteiRulesCategory, remoteiRulesCategory.getChildCount());
    	resultsPanelNoticesBox.setText("Hit Save to complete creation on BIGIP.");
    	navigationTree.expandPath(remoteiRulesPath);
		
	}
	
	
	
	/**
	 * Sets the contents of the notices box. For sub classes to be able to send back a message to us to set.
	 * @param message
	 */
	public void setNoticesText(String message) {
		resultsPanelNoticesBox.setText(message);	
	}
	
	/**
	 * loadPreferences will handle reading in connections preferences and creating the prefs file if need be. 
	 * That is if I use the whole file thing at all instead of changing to java.util.prefs
	 * @return
	 */
	private boolean loadPreferences() {
		userHome = System.getProperty("user.home");
		
		//http://www.particle.kth.se/~lindsey/JavaCourse/Book/Part1/Java/Chapter10/Preferences.html
		//TODO: Replace this whole file thing with java.util.prefs maybe
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
	
	/**
	 * Currently new iRule is going to use null for iRuleLocation and I'll need to create a 'deploy to bigip' function.
	 * 
	 * @param iRuleLocation Expects mostly a NjordFileLocation but should be generic enough to handle any. 
	 * @param iRuleName
	 * @return
	 */
	public TextEditorPane getEditorForRule(FileLocation iRuleLocation, String iRuleName) {
		
//		How do to this so one should go if there's a remote location and another if it's local
//		
//		if we are handed a NjordFileLocation then it's remote and open an editor with that. If not then open an editor with a default local location?
//		Let's cheat w/ the new iRule dialog again and if we have local rules selected we do a new local irule and if we have remote we do a remote?
//		Also select remote if we have nothing selected but we _do_ have a valid connection to the bigip?
//		No because I have to make it so we can select non-leaf nodes first.	
//		new iRule just has to be expanded to handle both local and remote and the local/remote selection can be set automatically based on what's selected.
//		Actually for RIGHT NOW le'ts just add a new local iRule button.
//		
//		So then new local iRule I suppose I should use a default location handler for local windows? Then just give it a path in your home directory?
//				
//		
//				
//				Ok here's what I'm going to do.
//				
//				NjordFileLocation is going to be modified to handle both local and remote Files.
//				If it's Local then we are going to use some other output stream. If it's remote we will use the njordoutputstream. How will we handle migrating local to remote?
//				Maybe a new deploy to bigip button that actually just gets the contents of the text editor, issues a create irule on the bigip with them then re-fetches the list of iRules from the server. I love this plan.
//				Then if the file is 'local' all actions like getOutputStream return some output stream suitable for 'local file system' or whatever and if remote uses the other.
//				
//				So then my todos right now are create this method have new iRule use a null for file location if there's no valid connection which would just let you create the editor and such. Then I can get this subroutine working and tested and deal with the other outputstream after.
				
						
		//For the methods that take a default encoding value "If this value is null, the system default encoding is used."
		TextEditorPane ruleEditor = null;
		try {
    		ruleEditor = new TextEditorPane(0, true, iRuleLocation);
    	} catch (IOException e) {
    		f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e);
    		exceptionHandler.processException();
    	}

    	ruleEditor.setName(iRuleName);
    	ruleEditor.discardAllEdits(); //This prevents undo from 'undoing' the loading of the editor with an iRule. Should be unnessesary now that I'm using FileLocation.
    	ruleEditor.getDocument().addDocumentListener(new NjordDocumentListener(this));

    	//Syntax Highlighting items
    	ruleEditor.setSyntaxEditingStyle("SYNTAX_STYLE_IRULES");
    	ruleEditor.setCodeFoldingEnabled(true);
    	ruleEditor.setAntiAliasingEnabled(true);
    	
    	//Colors pulled out of the eclipse gui
//    	Variables blue             00 00 192 0000C0
//    	keywords  purpleish        127 00 85  7F0055
//    	Operators solid black 	   00 00 00  000000
//    	Strings  also blue         42 00 255  2A00FF
//    	Annotations grey           100 100 100 646464   (I think this is stuff like @Test)
//    	comments greenish          63  127  95  3F7F5F
//    	todo tag blue grey         127 159 191  7F9FBF
    	
    	//Some of my own
    	// A couple of slightly different but good blues
    	//0000CC 0000FF    how about 000066 and  0033ff
    	//Some good purples
    	// 660066 990099
    	
    	SyntaxScheme scheme = ruleEditor.getSyntaxScheme();
    	scheme.getStyle(Token.ANNOTATION).foreground = annotationColor;
    	scheme.getStyle(Token.VARIABLE).foreground = variableColor;
//    	scheme.getStyle(Token.IDENTIFIER).foreground = identifyerColor; //I think 'identifyer' is all the other words.
    	scheme.getStyle(Token.FUNCTION).foreground = functionColor;
    	scheme.getStyle(Token.REGEX).foreground = regexColor;
    	scheme.getStyle(Token.COMMENT_EOL).foreground = commentColor;
//    	scheme.getStyle(Token.COMMENT_MULTILINE).foreground = commentColor; // I don't think there's a multi-line comment defined yet
    	scheme.getStyle(Token.RESERVED_WORD_2).foreground = reservedWord2Color;
    	scheme.getStyle(Token.RESERVED_WORD).foreground = reservedWordColor;
    	scheme.getStyle(Token.OPERATOR).foreground = operatorColor;
//    	scheme.getStyle(Token.LITERAL_STRING_DOUBLE_QUOTE).foreground = doublequoteColor;
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
        
        return ruleEditor;
	}
	
	/**
	 * Writes preferences to disk after you have edited them.
	 * 
	 * @return Success/Failure status.
	 */
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

	private static void makeLinkable(JLabel c, MouseListener ml) {
	    assert ml != null;
	    c.setText(htmlIfy(linkIfy(c.getText(), c.getToolTipText())));
	    c.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
	    c.addMouseListener(ml);
	}

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

	private static String getPlainLink(String s) {
	    return s.substring(s.indexOf(A_HREF) + A_HREF.length(), s.indexOf(HREF_CLOSED));
	}

	//WARNING
	//This method requires that s is a plain string that requires
	//no further escaping
	private static String linkIfy(String s) {
	    return A_HREF.concat(s).concat(HREF_CLOSED).concat(s).concat(HREF_END);
	}

	private static String linkIfy(String s, String l) {
	    return A_HREF.concat(l).concat(HREF_CLOSED).concat(s).concat(HREF_END);
	}
	
	//WARNING
	//This method requires that s is a plain string that requires
	//no further escaping
	private static String htmlIfy(String s) {
	    return HTML.concat(s).concat(HTML_END);
	}

}
