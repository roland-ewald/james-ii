/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.univariate;

import org.jamesii.core.math.statistics.BetaFunction;

/**
 * Class for calculating the quantils of Student T distributions.
 * <p>
 * These methods were adapted from the <a
 * href="http://www.netlib.org/cephes/">Cephes Mathematical Library</a> by <a
 * href="http://www.moshier.net">Stephen L. Moshier</a>.
 * 
 * @author Stefan Leye
 */

public final class InverseStudentsTDistributionFunction {

  /**
   * Hidden constructor.
   */
  private InverseStudentsTDistributionFunction() {
  }

  /**
   * Calculates the p-1 quantil of the T-distribution
   * 
   * @param probability
   *          the probability p
   * @param degreesOfFreedom
   *          the degrees of freedom
   * @return the p-1 quantil
   */
  public static Double quantil(double probability, int degreesOfFreedom) {
    double t, rk, z;
    int rflg;

    if (degreesOfFreedom <= 0 || probability <= 0.0 || probability >= 1.0) {
      return null;
    }

    rk = degreesOfFreedom;

    if (probability > 0.25 && probability < 0.75) {// NOSONAR
      if (probability == 0.5) {// NOSONAR
        return 0.0;
      }
      z = 1.0 - 2.0 * probability;
      z = BetaFunction.inverseBeta(Math.abs(z), 0.5, 0.5 * rk,// NOSONAR
          Math.pow(10, -6), 0, 0);// NOSONAR
      t = Math.sqrt(rk * z / (1.0 - z));
      if (probability < 0.5) {// NOSONAR
        t = -t;
      }
      return t;
    }
    rflg = -1;
    if (probability >= 0.5) {// NOSONAR
      probability = 1.0 - probability;// NOSONAR
      rflg = 1;
    }
    z = BetaFunction.inverseBeta(2.0 * probability, 0.5 * rk, 0.5,// NOSONAR
        Math.pow(10, -6), 0, 0);// NOSONAR
    // TODO do we need this??
    if (Double.MAX_VALUE * z < rk) {
      return (rflg * Double.MAX_VALUE);
    }
    t = Math.sqrt(rk / z - rk);
    return (rflg * t);
  }
}
