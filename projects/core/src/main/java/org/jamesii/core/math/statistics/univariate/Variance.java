/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.univariate;

import java.util.List;

import org.jamesii.core.util.misc.Pair;

/**
 * The Class Variance. This class contains methods for computing the variance of
 * a list of values.
 * 
 * <br>
 * There are different ways how to compute the variance of a given list of
 * numbers. Here we realized several of those, so that you can choose on you own
 * which one you will use. There is a chance that some of them will produce
 * wrong results if used with some data. <br>
 * Basically there are two- and one-pass methods in here
 * 
 * created on 24.02.2005
 * 
 * @author Jan Himmelspach
 */
public final class Variance {

  /**
   * Hidden constructor.
   */
  private Variance() {
  }

  /**
   * This methods computes the variance of the values in the given list.
   * 
   * @param x
   *          list of numbers (doubleValue will be used for calculation, see
   *          {@link Number})
   * @return variance
   */
  public static double variance(List<? extends Number> x) {

    int n = x.size();
    double z = 0;
    double aM = ArithmeticMean.arithmeticMean(x);
    for (int i = 0; i < n; i++) {
      double help = x.get(i).doubleValue() - aM;
      z = z + (help * help);
    }
    return z / (n - 1);
  }

  /**
   * This methods computes the variance of the values in the given array
   * 
   * @param x
   *          array of double values (x[i])
   * @return variance
   */
  public static double variance(double[] x) {

    int n = x.length;
    double z = 0;
    double aM = ArithmeticMean.arithmeticMean(x);
    for (int i = 0; i < n; i++) {
      double help = x[i] - aM;
      z += (help * help);
    }
    return z / (n - 1);
  }

  /**
   * This methods computes the variance of the values in the given array
   * 
   * @param x
   *          array of integer values (x[i])
   * @return variance
   */
  public static double variance(int[] x) {

    int n = x.length;
    double z = 0;
    double aM = ArithmeticMean.arithmeticMean(x);
    for (int i = 0; i < n; i++) {
      double help = x[i] - aM;
      z = z + (help * help);
    }
    return z / (n - 1);
  }

  /**
   * This methods computes the variance and the arithmetic mean of the values in
   * the given array. The algorithm is numerically stable.
   * 
   * @param x
   *          array of double values (x[i])
   * @return (variance, am)
   */
  public static Pair<Double, Double> varianceAndAM(List<? extends Number> x) {

    double mean = 0;
    double m2 = 0;

    for (int i = 0; i < x.size(); i++) {
      double delta = x.get(i).doubleValue() - mean;
      mean += delta / (i + 1);
      m2 += delta * (x.get(i).doubleValue() - mean); // updated mean!
    }
    return new Pair<>(m2 / (x.size() - 1), mean);
  }

  /**
   * This methods computes the variance and the arithmetic mean of the values in
   * the given array. The algorithm is numerically stable.
   * 
   * @param x
   *          array of double values (x[i])
   * @return (variance, am)
   */
  public static Pair<Double, Double> varianceAndAM(double[] x) {

    double mean = 0;
    double m2 = 0;

    for (int i = 0; i < x.length; i++) {
      double delta = x[i] - mean;
      mean += delta / (i + 1);
      m2 += delta * (x[i] - mean); // updated mean!
    }
    return new Pair<>(m2 / (x.length - 1), mean);
  }

  /**
   * This methods computes the variance and the arithmetic mean of the values in
   * the given array. The algorithm is numerically stable.
   * 
   * @param x
   *          array of double values (x[i])
   * @return (variance, am)
   */
  public static Pair<Double, Double> varianceAndAM(Double[] x) {

    double mean = 0;
    double M2 = 0;

    for (int i = 0; i < x.length; i++) {
      double delta = x[i] - mean;
      mean += delta / (i + 1);
      M2 += delta * (x[i] - mean); // updated mean!
    }
    return new Pair<>(M2 / (x.length - 1), mean);
  }

  /**
   * This methods computes the variance and the arithmetic mean of the values in
   * the given array. The algorithm is numerically stable.
   * 
   * @param x
   *          array of double values (x[i])
   * @return (variance, am)
   */
  public static Pair<Double, Double> varianceAndAM(int[] x) {

    double mean = 0;
    double m2 = 0;

    for (int i = 0; i < x.length; i++) {
      double delta = x[i] - mean;
      mean += delta / (i + 1);
      m2 += delta * (x[i] - mean); // updated mean!
    }
    return new Pair<>(m2 / (x.length - 1), mean);
  }

  /**
   * Variance, numerical stable algorithm. A one - pass algorithm for the
   * computation of the variance.
   * 
   * @param x
   *          the List of double values
   * 
   * @return the variance of the array passed
   */
  public static double varianceNStable(List<? extends Number> x) {

    return varianceAndAM(x).getFirstValue();
  }

  /**
   * Variance, numerical stable algorithm. A one - pass algorithm for the
   * computation of the variance.
   * 
   * @param x
   *          the array of double values
   * 
   * @return the variance of the array passed
   */
  public static double varianceNStable(double[] x) {

    return varianceAndAM(x).getFirstValue();
  }

  /**
   * Variance, numerical stable algorithm. A one - pass algorithm for the
   * computation of the variance.
   * 
   * @param x
   *          the array of double values
   * 
   * @return the variance of the array passed
   */
  public static double varianceNStable(Double[] x) {

    return varianceAndAM(x).getFirstValue();
  }

  /**
   * Variance, numerical stable algorithm. A one - pass algorithm for the
   * computation of the variance.
   * 
   * @param x
   *          the array of double values
   * 
   * @return the variance of the array passed
   */
  public static double varianceNStable(int[] x) {

    return varianceAndAM(x).getFirstValue();
  }

  /**
   * Variance, numerical "unstable" algorithm. A one - pass algorithm for the
   * computation of the variance. This algorithm might return wrong results.
   * 
   * @param x
   *          theSamples array of double values
   * 
   * @return the variance of the array passed
   */
  public static double varianceUnStable(double[] x) {

    double sum = 0;
    double sumSqr = 0;

    for (int i = 0; i < x.length; i++) {
      sum += x[i];
      sumSqr += x[i] * x[i];
    }

    double mean = sum / x.length;
    return (sumSqr - x.length * mean * mean) / (x.length - 1);
  }

  /**
   * Variance, numerical "unstable" algorithm. A one - pass algorithm for the
   * computation of the variance. This algorithm might return wrong results.
   * 
   * @param x
   *          the List of double values
   * 
   * @return the variance of the array passed
   */

  public static double varianceUnStable(List<? extends Number> x) {

    double sum = 0;
    double sumSqr = 0;

    for (int i = 0; i < x.size(); i++) {
      Double value = x.get(i).doubleValue();
      sum += value;
      sumSqr += value * value;
    }

    double mean = sum / x.size();
    return (sumSqr - x.size() * mean * mean) / (x.size() - 1);
  }

  /**
   * 
   * A two-pass algorithm for the computation of the variance
   * 
   * @param x
   *          the array of double values
   * 
   * @return the variance of the array
   */

  public static double varianceTwoPass(double[] x) {
    double sum1 = 0;

    for (int i = 0; i < x.length; i++) {
      sum1 = sum1 + x[i];
    }
    double mean = sum1 / x.length;
    double sum2 = 0;
    for (int i = 0; i < x.length; i++) {
      sum2 = sum2 + ((x[i] - mean) * (x[i] - mean));
    }
    return sum2 / (x.length - 1);
  }

  /**
   * 
   * A two-pass algorithm for the computation of the variance
   * 
   * @param x
   *          the list of double values
   * 
   * @return the variance of the array
   */

  public static double varianceTwoPass(List<Double> x) {
    double sum1 = 0;

    for (int i = 0; i < x.size(); i++) {
      sum1 = sum1 + x.get(i);
    }
    double mean = sum1 / x.size();
    double sum2 = 0;
    for (int i = 0; i < x.size(); i++) {
      sum2 = sum2 + ((x.get(i) - mean) * (x.get(i) - mean));
    }
    return sum2 / (x.size() - 1);
  }

  /**
   * Compute/Update the variance. This method is considered to be numerically
   * stable (see e.g., Sandmann 2009 (doi:10:1016/j.mbs.2009.06.006)). It
   * updates an already existing variance by integrating the new value passed).
   * 
   * @param oldVariance
   *          contains the variance of step n-1
   * @param oldSamplesMean
   *          contains the old mean of step n-1
   * @param sample
   *          contains the current value to be integrated into the variance
   * @param n
   *          the current n; n has to be > 1 !
   * @return the updated variance
   */
  public static double variance(Double oldVariance, Double oldSamplesMean,
      Double sample, int n) {
    double diff = sample - oldSamplesMean;
    double var =
        (((n - 2.0) / (n - 1.0)) * oldVariance) + ((1.0 / n) * (diff * diff));
    return var;
  }

  /**
   * Updates an already existing variance by integrating the new value passed.
   * 
   * @param oldVariance
   *          contains the variance of step n-1
   * @param oldMean
   *          contains the old mean of step n-1
   * @param entry
   *          contains the current value to be integrated into the variance
   * @param n
   *          the current n
   * @return the updated variance
   */
  public static double varianceAddEntry(Double oldVariance, Double oldMean,
      Double entry, Integer n) {
    if (n == 1) {
      return 0.0;
    }
    double delta = entry - oldMean;
    double newMean = oldMean + (delta / n);
    return ((oldVariance * (n - 2)) + delta * (entry - newMean)) / (n - 1);
  }

  /**
   * Updates an already existing variance by removing an old value.
   * 
   * @param currentVariance
   *          contains the variance of step n+1
   * @param currentMean
   *          contains the old mean of step n+1
   * @param entry
   *          contains the current value to be removed from the variance
   * @param the
   *          sample size after the entry has been removed
   * @return the updated variance
   */
  public static double varianceRemoveEntry(Double currentVariance,
      Double currentMean, Double entry, Integer n) {
    int nPlusOne = n + 1;
    double delta = currentMean - entry;
    double oldMean = ((nPlusOne * currentMean) - entry) / (nPlusOne - 1);
    return ((currentVariance * (nPlusOne - 1)) + (entry - oldMean) * delta)
        / (nPlusOne - 2);
  }

}
