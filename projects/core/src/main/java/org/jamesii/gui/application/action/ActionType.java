/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.action;

/**
 * Enumeration of currently available and supported action types.
 * 
 * @author Stefan Rybacki
 */
public enum ActionType {
  /**
   * Basic action that can execute using {@link IAction#execute()} If an action
   * as sub actions in the action's tree this action will act as action set and
   * {@link IAction#execute()} is not called
   */
  ACTION,
  /**
   * Separator action representing a separator in menus or toolbars
   */
  SEPARATOR,
  /**
   * An action that represents a on or off state where
   * {@link IAction#isToggleOn()} is used to determine the state
   */
  TOGGLEACTION,

  /**
   * An action that represents a placeholder action in a menu or toolbar where
   * usually no action is associated.
   */
  ACTIONSET
}
