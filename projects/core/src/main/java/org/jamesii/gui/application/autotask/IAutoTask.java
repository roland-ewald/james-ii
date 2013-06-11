/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.autotask;

import org.jamesii.gui.application.IWindowManager;

/**
 * An extension point for the JAMES II GUI defining tasks that are automatically
 * run at start and end of application.
 * 
 * @author Stefan Rybacki
 */
public interface IAutoTask {
  /**
   * Supposed to be called when the GUI was started and can be used to open
   * windows or restore a previous session or so on.
   * 
   * @param windowManager
   *          the current window manger
   */
  void applicationStarted(IWindowManager windowManager);

  /**
   * Supposed to be called when the GUI exits and can be used to clean up or to
   * store the current session for later reuse.
   * 
   * @param windowManager
   *          the current window manger
   */
  void applicationExited(IWindowManager windowManager);
}
