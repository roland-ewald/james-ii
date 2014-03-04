/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.gui.actions;

import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.experiments.ComputationTaskRuntimeInformation;
import org.jamesii.core.experiments.IExperimentExecutionListener;
import org.jamesii.core.experiments.taskrunner.ITaskRunner;
import org.jamesii.gui.application.IWindowManager;
import org.jamesii.gui.application.action.AbstractAction;
import org.jamesii.gui.experiment.actions.RunExpAction;
import org.jamesii.gui.experiment.windows.edit.ExperimentEditor;
import org.jamesii.simspex.gui.PerfDBRecorder;


/**
 * Action that adds the performance recorder to the {@link RunExpAction} for the
 * current {@link BaseExperiment}, and the executes it.
 * 
 * @author Roland Ewald
 * 
 */
public class ExecExplExperiment extends AbstractAction implements
    IExperimentExecutionListener {

  /** Current experiment editor. */
  private final ExperimentEditor expEditor;

  /** Window manager. */
  private final IWindowManager winManager;

  /** Action to execute experiment. */
  private final RunExpAction runExpAction;

  /** Performance database recorder. */
  private final PerfDBRecorder perfDBRecorder = new PerfDBRecorder();

  /** Counter for executed simulation runs. */
  private int runCounter = 0;

  /**
   * 
   * @param expEditor
   * @param windowManager
   */
  public ExecExplExperiment(ExperimentEditor experimentEditor,
      IWindowManager windowManager) {
    super("org.jamesii.simspex.experiment.execute", "Execute Exploration Experiment",
        new String[] { "" }, experimentEditor);

    expEditor = experimentEditor;
    winManager = windowManager;
    runExpAction = expEditor.getRunExpAction();
    runExpAction.addExpExecListener(this);
    runExpAction.addExpExecListener(perfDBRecorder);
  }

  @Override
  public void execute() {
    expEditor.getExperiment().setRepeatRuns(1);
    perfDBRecorder.start();
    runExpAction.actionPerformed(null);
  }

  @Override
  public void experimentExecutionStopped(BaseExperiment experiment) {
    runExpAction.removeExpExecListener(perfDBRecorder);
    runExpAction.removeExpExecListener(this);
    perfDBRecorder.stop();
    this.setEnabled(true);
  }

  @Override
  public void simulationExecuted(ITaskRunner simRunner,
      ComputationTaskRuntimeInformation srti, boolean jobDone) {
    runCounter++;
    setLabel(getStatus());
  }

  @Override
  public void simulationInitialized(ITaskRunner simRunner,
      ComputationTaskRuntimeInformation srti) {
  }

  @Override
  public void experimentExecutionStarted(BaseExperiment experiment) {
    setLabel(getStatus());
    this.setEnabled(false);
  }

  private String getStatus() {
    return "Running (" + runCounter + " completed)";
  }

  protected IWindowManager getWinManager() {
    return winManager;
  }
}
