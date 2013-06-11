/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.action;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.KeyStroke;

import org.jamesii.gui.application.IWindow;

/**
 * Wrapper class that creates an {@link IAction} from a standard Swing
 * {@link Action} instance.
 * 
 * @author Stefan Rybacki
 * 
 */
public final class ActionIAction extends AbstractAction implements
    PropertyChangeListener {
  /**
   * the swing action
   */
  private Action action;

  /**
   * Creates a new wrapper for a given swing {@link Action} implementing the
   * {@link IAction} interface for JAMES II actions.
   * 
   * @param action
   *          the swing action
   * @param id
   *          the action id
   * @param paths
   *          the action's paths
   * @param window
   *          the window the action belongs to
   */
  public ActionIAction(Action action, String id, String[] paths, IWindow window) {
    super(id, null, paths, window);
    if (action == null) {
      throw new IllegalArgumentException("Action can't be null!");
    }
    this.action = action;
    action.addPropertyChangeListener(new WeakPropertyChangeListenerProxy(this));
    setEnabled(action.isEnabled());
  }

  @Override
  public void setLabel(String label) {
    super.setLabel(label);
    action.putValue(Action.NAME, label);
  }

  @Override
  public void execute() {
    action.actionPerformed(null);
  }

  @Override
  public String getDescription() {
    return (String) action.getValue(Action.LONG_DESCRIPTION);
  }

  @Override
  public boolean isEnabled() {
    return action.isEnabled();
  }

  @Override
  public void setEnabled(boolean e) {
    super.setEnabled(e);
    action.setEnabled(e);
  }

  @Override
  public Icon getIcon() {
    return (Icon) action.getValue(Action.SMALL_ICON);
  }

  @Override
  public String getKeyStroke() {
    if (action.getValue(Action.ACCELERATOR_KEY) != null) {
      return ((KeyStroke) action.getValue(Action.ACCELERATOR_KEY)).toString();
    }
    return null;
  }

  @Override
  public String getLabel() {
    return (String) action.getValue(Action.NAME);
  }

  @Override
  public Integer getMnemonic() {
    return (Integer) action.getValue(Action.MNEMONIC_KEY);
  }

  @Override
  public String getShortDescription() {
    return (String) action.getValue(Action.SHORT_DESCRIPTION);
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    firePropertyChange("action", null, action);
  }

  @Override
  public void setToggleOn(boolean toggleState) {
    action.putValue(Action.SELECTED_KEY, toggleState);
  }

  @Override
  public boolean isToggleOn() {
    if (action.getValue(Action.SELECTED_KEY) == null) {
      return false;
    }
    return (Boolean) action.getValue(Action.SELECTED_KEY);
  }

  @Override
  public void setIcon(Icon icon) {
    action.putValue(Action.SMALL_ICON, icon);
  }

}
