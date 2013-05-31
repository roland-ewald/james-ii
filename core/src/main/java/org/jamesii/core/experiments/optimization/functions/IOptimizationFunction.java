/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization.functions;

import java.util.Map;

/**
 * Simple interface a function to be optimized needs to implement.
 * 
 * @author Roland Ewald
 */
public interface IOptimizationFunction {

  /**
   * The name of the function.
   * 
   * @return the name
   */
  String getName();

  /**
   * Calls the functions with a map (parameter name => value).
   * 
   * @param arguments
   *          the map containing the arguments
   * 
   * @return the value of the function
   */
  double call(Map<String, Object> arguments);
}
