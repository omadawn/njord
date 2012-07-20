package com.f5.AaronForster.javaiRulesEditor;

import iControl.LocalLBRuleRuleDefinition;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Properties;

import javax.swing.Box;
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
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.xml.rpc.ServiceException;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.f5.AaronForster.javaiRulesEditor.util.f5ExceptionHandler;
import com.f5.AaronForster.javaiRulesEditor.util.f5JavaGuiTree;


//A tutorial on how to write a text editor with syntax highlighting. Seems like it could be a tiny bit sketchy but worth a look
// http://ostermiller.org/syntax/editor.html
// An older tutorial from sun (98)
// http://java.sun.com/products/jfc/tsc/articles/text/editor_kit/index.html
// Some source code called 'simple text editor'
// http://www.picksourcecode.com/articles/explan.php?id=6c9882bbac1c7093bd25041881277658&ems=9a0fa83125d48ab7258eab27754dd23e&lg=10
// https://github.com/jgruber/iControlExamples/tree/master/src/main/java/com/f5se/examples

/*
 * TODO SECTION
 * TODO: go through all the 'Auto-generated catch block' sections
 * TODO: Figure out if I want iControl Assembly or JGruber's method.
 * TODO: It seems like now adays the app stays running even after closing w/ x in corner. Make it quit when you stop running it.
 * TODO: Convert to maven package or somehow otherwise figure out how to build a jar than includes all the dependencies.
 * TODO: Come up with some way to switch back and forth between jgruber and icontrol assembly mechanisms?
 * TODO: Make this generic and plugin oriented.
 * TODO: Get rid of 'DestkopBigIPManager and replace it with this as the base.
 * TODO: Catch connect from main window
 * TODO: Have a list iRules button. Do this by default on a connect
 * TODO: Figure out why the connect button in the middle of the screen doesn't work but the menu item one does.
 * TODO: Get rid of the connect/disconnect verbage. It's a web UI and there's really no 'connected' ever. Have a 'verify connection' button instead.
 * TODO: Let's try and be sure not to allow a user to edit a built in iRule so they don't have to get pissed off when it won't let them save their changes.
 * TODO: Related, figure out how to identify them.
 * TODO: Investigate the way 'Java Simple Text Editor' builds it's GUI from an XML config file. http://javatxteditor.sourceforge.net/ maybe not too closely since it's gui is crap but perhaps....
 * TODO: Get rid of the top of the jtree unless I'm planning on having it be the BigIP's host name and having sections for virtuals and iRules and such.
 * TODO: Move some code out of the actions in the actionListener and into subroutines.
 * TODO: See if it's possible to have multiple action listeners or if there's some other way to make that portion of it simpler.
 * TODO: Add a heirarchy for things. Use http://docs.oracle.com/javase/tutorial/uiswing/events/treeexpansionlistener.html to not load the contents of a branch node until you expand it.
 * TODO: Rename this to Njord see note on the name below
 * 
 * 
 * JTree/editor items:
 * JTree has a TreeCellEditor in addition to it's TreeCellRenderer. I believe these are both interfaces. And that you have to implement at least TreeCellRenderer. TreeCellRenderer determines what happens when you select a tree node. If in addition
 * 	you create a TreeCellEditor you define actions that take user input (this is brilliant BTW.) You have to also define TreeCellRenderer because that defines how to render the content _within_ the editor. The editor itself doesn't have to be a text 
 * 	editor it can just be a couple of radio buttons. Or maybe... an enable/disable button. ^_^
 * 
 * 
 * PROGRESS BAR ITEMS:
 * TODO: Get rid of the progress bar after we have verified our connection and replace it with an icon that says 'Connection Verified' or something.
 * 
 * 
 * TODO: I think I accidentally deleted most of my todos by deleting the 'main application' class which turned out to be unneccesary
 * TODO: Peep this http://www.ibm.com/developerworks/opensource/tutorials/os-eclipse-code-templates/os-eclipse-code-templates-pdf.pdf for some in depth dialog on template customization.
 * 
 */
/**
 * Currently a java based iRule editor which needs some work.
 * The Norse god of winds, sea and fire. He brings good fortune at sea and in the hunt. He is married to the giantess Skadi. His children are Freya and Freyr, whom he fathered on his own sister.
 * 
 * Originally, Njord was one of the Vanir but when they made peace with the Aesir, he and his children were given to them as hostages. The Aesir appointed both Njord and Freyr as high priests to preside over sacrifices. Freya was consecrated as sacrificial priestess. She taught the Aesir witchcraft, an art that was common knowledge among the Vanir.
 *  
 * Njord will begin it's life solely as an iRule editor but I'd like it to become a full on bigip manager thus the name.
 * 
 * @author forster
 * @version 0.5
 */
public class MainGuiWindow implements ActionListener, TreeSelectionListener {
	private String logPrefix = "MainGuiWindow: ";
	private JFrame frame;
	private String newline = "\n";

	
	JTextArea output;
    JScrollPane scrollPane;
    
    
    // Might need these here due to scoping
    JTextPane defaultResultsPanelTextPane = new JTextPane();
    JLabel lblStatusLabel = new JLabel();
    
    //iControl.BigIP ic; //jgruber syntax
//    iControl.Interfaces ic; // jpruit syntax
    // Let's not initialize it yet.
//    iControl.BigIP  ic = new iControl.BigIP(); // From WSDL Generated
	iControl.Interfaces ic = new iControl.Interfaces(); //From Assembly
	
	//iControl.BigIP from the maven wsdl thingy is reasonably equivalent to iControl.Interfaces from the iControl 
	//	'Assembly' available on DevCentral.
    
    PreferencesDialog preferencesDialog;
    
	//Connection information stuff
	String iIPAddress;
	long iPort;
	String iUserName;
	String iPassword;
	
//	String iIPAddress = "192.168.215.251";
//	long iPort = 443; // When this is used and what format it needs to be in varries depending on the version of the library
//	String iUserName = "admin"; // I'm 99% sure this is right. ^_^
//	String iPassword = "admin";
	
	
	// Here we have to create a couple of things ahead of time so that they are available 'globally'
	// These two are used to show the progress bar progressing.
	JProgressBar progressBar;
	Timer timer;
	
	
	//This part is for the text editor to work.
	JPanel resultsPanel = null;
	JSplitPane splitPane = new JSplitPane();
	JEditorPane iRuleEditorPane = null;
	RSyntaxTextArea rSyntaxTextArea = null;
	
//	JTree tree = null; // Initializing this with null so we don't create an object with the sample data which is what new JTree() would do.
	f5JavaGuiTree tree = null;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		//Util.log(logPrefix, "Starting up");
		//TODO: Figure out why I can't do this
		//Util.log(logPrefix + "Starting up");
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					MainGuiWindow window = new MainGuiWindow();
					window.frame.setVisible(true);

				} catch (UnsupportedLookAndFeelException e) {
					// handle exception
				} catch (ClassNotFoundException e) {
					// handle exception
				} catch (InstantiationException e) {
					// handle exception
				} catch (IllegalAccessException e) {
					// handle exception
				}			
//				catch (Exception e) {
//					e.printStackTrace();
//				}
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
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 1118, 683);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Get the preferences from a file
		Properties properties = new Properties();
		File propsFile=new File("resources/settings.properties");

		try {
		    properties.load(new FileInputStream(propsFile));
		} catch (IOException e) {
			System.out.println("Whoa, caught an error: " + e);
		}
		
		iIPAddress = properties.getProperty("connection.host");
		iPort = Long.valueOf(properties.getProperty("connection.port"));
		iUserName = properties.getProperty("connection.usernameame");
		iPassword = properties.getProperty("connection.password");
		
		
		// Preferences Dialog stuff
		//TODO: Get rid of the whole geisel thing
		preferencesDialog = new PreferencesDialog(frame, "geisel", this); // This creates a validated text type dialog which will only allow you to submit if you enter the text in the second section
		preferencesDialog.pack();
		
		
		// The menu definition
		
		JMenuBar menuBar = new JMenuBar();
		frame.getContentPane().add(menuBar, BorderLayout.NORTH);
		
		JMenu mnConnMenu = new JMenu("Connection");
		menuBar.add(mnConnMenu);
		
		// This one might be my template for how to set action menu items. It also includes keyboard shortcut settings
		JMenuItem mntmEditSettings = new JMenuItem("Edit Settings");
		mntmEditSettings.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_2, ActionEvent.ALT_MASK)); // I think this is the part that adds the hotkey
		mntmEditSettings.addActionListener(this); // This adds an action listener so if you click this it will trigger actionListener() method defined below
    	mnConnMenu.add(mntmEditSettings);
		
    	
		JMenuItem mntmConnect = new JMenuItem("Connect");
		mntmConnect.addActionListener(this);
		mnConnMenu.add(mntmConnect);
		
		JMenuItem mntmDisconnect = new JMenuItem("Disconnect");
		mntmDisconnect.addActionListener(this);
		mnConnMenu.add(mntmDisconnect);
		
		

		JMenu mnActMenu = new JMenu("Actions");
		menuBar.add(mnActMenu);		
		
		JMenuItem actMnGetVirtuals = new JMenuItem("List Virtual Servers");
		actMnGetVirtuals.addActionListener(this); // This adds an action listener so if you click this it will trigger actionListener() method defined below
		mnActMenu.add(actMnGetVirtuals);
		
		JMenuItem actMnGetiRules = new JMenuItem("List iRules");
		actMnGetiRules.addActionListener(this); // This adds an action listener so if you click this it will trigger actionListener() method defined below
		mnActMenu.add(actMnGetiRules);
		
		JMenuItem actMnEditRule = new JMenuItem("Edit Rule");
		actMnEditRule.addActionListener(this); // This adds an action listener so if you click this it will trigger actionListener() method defined below
		mnActMenu.add(actMnEditRule);

		JMenuItem actMnSave = new JMenuItem("Save");
		actMnSave.addActionListener(this); // This adds an action listener so if you click this it will trigger actionListener() method defined below
		mnActMenu.add(actMnSave);
		
		
		// The rest of the gui def
		
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);
		
		// I need to figure out how to make the items in here stack vertically instead of horizontally the way they are right now 
		JPanel NavPanel = new JPanel();
		splitPane.setLeftComponent(NavPanel);
		
		JScrollPane scrollPane = new JScrollPane();
		NavPanel.add(scrollPane);
		
		Component horizontalStrut = Box.createHorizontalStrut(54);
		NavPanel.add(horizontalStrut);
	
		//I can get rid of this here once I make it so we don't automatically populate the iRules.
		initializeConnection();
		//TODO: Figure out how to only display the list once I've clicked a 'display' button.
		//TODO: I'm pretty sure this is causing an infinite loop. Review code
		DefaultMutableTreeNode top = new DefaultMutableTreeNode("First!");
		createNodes(top);
//		tree = new JTree(top); // Now that TOP exists let's initialize the tree.
		tree = new f5JavaGuiTree(top);
		
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

		//DEFAULT RESULTS PANE CONTENT STARTS HERE

		
		resultsPanel = new JPanel();
		splitPane.setRightComponent(resultsPanel);
		GridBagLayout gbl_resultsPanel = new GridBagLayout();
		gbl_resultsPanel.columnWidths = new int[]{138, 1, 20, 743, 0};
		gbl_resultsPanel.rowHeights = new int[]{20, 0, 0, 0, 33, 0, 0};
		gbl_resultsPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_resultsPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		resultsPanel.setLayout(gbl_resultsPanel);
		
		JLabel lblDefaultResultspanelContent = new JLabel("Default resultsPanel Content");
		GridBagConstraints gbc_lblDefaultResultspanelContent = new GridBagConstraints();
		gbc_lblDefaultResultspanelContent.insets = new Insets(0, 0, 5, 0);
		gbc_lblDefaultResultspanelContent.gridx = 3;
		gbc_lblDefaultResultspanelContent.gridy = 1;
		resultsPanel.add(lblDefaultResultspanelContent, gbc_lblDefaultResultspanelContent);
		lblDefaultResultspanelContent.setVerticalAlignment(SwingConstants.TOP);
		
		JTextPane defaultResultsDevCentralURLTextPane = new JTextPane();
		GridBagConstraints gbc_defaultResultsDevCentralURLTextPane = new GridBagConstraints();
		gbc_defaultResultsDevCentralURLTextPane.fill = GridBagConstraints.BOTH;
		gbc_defaultResultsDevCentralURLTextPane.insets = new Insets(0, 0, 5, 0);
		gbc_defaultResultsDevCentralURLTextPane.gridx = 3;
		gbc_defaultResultsDevCentralURLTextPane.gridy = 2;
		resultsPanel.add(defaultResultsDevCentralURLTextPane, gbc_defaultResultsDevCentralURLTextPane);
		defaultResultsDevCentralURLTextPane.setText("This will contain a link to dev central or something");
		
//		JTextPane defaultResultsPanelTextPane = new JTextPane();
		GridBagConstraints gbc_defaultResultsPanelTextPane = new GridBagConstraints();
		gbc_defaultResultsPanelTextPane.fill = GridBagConstraints.BOTH;
		gbc_defaultResultsPanelTextPane.insets = new Insets(0, 0, 5, 0);
		gbc_defaultResultsPanelTextPane.gridx = 3;
		gbc_defaultResultsPanelTextPane.gridy = 3;
		resultsPanel.add(defaultResultsPanelTextPane, gbc_defaultResultsPanelTextPane);
		defaultResultsPanelTextPane.setText("Should have some welcome text here that perhaps includes a little help message and maybe some copywrite information. I would prefer if it was pinned to the bottom of the containing pane.");
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut_1 = new GridBagConstraints();
		gbc_horizontalStrut_1.anchor = GridBagConstraints.WEST;
		gbc_horizontalStrut_1.insets = new Insets(0, 0, 5, 5);
		gbc_horizontalStrut_1.gridx = 2;
		gbc_horizontalStrut_1.gridy = 4;
		resultsPanel.add(horizontalStrut_1, gbc_horizontalStrut_1);
		
		JPanel panel = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel.getLayout();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.anchor = GridBagConstraints.NORTHWEST;
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.gridx = 3;
		gbc_panel.gridy = 4;
		resultsPanel.add(panel, gbc_panel);
		
		JButton defaultTextPaneConnectButton = new JButton("Connect");
		GridBagConstraints gbc_defaultTextPaneConnectButton = new GridBagConstraints();
		gbc_defaultTextPaneConnectButton.gridx = 3;
		gbc_defaultTextPaneConnectButton.gridy = 5;
		resultsPanel.add(defaultTextPaneConnectButton, gbc_defaultTextPaneConnectButton);
		
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
	
	
	private void createNodes(DefaultMutableTreeNode tree) {
	    DefaultMutableTreeNode category = null;
	    DefaultMutableTreeNode addRule = null;
	    
	    category = new DefaultMutableTreeNode("iRules");
	    tree.add(category);
	    
	    //TODO: move the try/catch block to getiRulesList()?
	    //TODO: Create a generic getSomethingList?
	    //TODO: create all the stuff needed around what happens when you click on an item in the list.
	    //TODO: Figure out how to handle the long path of the iRule's full name. IE do I pull off the path and just show the name, do I have expandable folders for the folders in the path? I like that idea but only if I automatically have 'common' be expanded.
	    //TODO: If I do the above I will need a way to remember what folders have been openened if the client does so.
	    
	    LocalLBRuleRuleDefinition[] myRules = null;
//	    JavaiRulesEditoriRuleDefinition[] myRules = null;
		try {
			myRules = getiRules();
//			myRules = getiRulesMyWay();
		} catch (Exception e) {
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e);
			exceptionHandler.processException();
			//Not:
			//e.printStackTrace();
		}
		
	    for (LocalLBRuleRuleDefinition rule : myRules){
//		for (JavaiRulesEditoriRuleDefinition rule : myRules){
	    	//TODO: Figure out how to add the actual RULE but have it display the rule name instead of the default results from rule.toString() which produces output not fit for humans.
	    	addRule = new DefaultMutableTreeNode(rule);
	    	    category.add(addRule);
	    	    
	    }
	        
//	    //original Tutorial
//	    book = new DefaultMutableTreeNode(new BookInfo
//	        ("The Java Tutorial: A Short Course on the Basics",
//	        "tutorial.html"));
//	    category.add(book);
	    

	    //...add more books for programmers...

//	    category = new DefaultMutableTreeNode("Books for Java Implementers");
//	    tree.add(category);

	    //VM
//	    book = new DefaultMutableTreeNode(new BookInfo
//	        ("The Java Virtual Machine Specification",
//	         "vm.html"));
//	    category.add(book);

	
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
        LocalLBRuleRuleDefinition nodeInfo = (LocalLBRuleRuleDefinition) node.getUserObject();
//        JavaiRulesEditoriRuleDefinition nodeInfo = (JavaiRulesEditoriRuleDefinition) node.getUserObject();
        
        
        // We do this the whole nodeInfo way because we have to check and see if it's a leaf node or not.
        if (node.isLeaf()) {
        	System.out.println("Rule " + nodeInfo + " Selected");
//            BookInfo book = (BookInfo)nodeInfo;
//            displayURL(book.bookURL);
//            if (DEBUG) {
//                System.out.print(book.bookURL + ":  \n    ");
//            }
        	s = "iRule Selection Event Detected";
        	
//    		JPanel resultsPanel = new JPanel();
//    		splitPane.setRightComponent(resultsPanel);




        	//TODO: Whoa, holycrapwow I think I found what I need. http://fifesoft.com/rsyntaxtextarea/ rsyntax text area is specifically a swing component for formatting CODE. you can do color highlighting stuff with the jeditorpane I'm already using but I would have to do it with things like appendToPane("hi", color.RED,paneToAppendTo); But this thing has rules like that already. It says it supports 30 languages. OOOH I hope one of them is TCL.
        	//Editor pane tutorial here: http://docs.oracle.com/javase/tutorial/uiswing/components/editorpane.html
            // and API def here: http://docs.oracle.com/javase/6/docs/api/javax/swing/JEditorPane.html
            
        	JPanel cp = new JPanel(new BorderLayout());

            rSyntaxTextArea = new RSyntaxTextArea(20, 60);
//            textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
            rSyntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_TCL);
            rSyntaxTextArea.setCodeFoldingEnabled(true);
            rSyntaxTextArea.setAntiAliasingEnabled(true);
            RTextScrollPane sp = new RTextScrollPane(rSyntaxTextArea);
            sp.setFoldIndicatorEnabled(true);
            cp.add(sp);

//            setContentPane(cp);
//            setTitle("Text Editor Demo");
//            setDefaultCloseOperation(EXIT_ON_CLOSE);
//            pack();
//            setLocationRelativeTo(null);
            
            
            
            //JeditorPane method
    		JPanel editorPanel = new JPanel();
    		GridBagLayout gbl_editorPanel = new GridBagLayout();
    		//TODO: Set these dynamically so they correspond to the size of the window on the screen
    		// I believe these numbers are in pixels. What really matters is each entry defines a colum or row. The number of entries defines how many of them there are. It's that simple.
    		gbl_editorPanel.columnWidths = new int[]{100, 100, 100, 100, 100, 100, 100};
    		gbl_editorPanel.rowHeights = new int[]{100, 100, 100, 100, 100, 100, 100, 100};
    		gbl_editorPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
    		gbl_editorPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
    		editorPanel.setLayout(gbl_editorPanel);
    		
    		iRuleEditorPane = new JEditorPane();
    		JScrollPane scrollPane_1 = new JScrollPane();
    		scrollPane_1.setViewportView(iRuleEditorPane);
    		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
    		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
    		gbc_scrollPane_1.gridheight = 8;
    		gbc_scrollPane_1.gridy = 0;
    		gbc_scrollPane_1.gridwidth = 7;
    		gbc_scrollPane_1.gridx = 0;
//    		editorPanel.add(scrollPane_1, gbc_scrollPane_1);
    		editorPanel.add(cp);
    				
    		JButton btnSave = new JButton("Save");
    		btnSave.addActionListener(this);
//    		actMnGetVirtuals.addActionListener(this)
    		GridBagConstraints gbc_btnSave = new GridBagConstraints();
    		gbc_btnSave.insets = new Insets(0, 0, 0, 5);
    		gbc_btnSave.anchor = GridBagConstraints.NORTHWEST;
    		gbc_btnSave.gridx = 2;
    		gbc_btnSave.gridy = 13;
    		editorPanel.add(btnSave, gbc_btnSave);
            
            rSyntaxTextArea.setText(nodeInfo.getRule_definition());
//    		iRuleEditorPane.setText(nodeInfo.getRule_definition());
            
            //TODO: figure out how to get the iRule content and stick it here
//            editPane.setText(iRuleContentHere);
  
            // getSelectedIndex is a function of JList not JTree. Bummer, let's find the JTree equivalent.
//            tree.getSelectedIndex();

            
            //TODO: Figure out howo tmake this remove whatever is currently there instead of statically coding 'resultsPanel' There will be a point when it's not 'resultsPanel' Quite often in fact. Like when it's now editPanelScrolePane1
            //I'm not sure if I need to do this but it seems like the right thing to do. Remove the existing contents of the right panel before setting a new one.
            splitPane.remove(resultsPanel);
//            splitPane.setRightComponent(editPaneScrollPane1);
//            splitPane.setRightComponent(editorPanel);
            splitPane.setRightComponent(cp);
        } else {
//            displayURL(helpURL); 
        }
    }
	
	//Put the treeexpansionlistener here
	public void whateverTheExpansionListenerMethodIsCalled(TreeExpansionEvent e) {
		//Don't forget to add TreeExpansionListener  to the interfaces implemented
		//Do stuff
	}
	
	//This is the action listener implementation. This handles when you click buttons or menu items.
	public void actionPerformed(ActionEvent e) {
        JMenuItem source = (JMenuItem)(e.getSource());
        String SourceType = source.getText();
        String s;
        
        if (SourceType == "Edit Settings") {
            s = "Edit Settings Event Detected."
                    + newline
                    + "    Event source: " + SourceType
                    + " (an instance of " + getClassName(source) + ")";
//            PreferencesDialog myPreferences = new PreferencesDialog();
//            setLabel("Edit Settings");
          int result = JOptionPane.showConfirmDialog(null, preferencesDialog.panel_1, 
          "Connection Preferences", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
        iIPAddress = preferencesDialog.ConnPreffsHostTextField.getText();
        iPort = Integer.parseInt( preferencesDialog.ConnPreffsPortTextField.getText());
        iUserName = preferencesDialog.ConnPreffsUserTextField.getText();
        iPassword = preferencesDialog.ConnPreffsPasswordTextField.getText();
        System.out.println("Host: " + iIPAddress);
        System.out.println("Port: " + iPort);
        System.out.println("User: " + iUserName);
        System.out.println("Pass: " + iPassword);
        //TODO: Set connected state to false if connection settings have been edited.
     }
        } else if (SourceType == "Connect") {
        	s = "Connect Event Detected.";
        	
        	
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

//        		bigip.setIgnoreInvalidCert(true);
        	
//        	try {
//				ic.setHost(iIPAddress);
//			} catch (MalformedURLException e1) {
//				f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e1);
//				exceptionHandler.processException();
//				//Not:
//				//${exception_var}.printStackTrace();
//			}
//        	//ic.setPort(iPort); // This doesn't exist.
//        	ic.setUsername(iUserName);
//        	ic.setPassword(iPassword);
        	//ic.initTransport() is private in iContro.BigIP. I need to either create BigIP.connect(vars[]) or just go w/ the constructor way.
          
          
        	//This might be something I could do to check and see if we have a valid connection.
        	//String version = bigip.SystemSystemInfo().get_version();

        	
    		//TODO: Move this part, the setting of the progress bar and displaying of connection validity out of this section and maybe back into the listener. We should only be returning true/false here.
    		if (successfullConnection == true) {
            	// TODO: Make this a check rather than printing to the console
            	lblStatusLabel.setText("Connected");
            	progressBar.setValue(100);
    		} else {
    			// scream, run and cry
    			progressBar.setValue(0);
    			System.out.println("Connection settings invalid");
    			lblStatusLabel.setText("Connection settings invalid");
    		}
            
        } else if (SourceType == "Disconnect") {
            s = "Disconnect Event Detected."
                    + newline
                    + "    Event source: " + SourceType
                    + " (an instance of " + getClassName(source) + ")";
        	System.out.println(s);
            JOptionPane.showMessageDialog(frame,
                    "The Disconnect Button Was Clicked.");
        } else if (SourceType == "List Virtual Servers"){
        	//TODO: Make 'List Virtual Servers' actually populate the jtree on the side
        	//TODO: Populate the gui with info and buttons which have actions for the virtual like and enable/disable (only one should be there at a time.)
        	//TODO: Then add some actions like enable/disable
        	s = "List Virtuals Detected";
//    		String [] meVirtuals = new String[0]; // Initialize an empty String array
        	System.out.println(s);
    		try {
    			String[] meVirtuals = getVirtualsList();
    		} catch (Exception f) {
    			// TODO Auto-generated catch block
    			System.out.println("Whoa, caught an error: " + f);
//    			e.printStackTrace();
    		}
        } else if (SourceType == "List iRules"){
        	//TODO: Make 'List iRules' actually populate the jtree on the side
        	s = "List iRules Detected";
//    		String [] meVirtuals = new String[0]; // Initialize an empty String array
        	System.out.println(s);
    		try {
    			String[] meRules = getiRuleList();
    		} catch (Exception g) {
    			// TODO Auto-generated catch block
    			System.out.println("Whoa, caught an error: " + g);
//    			e.printStackTrace();
    		}
        } else if (SourceType == "New iRule"){
        	//TODO: I'll have to figure out how to pass an argument to this action
        	//TODO: Also 'edit rule' is a flat lie. Right now it's just openning a blank text editor.
        	//TODO: For some reason this actually causes the jTree to get all screwed up. Make that not happen
        	//TODO: Make 'Edit Rule' Be the default action for a double click on an iRule in the list.
        	//TODO: Create a 'New Rule' function. This will probably go hand in hand with:
        	//TODO: Figure out how to handle renaming of iRules.
        	//TODO: Figure out how to handle 'offline iRules' IE one you have created but isn't uploaded yet.
        	s = "Edit Rule Detected";
        	
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
            
            // getSelectedIndex is a function of JList not JTree. Bummer, let's find the JTree equivalent.
//            tree.getSelectedIndex();

            
            //TODO: Figure out howo tmake this remove whatever is currently there instead of statically coding 'resultsPanel' There will be a point when it's not 'resultsPanel' Quite often in fact. Like when it's now editPanelScrolePane1
            //I'm not sure if I need to do this but it seems like the right thing to do. Remove the existing contents of the right panel before setting a new one.
            splitPane.remove(resultsPanel);
            splitPane.setRightComponent(editPaneScrollPane1);
            
        } else if (SourceType == "Save"){
        	//TODO: Apparently blank is a valid iRule as far as mcpd is concerned. Check to ensure the rule isn't completely blank and handle that.
        	//This will tell me what iRule is selected. Then I need to set the definition. tree.getLastSelectedPathComponent()
        	s = "Save detected";
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                    tree.getLastSelectedPathComponent();
            if (node == null) return;
            LocalLBRuleRuleDefinition nodeInfo = (LocalLBRuleRuleDefinition) node.getUserObject();
//            nodeInfo.setRule_definition(iRuleEditorPane.getText());// In theory this should work.
            nodeInfo.setRule_definition(rSyntaxTextArea.getText());// In theory this should work.
            //So just setting the definition like the above doesn't write it back to the bigIP. It only updates it in memory. HOWEVER, that is a good way so we can keep our data if we swtich to a different object but haven't saved yet.
            LocalLBRuleRuleDefinition[] saveRules = new LocalLBRuleRuleDefinition[1];
            saveRules[0] = nodeInfo;
            try {
				ic.getLocalLBRule().modify_rule(saveRules);
			} catch (RemoteException e1) {
				f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e1);
				exceptionHandler.processException();
				//Not:
				//e1.printStackTrace();
			} catch (Exception e1) {
				f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e1);
				exceptionHandler.processException();
				//Not:
				//e1.printStackTrace();
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
            s = "Un-Known Action Event Detected."
                    + newline
                    + "    Event source: " + SourceType
                    + " (an instance of " + getClassName(source) + ")";
            System.out.println(s);
        } 
        

        
        
        defaultResultsPanelTextPane.setText(s);

        //output.append(s + newline);
        //output.setCaretPosition(output.getDocument().getLength());
    }
	
	private boolean initializeConnection() {
		//TODO: Figure out how to get the status of this.
        // Hrm... interesting. The initialize(addy,port,username,password) method from the 'assembly' returns a boolean true/false.
        // The equivalent in the WSDL code is multiple steps and returns nothing.
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
			//Not:
			//e1.printStackTrace();
		} catch (ServiceException e1) {
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e1);
			exceptionHandler.processException();
			//Not:
			//e1.printStackTrace();
		} catch (Exception e1) {
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e1);
			exceptionHandler.processException();
		} // Let's just see if we can
		
		//TODO: Move this part, the setting of the progress bar and displaying of connection validity out of this section and maybe back into the listener. We should only be returning true/false here.
		if (version != null) {
        	// TODO: Make this a check rather than printing to the console
        	System.out.println("My Big-IP is version:"+version);
        	return true;
		} else {
			// scream, run and cry
			System.out.println("Connection settings invalid");
			return false;
		}
		

	}
	
	// Probably want to get rid of this after testing
	// Returns just the class name -- no package info.
    protected String getClassName(Object o) {
        String classString = o.getClass().getName();
        int dotIndex = classString.lastIndexOf(".");
        return classString.substring(dotIndex+1);
    }

    // I will delete this in a while after I update actionPerformed()
    public Container createContentPane() {
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
 
    /** Sets the text displayed at the bottom of the frame. */
    void setLabel(String newText) {
    	lblStatusLabel.setText(newText);
    }
    
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
	
	public String [] getiRuleList() throws Exception {
		String [] rule_list = ic.getLocalLBRule().get_list();
		System.out.println("Available Rules");
		for (String string : rule_list) {
			System.out.println("   " + string);
		}
		return rule_list;
	}
	
		
	
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

//		String[] rule_list = ic.getLocalLBRule().get_list();
		return iRules;
	}
	
	public JavaiRulesEditoriRuleDefinition[] getiRulesMyWay() {
		JavaiRulesEditoriRuleDefinition[] iRules = null;
		try {
			iRules = (JavaiRulesEditoriRuleDefinition[]) ic.getLocalLBRule().query_all_rules();
			System.out.println("Available Rules");
			for (JavaiRulesEditoriRuleDefinition rule : iRules) {
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

//		String[] rule_list = ic.getLocalLBRule().get_list();
		return iRules;
	}
    
    
}
