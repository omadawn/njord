package com.f5.AaronForster.njord.test;


import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.junit.Test;
import org.uispec4j.Button;
import org.uispec4j.TextBox;
import org.uispec4j.Trigger;
import org.uispec4j.UISpec4J;
import org.uispec4j.UISpecAdapter;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
import org.uispec4j.interception.WindowHandler;
import org.uispec4j.interception.WindowInterceptor;

import com.f5.AaronForster.njord.MainGuiWindow;
import com.f5.AaronForster.njord.util.f5ExceptionHandler;

/*TODOS SECTION
 * 
 * 
 * 
 */


public class Uispec4jMainGuiWindowTest extends UISpecTestCase  implements UISpecAdapter {
//	static final Logger log = LoggerFactory.getLogger(SwingMainGuiWindowTests.class);
	public MainGuiWindow realWindow = null;
	public int initialRuleCount = 5; // Move this to preferences or Something.
	public String iRulesTreeExpectedContent = "BigIP\n" +
		    "  iRules\n" +
			"    /Common/junit_test_irule\n" +
			"    /Common/yetAnotherRule\n" +
			"    /Common/new_rule\n" +
			"    /Common/myIrulesOutputTest\n" +
			"    /Common/http_responder";
	public String connectionVerifiedString = "Connected to BIG-IP version: 11.1.0";

	static {
		UISpec4J.init();
	}
	
	
	//Start app confirm that the app is started by checking the contents of the main notices window.
	@Test
	public void testStartMainWindow() {
		Window window = getMainWindow();
		TextBox iRulesURLBox = window.getTextBox("DevCentralURLBox");
//		assertTrue();
		iRulesURLBox.textContains("iRules.*");
		//This is from the original non-uispec4j test implementation
//		String notificationsBoxText = window.defaultResultsDevCentralURLTextPane.getText();
//		log.info("Text box contains: " + notificationsBoxText);
//		assertTrue(notificationsBoxText.matches("iRules.*"));	
	}
	
	
	@Test
	public void testPreferences() {
		//Open the preferences dialog and make a change. Confirm that change took place in the file.
		//Then replace the change
		//Then click confirm connection
		//    Confirm connection by checking the output of the initialize() iControl function and confirm the contents of the connection status indicaton
		fail("Preferences tests not yet implemented in uispec4j due to issues with getMenu()");
	}
	
	
	@Test
	public void testConfirmConnection() {
		//Confirm connectivity through the 'verify connection' button on the front page.
		Window window = getMainWindow();
		window.getButton("defaultTxtPnConnectButton").click(); //Connect
		assertFalse(window.getTextBox("noticesTextBox").isEditable());
		assertTrue(window.getTextBox("noticesTextBox").textContains(connectionVerifiedString)); //TODO: I think this test will succeed even when the connection isn't there.
//		assertFalse(window.getTextBox("noticesTextBox").textContains("Connection Verified"));
		
	}
	
	@Test
	public void testGetiRulesList() {
		//Click list iRules - confirm the contents of the navigation tree has iRules
		Window window = getMainWindow();
		//Let's find out if it's time to learn how to use doLater()
		window.getButton("listiRulesButton").click();
		assertEquals(initialRuleCount, window.getTree().getChildCount("iRules"));
		//This is a little lame since it will need to be updated every time the list changes but it's a good start
		assertTrue(window.getTree().contentEquals(iRulesTreeExpectedContent));
	
//		window.getTree().expandAll();
		
//		int iRulesCount = window.getTree().getChildCount("BigIP/iRules");

		// LATER click on branch nodes to ensure they do whatever branch nodes are supposed to do.
		
	}
	
	@Test
	public void testExistingiRule() {
		Window window = getMainWindow();
		//This doesn't work due to it failing to interpret the fact that the iRule name has a path in it. I was planning on getting rid of that anyway. I'm going to go extreme programming here and write the test first and leave this in here. ^_^
		window.getButton("listiRulesButton").click();
		window.getTree().click("iRules/Common/new_rule");
		assertTrue(window.getTextBox("editorPane").textContains("when CLIENT_ACCEPTED"));
		//Choose an iRule from the list, make an incorrect modification. Confirm that we get an error message in the notices pane.
		//Then make a correct change to the same iRule. Confirm that the save is successfull by A) confirming that it says Save Successful and B) Getting the iRule itself from the bigip and confirming it matches our change.
		
		
	}
	
	@Test
	public void testCreateNewiRule() {
		//TODO: I'm going to have to add some functionality here to record the list of iRules so that the tests will continue to work after I do this step
		Window window = getMainWindow();
		//Click new Irule
		WindowInterceptor.init(window.getButton("newiRuleButton").triggerClick())
		  .process(new WindowHandler() {
		    public Trigger process(Window dialog) {
		      assertTrue(dialog.titleEquals("New iRule"));
		      //TODO: Turn this into junit_test_irule_${DATE} or something for uniqueness
		      dialog.getInputTextBox("iRuleNameTextbox").setText("new_junit_test_irule");
//		      dialog.getInputTextBox().setText(categoryName);
		      return dialog.getButton("OK").triggerClick();
		    }
		  })
		  .run();
		
		// Confirm it is added to the list of iRules
		int newRuleCount = initialRuleCount + 1;
		assertEquals(newRuleCount, window.getTree().getChildCount("iRules"));
		
		//TODO: Add this part
		//Write some code to it and save it
		// Confirm it is saved through the same methods as the modify iRule test.
		// I'm going to need to add a 'Delete iRule' button really soon if I keep this up.
	}

	
//	WindowInterceptor.init(window.getButton("New Category").triggerClick())
//	  .process(new WindowHandler() {
//	    Trigger process(Window dialog) {
//	      assertTrue(dialog.titleEquals("Category name:"));
//	      dialog.getInputTextBox().setText(categoryName);
//	      return dialog.getButton("OK").triggerClick();
//	    }
//	  })
//	  .run();
	
	
	
	public Window getMainWindow() {
		MainGuiWindow window = null;
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			window = new MainGuiWindow();
//			window.frame.setVisible(true);
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
		Window somewindow = new Window(window.frame);
		return somewindow;
	}
}
