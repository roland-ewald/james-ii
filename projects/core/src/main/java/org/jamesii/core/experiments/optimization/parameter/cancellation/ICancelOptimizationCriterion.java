/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization.parameter.cancellation;

import org.jamesii.core.experiments.optimization.OptimizationStatistics;

/**
 * Interface for all optimisation stopping criteria. These are intended to stop
 * an optimisation algorithm prematurely, e.g. if the number of evaluated
 * parameter configurations or consumed run time is exceeded. Called by the base
 * {@link org.jamesii.core.experiments.optimization.Optimizer} class.
 * 
 * @author Arvid Schwecke
 * @author Roland Ewald
 * 
 */
public interface ICancelOptimizationCriterion {

  /**
   * Checks {@link OptimizationStatistics} to decide if the optimisation shall
   * be stopped.
   * 
   * @param state
   *          the state
   * 
   * @return true if optimisation shall be finished
   */
  boolean meetsCancelCriterion(OptimizationStatistics state);

}
