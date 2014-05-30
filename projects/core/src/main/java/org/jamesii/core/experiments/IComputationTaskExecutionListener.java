/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments;


/**
 * Similar to {@link IExperimentExecutionListener}, components that implement
 * this interface can register at an
 * {@link org.jamesii.core.experiments.tasks.IComputationTask}'s corresponding
 * {@link ComputationTaskRuntimeInformation} to be notified upon state changes
 * of the computation task. For example, if a simulation run changes its state
 * from running to paused or finished.
 * 
 * @author Roland Ewald
 */
public interface IComputationTaskExecutionListener {

  /**
   * Notifies listener that the state of the computation task associated with
   * this {@link ComputationTaskRuntimeInformation} has been changed. The new
   * state can be retrieved via {@link ComputationTaskRuntimeInformation}
   * 
   * @param srti
   */
  void stateChanged(ComputationTaskRuntimeInformation srti);

}
