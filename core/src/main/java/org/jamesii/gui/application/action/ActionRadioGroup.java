/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.action;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * This class can be used to build a radio group for a set of
 * {@link ToggleAction}s that are {@link ActionType#TOGGLEACTION}s. Which means
 * only one of the registered toggle actions can have its
 * {@link IAction#isToggleOn()} {@code true}
 * 
 * @author Stefan Rybacki
 */
public final class ActionRadioGroup implements PropertyChangeListener {
  /**
   * list of registered toggle actions
   */
  private final List<IAction> actions = new ArrayList<>();

  /**
   * Registers a new {@link ToggleAction} to be used as radio like action within
   * this group.
   * 
   * @param action
   *          the action to register
   */
  public void addAction(ToggleAction action) {
    if (actions.contains(action)) {
      return;
    }
    actions.add(action);
    action.addPropertyChangeListener(this);
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getPropertyName().equals("toggleOn")
        && Boolean.TRUE.equals(evt.getNewValue())) {
      // after registering a change in one of the registered toggle
      // actions to toggle state true all other actions must get
      // toggle state false
      for (IAction a : actions) {
        if (a != evt.getSource()) {
          a.setToggleOn(false);
        }
      }
      // TODO sr137: it might be the case that a toggle action can't
      // change to false for different reasons so this must be
      // addressed here too
    }
  }
}
