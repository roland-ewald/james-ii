/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment.actions;

import org.jamesii.gui.application.IWindow;
import org.jamesii.gui.application.IWindowManager;
import org.jamesii.gui.application.action.AbstractAction;

/**
 * Action to create a new experiment suite.
 * 
 * @author Roland Ewald
 */
public class NewExperimentSuiteAction extends AbstractAction {

  /** The window manager. */
  private final IWindowManager winManager;

  /**
   * Instantiates a new new experiment suite action.
   * 
   * @param windowManager
   *          the window manager
   * @param window
   *          the window the action is used in
   */
  public NewExperimentSuiteAction(IWindowManager windowManager, IWindow window) {
    super(
        "org.jamesii.experimentsuites.new",
        "Experiment Suite",
        new String[] {
            "org.jamesii.menu.main/org.jamesii.file/org.jamesii.new?after=org.jamesii.experiment.new",
            "org.jamesii.toolbar.main/org.jamesii.new?after=org.jamesii.experiment.new" },
        null, null, window);
    winManager = windowManager;
  }

  @Override
  public void execute() {
    // TODO: fix experiment suite creation
    // ExperimentSuite<BaseExperiment> newSuite = new
    // ExperimentSuite<BaseExperiment>();
    // SimSystem.report(Level.INFO, "Created new experiment suite");
    // winManager.addWindow(new EditExperimentSuiteWindow(null, newSuite));
  }

}
