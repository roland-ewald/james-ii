/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class AbstractMenuButtonModel implements IMenuButtonModel {

  private final PropertyChangeSupport pcSupport = new PropertyChangeSupport(
      this);

  @Override
  public final void addPropertyChangeListener(PropertyChangeListener listener) {
    pcSupport.addPropertyChangeListener(listener);
  }

  // public final void removePropertyChangeListener(PropertyChangeListener
  // listener) {
  // pcSupport.removePropertyChangeListener(listener);
  // }

  protected final void fireDefaultActionChanged() {
    pcSupport.firePropertyChange(ACTION, null, getDefaultAction());
  }

  protected final void firePopUpMenuChanged() {
    pcSupport.firePropertyChange(MENU, null, getPopUpMenu());
  }
}
