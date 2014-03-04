/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.gui.actions;


import java.util.logging.Level;

import org.jamesii.core.util.logging.ApplicationLogger;
import org.jamesii.gui.application.IWindow;
import org.jamesii.gui.application.IWindowManager;
import org.jamesii.gui.application.action.AbstractAction;
import org.jamesii.gui.experiment.windows.edit.ExperimentEditor;
import org.jamesii.simspex.gui.dialogs.ConfigureSimSpExWindow;


/**
 * Action to configure an experiment for simulation space exploration.
 * 
 * @author Roland Ewald
 */
public class SimSpExExperiment extends AbstractAction {

  /** The window manager. */
  private final IWindowManager winManager;

  public SimSpExExperiment(IWindowManager windowManager, IWindow window) {
    super("org.jamesii.simspex.experiment", "SimSpEx Experiment...",
        new String[] { "org.jamesii.menu.main/org.jamesii.simspex" }, window);
    winManager = windowManager;
  }

  @Override
  public void execute() {
    IWindow window = winManager.getActiveWindow();
    if (!(window instanceof ExperimentEditor)) {
      ApplicationLogger
          .log(
              Level.INFO,
              "Please select an experiment which shall be used for simulation space exploration.");
    }
    winManager.addWindow(new ConfigureSimSpExWindow((ExperimentEditor) window,
        winManager));
  }
}
