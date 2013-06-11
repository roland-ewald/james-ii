/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization.parameter.cancellation;

import org.jamesii.core.experiments.optimization.OptimizationStatistics;

/**
 * Interface for cancellation criteria for specific simulation runs. These can
 * be used to cancel a simulation run preemptively if one of the
 * post-constraints on the optimisation problem has already been violated.
 * 
 * TODO: This interface needs a major revision and its implementations are
 * currently unused.
 * 
 * @author Arvid Schwecke
 * 
 */
public interface ICancelSimulationCriterion {

  /**
   * Tests for cancel criterion.
   * 
   * @param state
   *          the state of the optimisation process
   * @return true if cancel criterion is fulfilled
   */
  boolean meetsCancelCriterion(OptimizationStatistics state);

}
