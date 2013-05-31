/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment;

import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.experiments.ComputationTaskRuntimeInformation;
import org.jamesii.core.experiments.IExperimentExecutionListener;
import org.jamesii.core.experiments.taskrunner.ITaskRunner;

/**
 * Class that maintains the current status of the {@link BaseExperiment}
 * execution.
 * 
 * @author Roland Ewald
 */
public class CurrentExperimentStatus implements IExperimentExecutionListener {

  /** Reference to runtime information of the current simulation. */
  private ComputationTaskRuntimeInformation simRuntimeInfo = null;

  @Override
  public void experimentExecutionStarted(BaseExperiment experiment) {
    // TODO Auto-generated method stub

  }

  @Override
  public void experimentExecutionStopped(BaseExperiment experiment) {
    // TODO Auto-generated method stub

  }

  @Override
  public void simulationExecuted(ITaskRunner simRunner,
      ComputationTaskRuntimeInformation srti, boolean jobDone) {
    this.simRuntimeInfo = null;

  }

  @Override
  public void simulationInitialized(ITaskRunner simRunner,
      ComputationTaskRuntimeInformation srti) {
    this.simRuntimeInfo = srti;
  }

  /**
   * Checks whether there is a current simulation that can be controlled.
   * 
   * @return true if there is a current simulation that can be controlled
   */
  protected boolean noSimControllingPossible() {
    return simRuntimeInfo == null
        || !simRuntimeInfo.getComputationTask().isProcessorRunnable();
  }

}
