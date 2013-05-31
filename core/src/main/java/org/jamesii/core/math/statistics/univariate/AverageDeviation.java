/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.univariate;

/**
 * The Class AverageDeviation.
 * 
 * @author Jan Pommerenke
 */
public final class AverageDeviation {

  /**
   * Hidden constructor.
   */
  private AverageDeviation() {
  }

  /**
   * This method calculated the Average Deviation of the incomming int-array .
   * 
   * @param x
   *          is array of double Values (x[i])
   * @return aDev the averageDeviation of this array as double
   */
  public static double averageDeviationDouble(double[] x) {
    double aDev = 0;
    int n = x.length;
    double z = 0;
    double aM = ArithmeticMean.arithmeticMean(x);

    for (int i = 0; i < n; i++) {
      z = z + Math.abs(x[i] - aM);
    }
    System.out.println(z);
    aDev = z / n;
    return aDev;
  }

  /**
   * This method calculated the Average Deviation of the incomming int-array .
   * 
   * @param x
   *          is array of Int Values (x[i])
   * 
   * @return aDev the averageDeviation of this array as float
   */
  public static double averageDeviationInt(int[] x) {
    double aDev = 0;
    int n = x.length;
    double z = 0;
    double aM = ArithmeticMean.arithmeticMean(x);
    for (int i = 0; i < n; i++) {
      z = z + Math.abs(x[i] - aM);

    }
    aDev = z / n;
    return aDev;
  }
}
