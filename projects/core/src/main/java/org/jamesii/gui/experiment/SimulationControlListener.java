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
import org.jamesii.core.processor.IRunnable;

/**
 * Listener which activates a {@link SimulationToolBar}, after a simulation has
 * been initialized.
 * 
 * @author Stefan Leye
 * 
 */
public class SimulationControlListener implements IExperimentExecutionListener {

  /**
   * The toolbar which shall control the simulation.
   */
  private SimulationToolBar toolbar;

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
    toolbar.setProcessor(null);
  }

  @Override
  public void simulationInitialized(ITaskRunner simRunner,
      ComputationTaskRuntimeInformation srti) {
    IRunnable processor =
        (IRunnable) srti.getComputationTask().getProcessorInfo().getLocal();
    toolbar.setProcessor(processor);
  }

}
