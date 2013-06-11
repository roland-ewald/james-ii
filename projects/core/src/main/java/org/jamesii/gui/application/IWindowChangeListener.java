/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application;

import org.jamesii.gui.application.action.IAction;

/**
 * This interface is used for listeners that are notified whenever properties of
 * {@link IWindow} like title, icon or actions change.
 * 
 * @author Stefan Rybacki
 * 
 */
public interface IWindowChangeListener {
  /**
   * Called if the title of an {@link IWindow} changed
   * 
   * @param window
   *          the window which's title changed
   */
  void windowTitleChanged(IWindow window);

  /**
   * Called if the icon of an {@link IWindow} changed
   * 
   * @param window
   *          the window which's icon changed
   */
  void windowIconChanged(IWindow window);

  /**
   * Called if the actions of an {@link IWindow} changed
   * 
   * @param window
   *          the window which's actions changed
   * @param oldActions
   *          the actions before the change
   */
  void windowActionsChanged(IWindow window, IAction[] oldActions);
}
