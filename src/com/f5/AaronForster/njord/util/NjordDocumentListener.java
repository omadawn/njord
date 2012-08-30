package com.f5.AaronForster.njord.util;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.f5.AaronForster.njord.MainGuiWindow;

/**
 * @author forster
 *
 */
public class NjordDocumentListener implements DocumentListener {
	/**
	 * Holds the logger factory for SLF4J so we can log.
	 */
	final Logger log = LoggerFactory.getLogger(NjordDocumentListener.class);
	/**
	 * Holder for the primary class so that we interact with it and do things like update the contents of the notices box.
	 */
	public MainGuiWindow owner = null;
	
	/**
	 * An overridden constructor which sets the owner.
	 * @param owner
	 */
	public NjordDocumentListener(MainGuiWindow owner) {
		this.owner = owner;
	}
	
	/**
	 * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
	 */
	@Override
	public void changedUpdate(DocumentEvent e) {
        //Plain text components apparently don't fire these events.
	}
	
	/**
	 * Enables the save button an triggers a repaint of the navigation tree which will display the documents now dirty status.
	 * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
	 */
	@Override
	public void insertUpdate(DocumentEvent e) {
		log.debug("Insert on " + e);
		owner.navigationTree.repaint();
		owner.btnActionSave.setEnabled(true);
	}
	
	/**
	 * Enables the save button an triggers a repaint of the navigation tree which will display the documents now dirty status.
	 * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
	 */
	@Override
	public void removeUpdate(DocumentEvent e) {
		log.info("Remove on " + e);
		owner.navigationTree.repaint();
		owner.btnActionSave.setEnabled(true);
	}
}
