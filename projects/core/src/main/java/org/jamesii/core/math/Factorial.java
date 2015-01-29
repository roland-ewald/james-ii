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
 * {@link #CACHED_VALUES} ) for the first m ns, and thus reduces the number of
 * multiplications to be done to 0 or n - number of pre-computed values
 * available.
 * 
 * @author Jan Himmelspach
 * @author Arne Bittig
 */
public final class Factorial {

  /**
   * The Constant cached_vals. Contains pre-computed facs for values up to the
   * length of the array starting with the fac of 0
   */
  public static final double[] CACHED_VALUES = new double[] { 1 /* = 0! */,
      1 /* 1! */, 2 /* 2! */, 6 /* 3! */, 24 /* 4! */, 120 /* 5! */,
      720 /* 6! */, 5040 /* 7! */, 40320 /* 8! */, 362880 /* 9! */,
      3628800 /* 10! */, 39916800 /* 11! */, 479001600 /* 12! */,
      6227020800l /* 13! */, 87178291200l /* 14! */, 1307674368000l /* 15! */,
      20922789888000l /* 16! */, 355687428096000l /* 17! */,
      6402373705728000l /* 18! */, 121645100408832000l /* 19! */,
      2432902008176640000l /* 20! */, 51090942171709440000d /* 21! */,
      1124000727777607680000d /* 22! */, 25852016738884976640000d /* 23! */,
      620448401733239439360000d /* 24! */,
      15511210043330985984000000d /* 25! */,
      403291461126605635584000000d /* 26! */,
      10888869450418352160768000000d /* 27! */,
      304888344611713860501504000000d /* 28! */,
      8841761993739701954543616000000d /* 29! */,
      8841761993739701954543616000000d * 30 /* 30! */
  };

  private static final int NUM_CACHED_VALS = CACHED_VALUES.length;

  /** Private empty constructor to prevent instantiation. */
  private Factorial() {
  }

  /**
   * Quick fac.
   * 
   * @param n
   *          the n, n! shall be computed of
   * 
   * @return the double
   */
  public static double quickFac(int n) {
    if (n < NUM_CACHED_VALS) {
      return CACHED_VALUES[n];
    }

    double result = CACHED_VALUES[NUM_CACHED_VALS - 1];
    for (int i = NUM_CACHED_VALS; i <= n; i++) {
      result *= i;
    }
    return result;
  }

  private static final long[] CACHED_LONG = new long[25];
  static {
    CACHED_LONG[0] = 1;
    CACHED_LONG[1] = 1;
    for (int i = 2; i < CACHED_LONG.length; i++) {
      CACHED_LONG[i] = i * CACHED_LONG[i - 1];
    }
  }

  /**
   * n!.
   * 
   * @param n
   *          the n
   * @return the factorial of n
   * @throws IllegalArgumentException
   *           if n<0
   * @throws ArithmeticException
   *           in case of long overflow
   */
  public static long fac(int n) {
    if (n < 0) {
      throw new IllegalArgumentException("Argument must be non-negative, not "
          + n);
    }
    if (n >= CACHED_LONG.length) {
      throw new ArithmeticException("Long overflow for argument " + n + " >= "
          + CACHED_LONG.length);
    }
    return CACHED_LONG[n];
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

  private static final long[] CACHED_SEMI = new long[34];
  static {
    CACHED_SEMI[0] = 1;
    CACHED_SEMI[1] = 1;
    for (int i = 2; i < CACHED_SEMI.length; i++) {
      CACHED_SEMI[i] = i * CACHED_SEMI[i - 2];
    }
  }

  /**
   * Semi-factorial, also called double factorial or n!!, is the product of all
   * positive integers up to some n with the same parity as n, i.e. the product
   * of every second integer from 1 to odd n or from 2 to even n.
   * 
   * @param n
   *          semi-factorial argument
   * @return n!!
   * @throws IllegalArgumentException
   *           for n<0
   * @throws ArithmeticException
   *           if result does not fit into long
   */
  public static long semiFac(int n) {
    if (n < 0) {
      throw new IllegalArgumentException("Argument must be non-negative, not "
          + n);
    }
    if (n >= CACHED_SEMI.length) {
      throw new ArithmeticException("Long overflow for argument " + n + " >= "
          + CACHED_SEMI.length);
    }
    return CACHED_SEMI[n];
  }

}
