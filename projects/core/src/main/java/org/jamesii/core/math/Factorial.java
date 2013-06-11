/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math;

/**
 * The Factorial class contains methods to compute n! of a given n. There are
 * several implementations of this method - having different return values and
 * thus allowing different max n for computation.
 * 
 * The {@link #quickFac(int)} methods uses pre-computed faculties (
 * {@link #cached_vals} ) for the first m ns, and thus reduces the number of
 * multiplications to be done to 0 or n - number of pre-computed values
 * available.
 * 
 * @author Jan Himmelspach
 * @version 1.0
 */
public final class Factorial {

  /**
   * The Constant cached_vals. Contains pre-computed facs for values up to the
   * length of the array starting with the fac of 0
   */
  public static final double[] cached_vals = new double[] { 1 /* = 0! */,
      1 /* 1! */, 2 /* 2! */, 6 /* 3! */, 24 /* 4! */, 120 /* 5! */,
      720 /* 6! */, 5040 /* 7! */, 40320 /* 8! */, 362880 /* 9! */,
      3628800 /* 10! */, 39916800 /* 11! */, 479001600 /* 12! */,
      6227020800l /* 13! */, 87178291200l /* 14! */, 1307674368000l /* 15! */,
      20922789888000l /* 16! */, 355687428096000l /* 17! */,
      6402373705728000l /* 18! */, 121645100408832000l /* 19! */,
      2432902008176640000l /* 20! */, 51090942171709440000d /* 21! */,
      1124000727777607680000d /* 22! */, 25852016738884976640000d /* 23! */,
      620448401733239439360000d /* 24! */, 15511210043330985984000000d /*
                                                                        * 25!
                                                                        */,
      403291461126605635584000000d /* 26! */,
      10888869450418352160768000000d /* 27! */,
      304888344611713860501504000000d /* 28! */,
      8841761993739701954543616000000d /* 29! */,
      8841761993739701954543616000000d * 30 /* 30! */
  };

  /**
   * Quick fac.
   * 
   * @param n
   *          the n, n! shall be computed of
   * 
   * @return the double
   */
  public static double quickFac(int n) {

    if (n < cached_vals.length) {
      return cached_vals[n];
    }

    double result = cached_vals[cached_vals.length - 1];

    for (int i = cached_vals.length; i <= n; i++) {
      result *= i;
    }

    return result;
  }

  /** Private empty constructor to prevent instantiation. */
  private Factorial() {
  }

  /**
   * n!.
   * 
   * @param n
   *          the n
   * 
   * @return the factorial of n
   */
  public static long fac(int n) {
    long result = 1;
    for (int i = 2; i <= n; i++) {
      result *= i;
    }
    return result;
  }

  /**
   * n!.
   * 
   * @param n
   *          the n
   * 
   * @return the factorial of n as double value (allows rather large facs to be
   *         computed)
   */
  public static double facD(int n) {
    double result = 1;
    for (int i = 2; i <= n; i++) {
      result *= i;
    }
    return result;
  }

  /**
   * This method computes n! div k!. Thus, the larger k is, the less terms needs
   * to be multiplied! n >= k n! / k!
   * 
   * @param n
   *          the n
   * @param k
   *          the k
   * 
   * @return result of n! / k!
   */
  public static long fac(int n, int k) {
    if (k > n) {
      throw new IllegalArgumentException("n must be larger than k");
    }
    long result = 1;
    for (int i = k + 1; i <= n; i++) {
      result *= i;
    }
    return result;
  }

}
