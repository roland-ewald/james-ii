package org.jamesii.core.math.rootfinding;

import org.jamesii.core.math.IFunction;

/**
 * IRootFindingAlgorithm.
 * 
 * Interface for root-finding algorithms, i.e., algorithms that find a value x_0
 * that satisfies f(x_0) = 0.
 * 
 * LICENCE: JAMESLIC
 * 
 * @author Matthias Jeschke
 * 
 */
public interface IRootFindingAlgorithm {

  /**
   * Sets the error threshold, i.e., a solutions is found if an x_n is found
   * that satisfies abs(f(x_n)) < epsilon.
   * 
   * @param epsilon
   *          the error threshold
   */
  void setEpsilon(double epsilon);

  /**
   * Returns the error threshold.
   * 
   * @return The error threshold.
   */
  double getEpsilon();

  /**
   * Sets the maximum number of iterations. The calculation is aborted if the
   * condition abs(f(x_n)) < epsilon cannot be satisfied after maxIter steps.
   * 
   * @param maxIter
   */
  void setMaxIterations(int maxIter);

  /**
   * Returns the maximum number of iterations.
   * 
   * @return The maximum number of iterations.
   */
  int getMaxIterations();

  /**
   * Runs the algorithm to find an x_n thats satisfies abs(f(x_n)) < epsilon.
   * 
   * @param startPoint
   *          the initial start points; note that some methods require just a
   *          single initial point
   * @return The point x_n that satisfies abs(f(x_n)) < epsilon or null if this
   *         condition cannot be satisfied after the maximum number of
   *         iterations.
   */
  double[] findRoot(double[]... startPoints);

  /**
   * Returns the function for which an x_n that satisfies abs(f(x_n)) < epsilon
   * should be found.
   * 
   * @return The function to analyze.
   */
  IFunction getFunction();
}
