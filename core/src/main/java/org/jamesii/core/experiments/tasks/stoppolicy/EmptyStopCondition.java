/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.tasks.stoppolicy;

import org.jamesii.core.experiments.tasks.IComputationTask;

/**
 * The empty stop condition means that the computation it is used on is not
 * stopped by a condition. For those having a time this means that the
 * computation will last until infinity. In general a user has to stop a
 * computation task created using this - or the computation has to terminate on
 * its own.
 * 
 * 
 * @author Jan Himmelspach
 * 
 */
public class EmptyStopCondition extends
    AbstractComputationTaskStopPolicy<IComputationTask> {

  /**
   * Create an instance of an empty stop condition.
   * 
   * @param run
   */
  public EmptyStopCondition(IComputationTask run) {
    super(run);
  }

  @Override
  public boolean hasReachedEnd() {
    return false;
  }

}
