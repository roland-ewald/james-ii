/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization.parameter.cancellation;

import org.jamesii.core.experiments.optimization.OptimizationStatistics;

/**
 * Stops optimization if a maximal number of simulation runs is reached. Not to
 * be confused with {@link TotalConfigurationCancellation}, which limits the
 * number of distinct set-ups to be tried out, not the number of simulation run
 * executions.
 * 
 * @author Arvid Schwecke
 */
public class TotalSimulationRunsCancellation implements
    ICancelOptimizationCriterion {

  /** The max simulation runs. */
  private int maxSimulationRuns;

  /**
   * Initializes a new instance of the {@link TotalSimulationRunsCancellation}
   * class with the given maximum number of simulation runs.
   * 
   * @param maxSimulationRuns
   *          Tha maximum number of simulation runs.
   */
  public TotalSimulationRunsCancellation(int maxSimulationRuns) {
    super();
    this.maxSimulationRuns = maxSimulationRuns;
  }

  @Override
  public boolean meetsCancelCriterion(OptimizationStatistics state) {
    return state.getTotalSimulationRuns() > maxSimulationRuns;
  }

}
