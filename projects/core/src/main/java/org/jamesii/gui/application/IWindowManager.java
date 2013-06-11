/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application;

import java.awt.Window;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * An interface used to define a window manager used in an {@link IApplication}.
 * It is supposed to be responsible for window creating, opening, closing,
 * deactivating and activating as well as default placement of those windows.
 * Managed windows should be addressed, activated and so only thru the window
 * manager implementing this {@link IWindowManager} interface.
 * 
 * @author Stefan Rybacki
 * 
 * @see AbstractWindowManager
 * 
 */
public interface IWindowManager {
  /**
   * Adds and opens a new window specified by the {@link IWindow} interface.
   * 
   * @param window
   *          the window to open
   */
  void addWindow(IWindow window);

  /**
   * Closes the specified window if previously registered and opend thru
   * {@link #addWindow(IWindow)}
   * 
   * @param window
   *          the window to close
   * @return true if window can be closed (so only take actions if this method
   *         returns true or if {@link IWindow#windowClosed()} is called
   */
  boolean closeWindow(IWindow window);

  /**
   * Sets the specified window as the currently active {@link IWindow} and is
   * supposed to make it visible if possible and if specified window was
   * previously registered to the manager.
   * 
   * @param window
   *          the window to activate
   */
  void activateWindow(IWindow window);

  /**
   * Deactivates the specified window if previously registered. Deactivating
   * doesn't mean closing it basically means losing foucs.
   * 
   * @param window
   *          the window to deactivate
   */
  void deactivateWindow(IWindow window);

  /**
   * @return all registered and not yet closed windows
   */
  List<IWindow> getWindows();

  /**
   * @return number of still open registered windows
   */
  int getWindowCount();

  /**
   * @param index
   *          index must be >=0 and < {@link #getWindowCount()} and represents
   *          the index of the window to return
   * @return the window specified by index
   */
  IWindow getWindow(int index);

  /**
   * @param window
   *          the window the visibility state is to determine
   * @return true if visible false else
   */
  boolean isVisible(IWindow window);

  /**
   * @param window
   *          the window the active state is to determine
   * @return true if specified window is the currently active window
   */
  boolean isActive(IWindow window);

  /**
   * @param main
   *          the {@link IApplication}'s main frame (used for owner setup of
   *          dialogs)
   */
  void setMainFrame(JFrame main);

  /**
   * Adds the given listener to the list of window listeners.
   * 
   * @param listener
   *          the listener to attach
   */
  void addWindowListener(IWindowListener listener);

  /**
   * Removes a previously registered window listener from the listeners list
   * 
   * @param listener
   *          the listener to remove
   */
  void removeWindowListener(IWindowListener listener);

  /**
   * @return the currently active window (null if none is active)
   */
  IWindow getActiveWindow();

  /**
   * @return the main frame
   */
  JFrame getMainWindow();

  /**
   * Gets the AWT window an {@link IWindow} resides in. This is useful if a
   * dialog is to be displayed and it should be placed according to an IWindow's
   * position on the screen.
   * 
   * @param window
   *          the IWindow the AWT window is requested for
   * @return the AWT window for given IWindow, can be <code>null</code> if no
   *         appropriate window was found.
   */
  Window getWindowFor(IWindow window);

  /**
   * This method is called when the application shuts down. So the window
   * manager can clean up prior to the application shutdown. This can and should
   * at least include closing all windows so that they can perform their clean
   * up.
   */
  void exitingApplication();

  /**
   * Used to generate unique namespaces for windows so that multiple instances
   * of an {@link IWindow} can have the same actions without interfering.
   * 
   * @param window
   *          the window the url is for
   * @return the relative url
   */
  String getRelativeURLFor(IWindow window);

  /**
   * Changes the look and feel to the look and feel given by the specified class
   * name. Implementing classes must make sure that this method is executed with
   * respect to the EDT.
   * 
   * @param lafclassname
   *          the look and feel's class name
   */
  void setLookAndFeel(String lafclassname);

  /**
   * Creates a suitable container for the given IWindow so that it can be used
   * in different Swing contexts.
   * 
   * @param window
   *          the window to put into container
   * @return a swing component containing the given IWindow
   */
  JComponent createContainer(IWindow window);

}
