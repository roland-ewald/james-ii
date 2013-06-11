/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.dialogs;

/**
 * Action interface for {@link BrowseFSDialogViaFileEndings} and the derived
 * classes.
 * 
 * @author Stefan Rybacki
 */
public interface IBrowseFSDialogListener {
  /**
   * Will be executed after a double click on an entry in
   * {@link BrowseFSDialogViaFileEndings} and his derived classes.
   * 
   * @param element
   *          The selected entry.
   */
  void elementChosen(IBrowseFSDialogEntry element);
}
