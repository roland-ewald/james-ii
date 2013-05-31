/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.univariate;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.NaN;
import static java.lang.Double.POSITIVE_INFINITY;
import static java.lang.Double.isInfinite;
import static java.lang.Double.isNaN;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.exp;
import static java.lang.Math.floor;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import static java.lang.Math.sin;

import org.jamesii.core.math.statistics.IncompleteGammaFunction;

/**
 * Class for calculating the Gamma function and a related function, the logGamma
 * function.
 * <p>
 * For the incomplete Gamma functions use the {@link IncompleteGammaFunction}
 * class.
 * <p>
 * These methods were adapted from the <a
 * href="http://www.netlib.org/cephes/">Cephes Mathematical Library</a> by <a
 * href="http://www.moshier.net">Stephen L. Moshier</a>.
 * 
 * @author Johannes Rössel
 * 
 * @see org.jamesii.core.math.statistics.IncompleteGammaFunction
 */
public final class GammaFunction {

  /** Private constructor to prevent instantiation. */
  private GammaFunction() {
  }

  /**
   * Calculates the Gamma function Γ(<i>z</i>). The gamma function has the
   * following formula:
   * <p>
   * Γ(<i>z</i>) = ∫<sub>0</sub><sup>∞</sup> <i>t</i><sup><i>z</i> −
   * 1</sup><i>e</i><sup>−<i>t</i></sup> d<i>t</i>
   * <p>
   * Arguments |<i>z</i>| ≤ 34 are reduced by recurrence and the function
   * approximated by a rational function of degree 6/7 in the interval (2, 3).
   * Large arguments are handled by Stirling's formula. Large negative arguments
   * are made positive using a reflection formula.
   * 
   * @param z
   *          Argument to Γ.
   * @return Γ(<i>z</i>).
   */
  public static double gamma(double z) {
    double p, q, x;
    int i, sgngam = 1;

    if (isNaN(z)) {
      return z;
    }
    if (z == POSITIVE_INFINITY) {
      return z;
    }
    if (z == NEGATIVE_INFINITY) {
      return NaN;
    }

    q = abs(z);

    if (Double.compare(q, 33.0) > 0) {// NOSONAR
      if (z < 0.0) {
        p = floor(q);
        if (Double.compare(p, q) == 0) {
          return NaN;
        }
        i = (int) p;
        if ((i & 1) == 0) {
          sgngam = -1;
        }
        x = q - p;
        if (Double.compare(x, 0.5) > 0) {// NOSONAR
          p += 1.0;
          x = q - p;
        }
        x = q * sin(PI * x);
        if (Double.compare(x, 0.0) == 0) {
          if (sgngam > 0) {
            return POSITIVE_INFINITY;
          }
          return NEGATIVE_INFINITY;
        }
        x = abs(x);
        x = PI / (x * gammaStirling(q));
      } else {
        x = gammaStirling(z);
      }
      return sgngam * x;
    }

    x = 1.0;
    while (Double.compare(z, 3.0) >= 0) {// NOSONAR
      z -= 1.0;
      x *= z;
    }

    while (z < 0.0) {
      if (z > -1.E-9) {
        if (Double.compare(z, 0.0) == 0) {
          return NaN;
        }
        return x / ((1.0 + 0.5772156649015329 * z) * z);// NOSONAR
      }
      x /= z;
      z += 1.0;
    }

    while (z < 2.0) {
      if (z < 1.e-9) {
        if (Double.compare(z, 0.0) == 0) {
          return NaN;
        }
        return x / ((1.0 + 0.5772156649015329 * z) * z);// NOSONAR
      }
      x /= z;
      z += 1.0;
    }

    if (Double.compare(z, 2.0) == 0) {
      return x;
    }

    z -= 2.0;

    p =
        ((((((1.60119522476751861407E-4 * z + 1.19135147006586384913E-3) * z + 1.04213797561761569935E-2)// NOSONAR
            * z + 4.76367800457137231464E-2)// NOSONAR
            * z + 2.07448227648435975150E-1)// NOSONAR
            * z + 4.94214826801497100753E-1)// NOSONAR
            * z + 9.99999999999999996796E-1);// NOSONAR
    q =
        (((((((-2.31581873324120129819E-5 * z + 5.39605580493303397842E-4) * z + -4.45641913851797240494E-3)// NOSONAR
            * z + 1.18139785222060435552E-2)// NOSONAR
            * z + 3.58236398605498653373E-2)// NOSONAR
            * z + -2.34591795718243348568E-1)// NOSONAR
            * z + 7.14304917030273074085E-2)// NOSONAR
            * z + 1.00000000000000000320E0);// NOSONAR
    return x * p / q;
  }

  /**
   * Computes the Gamma function using Stirling's formula. This computation is
   * valid for 33 ≤ <i>z</i> ≤ 172.
   * 
   * @param z
   *          Argument to Γ.
   * @return Γ(<i>z</i>).
   */
  private static double gammaStirling(double z) {
    double y, w, v;

    w = 1.0 / z;
    w =
        1.0 + w
            * ((((7.87311395793093628397E-4 * w + -2.29549961613378126380E-4)// NOSONAR
                * w + -2.68132617805781232825E-3)// NOSONAR
                * w + 3.47222221605458667310E-3)// NOSONAR
                * w + 8.33333333333482257126E-2);// NOSONAR
    y = exp(z);
    if (z > 143.01608) { /* Avoid overflow in pow() */// NOSONAR
      v = pow(z, 0.5 * z - 0.25);
      y = v * (v / y);
    } else {
      y = pow(z, z - 0.5) / y;
    }
    y = 2.50662827463100050242E0 * y * w; // NOSONAR
    return (y);
  }

  /**
   * Calculates the natural (base <i>e</i>) logarithm of the absolute value of
   * the gamma function of the argument.
   * <p>
   * For arguments greater than 13, the logarithm of the gamma function is
   * approximated by the logarithmic version of Stirling's formula using a
   * polynomial approximation of degree 4. Arguments between −33 and +33 are
   * reduced by recurrence to the interval [2, 3] of a rational approximation.
   * The cosecant reflection formula is employed for arguments less than −33.
   * 
   * @param x
   *          The argument to the function.
   * @return <b>ln</b> |Γ(<i>x</i>)|.
   */
  public static double logGamma(double x) {
    if (isNaN(x)) {
      return NaN;
    }
    if (isInfinite(x)) {
      return POSITIVE_INFINITY;
    }

    double p, q, u, w, z;

    if (Double.compare(x, -34) < 0) { // NOSONAR
      q = -x;
      w = logGamma(q);
      p = floor(q);
      if (Double.compare(p, q) == 0) {
        return POSITIVE_INFINITY;
      }

      z = q - p;
      if (Double.compare(z, 0.5) > 0) {
        p += 1;
        z = p - q;
      }
      z = q * sin(PI * z);
      if (z == 0.0) {
        return POSITIVE_INFINITY;
      }

      z = log(PI) - log(z) - w;
      return z;
    }

    double x2 = x;

    if (Double.compare(x2, 13) < 0) {
      z = 1.0;
      p = 0.0;
      u = x2;
      while (Double.compare(u, 3.0) >= 0) {
        p -= 1.0;
        u = x2 + p;
        z *= u;
      }
      while (Double.compare(u, 2.0) < 0) {
        if (u == 0.0) {
          return POSITIVE_INFINITY;
        }
        z /= u;
        p += 1.0;
        u = x2 + p;
      }

      if (Double.compare(z, 0.0) < 0) {
        z = -z;
      }

      if (Double.compare(u, 2.0) == 0) {
        return log(z);
      }

      p -= 2.0;
      x2 = x2 + p;
      p =
          x2
              * (((((-1.37825152569120859100E3 * x2 - 3.88016315134637840924E4) // NOSONAR
                  * x2 - 3.31612992738871184744E5)// NOSONAR
                  * x2 - 1.16237097492762307383E6)// NOSONAR
                  * x2 - 1.72173700820839662146E6)// NOSONAR
                  * x2 - 8.53555664245765465627E5)// NOSONAR
              / ((((((x2 - 3.51815701436523470549E2) * x2 - 1.70642106651881159223E4)// NOSONAR
                  * x2 - 2.20528590553854454839E5)// NOSONAR
                  * x2 - 1.13933444367982507207E6)// NOSONAR
                  * x2 - 2.53252307177582951285E6)// NOSONAR
                  * x2 - 2.01889141433532773231E6);// NOSONAR

      return log(z) + p;
    }

    if (Double.compare(x2, 2.556348e305) > 0) {// NOSONAR
      return POSITIVE_INFINITY;
    }

    q = (x2 - 0.5) * log(x2) - x2 + 0.91893853320467274178;// NOSONAR
    if (Double.compare(x2, 1e8) > 0) {
      return q;
    }

    p = 1.0 / (x2 * x2);
    if (x2 > 1000) {// NOSONAR
      q +=
          ((7.9365079365079365079365e-4 * p - 2.7777777777777777777778e-3) * p + 0.0833333333333333333333)// NOSONAR
              / x2;
    } else {
      q +=
          ((((8.11614167470508450300E-4 * p - 5.95061904284301438324E-4) * p + 7.93650340457716943945E-4)// NOSONAR
              * p - 2.77777777730099687205E-3)// NOSONAR
              * p + 8.33333333333331927722E-2)// NOSONAR
              / x2;
    }

    return q;
  }

}
