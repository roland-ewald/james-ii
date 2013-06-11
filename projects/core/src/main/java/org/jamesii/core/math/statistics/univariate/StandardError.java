/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.univariate;

import java.util.List;

/**
 * The class calculates the standard error for a list of double values.
 * 
 * @author Stefan Leye
 * 
 */
public final class StandardError {

  /**
   * Hidden constructor.
   */
  private StandardError() {
  }

  /**
   * This method computes the standard error of the values in the given list.
   * 
   * @param x
   *          is a list of double values (x.get(i))
   * @return the standard error of the values of the given ArrayList
   */
  public static double standardError(List<Double> x) {
    return StandardDeviation.standardDeviation(x) / Math.sqrt(x.size());
  }

  /**
   * This method computes the standard error of the values in the given array.
   * 
   * @param x
   *          is array of double values (x[i])
   * @return the standard error of the values of the given array
   */
  public static double standardError(double[] x) {
    return StandardDeviation.standardDeviation(x) / Math.sqrt(x.length);
  }

  /**
   * This method computes the standard error of the values in the given array.
   * 
   * @param x
   *          is array of integer values (x[i])
   * @return the standard error of the values of the given array
   */
  public static double standardError(int[] x) {
    return StandardDeviation.standardDeviation(x) / Math.sqrt(x.length);
  }
}
