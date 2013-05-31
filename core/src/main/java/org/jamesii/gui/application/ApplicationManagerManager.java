/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application;

/**
 * Simple static class that provides access to the currently used
 * {@link IApplicationManager}.
 * 
 * @author Stefan Rybacki
 * 
 */

public final class ApplicationManagerManager {
  /**
   * the currently set application manager
   */
  private static IApplicationManager manager;

  /**
   * sets the current application manager
   * 
   * @param mgr
   *          the current manager
   * 
   */
  public static void setApplicationManager(IApplicationManager mgr) {
    manager = mgr;
  }

  /**
   * @return the currently set application manager
   */
  public static IApplicationManager getApplicationManager() {
    return manager;
  }

  private ApplicationManagerManager() {
  }
}
