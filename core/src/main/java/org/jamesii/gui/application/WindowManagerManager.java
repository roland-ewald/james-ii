/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application;

/**
 * Convenience class that provides static access to the current
 * {@link IWindowManager}.
 * 
 * @author Stefan Rybacki
 */
public class WindowManagerManager {
  /**
   * Provides access to the currently use {@link IWindowManager}
   * 
   * @return the currently used window manager
   */
  public static IWindowManager getWindowManager() {
    if (ApplicationManagerManager.getApplicationManager() == null) {
      return null;
    }
    return ApplicationManagerManager.getApplicationManager().getWindowManager();
  }

  private WindowManagerManager() {
  }

}
