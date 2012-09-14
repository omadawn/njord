package com.f5.AaronForster.njord.util;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.event.AWTEventListener;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.swing.JFrame;

public class WindowSaver implements AWTEventListener {
	private static WindowSaver saver;
	private Map framemap;

	private WindowSaver( ) {
		framemap = new HashMap( );
	}

	/* (non-Javadoc)
	 * @see java.awt.event.AWTEventListener#eventDispatched(java.awt.AWTEvent)
	 */
	@Override
	public void eventDispatched(AWTEvent evt) {
		try {
			if(evt.getID( ) == WindowEvent.WINDOW_OPENED) {
				ComponentEvent cev = (ComponentEvent)evt;
				if(cev.getComponent( ) instanceof JFrame) {
					JFrame frame = (JFrame)cev.getComponent( );
					loadSettings(frame);
				}
			}
		} catch(Exception ex) {
			System.out.println("Error: " + ex.toString( ));
		}
	}

	public static void loadSettings(JFrame frame) throws IOException {
		Properties settings = new Properties( );
		settings.load(new FileInputStream("configuration.props"));
		String name = frame.getName( );
		int x = getInt(settings,name+".x",100);
		int y = getInt(settings,name+".y",100);
		int w = getInt(settings,name+".w",500);
		int h = getInt(settings,name+".h",500);
		frame.setLocation(x,y);
		frame.setSize(new Dimension(w,h));
		saver.framemap.put(name,frame);
		frame.validate( );
	}

	public static void saveSettings( ) throws IOException {
		Properties settings = new Properties( );
		settings.load(new FileInputStream("configuration.props"));

		Iterator it = saver.framemap.keySet( ).iterator( );
		while(it.hasNext( )) {    
			String name = (String)it.next( ); 
			JFrame frame = (JFrame)saver.framemap.get(name);    
			settings.setProperty(name+".x",""+frame.getX( ));    
			settings.setProperty(name+".y",""+frame.getY( ));    
			settings.setProperty(name+".w",""+frame.getWidth( ));    
			settings.setProperty(name+".h",""+frame.getHeight( ));
		} 
		settings.store(new FileOutputStream("configuration.props"),null); 
	}

	public static int getInt(Properties props, String name, int value) { 
		String v = props.getProperty(name); 
		if(v == null) {
			return value;
		}
		return Integer.parseInt(v);

	}

	public static WindowSaver getInstance( ) {
		if(saver == null) {
			saver = new WindowSaver( );
		}
		return saver;
	}
}