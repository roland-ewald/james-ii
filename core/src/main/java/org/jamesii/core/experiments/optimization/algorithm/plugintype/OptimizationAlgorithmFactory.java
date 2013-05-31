/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization.algorithm.plugintype;

import org.jamesii.core.experiments.optimization.algorithm.IOptimizationAlgorithm;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.factories.IParameterFilterFactory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Base factory for optimization algorithms.
 * 
 * @author Stefan Leye
 * 
 */
public abstract class OptimizationAlgorithmFactory extends
    Factory<IOptimizationAlgorithm> implements IParameterFilterFactory {

  /**
   * The serialization ID.
   */
  private static final long serialVersionUID = -1969541763833448219L;

  /**
   * Creates an optimization algorithm.
   * 
   * @param params
   *          parameters of the algorithm.
   * @return the algorithm
   */
  @Override
  public abstract IOptimizationAlgorithm create(ParameterBlock params);

}
