/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization.parameter.cancellation;

import org.jamesii.core.experiments.optimization.OptimizationStatistics;

/**
 * Returns true until the number of configuration has reached a certain (fixed)
 * number.
 * 
 * @author Arvid Schwecke
 * @author Roland Ewald
 */
public class TotalConfigurationCancellation implements
    ICancelOptimizationCriterion {

  /** Maximal number of configurations to be tested. */
  private int maxConfigurations;

  /**
   * The Constructor.
   * 
   * @param maxConfigs
   *          the maximal number of configurations to be tested
   */
  public TotalConfigurationCancellation(int maxConfigs) {
    this.maxConfigurations = maxConfigs;
  }

  /**
   * Tests whether cancel criterion is met.
   * 
   * @param state
   *          the current state of the optimisation process
   * 
   * @return true if cancel criterion is met
   * 
   */
  @Override
  public boolean meetsCancelCriterion(OptimizationStatistics state) {
    return (state.getTotalConfigurationRuns() + state.getNumOfStorageHits()
        + state.getNumOfViolatedConstraints() >= maxConfigurations);
  }

}
