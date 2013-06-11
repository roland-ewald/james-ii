/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;

/**
 * Implement this to provide customized applications used with
 * {@link ApplicationManager}.
 * 
 * @author Stefan Rybacki
 */
public interface IApplication {
  /**
   * Implement this method to provide the main content for the main window
   * 
   * @return content of main window
   */
  JComponent createContent();

  /**
   * Implement to provide the menubar of the main window
   * 
   * @return main window's menubar
   */
  JMenuBar createMenuBar();

  /**
   * Implement to provide the toolbar of the main window
   * 
   * @return main window's toolbar
   */
  JToolBar createToolBar();

  /**
   * Implement to provide the starting size of the main window
   * 
   * @return size of main window on start
   */
  Dimension getMainWindowSize();

  /**
   * Implement to provide the starting position of the main window.
   * 
   * @return the main window position
   */
  Point getMainWindowPosition();

  /**
   * Implement this to provide the starting state of the main window.
   * 
   * @return the state of the window
   * @see Frame#MAXIMIZED_BOTH
   * @see Frame#MAXIMIZED_HORIZ
   * @see Frame#MAXIMIZED_VERT
   * @see Frame#ICONIFIED
   */
  int getMainWindowState();

  /**
   * Called by {@link IApplicationManager#close(boolean)} to determine whether
   * the application can be closed or not. For instance one could implement a
   * choice dialog that asks whether to close or not in this method.
   * 
   * @return true if application can be closed false else
   */
  boolean canClose();

  /**
   * Is called after the main window is closed right before the application
   * exits. This can be used to store stuff or clean up things.
   * 
   * @param progress
   *          used to provide progress and/or task information that are
   *          performed during exiting
   * @param emergency
   *          if true an emergency exit is invoked by the application manager
   *          which means that the application is about to close most likely
   *          because of an error that can't be recovered from which also means
   *          {@link #canClose()} was not called before reaching this method.
   *          Which also means most likely not everything is initialized so be
   *          aware when using instances you didn't create yourself in that
   *          method
   */
  void exitingApplication(IProgressListener progress, boolean emergency);

  /**
   * called after the application's main window is shown
   */
  void started();

  /**
   * Called after showing the splash screen before the main window is generated.
   * So all initialization is done here before the main window is constructed.
   * Using {@code progress} it is possible to provide progress and/or task
   * information to the splash screen.
   * 
   * @param progress
   *          used to provide progress and/or task information
   * @return true if initialization was successful
   */
  boolean initialize(IProgressListener progress);

  /**
   * indicates whether to show a splash screen or not while starting and
   * initializing
   * 
   * @return true if splash screen should be visible during start up
   */
  boolean showSplashScreen();

  /**
   * provides an Image that used by the splash screen as background image
   * 
   * @return the image to display on splash screen
   */
  Image getSplashImage();

  /**
   * called if the main window is iconified
   */
  void iconified();

  /**
   * called if the main window is deiconified
   */
  void deIconified();

  /**
   * @return the application information
   */
  IApplicationInformation getApplicationInformation();

  /**
   * @return the window manager to be used for the application
   */
  IWindowManager getWindowManager();

  /**
   * @return a component that is added to the status bar of the main window
   */
  JComponent getStatusBar();
}
