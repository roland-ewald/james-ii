/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.tasks;

import org.jamesii.core.experiments.RunInformation;

/**
 * A computation task which has been setup by a corresponding factory. An
 * initialized computation task wraps a "computation tasks" and adds information
 * about its runtime behavior. Thus, whichever computation task is being
 * instantiated by a class implementing this interface, the class should make
 * sure that run time information is collected and provided via the
 * {@link #getRunInfo()}.
 * 
 * @author Jan Himmelspach
 * 
 */
public interface IInitializedComputationTask {

  /**
   * Returns the performance information of the computation task.
   * 
   * 
   * @return run information of the computation task
   */
  RunInformation getRunInfo();

  /**
   * Gets the computation task. In the computation task the computation of the
   * model has to be realized.
   * 
   * @return the computation task
   */
  IComputationTask getComputationTask();

}
