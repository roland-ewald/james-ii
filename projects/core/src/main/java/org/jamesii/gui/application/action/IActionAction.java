/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.action;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.KeyStroke;

/**
 * Wrapper class that creates a Swing {@link javax.swing.Action} class from an
 * {@link IAction} class.
 * 
 * @author Stefan Rybacki
 */
final class IActionAction extends AbstractAction implements
    PropertyChangeListener {
  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 4598983900055525458L;

  /**
   * the {@link IAction} to wrap
   */
  private final IAction action;

  /**
   * Creates a new wrapper for the given {@link IAction} providing a Swing
   * {@link javax.swing.Action} for further use.
   * 
   * @param action
   *          the action to wrap
   */
  public IActionAction(IAction action) {
    super();
    if (action == null) {
      throw new IllegalArgumentException("action can't be null!");
    }
    putValue(NAME, action.getLabel());
    putValue(SMALL_ICON, action.getIcon());
    putValue(SHORT_DESCRIPTION, action.getShortDescription());
    putValue(LONG_DESCRIPTION, action.getDescription());
    // if (action.getType() == ActionType.TOGGLEACTION)
    putValue(SELECTED_KEY, action.isToggleOn());
    KeyStroke stroke = KeyStroke.getKeyStroke(action.getKeyStroke());
    if (stroke != null) {
      putValue(ACCELERATOR_KEY, stroke);
    }
    if (action.getMnemonic() != null) {
      putValue(MNEMONIC_KEY, action.getMnemonic());
    }

    this.action = action;

    setEnabled(action.isEnabled());

    action.addPropertyChangeListener(this);
    addPropertyChangeListener(this);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (action != null) {
      action.execute();
    }
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    // propagate from IAction to Action
    if (evt.getSource() == action) {
      setEnabled(action.isEnabled());

      if (!action.getLabel().equals(getValue(NAME))) {
        putValue(NAME, action.getLabel());
      }
      if (getValue(SMALL_ICON) != action.getIcon()) {
        putValue(SMALL_ICON, action.getIcon());
      }
      if (!Boolean.valueOf(action.isToggleOn()).equals(getValue(SELECTED_KEY))
      /* && action.getType() == ActionType.TOGGLEACTION */) {
        putValue(SELECTED_KEY, action.isToggleOn());
      }

      putValue(SHORT_DESCRIPTION, action.getShortDescription());
      putValue(LONG_DESCRIPTION, action.getDescription());
    }
    // propagate from Action to IAction
    if (evt.getSource() == this) {
      action.setEnabled(isEnabled());
      if (!action.getLabel().equals(getValue(NAME))) {
        action.setLabel((String) getValue(NAME));
      }
      if (action.getIcon() != getValue(SMALL_ICON)) {
        action.setIcon((Icon) getValue(SMALL_ICON));
      }
      if (!Boolean.valueOf(action.isToggleOn()).equals(getValue(SELECTED_KEY))
      /* && action.getType() == ActionType.TOGGLEACTION */) {
        if (getValue(SELECTED_KEY) == null) {
          action.setToggleOn(false);
        } else {
          action.setToggleOn((Boolean) getValue(SELECTED_KEY));
        }
      }
    }
  }

}
