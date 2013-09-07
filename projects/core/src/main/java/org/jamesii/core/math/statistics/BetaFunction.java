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
import static java.lang.Math.floor;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import static java.lang.Math.signum;
import static org.jamesii.core.math.statistics.univariate.GammaFunction.gamma;
import static org.jamesii.core.math.statistics.univariate.GammaFunction.logGamma;

/**
 * Class for calculating the Beta function and related functions, such as the
 * incomplete Beta function.
 * <p>
 * These methods were adapted from the <a
 * href="http://www.netlib.org/cephes/">Cephes Mathematical Library</a> by <a
 * href="http://www.moshier.net">Stephen L. Moshier</a>.
 * 
 * @author Johannes Rössel
 */
public final class BetaFunction {

  /** Natural logarithm of 2<sup>1024</sup>. */
  private static final double MAXLOG = 7.09782712893383996843E2;

  /** Natural logarithm of 2<sup>-1022</sup>. */
  private static final double MINLOG = -7.08396418532264106224E2;

  /** 2<sup>-53</sup>. Smallest mantissa value in a double. */
  private static final double MACHEP = 1.11022302462515654042E-16;

  /** Maximum argument value for the gamma function. */
  private static final double MAXGAM = 171.624376956302725;

  /** Private constructor to prevent instantiation. */
  private BetaFunction() {
  }

  /**
   * Calculates the Beta function <i>Β</i>(<i>a</i>, <i>b</i>) which is defined
   * by
   * <p>
   * <i>Β</i>(<i>a</i>, <i>b</i>) = Γ(<i>a</i>)Γ(<i>b</i>)/Γ(<i>a</i> +
   * <i>b</i>).
   * <p>
   * For large arguments the logarithm of the function is evaluated using
   * {@link org.jamesii.core.math.statistics.univariate.GammaFunction#logGamma(double)}
   * , then exponentiated.
   * 
   * @param a
   *          First argument.
   * @param b
   *          Second argument.
   * @return <i>Β</i>(<i>a</i>, <i>b</i>)
   */
  public static double beta(double a, double b) {
    double y;
    int sign = 1;

    if (a <= 0 && a == floor(a)) {
      return sign * POSITIVE_INFINITY;
    }
    if (b <= 0 && b == floor(b)) {
      return sign * POSITIVE_INFINITY;
    }

    y = a + b;
    if (abs(y) > MAXGAM) {
      y = logGamma(y);
      sign *= signum(y);
      double lgb = logGamma(b);
      y = lgb - y;
      sign *= signum(lgb);
      double lga = logGamma(a);
      y = lga + y;
      sign *= signum(lga);

      if (y > MAXLOG) {
        return sign * POSITIVE_INFINITY;
      }

      return sign * exp(y);
    }

    y = gamma(y);

    if (y == 0.0) {
      return sign * POSITIVE_INFINITY;
    }

    if (a > b) {
      y = gamma(a) / y;
      y *= gamma(b);
    } else {
      y = gamma(b) / y;
      y *= gamma(a);
    }

    return (y);
  }

  /**
   * Calculates the regularized incomplete beta function
   * <i>I</i><sub><i>x</i></sub>(<i>a</i>, <i>b</i>) which is defined by
   * <p>
   * <i>I</i><sub><i>x</i></sub>(<i>a</i>, <i>b</i>) = <i>Β</i>(<i>x</i>;
   * <i>a</i>, <i>b</i>)/<i>Β</i>(<i>a</i>, <i>b</i>)
   * <p>
   * where <i>Β</i>(<i>a</i>, <i>b</i>) is the <em>complete</em> beta function
   * and <i>Β</i>(<i>x</i>; <i>a</i>, <i>b</i>) is the <em>incomplete</em> beta
   * function, defined by
   * <p>
   * <i>Β</i>(<i>x</i>; <i>a</i>, <i>b</i>) = ∫<sub>0</sub><sup><i>x</i></sup>
   * <i>t</i><sup><i>a</i> − 1</sup>(1 − <i>t</i>)<sup><i>b</i> − 1</sup>
   * d<i>t.</i>
   * <p>
   * Therefore:
   * <p>
   * <i>I</i><sub><i>x</i></sub>(<i>a</i>, <i>b</i>) = Γ(<i>a</i> +
   * <i>b</i>)/(Γ(<i>a</i>)Γ(<i>b</i>)) · ∫<sub>0</sub><sup><i>x</i></sup>
   * <i>t</i><sup><i>a</i> − 1</sup>(1 − <i>t</i>)<sup><i>b</i> − 1</sup>
   * d<i>t.</i>
   * <p>
   * <strong>Note:</strong> For consistency reasons with other software
   * implementations of this function the parameters are in a different order
   * than they are in mathematical contexts. The upper integration limit
   * <i>x</i> is the last argument to this method, it is, however, usually given
   * as the first argument (or a subscript) in mathematics.
   * 
   * @param a
   *          First argument to the function.
   * @param b
   *          Second argument to the function.
   * @param x
   *          Integration limit.
   * @return <i>I</i><sub><i>x</i></sub>(<i>a</i>, <i>b</i>)
   */
  public static double iBeta(double a, double b, double x) {
    if (a <= 0.0 || b <= 0.0) {
      throw new IllegalArgumentException("a and b must be positive.");
    }

    if ((x <= 0.0) || (x >= 1.0)) {
      if (x == 0.0) {
        return 0;
      }
      if (x == 1.0) {
        return 1;
      }
      throw new IllegalArgumentException("x must be between 0 and 1.");
    }

    double aa, bb, t, xx, xc, w, y;
    int flag = 0;

    if (b * x <= 1 && x <= 0.95) {
      return iBetaPowerSeries(a, b, x);
    }

    w = 1.0 - x;

    // reverse a and b if x is greater than the mean
    if (x > (a / (a + b))) {
      flag = 1;
      aa = b;
      bb = a;
      xc = x;
      xx = w;
    } else {
      aa = a;
      bb = b;
      xc = w;
      xx = x;
    }

    if (flag == 1 && bb * xx <= 1.0 && xx <= 0.95) {
      t = iBetaPowerSeries(aa, bb, xx);
      if (t <= MACHEP) {
        t = 1.0 - MACHEP;
      } else {
        t = 1.0 - t;
      }
      return t;
    }

    // choose expansion for better convergence
    y = xx * (aa + bb - 2.0) - (aa - 1.0);
    if (y < 0.0) {
      w = iBetaContinuedFraction1(aa, bb, xx);
    } else {
      w = iBetaContinuedFraction2(aa, bb, xx) / xc;
    }

    // multiply w by x^a (1-x)^b gamma(a+b) / (a gamma(a) gamma(b)).
    y = aa * log(xx);
    t = bb * log(xc);
    if ((aa + bb) < MAXGAM && abs(y) < MAXLOG && abs(t) < MAXLOG) {
      t = pow(xc, bb);
      t *= pow(xx, aa);
      t /= aa;
      t *= w;
      t *= gamma(aa + bb) / (gamma(aa) * gamma(bb));
      if (flag == 1) {
        if (t <= MACHEP) {
          t = 1.0 - MACHEP;
        } else {
          t = 1.0 - t;
        }
      }
      return t;
    }

    // resort to logarithms
    y += t + logGamma(aa + bb) - logGamma(aa) - logGamma(bb);
    y += log(w / aa);
    if (y < MINLOG) {
      t = 0.0;
    } else {
      t = exp(y);
    }

    if (flag == 1) {
      if (t <= MACHEP) {
        t = 1.0 - MACHEP;
      } else {
        t = 1.0 - t;
      }
    }
    return t;
  }

  /**
   * Calculates the incomplete beta function using a specific continued fraction
   * expansion.
   * 
   * @param a
   *          First argument to the function.
   * @param b
   *          Second argument to the function.
   * @param x
   *          Integration limit.
   * @return <i>I</i><sub><i>x</i></sub>(<i>a</i>, <i>b</i>)
   */
  private static double iBetaContinuedFraction1(double a, double b, double x) {
    double xk, pk, pkm1, pkm2, qk, qkm1, qkm2;
    double k1, k2, k3, k4, k5, k6, k7, k8;
    double r, t, ans, thresh;
    int n;

    double big = 4.503599627370496e15;
    double biginv = 2.22044604925031308085e-16; // 1/big

    k1 = a;
    k2 = a + b;
    k3 = a;
    k4 = a + 1.0;
    k5 = 1.0;
    k6 = b - 1.0;
    k7 = k4;
    k8 = a + 2.0;

    pkm2 = 0.0;
    qkm2 = 1.0;
    pkm1 = 1.0;
    qkm1 = 1.0;
    ans = 1.0;
    r = 1.0;
    n = 0;
    thresh = 3.0 * MACHEP;
    do {

      xk = -(x * k1 * k2) / (k3 * k4);
      pk = pkm1 + pkm2 * xk;
      qk = qkm1 + qkm2 * xk;
      pkm2 = pkm1;
      pkm1 = pk;
      qkm2 = qkm1;
      qkm1 = qk;

      xk = (x * k5 * k6) / (k7 * k8);
      pk = pkm1 + pkm2 * xk;
      qk = qkm1 + qkm2 * xk;
      pkm2 = pkm1;
      pkm1 = pk;
      qkm2 = qkm1;
      qkm1 = qk;

      if (qk != 0) {
        r = pk / qk;
      }
      if (r != 0) {
        t = abs((ans - r) / r);
        ans = r;
      } else {
        t = 1.0;
      }

      if (t < thresh) {
        break;
      }

      k1 += 1.0;
      k2 += 1.0;
      k3 += 2.0;
      k4 += 2.0;
      k5 += 1.0;
      k6 -= 1.0;
      k7 += 2.0;
      k8 += 2.0;

      if ((abs(qk) + abs(pk)) > big) {
        pkm2 *= biginv;
        pkm1 *= biginv;
        qkm2 *= biginv;
        qkm1 *= biginv;
      }
      if ((abs(qk) < biginv) || (abs(pk) < biginv)) {
        pkm2 *= big;
        pkm1 *= big;
        qkm2 *= big;
        qkm1 *= big;
      }
    } while (++n < 300);

    return ans;
  }

  /**
   * Calculates the incomplete beta function using a specific continued fraction
   * expansion.
   * 
   * @param a
   *          First argument to the function.
   * @param b
   *          Second argument to the function.
   * @param x
   *          Integration limit.
   * @return <i>I</i><sub><i>x</i></sub>(<i>a</i>, <i>b</i>)
   */
  private static final double iBetaContinuedFraction2(double a, double b,
      double x) {
    double xk, pk, pkm1, pkm2, qk, qkm1, qkm2;
    double k1, k2, k3, k4, k5, k6, k7, k8;
    double r, t, ans, z, thresh;
    int n;

    double big = 4.503599627370496e15;
    double biginv = 2.22044604925031308085e-16; // 1/big

    k1 = a;
    k2 = b - 1.0;
    k3 = a;
    k4 = a + 1.0;
    k5 = 1.0;
    k6 = a + b;
    k7 = a + 1.0;
    k8 = a + 2.0;

    pkm2 = 0.0;
    qkm2 = 1.0;
    pkm1 = 1.0;
    qkm1 = 1.0;
    z = x / (1.0 - x);
    ans = 1.0;
    r = 1.0;
    n = 0;
    thresh = 3.0 * MACHEP;
    do {

      xk = -(z * k1 * k2) / (k3 * k4);
      pk = pkm1 + pkm2 * xk;
      qk = qkm1 + qkm2 * xk;
      pkm2 = pkm1;
      pkm1 = pk;
      qkm2 = qkm1;
      qkm1 = qk;

      xk = (z * k5 * k6) / (k7 * k8);
      pk = pkm1 + pkm2 * xk;
      qk = qkm1 + qkm2 * xk;
      pkm2 = pkm1;
      pkm1 = pk;
      qkm2 = qkm1;
      qkm1 = qk;

      if (qk != 0) {
        r = pk / qk;
      }
      if (r != 0) {
        t = abs((ans - r) / r);
        ans = r;
      } else {
        t = 1.0;
      }

      if (t < thresh) {
        break;
      }

      k1 += 1.0;
      k2 -= 1.0;
      k3 += 2.0;
      k4 += 2.0;
      k5 += 1.0;
      k6 += 1.0;
      k7 += 2.0;
      k8 += 2.0;

      if ((abs(qk) + abs(pk)) > big) {
        pkm2 *= biginv;
        pkm1 *= biginv;
        qkm2 *= biginv;
        qkm1 *= biginv;
      }
      if ((abs(qk) < biginv) || (abs(pk) < biginv)) {
        pkm2 *= big;
        pkm1 *= big;
        qkm2 *= big;
        qkm1 *= big;
      }
    } while (++n < 300);

    return (ans);
  }

  /**
   * Calculates the incomplete beta function using a power series.
   * 
   * @param a
   *          First argument to the function.
   * @param b
   *          Second argument to the function.
   * @param x
   *          Integration limit.
   * @return <i>I</i><sub><i>x</i></sub>(<i>a</i>, <i>b</i>)
   */
  private static double iBetaPowerSeries(double a, double b, double x) {
    double s, t, u, v, n, t1, z, ai;

    ai = 1.0 / a;
    u = (1.0 - b) * x;
    v = u / (a + 1.0);
    t1 = v;
    t = u;
    n = 2.0;
    s = 0.0;
    z = MACHEP * ai;

    while (abs(v) > z) {
      u = (n - b) * x / n;
      t *= u;
      v = t / (a + n);
      s += v;
      n += 1.0;
    }
    s += t1;
    s += ai;

    u = a * log(x);
    if ((a + b) < MAXGAM && abs(u) < MAXLOG) {
      t = gamma(a + b) / (gamma(a) * gamma(b));
      s = s * t * pow(x, a);
    } else {
      t = logGamma(a + b) - logGamma(a) - logGamma(b) + u + log(s);
      if (t < MINLOG) {
        s = 0.0;
      } else {
        s = exp(t);
      }
    }
    return (s);
  }

  /**
   * Calculates the inverse incomplete Beta function, numerically.
   * 
   * @param p
   *          probability associated with the beta distribution
   * @param alpha
   *          parameter of the distribution
   * @param beta
   *          parameter of the distribution
   * @param precision
   *          of the result
   * @param A
   *          optional lower bound to the interval of x
   * @param B
   *          optional upper bound to the interval of x
   * @return inverse of the cumulative beta probability density function for a
   *         given probability
   */
  public static double inverseBeta(double p, double alpha, double beta,
      double precision, double A, double B) {
    double x = 0;
    double a = 0;
    double b = 1;
    while ((b - a) > precision) {
      x = (a + b) / 2;
      if (iBeta(alpha, beta, x) > p) {
        b = x;
      } else {
        a = x;
      }
    }
    if ((B > 0) && (A > 0)) {
      x = x * (B - A) + A;
    }
    return x;
  }

}
