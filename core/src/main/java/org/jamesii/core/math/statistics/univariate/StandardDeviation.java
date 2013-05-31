/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.univariate;

import java.util.List;

/**
 * The Class StandardDeviation.
 * 
 * Created on 23.02.2005
 * 
 * @author Jan Pommerenke
 * @version 1.0 history Jan Himmelspach, 24.2.2005, fixed computation of std
 *          deviation
 */
public final class StandardDeviation {

  /**
   * Hidden constructor.
   */
  private StandardDeviation() {
  }

  /**
   * This method computes the standard deviation of the values in the given
   * list. That's done by taking the square root of the variance.
   * 
   * @param x
   *          is a list of numbers (doubleValue will be used for calculation,
   *          see {@link Number})
   * @return the standard deviation of the list values
   */
  public static double standardDeviation(List<? extends Number> x) {
    return Math.sqrt(Variance.variance(x));
  }

  /**
   * This method computes the Standard Deviation of the values in the given
   * array (That's the square root of the variance.)
   * 
   * @param x
   *          is array of double values (x[i])
   * @return the standard deviation of the values of the given array
   */
  public static double standardDeviation(double[] x) {
    return Math.sqrt(Variance.variance(x));
  }

  /**
   * This method computes the Standard Deviation of the values in the given
   * array (That's the square root of the variance.)
   * 
   * @param x
   *          is array of integer values (x[i])
   * @return the standard deviation of the values of the given array
   */
  public static double standardDeviation(int[] x) {
    return Math.sqrt(Variance.variance(x));
  }
}
