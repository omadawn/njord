/**
 * 
 */
package com.f5.AaronForster.javaiRulesEditor;

/**
 * This is the main class for the app. This is waht will be run wich will then launch the gui and deal with it.
 * Maybe, on the other hand I might ahve to have logic in the GUI portion itself I really haven't ever worked with Swing before so who bloody well knows.
 * 
 * @author Aaron Forster @date 20120601
 * @version 0.1
 */

//TODO: Figure out how to implement the ActionPanel in such a way that I can have an ActionPanelByType
// ActionPanelByType will initially be an abstract class I will write a default implementation of that generic class as a reference/starting point but I will use an abstract class to define the requirements for that pane.
// ActionPanelByType will be the portion of the gui that displays an item which you have selected from the list. There will need to be a 'default' type which will likely just show a welcome message or something simple. Perhaps a couple of important actions like connect and edit preferences.
// The Second one I will write will be the Virtual Server pane which will only have enable/disable and a couple of text items. The first of course is the default
// 
//TODO: Implement a default user preferences file. At startup check for a file in say $HOME/.DesktopBigIPManager/preferences.txt If it exists pull from and write to it for the saved prefs. Otherwise use a default value. This however will not only be cool for users but
//TODO: MainGuiWindow class has a Main() in it. Apparently we start the app from there not from a separate class.
//TODO: Get rid of any auto generated stack traces. I wonder if there's a code style setting or maybe a plugin that would make it write an error instead of stupidly dumping the stack.
//TODO: Implement a logging mechanism.
public class AppLogicClass {
	/**
	 * @param args
	 */
	private String logPrefix = "AppLogicClass: ";
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	
	}

}
