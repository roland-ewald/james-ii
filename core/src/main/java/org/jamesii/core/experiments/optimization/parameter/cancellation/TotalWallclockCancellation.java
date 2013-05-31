/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization.parameter.cancellation;

import org.jamesii.core.experiments.optimization.OptimizationStatistics;

/**
 * Stops the optimisation process if a certain amount of wall-clock time has
 * passed.
 * 
 * 
 * @author Arvid Schwecke
 * @author Roland Ewald
 */
public class TotalWallclockCancellation implements ICancelOptimizationCriterion {

  /** Maximal amount of wall-clock time to be consumed. */
  private final long timeSpan;

  private Long currentTime;

  private OptimizationStatistics currentState;

  /**
   * The Constructor.
   * 
   * @param maxWallClockTime
   *          the max wall clock time (in ms)
   */
  public TotalWallclockCancellation(long maxWallClockTime) {
    this.timeSpan = maxWallClockTime;
  }

  /**
   * Meets cancel criterion.
   * 
   * @param state
   *          the state
   * 
   * @return true, if meets cancel criterion
   * 
   * @see org.jamesii.core.experiments.optimization.parameter.cancellation.ICancelOptimizationCriterion#meetsCancelCriterion(org.jamesii.core.experiments.optimization.OptimizationStatistics)
   */
  @Override
  public boolean meetsCancelCriterion(OptimizationStatistics state) {
    // TODO: This is a *very* awkward way of testing whether there is a new
    // optimisation run going on, but with the current interface it's hard to do
    // better :/
    if (currentState != state) {
      currentState = state;
      currentTime = System.currentTimeMillis();
    }
    return System.currentTimeMillis() - currentTime > timeSpan;
  }

}
