/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.multivariate;

import java.util.List;

import org.jamesii.core.math.statistics.univariate.Variance;

/**
 * This class provides methods for calculating the covariance of two random
 * samples.
 * 
 * @author Stefan Leye
 */
public final class Covariance {

  /**
   * Hidden constructor.
   */
  private Covariance() {
  }

  private static void checkExamples(int sample1, int sample2) {
    if (sample1 != sample2) {
      throw new InvalidSampleSizeException(
          "Samples for calculating the covariance have to be equal in size!");
    }
  }

  /**
   * This methods computes the covariance of the values in the two given arrays.
   * 
   * @param sample1
   *          array of double values
   * @param sample2
   *          array of double values
   * @return covariance
   */
  public static double covariance(double[] sample1, double[] sample2) {
    checkExamples(sample1.length, sample2.length);
    int n = sample1.length;
    double mean1 = Variance.varianceAndAM(sample1).getSecondValue();
    double mean2 = Variance.varianceAndAM(sample2).getSecondValue();
    double result = 0.0;
    for (int i = 0; i < n; i++) {
      result += (sample1[i] - mean1) * (sample2[i] - mean2);
    }
    return result / (n - 1);
  }

  /**
   * This methods computes the covariance of the values in the two given arrays.
   * 
   * @param sample1
   *          array of int values
   * @param sample2
   *          array of int values
   * @return covariance
   */
  public static double covariance(int[] sample1, int[] sample2) {

    checkExamples(sample1.length, sample2.length);
    int n = sample1.length;
    double mean1 = Variance.varianceAndAM(sample1).getSecondValue();
    double mean2 = Variance.varianceAndAM(sample2).getSecondValue();
    double result = 0.0;
    for (int i = 0; i < n; i++) {
      result += (sample1[i] - mean1) * (sample2[i] - mean2);
    }
    return result / (n - 1);
  }

  /**
   * This methods computes the covariance of the values in the two given arrays.
   * 
   * @param sample1
   *          list of values
   * @param sample2
   *          list of values
   * @return covariance
   */
  public static double covariance(List<? extends Number> sample1,
      List<? extends Number> sample2) {

    checkExamples(sample1.size(), sample2.size());
    int n = sample1.size();
    double mean1 = Variance.varianceAndAM(sample1).getSecondValue();
    double mean2 = Variance.varianceAndAM(sample2).getSecondValue();
    double result = 0.0;
    for (int i = 0; i < n; i++) {
      result +=
          (sample1.get(i).doubleValue() - mean1)
              * (sample2.get(i).doubleValue() - mean2);
    }
    return result / (n - 1);
  }

}
