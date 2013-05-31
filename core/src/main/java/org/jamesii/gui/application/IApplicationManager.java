/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application;

/**
 * Basic interface for an application manager.
 * 
 * @author Stefan Rybacki
 * @see #start()
 * @see #close(boolean)
 */
public interface IApplicationManager {
  /**
   * Starts the managed {@link IApplication}
   * 
   * @throws Throwable
   */
  void start();

  /**
   * Closes the managed {@link IApplication}
   * 
   * @param emergency
   *          if true no check whether the application can be closed is executed
   *          and the application is closed no matter what most likely due to an
   *          error that occurred
   */
  void close(boolean emergency);

  /**
   * @return the window manager
   */
  IWindowManager getWindowManager();

  /**
   * @return the managed {@link IApplication}
   */
  IApplication getApplication();
}
