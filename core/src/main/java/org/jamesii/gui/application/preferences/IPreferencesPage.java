/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.preferences;

import javax.swing.JComponent;

import org.jamesii.gui.application.IProgressListener;

/**
 * 
 * @author Stefan Rybacki
 * 
 */
public interface IPreferencesPage {
  /**
   * Called when the preferences page is called once when the preferences dialog
   * is shown and before {@link #getPageContent()}
   */
  void init();

  /**
   * issued when the preferences dialog is closed (note: not when another page
   * is displayed)
   */
  void closed();

  /**
   * Returns the preference pages content. That means usually a preferences
   * form.
   * 
   * @return the pages content
   */
  JComponent getPageContent();

  /**
   * Will be called when OK or Apply is issued on the page so it can store the
   * preferences.
   * 
   * @param progress
   *          a listener that is notified about the progress of the applying
   *          process
   * 
   */
  void applyPreferences(IProgressListener progress);

  /**
   * is issued when the preferences dialog is canceled or closed without
   * confirming to apply changes
   */
  void cancelPreferences();

  /**
   * Is invoked when the default settings shall be restored.
   */
  void restoreDefaults();

  /**
   * Pages with same location are being tabbed in the dialog
   * 
   * @return location url (path1/path2/path3)
   */
  String getLocation();

  /**
   * @return true if the values entered for the current page are valid and
   *         therefore an apply can be issued. If false only canceling the
   *         dialog will be possible.
   */
  boolean isValid();

  /**
   * Adds a listener that can be notified whenever {@link #isValid()} state
   * might has changed.
   * 
   * @param l
   *          the listener to add
   */
  void addPreferencePageListener(IPreferencesPageListener l);

  /**
   * Removes a previously registered listener
   * 
   * @param l
   *          the listener to remove
   */
  void removePreferencePageListener(IPreferencesPageListener l);

  /**
   * @return the pages title
   */
  String getTitle();
}
