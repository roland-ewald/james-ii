/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.action;

import java.beans.PropertyChangeListener;

import javax.swing.Icon;

import org.jamesii.gui.application.IWindow;

/**
 * Interface for actions that can be used within the JAMES II GUI.
 * 
 * @author Stefan Rybacki
 * @see ActionType
 * @see ActionManager
 */
public interface IAction {

  /**
   * Gets the window the action belongs to.
   * 
   * @return the window the action belongs to or null if global action
   */
  IWindow getWindow();

  /**
   * @return the action's unique id
   */
  String getId();

  /**
   * @return the action's label used in menus for instance
   */
  String getLabel();

  /**
   * Sets the action's label
   * 
   * @param label
   *          the label
   */
  void setLabel(String label);

  /**
   * @return the action's key stroke as version that
   *         {@link javax.swing.KeyStroke#getKeyStroke(String) understands}
   */
  String getKeyStroke();

  /**
   * @return the mnemonic of the action
   */
  Integer getMnemonic();

  /**
   * @return the paths the action can be found at (menu, toolbars, etc.)
   */
  String[] getPaths();

  /**
   * @return the path to the action's icon
   */
  Icon getIcon();

  /**
   * @return a description describing the action's purpose in short (can be used
   *         for ToolTips)
   */
  String getShortDescription();

  /**
   * @return a description describing the action's purpose in a general way
   */
  String getDescription();

  /**
   * executes the action
   */
  void execute();

  /**
   * adds a property change listener to the action to keep track of changes to
   * the action
   * 
   * @param l
   *          the listener to attach
   */
  void addPropertyChangeListener(PropertyChangeListener l);

  /**
   * removes a previously attached property change listener
   * 
   * @param l
   *          the listener to remove
   */
  void removePropertyChangeListener(PropertyChangeListener l);

  /**
   * enables/disables this action
   * 
   * @param e
   *          true if action should be enabled false else
   */
  void setEnabled(boolean e);

  /**
   * @return true if this action is enabled false else
   */
  boolean isEnabled();

  /**
   * @return the action's type
   */
  ActionType getType();

  /**
   * @return true if the current toggle state is on
   */
  boolean isToggleOn();

  /**
   * Sets the toggle state
   * 
   * @param toggleState
   *          the state to set
   */
  void setToggleOn(boolean toggleState);

  /**
   * Sets the icon.
   * 
   * @param icon
   *          the new icon
   */
  void setIcon(Icon icon);
}
