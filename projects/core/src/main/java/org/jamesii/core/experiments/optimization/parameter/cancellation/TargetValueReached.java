/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization.parameter.cancellation;

import org.jamesii.core.experiments.optimization.OptimizationStatistics;

/**
 * The Class TargetValueReached.
 */
public class TargetValueReached implements ICancelOptimizationCriterion {

  /** The value to reach. */
  private double targetValue;

  /**
   * Instantiates a new target value reached.
   */
  public TargetValueReached() {
    targetValue = 0;
  }

  /**
   * Instantiates a new target value reached.
   * 
   * @param value
   *          the value
   */
  public TargetValueReached(double value) {
    targetValue = value;
  }

  @Override
  public boolean meetsCancelCriterion(OptimizationStatistics state) {

    if (state.getTotalConfigurationRuns() == 0) {
      return false;
    }

    if (state.getBestConfigs().get(state.getBestConfigs().size() - 1)
        .getRepresentativeObjectives().get(0).compareTo(targetValue) <= 0) {
      return true;
    }

    return false;
  }

  /**
   * Gets the value_to_reach.
   * 
   * @return the value_to_reach
   */
  public double getTargetValue() {
    return targetValue;
  }

  /**
   * Sets the value_to_reach.
   * 
   * @param value_to_reach
   *          the new value_to_reach
   */
  public void setTargetValue(double value_to_reach) {
    this.targetValue = value_to_reach;
  }

}
