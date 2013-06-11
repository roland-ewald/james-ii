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

import org.jamesii.gui.application.resource.IconManager;
import org.jamesii.gui.application.resource.iconset.IconIdentifier;

/**
 * Simple backward action used in the {@link Wizard} class.
 * 
 * @author Stefan Rybacki
 */
class BackwardAction extends AbstractAction {
  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 2794520553772691067L;

  /**
   * the action listener
   */
  private final ActionListener listener;

  /**
   * Creates a new backwarding action using the specified listener for
   * notifications.
   * 
   * @param l
   *          the action listener
   */
  public BackwardAction(ActionListener l) {
    super("Back");

    listener = l;

    putValue(SMALL_ICON,
        IconManager.getIcon(IconIdentifier.BACKWARD_SMALL, "<"));
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (listener != null) {
      listener.actionPerformed(e);
    }
  }

}
