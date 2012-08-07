package com.f5.AaronForster.njord.test;


import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.junit.Test;
import org.uispec4j.MenuBar;
import org.uispec4j.TextBox;
import org.uispec4j.UISpec4J;
import org.uispec4j.UISpecAdapter;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;

import com.f5.AaronForster.njord.MainGuiWindow;
import com.f5.AaronForster.njord.util.f5ExceptionHandler;

public class Uispec4jMainGuiWindowTest extends UISpecTestCase  implements UISpecAdapter {
//	static final Logger log = LoggerFactory.getLogger(MainGuiWindowTest.class);

	static {
		UISpec4J.init();
	}
	
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
	public void testPreferencesDialog() {
		final Window window = getMainWindow();
		window.containsMenuBar(); //This succeeds
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// But this fails with an error that the menubar shouldn't be null. WTF?
				MenuBar myMenuBar = window.getMenuBar();
				String menuBarName = myMenuBar.getName();
				}
			});
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			f5ExceptionHandler exceptionHandler = new f5ExceptionHandler(e);
			exceptionHandler.processException();
		}
//		window.getButton("defaultTxtPnConnectButton").click();
		 //We never make it to here since it's unable to get the menubar from the previous method.
//		WindowInterceptor.init(window.getMenuBar().getMenu("ConnectionMenu").getSubMenu("editSettingsBtn").triggerClick())
//		  .process(new WindowHandler() {
//		    public Trigger process(Window dialog) {
//		      assertTrue(dialog.titleEquals("Category name:"));
//		      return dialog.getButton("Cancel").triggerClick();
//		    }
//		  })
//		  .run();
		
		
//		window.mntmEditSettings.doClick();
//		String adminAccount = window.preferencesDialog.ConnPreffsUserTextField.getText();
//		assertTrue(adminAccount.matches("admin"));
	}
	
	
	
	
	
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
