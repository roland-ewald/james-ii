/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application;

/**
 * Listener interface that provides notifications for events of an
 * {@link IWindow}. Those events can be
 * <ul>
 * <li>activating</li>
 * <li>deactivating</li>
 * <li>closing</li>
 * <li>opening</li>
 * </ul>
 * of an {@link IWindow}.
 * 
 * @author Stefan Rybacki
 * @see IWindow
 * 
 */
public interface IWindowListener {
  /**
   * Called if an {@link IWindow} switched to state deactivated.
   * 
   * @param window
   *          the window that was deactivated
   */
  void windowDeactivated(IWindow window);

  /**
   * Called if an {@link IWindow} switched to state activated.
   * 
   * @param window
   *          the window that was activated
   */
  void windowActivated(IWindow window);

  /**
   * Called if an {@link IWindow} was closed
   * 
   * @param window
   *          the window that was closed
   */
  void windowClosed(IWindow window);

  /**
   * Called if an {@link IWindow} was opened
   * 
   * @param window
   *          the window that was opened
   */
  void windowOpened(IWindow window);
}
