/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization.algorithm.plugintype;

import org.jamesii.core.factories.AbstractFilteringFactory;

/**
 * Abstract factory for optimization algorithms.
 * 
 * @author Stefan Leye
 * 
 */
public class AbstractOptimizationAlgorithmFactory extends
    AbstractFilteringFactory<OptimizationAlgorithmFactory> {

  /**
   * The constant serial version id.
   */
  private static final long serialVersionUID = -3429669950387436721L;

  /**
   * Tag for the class of the algorithm.
   */
  public static final String OPTIMIZATION_ALGORITHM_CLASS =
      "optimizationAlgorithmClass";

  /**
   * Default constructor.
   */
  public AbstractOptimizationAlgorithmFactory() {
    super();
  }

}
