/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments;

import org.jamesii.core.experiments.taskrunner.ITaskRunner;

/**
 * Interface for all components interested in the execution progress of an
 * experiment.
 * 
 * @author Roland Ewald
 */
public interface IExperimentExecutionListener {

  /**
   * Called when an experiment was started.
   * 
   * @param experiment
   *          the experiment that was started
   */
  void experimentExecutionStarted(BaseExperiment experiment);

  /**
   * Called when an experiment was stopped.
   * 
   * @param experiment
   *          the experiment that was stopped
   */
  void experimentExecutionStopped(BaseExperiment experiment);

  /**
   * Called when a task has been executed.
   * 
   * @param taskRunner
   *          the task runner which executed the simulation.
   * @param crti
   *          the computation task runtime information
   * @param jobDone
   *          true, if the simulation configuration had been finished (i.e. it
   *          is false if and only if additional replications are necessary)
   */
  void simulationExecuted(ITaskRunner taskRunner,
      ComputationTaskRuntimeInformation crti, boolean jobDone);

  /**
   * Called when a task was initialised.
   * 
   * @param taskRunner
   *          the task runner that initialised the task
   * @param crti
   *          the computation task runtime information
   */
  void simulationInitialized(ITaskRunner taskRunner,
      ComputationTaskRuntimeInformation crti);

}
