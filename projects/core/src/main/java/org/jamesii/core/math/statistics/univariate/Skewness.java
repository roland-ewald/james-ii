/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.univariate;

import org.jamesii.core.math.Calc;

/**
 * The Class Skewness.
 * 
 * @author Jan Pommerenke
 */
public final class Skewness {

  /**
   * Hidden constructor.
   */
  private Skewness() {
  }

  /**
   * This method determines whether the distribution of the given values is
   * symmetric to the normal distribution or whether there is any skewness.
   * 
   * @param x
   *          array of integer values
   * 
   * @return skew < 0 => left skew, right steep distribution skew = 0 =>
   *         symmetric distribution skew > 0 => right skew, left steep
   *         distribution
   */
  public static double skewness(int[] x) {
    int n = x.length;
    double z = 0;
    double skew = 0;
    double aM = ArithmeticMean.arithmeticMean(x);
    for (int i = 0; i < n; i++) {
      z = z + Math.pow(x[i] - aM, Calc.CUBIC);
    }
    skew = z / n;
    return skew / Math.pow(Variance.variance(x), Calc.CUBIC);
  }

  /**
   * This method determines whether the distribution of the given values is
   * symmetric to the normal distribution or whether there is any skewness.
   * 
   * @param x
   *          array of integer values
   * 
   * @return skew < 0 => left skew, right steep distribution skew = 0 =>
   *         symmetric distribution skew > 0 => right skew, left steep
   *         distribution
   */
  public static double skewness(double[] x) {
    int n = x.length;
    double z = 0;
    double skew = 0;
    double aM = ArithmeticMean.arithmeticMean(x);
    for (int i = 0; i < n; i++) {
      z = z + Math.pow(x[i] - aM, Calc.CUBIC);
    }
    skew = z / n;
    return skew / Math.pow(Variance.variance(x), Calc.CUBIC);
  }

}
