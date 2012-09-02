package com.f5.AaronForster.njord.test;

import static org.junit.Assert.assertTrue;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.fife.ui.rsyntaxtextarea.TextEditorPane;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uispec4j.UISpec4J;

import com.f5.AaronForster.njord.MainGuiWindow;
import com.f5.AaronForster.njord.ui.NjordTreeNode;
import com.f5.AaronForster.njord.util.f5ExceptionHandler;

public class SwingMainGuiWindowTests {
	//Some tests to write
	//Start app confirm that the app is started by checking the contents of the main notices window.
	//Open the preferences dialog and make a change. Confirm that change took place in the file.
	//Then replace the change
	//Then click confirm connection
	//    Confirm connection by checking the output of the initialize() iControl function and confirm the contents of the connection status indicator
	//Click list iRules - confirm the contents of the navigation tree has iRules
	// LATER click on branch nodes to ensure they do whatever branch nodes are supposed to do.
	//Choose an iRule from the list, make an incorrect modification. Confirm that we get an error message in the notices pane.
	//Then make a correct change to the same iRule. Confirm that the save is successfull by A) confirming that it says Save Successful and B) Getting the iRule itself from the bigip and confirming it matches our change.
	//Click new Irule
	// Confirm it is added to the list of iRules
	//Write some code to it and save it
	// Confirm it is saved through the same methods as the modify iRule test.

	/**
	 * SLF4J logger factory for logging.
	 */
	static final Logger log = LoggerFactory.getLogger(SwingMainGuiWindowTests.class);
	/**
	 * Holds the app itself
	 */
	MainGuiWindow window = null;
	/**
	 * A static version of the default constructor which initializes the UISpec4j framework.
	 */
	static {
		UISpec4J.init();
	}
	
	/**
	 * Runs at the beginning of each test.
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		//setUp() Is called before EVERY single test
		//TODO: This might need to be moved. I'm not sure all of the tests will need it. Oooh if I want to do anything that doesn't need the app I can put it in a different test class.
		//Start the app
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			window = new MainGuiWindow();
			window.frame.setVisible(true);
		} catch (ClassNotFoundException e) {
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e);
			exceptionHandler.processException();
		} catch (InstantiationException e) {
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e);
			exceptionHandler.processException();
		} catch (IllegalAccessException e) {
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e);
			exceptionHandler.processException();
		} catch (UnsupportedLookAndFeelException e) {
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e);
			exceptionHandler.processException();
		}
		
	}
	
	@Test
	public void testStartApplication() {
		//This is how we assert I assume there is an assertFalse as well. 
//		assertTrue(collection.isEmpty());
 
		//Start the app
//		try {
//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//			window = new MainGuiWindow();
//			window.frame.setVisible(true);
//		} catch (ClassNotFoundException e) {
//			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e, this, log);
//			exceptionHandler.processException();
//		} catch (InstantiationException e) {
//			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e, this, log);
//			exceptionHandler.processException();
//		} catch (IllegalAccessException e) {
//			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e, this, log);
//			exceptionHandler.processException();
//		} catch (UnsupportedLookAndFeelException e) {
//			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e, this, log);
//			exceptionHandler.processException();
//		}
		
		//confirm that the app is started by checking the contents of the main notices window.
		String notificationsBoxText = window.defaultResultsDevCentralURLTextPane.getText();
		log.info("Text box contains: " + notificationsBoxText);
		assertTrue(notificationsBoxText.matches("iRules.*"));	
		
	}
	
	//Skip this one for now. I think commenting @Test might do that.
//	@Test 
	public void testPreferences() {
		//TODO: Figure out how to get the prefs window object so I can click it's buttons and modify it's text. 
		//This isn't going to work if I instantiate the prefs class on it's own because the actionListener is in MainGuiWindow so I must do it here.
		//Open the preferences dialog 
		window.mntmEditSettings.doClick();
		String adminAccount = window.preferencesDialog.ConnPreffsUserTextField.getText();
		assertTrue(adminAccount.matches("admin"));
//		assertTrue(window.preferencesDialog.isShowing());
//		window.preferencesDialog.setVisible(false); //This doesn't hide it.
//		window.preferencesDialog.dispose(); // Neither does this
		
		//Confirm the dialog is open? But how?
		
		//Make a change. Confirm that change took place in the file.
//		window.preferencesDialog.ConnPreffsUserTextField.setText("TestUserName");
		//TODO: Figure out how to click OK when I don't have an explicit OK button to call a doClick() on.
		//Then replace the change
	}
	
	//Click confirm connection
	@Test
	public void testTxtPaneButtonVerfiyConnection() {
		//There are two different buttons to initialize the connection, test them both.
		window.defaultTextPaneConnectButton.doClick();
		assertTrue(window.bigIPVersion.matches("1.*"));
		log.debug("BigIP Version is " + window.bigIPVersion);
	}
	//TODO: Check for the second criteria here:
	//    Confirm connection by checking the output of the initialize() iControl function and confirm the contents of the connection status indicator

	@Test
	public void testVerifyConnectionMenuItem() {
		window.mntmConnect.doClick();
		assertTrue(window.bigIPVersion.matches("1.*"));
		log.debug("BigIP Version is " + window.bigIPVersion);
	}
	
	//Click list iRules - confirm the contents of the navigation tree has iRules
	@Test
	public void testListiRules() {
		window.btnGetiRules.doClick();
        NjordTreeNode node = (NjordTreeNode)
        		window.remoteTree.getLastLeaf(); 
        TextEditorPane nodeInfo = (TextEditorPane) node.getUserObject();
        //I might be able to get away with stopping here and this will fail if we haven't gotten an object of type TextEditorPane
        //TODO: this will fail on V10 machines. Gotta make sure this test runs against a v11 box.
        assertTrue(nodeInfo.getName().matches("/Common.*"));
        log.debug("Last rule is: " + nodeInfo.getName());
	}
	
	// LATER click on branch nodes to ensure they do whatever branch nodes are supposed to do.

	//Choose an iRule from the list, make an incorrect modification. Confirm that we get an error message in the notices pane.
	//Then make a correct change to the same iRule. Confirm that the save is successfull by A) confirming that it says Save Successful and B) Getting the iRule itself from the bigip and confirming it matches our change.
	@Test
	public void testModifyExistingiRule() {
		window.btnGetiRules.doClick();
		int numrules = window.remoteTree.getChildCount();
		for(int i=1; i<numrules +1; i++){
			
			log.info("Count is: " + i);
			NjordTreeNode node = (NjordTreeNode)
					window.remoteTree.getChildAt(i); // This is the branch node "iRules" //TODO: Fix it so I'm actually getting leaf nodes.
	        TextEditorPane nodeInfo = (TextEditorPane) node.getUserObject();
	        log.info("Log name is: " + nodeInfo.getName());
	        if (nodeInfo.getName() == "/Common/junit_test_irule") {
	        	//Do stuff
	        	String badiRuleCode = "This is not valid iRule code";
	        	String goodiRuleCode = "when CLIENT_ACCEPTED {\r\n    pool defaultpool\r\n}"; //Valid iRule Code I think.
	        	//TODO: Save uses tree.getLastSelectedPathComponent() But since there's no mouse event none will be selected. I don't see a way to select one. Figure out how to save an iRule programatically.
//	        	assertTrue(nodeInfo.getName().matches("/Common.*"));
	        } else {
	        	
	        }
			
       }
		
        
        //I might be able to get away with stopping here and this will fail if we haven't gotten an object of type TextEditorPane
        //TODO: this will fail on V10 machines. Gotta make sure this test runs against a v11 box.
	}

	@Test
	public void testSelectRule() {
		
	}
	//Click new Irule
	// Confirm it is added to the list of iRules
	//Write some code to it and save it
	// Confirm it is saved through the same methods as the modify iRule test.
	

}
