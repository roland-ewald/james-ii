/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics;

import static java.lang.Double.POSITIVE_INFINITY;
import static java.lang.Math.abs;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static org.jamesii.core.math.statistics.univariate.GammaFunction.logGamma;

/**
 * Class for calculating the incomplete Gamma function. Currently, only the
 * regularized incomplete Gamma functions <i>P</i>(<i>a</i>, <i>x</i>) and
 * <i>Q</i>(<i>a</i>, <i>x</i>) are implemented.
 * <p>
 * For the complete Gamma function use the
 * {@link org.jamesii.core.math.statistics.univariate.GammaFunction} class.
 * <p>
 * These methods were adapted from the <a
 * href="http://www.netlib.org/cephes/">Cephes Mathematical Library</a> by <a
 * href="http://www.moshier.net">Stephen L. Moshier</a>.
 * 
 * @author Johannes Rössel
 * 
 * @see org.jamesii.core.math.statistics.univariate.GammaFunction
 */
public final class IncompleteGammaFunction {

  /** Private constructor to prevent instantiation. */
  private IncompleteGammaFunction() {
  }

  /**
   * Calculates the regularized incomplete gamma function
   * <i><b>P</b></i>(<i>a</i>, <i>x</i>) (incomplete gamma integral,
   * <b>igam</b>) which is defined as follows:
   * <p>
   * <i>P</i>(<i>a</i>, <i>x</i>) = <i>γ</i>(<i>a</i>, <i>x</i>)/Γ(<i>a</i>)
   * <p>
   * where <i>γ</i>(<i>a</i>, <i>x</i>) is the <strong>lower</strong> incomplete
   * gamma function defined by
   * <p>
   * <i>γ</i>(<i>a</i>, <i>x</i>) = ∫<sub>0</sub><sup><i>x</i></sup>
   * <i>t</i><sup><i>a</i> − 1</sup><i>e</i><sup>−<i>t</i></sup> d<i>t</i>.
   * <p>
   * The integral is evaluated by either a power series or continued fraction
   * expansion, depending on the relative values of <i>a</i> and <i>x</i>.
   * 
   * @param a
   *          Parameter <i>a</i> in above formula. Must be non-negative.
   * @param x
   *          Upper bound of the integral.
   * @return The value of <i>P</i>(<i>a</i>, <i>x</i>) = <i>γ</i>(<i>a</i>,
   *         <i>x</i>)/Γ(<i>a</i>).
   * @exception IllegalArgumentException
   *              when <i>x</i> is less than 0 or when <i>a</i> is less than or
   *              equal to 0.
   */
  public static double iGammaP(double a, double x) {
    // exceptions
    if (x < 0 && a <= 0) {
      throw new IllegalArgumentException(
          "x must be non-negative, a must be positive");
    }
    if (x < 0) {
      throw new IllegalArgumentException("x must be non-negative");
    }
    if (a <= 0) {
      throw new IllegalArgumentException("a must be positive");
    }

    // special case, integral from x to x always equals 0.
    if (x == 0) {
      return 0.0;
    }

    if (x > 1 && x > a) {
      return 1 - iGammaQ(a, x);
    }

    double ans, ax, r, c;

    /* Compute x^a * e^-x / gamma(a) */
    ax = a * log(x) - x - logGamma(a);

    if (ax < -7.09782712893383996843E2) {
      // underflow
      return 0.0;
    }

    ax = exp(ax);

    // Power series
    r = a;
    c = 1.0;
    ans = 1.0;
    do {
      r += 1.0;
      c *= x / r;
      ans += c;
    } while (c / ans > 1.11022302462515654042E-16);

    return ans * ax / a;
  }

  /**
   * Calculates the regularized incomplete gamma function
   * <i><b>Q</b></i>(<i>a</i>, <i>x</i>) (complemented incomplete gamma
   * integral, <b>igamc</b>) which is defined as follows:
   * <p>
   * <i>Q</i>(<i>a</i>, <i>x</i>) = Γ(<i>a</i>, <i>x</i>)/Γ(<i>a</i>)
   * <p>
   * where Γ(<i>a</i>, <i>x</i>) is the <strong>upper</strong> incomplete gamma
   * function defined by
   * <p>
   * Γ(<i>a</i>, <i>x</i>) = ∫<sub><i>x</i></sub><sup>∞</sup>
   * <i>t</i><sup><i>a</i> − 1</sup><i>e</i><sup>−<i>t</i></sup> d<i>t</i>.
   * <p>
   * The integral is evaluated by either a power series or continued fraction
   * expansion, depending on the relative values of <i>a</i> and <i>x</i>.
   * 
   * @param a
   *          Parameter <i>a</i> in above formula. Must be non-negative.
   * @param x
   *          Lower bound of the integral.
   * @return The value of <i>Q</i>(<i>a</i>, <i>x</i>) = Γ(<i>a</i>,
   *         <i>x</i>)/Γ(<i>a</i>).
   * @exception IllegalArgumentException
   *              when <i>x</i> is less than 0 or when <i>a</i> is less than or
   *              equal to 0.
   */
  public static double iGammaQ(double a, double x) {
    // exceptions
    if (x < 0 && a <= 0) {
      throw new IllegalArgumentException(
          "x must be non-negative, a must be positive");
    }
    if (x < 0) {
      throw new IllegalArgumentException("x must be non-negative");
    }
    if (a <= 0) {
      throw new IllegalArgumentException("a must be positive");
    }

    double ans, ax, c, yc, r, t, y, z;
    double pk, pkm1, pkm2, qk, qkm1, qkm2;

    if (x == 0) {
      return 1.0;
    }

    if (x == POSITIVE_INFINITY) {
      return 0.0;
    }

    if (x < 1.0 || x < a) {
      return 1 - iGammaP(a, x);
    }

    ax = a * log(x) - x - logGamma(a);

    if (ax < -7.08396418532264106224E2) {
      return 0.0;
    }

    ax = exp(ax);

    // continued fraction
    y = 1.0 - a;
    z = x + y + 1.0;
    c = 0.0;
    pkm2 = 1.0;
    qkm2 = x;
    pkm1 = x + 1.0;
    qkm1 = z * x;
    ans = pkm1 / qkm1;

    do {
      c += 1.0;
      y += 1.0;
      z += 2.0;
      yc = y * c;
      pk = pkm1 * z - pkm2 * yc;
      qk = qkm1 * z - qkm2 * yc;
      if (qk != 0) {
        r = pk / qk;
        t = abs((ans - r) / r);
        ans = r;
      } else {
        t = 1.0;
      }
      pkm2 = pkm1;
      pkm1 = pk;
      qkm2 = qkm1;
      qkm1 = qk;
      if (abs(pk) > 4.503599627370496e15) {
        pkm2 *= 2.22044604925031308085e-16;
        pkm1 *= 2.22044604925031308085e-16;
        qkm2 *= 2.22044604925031308085e-16;
        qkm1 *= 2.22044604925031308085e-16;
      }
    } while (t > 1.11022302462515654042E-16);

    return ans * ax;
  }

}
