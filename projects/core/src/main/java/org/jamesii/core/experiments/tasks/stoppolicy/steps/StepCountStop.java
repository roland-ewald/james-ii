/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.tasks.stoppolicy.steps;

import java.io.Serializable;

import org.jamesii.core.experiments.tasks.IComputationTask;
import org.jamesii.core.experiments.tasks.stoppolicy.AbstractComputationTaskStopPolicy;

/**
 * The Class StepCountEnd. Runs until a pre-defined number of computation steps
 * have been executed. (Actually counts calls to #hasReachedEnd, which will
 * coincide with the number of calls to
 * {@link org.jamesii.core.processor.IProcessor#executeNextStep()}
 * 
 * @author Jan Himmelspach
 */
public class StepCountStop extends
    AbstractComputationTaskStopPolicy<IComputationTask> implements Serializable {

  /** The stop counter. Default is 1. */
  private long stopCount = 1;

  /** For record keeping, the initially defined number of steps */
  private long initialStopCount;

  /**
   * The Constructor.
   * @param stopCount
   *          the stop count
   */
  public StepCountStop(long stopCount) {
    super();
    this.stopCount = stopCount;
    this.initialStopCount = stopCount;
  }

  @Override
  public boolean hasReachedEnd(IComputationTask t) {
    stopCount--;
    return (stopCount <= 0);
  }

  /**
   * @param stopCount
   *          the stopCount to set
   */
  public void setStopCount(long stopCount) {
    this.stopCount = stopCount;
    this.initialStopCount = stopCount;
  }

  /**
   * @return the stopCount
   */
  public long getStopCount() {
    return stopCount;
  }

  @Override
  public String toString() {
    return "StepCountStop [initialStopCount=" + initialStopCount + "]";
  }
}
