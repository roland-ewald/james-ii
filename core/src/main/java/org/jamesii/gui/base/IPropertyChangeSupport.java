/*
 * The general modeling and simulation frameworkimport java.beans.PropertyChangeListener;
of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.base;

import java.beans.PropertyChangeListener;

/**
 * Interface that needs to be implemented by a model that should be able to be
 * bound.
 * 
 * @author Stefan Rybacki
 * 
 */
public interface IPropertyChangeSupport {
  /**
   * adds a property change listener
   * 
   * @param l
   *          the listener to add
   */
  void addPropertyChangeListener(PropertyChangeListener l);

  /**
   * removes a property change listener
   * 
   * @param l
   *          the listener to remove
   */
  void removePropertyChangeListener(PropertyChangeListener l);
}
