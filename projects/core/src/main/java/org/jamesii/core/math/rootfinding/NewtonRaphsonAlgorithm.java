/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.rootfinding;

import org.jamesii.core.math.IFunction;

/**
 * NewtonRaphsonAlgorithm.
 * 
 * Root-finding algorithm implementation based on the Newton-Raphson method.
 * 
 * 
 * @author Matthias Jeschke
 * 
 */
public class NewtonRaphsonAlgorithm extends AbstractRootFindingAlgorithm {

  /** Serialization ID. */
  private static final long serialVersionUID = 3690375547091332140L;

  private IFunction derivative = null;

  public NewtonRaphsonAlgorithm(IFunction function, IFunction derivative) {
    super(function);
    this.derivative = derivative;
  }

  public NewtonRaphsonAlgorithm(IFunction function, IFunction derivative,
      double epsilon, int maxIter) {
    super(function, epsilon, maxIter);
    this.derivative = derivative;
  }

  /**
   * Returns the derivative of the function for which an x_n that satisfies
   * abs(f(x_n)) < epsilon should be found.
   * 
   * @return The derivative of the function to analyze.
   */
  public IFunction getDerivative() {
    return this.derivative;
  }

  @Override
  public double[] findRoot(double[]... startPoints) {
    if (startPoints == null || startPoints.length < 1) {
      throw new IllegalArgumentException(
          "Newton-Raphson Algorithm needs at least one start point!");
    }
    double[] result = startPoints[0].clone();
    int currentIter = 0;

    while (true) {
      double[] fx = getFunction().eval(result);
      double[] fPrimeX = this.derivative.eval(result);

      if (currentIter == getMaxIter() || checkAbortCondition(fx)) {
        break;
      }

      for (int i = 0; i < result.length; i++) {
        result[i] = result[i] - fx[i] / fPrimeX[i];
        // System.out.printf("i:%d -> fx:%g, fp:%g, fx/fp:%g, res:%g\n",
        // i, fx[i], fPrimeX[i], fx[i] / fPrimeX[i], result[i]);
      }
      currentIter++;
    }

    if (currentIter == this.getMaxIter()) {
      return null;
    }
    return result;
  }
}
