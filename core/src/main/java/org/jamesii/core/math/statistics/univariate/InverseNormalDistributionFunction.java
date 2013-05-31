/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.univariate;

/**
 * InverseNormalCDF.
 * 
 * Class for calculating the inverse of the normal cumulative distribution
 * function. It is based on an algorithm devised by Peter John Acklam, see the
 * link below.
 * 
 * LICENCE: JAMESLIC
 * 
 * @author mj
 * @see <a
 *      href="http://home.online.no/~pjacklam/notes/invnorm/index.html">Acklam,
 *      P.J., An algorithm for computing the inverse normal cumulative
 *      distribution function, last accessed: 09/30/2010</a>
 * 
 */
public final class InverseNormalDistributionFunction {

  private static final double[] A = { 3.969683028665376e+01,// NOSONAR
      2.209460984245205e+02,// NOSONAR
      -2.759285104469687e+02, 1.383577518672690e+02, -3.066479806614716e+01,// NOSONAR
      2.506628277459239e+00 };// NOSONAR

  private static final double[] B = { -5.447609879822406e+01,// NOSONAR
      1.615858368580409e+02,// NOSONAR
      -1.556989798598866e+02, 6.680131188771972e+01, -1.328068155288572e+01 };// NOSONAR

  private static final double[] C = { -7.784894002430293e-03,// NOSONAR
      -3.223964580411365e-01,// NOSONAR
      -2.400758277161838e+00, -2.549732539343734e+00, 4.374664141464968e+00,// NOSONAR
      2.938163982698783e+00 };// NOSONAR

  private static final double[] D = { 7.784695709041462e-03,// NOSONAR
      3.224671290700398e-01,// NOSONAR
      2.445134137142996e+00, 3.754408661907416e+00 };// NOSONAR

  private static final double P_LOW = 0.02425;

  private static final double P_HIGH = 1 - P_LOW;

  /**
   * Hidden constructor.
   */
  private InverseNormalDistributionFunction() {
  }

  /**
   * Calculates the p-1 quantil of the normal distribution
   * 
   * @param probability
   *          the probability p
   * @param highPrecision
   *          flag whether the algorithm should exploit full precision of the
   *          machine
   * @return the p-1 quantil
   */
  public static double quantil(double p, boolean highPrecision) {
    double x = 0;

    if (Double.compare(p, 0.) == 0) {
      return Double.NEGATIVE_INFINITY;
    } else if (Double.compare(p, 1.) == 0) {
      return Double.POSITIVE_INFINITY;
    } else if (Double.isNaN(p) || p < 0. || p > 1.) {
      return Double.NaN;
    } else if (p < P_LOW) {
      double q = Math.sqrt(-2. * Math.log(p));
      x =
          (((((C[0] * q + C[1]) * q + C[2]) * q + C[3]) * q + C[4]) * q + C[5])
              / ((((D[0] * q + D[1]) * q + D[2]) * q + D[3]) * q + 1);
    } else if (P_LOW <= p && p <= P_HIGH) {
      double q = p - 0.5;
      double r = q * q;
      x =
          (((((A[0] * r + A[1]) * r + A[2]) * r + A[3]) * r + A[4]) * r + A[5])
              * q
              / (((((B[0] * r + B[1]) * r + B[2]) * r + B[3]) * r + B[4]) * r + 1);
    } else {
      double q = Math.sqrt(-2. * Math.log(1. - p));
      x =
          -(((((C[0] * q + C[1]) * q + C[2]) * q + C[3]) * q + C[4]) * q + C[5])
              / ((((D[0] * q + D[1]) * q + D[2]) * q + D[3]) * q + 1);
    }

    if (highPrecision) {
      double e = 0.5 * ErrorFunction.erfc(-x / Math.sqrt(2.)) - p;
      double u = e * Math.sqrt(2. * Math.PI) * Math.exp(x * x / 2.);
      x = x - u / (1. + x * u / 2.);
    }
    return x;
  }
}
