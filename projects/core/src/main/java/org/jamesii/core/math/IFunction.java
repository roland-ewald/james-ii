/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math;

import java.util.Map;

/**
 * IFunction.
 * 
 * Interface for any function objects that can represent mathematical functions
 * of the type y = f(x).
 * 
 * LICENCE: JAMESLIC
 * 
 * @author Matthias Jeschke
 * 
 */
public interface IFunction {

  /**
   * Evaluates the function at the given argument x. The argument is a vector of
   * values.
   * 
   * @param x
   *          the argument vector
   * @return The value f(x).
   */
  double[] eval(double[] x);

  /**
   * Sets the parameters. Note: this should only be used for parameters that
   * change between two calls of {@link #eval(double[])}
   * 
   * @param parameterMap
   *          map that holds the parameters
   */
  void setParameters(Map<String, double[]> parameterMap);
}
