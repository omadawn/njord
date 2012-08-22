/**
 * 
 */
package com.f5.AaronForster.njord.util;

import java.io.IOException;

import org.fife.ui.rsyntaxtextarea.FileLocation;
import org.fife.ui.rsyntaxtextarea.TextEditorPane;

/**
 * @author forster
 *
 */
public class NjordTextEditorpane extends TextEditorPane {
	public boolean localRule = false;
	public boolean dirtyRule = true;
	
	/**
	 * 
	 */
	public NjordTextEditorpane() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param textMode
	 */
	public NjordTextEditorpane(int textMode) {
		super(textMode);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param textMode
	 * @param wordWrapEnabled
	 */
	public NjordTextEditorpane(int textMode, boolean wordWrapEnabled) {
		super(textMode, wordWrapEnabled);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param textMode
	 * @param wordWrapEnabled
	 * @param loc
	 * @throws IOException
	 */
	public NjordTextEditorpane(int textMode, boolean wordWrapEnabled,
			FileLocation loc) throws IOException {
		super(textMode, wordWrapEnabled, loc);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param textMode
	 * @param wordWrapEnabled
	 * @param loc
	 * @param defaultEnc
	 * @throws IOException
	 */
	public NjordTextEditorpane(int textMode, boolean wordWrapEnabled,
			FileLocation loc, String defaultEnc) throws IOException {
		super(textMode, wordWrapEnabled, loc, defaultEnc);
		// TODO Auto-generated constructor stub
	}
	
	
	
	/**
	 * Overridden from the original so that I can set it.
	 * @see org.fife.ui.rsyntaxtextarea.TextEditorPane#isDirty()
	 */
	@Override
	public boolean isDirty() {
		return dirtyRule;
	}

	/**
	 * Overridden so I can set this rule as non-dirty after loading it with text for the first time.
	 * @param isdirty
	 */
	public void isDirty(boolean isdirty) {
		dirtyRule = isdirty;
	}
	
	/** 
	 * Is this file local only or does it exist remotely?
	 * @see org.fife.ui.rsyntaxtextarea.TextEditorPane#isLocal()
	 */
	@Override
	public boolean isLocal() {
		return localRule;
	}

}
