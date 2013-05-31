/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments;

import org.jamesii.core.experiments.taskrunner.ITaskRunner;

/**
 * Execution controller for an experiment. Used to start/cancel individual
 * computation tasks from the GUI.
 * 
 * @author Roland Ewald
 */
public interface IExperimentExecutionController extends Runnable {

  /**
   * Add experiment execution listener.
   * 
   * @param expExecListener
   *          the experiment execution listener to be added
   */
  void addExecutionListener(IExperimentExecutionListener expExecListener);

  /**
   * Get experiment to be controlled.
   * 
   * @return experiment to be controlled
   */
  BaseExperiment getExperiment();

  /**
   * Remove experiment execution listener.
   * 
   * @param expExecListener
   *          the experiment execution listener to be removed
   * 
   * @return true, if the listener has been found
   */
  boolean removeExecutionListener(IExperimentExecutionListener expExecListener);

  /**
   * Set experiment to be controlled.
   * 
   * @param exp
   *          experiment to be controlled
   */
  void setExperiment(BaseExperiment exp);

  /**
   * This function gets called when the execution of a computation is finished.
   * 
   * @param taskRunner
   *          the computation task runner that is calling
   * @param ctRTI
   *          all runtime information on the computation task
   * @param results
   *          the (runtime) results of the computation task
   * @param jobDone
   *          flag that states whether the computation was finished
   */
  void computationTaskExecuted(ITaskRunner taskRunner,
      ComputationTaskRuntimeInformation ctRTI, RunInformation results);

  /**
   * This function gets called when a new computation task - triggered by the
   * base experiment - was created and is ready for execution.
   * 
   * @param taskRunner
   *          the task runner that is calling
   * @param runtimeInfo
   *          runtime information for the computation task
   */
  void computationTaskInitialized(ITaskRunner taskRunner,
      ComputationTaskRuntimeInformation runtimeInfo);

  /**
   * Stops execution controlling thread.
   * 
   * @param stopTasks
   *          flag to determine if all currently running computations shall be
   *          aborted when stopping
   */
  void stop(boolean stopTasks);

}
