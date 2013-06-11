/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.perspective.autotask;

import org.jamesii.gui.application.IWindowManager;
import org.jamesii.gui.application.autotask.IAutoTask;
import org.jamesii.gui.application.logging.LogView;

/**
 * Basic autotask that opens the {@link LogView} on James GUI startup.
 * 
 * @author Stefan Rybacki
 * 
 */
class DefaultAutoTask implements IAutoTask {

  @Override
  public void applicationExited(IWindowManager windowManager) {
  }

  @Override
  public void applicationStarted(IWindowManager windowManager) {
    windowManager.addWindow(LogView.getInstance());
  }

}
