/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization.plugintype;

import org.jamesii.core.experiments.optimization.ParallelOptimizer;
import org.jamesii.core.experiments.optimization.parameter.ParallelOptimizationProblemDefinition;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Creates an instance of an parallel optimizer algorithm.
 * 
 * @author Peter Sievert
 */
public abstract class ParallelOptimizerFactory extends OptimizerFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = -7189225525175223750L;

  /**
   * Instantiate and return a parallel optimizer.
   * 
   * @param problemDefinition
   *          the problem definition
   * @param parameter
   *          the parameter block with further settings
   * 
   * @return the optimizer
   */
  public abstract ParallelOptimizer getOptimizer(
      ParallelOptimizationProblemDefinition problemDefinition,
      ParameterBlock parameter);

}