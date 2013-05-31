/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.wizard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;

/**
 * Simple canceling action used by the {@link Wizard} class.
 * 
 * @author Stefan Rybacki
 * 
 */
class CancelAction extends AbstractAction {
  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 2794520553772691068L;

  /**
   * the action listener
   */
  private final ActionListener listener;

  /**
   * Creates a new canceling action using the specified listener for
   * notifications.
   * 
   * @param l
   *          the action listener
   */
  public CancelAction(ActionListener l) {
    super("Cancel");

    listener = l;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (listener != null) {
      listener.actionPerformed(e);
    }
  }

}
