/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.rootfinding;

import org.jamesii.core.math.IFunction;

/**
 * SecantMethod.
 * 
 * Root-finding algorithm implementation based on the secant method.
 * 
 * 
 * @author Matthias Jeschke
 * 
 */
public class SecantMethod extends AbstractRootFindingAlgorithm {

  /** Serialization ID. */
  private static final long serialVersionUID = -371385418736284451L;

  public SecantMethod(IFunction function) {
    super(function);
  }

  public SecantMethod(IFunction function, double epsilon, int maxIter) {
    super(function, epsilon, maxIter);
  }

  @Override
  public double[] findRoot(double[]... startPoints) {
    if (startPoints == null || startPoints.length < 2) {
      throw new IllegalArgumentException(
          "Secant method needs at least two start points!");
    }

    // initial values
    double[] xnm1 = startPoints[0].clone();
    double[] xn = startPoints[1].clone();

    int currentIter = 0;

    while (true) {
      double[] fx = this.getFunction().eval(xn);
      double[] fxm1 = this.getFunction().eval(xnm1);

      if (currentIter == this.getMaxIter() || this.checkAbortCondition(fx)) {
        break;
      }

      for (int i = 0; i < xn.length; i++) {
        double d = ((xn[i] - xnm1[i]) / (fx[i] - fxm1[i])) * fx[i];
        xnm1[i] = xn[i];
        xn[i] = xn[i] - d;
      }
      currentIter++;
    }

    if (currentIter == this.getMaxIter()) {
      return null;
    }
    return xn;
  }
}
