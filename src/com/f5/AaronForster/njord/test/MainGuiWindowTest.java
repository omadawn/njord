package com.f5.AaronForster.njord.test;

import static org.junit.Assert.assertTrue;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.tree.DefaultMutableTreeNode;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uispec4j.UISpec4J;

import com.f5.AaronForster.njord.MainGuiWindow;
import com.f5.AaronForster.njord.NjordiRuleDefinition;
import com.f5.AaronForster.njord.util.f5ExceptionHandler;

public class MainGuiWindowTest {
	
	//TODO: Create a special BIGIP v11 virtual so that we can't screw up something I want to use elsewhere.
	//TODO: Also create a special BIGIP v10 virtual so I can confirm V10 Functionality.
	//TODO: Figure out how to start and stop virtual servers for the tests.
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

	static final Logger log = LoggerFactory.getLogger(MainGuiWindowTest.class);

	MainGuiWindow window = null;

	//Hopefully BeforeClass is run at the beginning of the test only. 
//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
//		//TODO: This might need to be moved. I'm not sure all of the tests will need it. Oooh if I want to do anything that doesn't need the app I can put it in a different test class.
//		//Start the app
//		try {
//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//			MainGuiWindow window = new MainGuiWindow();
//			window.frame.setVisible(true);
//		} catch (ClassNotFoundException e) {
//			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e, log);
//			exceptionHandler.processException();
//		} catch (InstantiationException e) {
//			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e, log);
//			exceptionHandler.processException();
//		} catch (IllegalAccessException e) {
//			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e, log);
//			exceptionHandler.processException();
//		} catch (UnsupportedLookAndFeelException e) {
//			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e, log);
//			exceptionHandler.processException();
//		}
//	}

//	@AfterClass
//	public static void tearDownAfterClass() throws Exception {
//	}

	static {
		UISpec4J.init();
	}
	
	
	
	
	/**
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
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e, this, log);
			exceptionHandler.processException();
		} catch (InstantiationException e) {
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e, this, log);
			exceptionHandler.processException();
		} catch (IllegalAccessException e) {
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e, this, log);
			exceptionHandler.processException();
		} catch (UnsupportedLookAndFeelException e) {
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e, this, log);
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
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
        		window.top.getLastLeaf(); 
        NjordiRuleDefinition nodeInfo = (NjordiRuleDefinition) node.getUserObject();
        //I might be able to get away with stopping here and this will fail if we haven't gotten an object of type NjordiRuleDefinition
        //TODO: this will fail on V10 machines. Gotta make sure this test runs against a v11 box.
        assertTrue(nodeInfo.getName().matches("/Common.*"));
        log.debug("Last rule is: " + nodeInfo.getRule_name());
	}
	
	// LATER click on branch nodes to ensure they do whatever branch nodes are supposed to do.

	//Choose an iRule from the list, make an incorrect modification. Confirm that we get an error message in the notices pane.
	//Then make a correct change to the same iRule. Confirm that the save is successfull by A) confirming that it says Save Successful and B) Getting the iRule itself from the bigip and confirming it matches our change.
	@Test
	public void testModifyExistingiRule() {
		window.btnGetiRules.doClick();
		int numrules = window.top.getChildCount();
		for(int i=1; i<numrules +1; i++){
			
			log.info("Count is: " + i);
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)
					window.top.getChildAt(i); // This is the branch node "iRules" //TODO: Fix it so I'm actually getting leaf nodes.
	        NjordiRuleDefinition nodeInfo = (NjordiRuleDefinition) node.getUserObject();
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
		
        
        //I might be able to get away with stopping here and this will fail if we haven't gotten an object of type NjordiRuleDefinition
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
