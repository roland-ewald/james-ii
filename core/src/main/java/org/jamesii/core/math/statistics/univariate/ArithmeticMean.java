/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.univariate;

import java.util.List;

/**
 * Helper class for computation of arithmetic means on various number lists and
 * arrays.
 * 
 * @author Jan Pommerenke
 */
public final class ArithmeticMean {

  /**
   * Hidden constructor.
   */
  private ArithmeticMean() {
  }

  /**
   * Computes the arithmetic mean of the values in the given list. This may fail
   * on arrays containing many very large values, i. e. the result is wrong if
   * the sum of the values in the array exceeds the range of {@code double}.
   * 
   * @param x
   *          list of double values
   * @return the arithmetic mean of the list values
   */
  public static double arithmeticMean(List<? extends Number> x) {
    return Variance.varianceAndAM(x).getSecondValue();
  }

  /**
   * Computes the arithmetic mean of the values in the given array. This may
   * fail on arrays containing very large values, i. e. the result is wrong if
   * the sum of the values in the array exceeds the range of {@code double}.
   * 
   * @param x
   *          array of double values
   * @return the arithmetic mean of the array values
   */
  public static double arithmeticMean(double[] x) {
    return Variance.varianceAndAM(x).getSecondValue();
  }

  /**
   * Computes the arithmetic mean of the values in the given array. This may
   * fail on arrays containing very large values, i. e. the result is wrong if
   * the sum of the values in the array exceeds the range of {@code double}.
   * 
   * @param x
   *          array of double values
   * @return the arithmetic mean of the array values
   */
  public static double arithmeticMean(Double[] x) {
    return Variance.varianceAndAM(x).getSecondValue();
  }

  /**
   * Computes the arithmetic mean of the values in the given array. This may
   * fail on arrays containing many very large values, i. e. the result is wrong
   * if the sum of the values in the array exceeds the range of {@code long}.
   * 
   * @param x
   *          array of integer values
   * @return the arithmetic mean of the array values
   */
  public static double arithmeticMean(int[] x) {
    return Variance.varianceAndAM(x).getSecondValue();
  }

  /**
   * Computes the arithmetic mean of the values in the given array. This method
   * is meant for very large arrays only! If the sum of the values in the array
   * is expected to not exceed the range of {@code double} the method
   * {@link #arithmeticMean(double[])} should be used instead!
   * <p>
   * This results of this method are probably not accurate.
   * 
   * @param x
   *          array of double values
   * @return the arithmetic mean of the array values
   */
  public static double arithmeticMeanLarge(double[] x) {
    if (x.length == 0) {
      return 0;
    }

    double y = 0;
    int n = x.length;
    for (int i = 0; i < n; i++) {
      y = y + x[i] / n;
    }

    return y;
  }

  /**
   * Arithmetic mean for large quantities, see @link
   * {@link ArithmeticMean#arithmeticMeanLarge(double[])}.
   * 
   * @param <N>
   *          the type of number
   * @param array
   *          the input array
   * @return the arithmetic mean
   */
  public static <N extends Number> double arithmeticMeanLarge(N[] array) {
    if (array.length == 0) {
      return 0;
    }

    double y = 0;
    int n = array.length;
    for (int i = 0; i < n; i++) {
      y = y + array[i].doubleValue() / n;
    }

    return y;
  }

  /**
   * Computes the arithmetic mean of the values in the given array. This method
   * is meant for very large arrays only! If the sum of the int values in the
   * array is expected not to exceed the range of {@code double} the method
   * {@link #arithmeticMean(int[])} should be used instead!
   * <p>
   * This results of this method are probably not accurate.
   * 
   * @param x
   *          array of integer values
   * @return the arithmetic mean of the array values
   */
  public static double arithmeticMeanLarge(int[] x) {
    if (x.length == 0) {
      return 0;
    }

    double y = 0;
    double n = x.length;
    for (int i = 0; i < n; i++) {
      y = y + x[i] / n;
    }

    return y;
  }

  /**
   * Compute/Update the mean. This method is considered to be numerically stable
   * (see e.g., Sandmann 2009 (doi:10:1016/j.mbs.2009.06.006)). It updates an
   * already existing mean by integrating the new value passed).
   * 
   * @param oldSamplesMean
   *          contains the old mean of step n-1
   * @param sample
   *          contains the current value to be integrated into the variance
   * @param n
   *          the current n
   * @return the updated mean
   */
  public static double arithmeticMean(Double oldSamplesMean, Double sample,
      int n) {

    return (sample + ((n - 1) * oldSamplesMean)) / (double) n;
  }

}
