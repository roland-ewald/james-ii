/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math;

import org.jamesii.core.math.statistics.univariate.GammaFunction;

/**
 * Class for calculation of the binomial coefficient.
 * 
 * @author Jan Himmelspach
 * @version 1.0
 */
public final class BinomialCoefficient {

  /**
   * Hidden constructor.
   */
  private BinomialCoefficient() {
  }

  /**
   * Calculates the binomial coefficient by using the definition
   * <i>n</i>! / (<i>k</i>!(<i>n</i> − <i>k</i>)!) for 0 ≤ <i>k</i> ≤ <i>n</i>
   * and returns 0 if <i>k</i> is outside that range.
   * 
   * @see Factorial
   * 
   * @param n
   *          non-negative integer
   * @param k
   *          integer
   * 
   * @return the binomial coefficient (n over k)
   */
  public static long binomial(int n, int k) {
    if (n < 0) {
      throw new IllegalArgumentException("n must be non-negative");
    }
    if (k < 0 || k > n) {
      return 0;
    }
    if (k > n - k) {
      return (Factorial.fac(n, k) / Factorial.fac(n - k));
    }
    return (Factorial.fac(n, n - k) / Factorial.fac(k));
  }

  /**
   * Calculates the binomial coefficient by expanding the factorials inline and
   * using {@code double}s instead of {@code long}s. This may be faster on some
   * occasions and also supports a wider input range.
   * 
   * @param n
   *          non-negative integer
   * @param k
   *          integer
   * @return binomial coefficient (n over k)
   */
  public static double binomialQuick(int n, int k) {
    if (n < 0) {
      throw new IllegalArgumentException("n must be non-negative");
    }
    if (k == 0 || k == n) {
      return 1;
    }
    if (k > n || k < 0) {
      return 0;
    }

    if (k > n / 2) {
      k = n - k;
    }

    double result = n;
    double kFac = 1;
    for (int i = 2; i <= k; i++) {
      result *= (n - i + 1);
      kFac *= i;
    }
    return result / kFac;
  }

  /**
   * Calculates the binomial coefficient by expanding the factorials inline and
   * using {@code double}s instead of {@code long}s. This may be faster on some
   * occasions and also supports a wider input range.
   * 
   * @param n
   *          non-negative long
   * @param k
   *          long
   * @return binomial coefficient (n over k)
   */
  public static double binomialQuick(long n, long k) {
    if (n < 0) {
      throw new IllegalArgumentException(
          "The value passed as first parameter must be non-negative. The value passed is "
              + n + ".");
    }
    if (k == 0 || k == n) {
      return 1;
    }
    if (k > n || k < 0) {
      return 0;
    }

    if (k > n / 2) {
      k = n - k;
    }

    double result = n;
    double kFac = 1;
    for (int i = 2; i <= k; i++) {
      result *= (n - i + 1);
      kFac *= i;
    }
    return result / kFac;
  }

  public static double binomialQuick3(long n, long k) {
    if (k == 1) {
      return n;
    }
    if (k == 2) {
      return n * (n - 1) / 2.;
    }
    if (n < 0) {
      throw new IllegalArgumentException("n must be non-negative");
    }
    if (k == 0 || k == n) {
      return 1;
    }
    if (k > n || k < 0) {
      return 0;
    }

    if (k > n / 2) {
      k = n - k;
    }

    double result = n;
    double kFac = 1;
    for (int i = 2; i <= k; i++) {
      result *= (n - i + 1);
      kFac *= i;
    }
    return result / kFac;
  }

  public static double binomialQuick2(long n, long k) {

    if (n < 0) {
      throw new IllegalArgumentException("n must be non-negative");
    }
    if (k == 0 || k == n) {
      return 1;
    }
    if (k > n || k < 0) {
      return 0;
    }

    if (k > n / 2) {
      k = n - k;
    }

    double result = n;
    double kFac = 1;

    // just check whether we can avoid the computation of the kFac
    if (k < Factorial.cached_vals.length) {
      kFac = Factorial.cached_vals[(int) k];
      for (int i = 1; i < k; i++) {
        result *= (n - i);
      }
      return result / kFac;
    }

    for (int i = 2; i <= k; i++) {
      result *= (n - i + 1);
      kFac *= i;
    }
    return result / kFac;
  }

  public static double binomial(double n, double k) {
    if (k > n) {
      return 0.0;
    }
    double gamma1 = GammaFunction.gamma(n + 1);
    double gamma2 = GammaFunction.gamma(k + 1);
    double gamma3 = GammaFunction.gamma(n - k + 1);
    return gamma1 / (gamma2 * gamma3);
  }
}
