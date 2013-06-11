/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.preferences;

/**
 * This interface provides access to the events of an {@link IPreferencesPage}.
 * 
 * @author Stefan Rybacki
 */
public interface IPreferencesPageListener {
  /**
   * Should be called by a preferences page whenever the valid state of that
   * page changed so that a listener can react on that state.
   * 
   * @param page
   *          the issuing page
   */
  void validStateChanged(IPreferencesPage page);
}
