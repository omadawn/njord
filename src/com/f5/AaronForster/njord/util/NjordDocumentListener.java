package com.f5.AaronForster.njord.util;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.f5.AaronForster.njord.MainGuiWindow;

public class NjordDocumentListener implements DocumentListener {
	final Logger log = LoggerFactory.getLogger(NjordDocumentListener.class);
	MainGuiWindow owner = null;
	
	public NjordDocumentListener(MainGuiWindow owner) {
		this.owner = owner;
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
        //Plain text components apparently don't fire these events.

	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		log.info("Insert on " + e);
		owner.tree.repaint();
		owner.btnActionSave.setEnabled(true);
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		log.info("Remove on " + e);
		owner.tree.repaint();
		owner.btnActionSave.setEnabled(true);
	}

}
