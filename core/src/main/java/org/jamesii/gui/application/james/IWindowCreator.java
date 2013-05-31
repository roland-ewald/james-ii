/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.james;

import java.awt.Component;

import javax.swing.JComponent;

import org.jamesii.gui.application.Contribution;
import org.jamesii.gui.application.IWindow;
import org.jamesii.gui.application.IWindowManager;

/**
 * Interface used by {@link WindowManager} to provide exchangable ways to create
 * and represent {@link IWindow}s.
 * 
 * @author Stefan Rybacki
 * 
 */
public interface IWindowCreator {
  /**
   * Shows the specified window in the way the creator represents
   * {@link IWindow}s.
   * 
   * @param window
   *          the window to show
   * @return the component responsible for containing the {@link IWindow}
   */
  Component showWindow(IWindow window);

  /**
   * Closes the specified window. The creator knows where to find this window
   * and how to close it.
   * 
   * @param window
   *          the window to close
   */
  void closeWindow(IWindow window);

  /**
   * It might be the case that the windows are managed in tabs rather than
   * individual windows so this method tries to bring the specified window to
   * front.
   * 
   * @param window
   *          the window that should be made visible
   */
  void makeVisible(IWindow window);

  /**
   * Changes the contribution of the specified {@link IWindow} to the one
   * specified.
   * 
   * @param window
   *          the window which's contribution to change
   * @param toContribution
   *          the new contribution
   * @return the component containing the window (because this component might
   *         change e.g. when changing from {@link Contribution#DIALOG} to any
   *         other)
   */
  Component changeContribution(IWindow window, Contribution toContribution);

  /**
   * Called when the application closes so the creator can clean up and or
   * delegate the event
   */
  void exitingApplication();

  /**
   * Returns the actual {@link Contribution} the specified window is currently
   * in. This might differ from {@link IWindowManager#getContribution(IWindow)}
   * and {@link IWindow#getContribution()}
   * 
   * @param window
   *          the specified window
   * @return null if window not found, Contribution else
   */
  Contribution getWindowContribution(IWindow window);

  /**
   * Creates a Swing container that can and actually holds the specified
   * {@link IWindow} using the given relative action path.
   * 
   * @param window
   *          the window to create a Swing container for
   * @param actionPath
   *          the relative action url
   * @return a Swing container holding the specified {@link IWindow}
   */
  JComponent createContainer(IWindow window, String actionPath);
}
