/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization.algorithm;

import java.util.Map;

import org.jamesii.core.experiments.optimization.parameter.Configuration;
import org.jamesii.core.experiments.variables.NoNextVariableException;

/**
 * Interface for classes realizing an optimization algorithm.
 * 
 * @author Roland Ewald
 * @author Stefan Leye
 * 
 */
public interface IOptimizationAlgorithm {

  /**
   * Update the algorithm with new information about a configuration.
   * 
   * @param config
   *          the configuration
   * @param results
   *          the results created by the configuration
   * @param currentParetoFront
   *          the current pareto front (i.e. best configurations)
   */
  void addResults(Configuration config, Map<String, Double> results,
      Map<Configuration, Map<String, Double>> currentParetoFront);

  /**
   * Generates a configuration to be executed.
   * 
   * @return the configuration
   * @throws NoNextVariableException
   *           the exception thrown if no new configuration could be executed
   */
  Configuration getNextConfiguration() throws NoNextVariableException;

}
