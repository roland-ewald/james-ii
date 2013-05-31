/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.base;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Abstract class implementing main functionality of
 * {@code IPropertyChangeSupport}. Extend from this class if you can to support
 * {@link IPropertyChangeSupport}.
 * 
 * @author Stefan Rybacki
 * 
 */
public class AbstractPropertyChangeSupport implements IPropertyChangeSupport {
  /**
   * support for property change events
   */
  private final PropertyChangeSupport changeSupport =
      new PropertyChangeSupport(this);

  @Override
  public synchronized void addPropertyChangeListener(PropertyChangeListener l) {
    changeSupport.addPropertyChangeListener(l);
  }

  @Override
  public synchronized void removePropertyChangeListener(PropertyChangeListener l) {
    changeSupport.removePropertyChangeListener(l);
  }

  /**
   * @see PropertyChangeSupport#firePropertyChange(String, Object, Object)
   * @param property
   *          {@link PropertyChangeSupport#firePropertyChange(String, Object, Object)}
   * @param oldValue
   *          {@link PropertyChangeSupport#firePropertyChange(String, Object, Object)}
   * @param newValue
   *          {@link PropertyChangeSupport#firePropertyChange(String, Object, Object)}
   * 
   */
  protected final synchronized void firePropertyChange(String property,
      Object oldValue, Object newValue) {
    changeSupport.firePropertyChange(property, oldValue, newValue);
  }

  /**
   * @param property
   *          {@link PropertyChangeSupport#firePropertyChange(String, boolean, boolean)}
   * @param oldValue
   *          {@link PropertyChangeSupport#firePropertyChange(String, boolean, boolean)}
   * @param newValue
   *          {@link PropertyChangeSupport#firePropertyChange(String, boolean, boolean)}
   * 
   * @see PropertyChangeSupport#firePropertyChange(String, boolean, boolean)
   */
  protected synchronized void firePropertyChange(String property,
      boolean oldValue, boolean newValue) {
    changeSupport.firePropertyChange(property, oldValue, newValue);
  }

  /**
   * @param property
   *          {@link PropertyChangeSupport#firePropertyChange(String, int, int)}
   * @param oldValue
   *          {@link PropertyChangeSupport#firePropertyChange(String, int, int)}
   * @param newValue
   *          {@link PropertyChangeSupport#firePropertyChange(String, int, int)}
   * @see PropertyChangeSupport#firePropertyChange(String, int, int)
   */
  protected synchronized void firePropertyChange(String property, int oldValue,
      int newValue) {
    changeSupport.firePropertyChange(property, oldValue, newValue);
  }

}
