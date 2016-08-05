/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.univariate;

import java.util.Collection;

/**
 * @author Arne Bittig
 * @date 29.11.2013
 */
public final class HarmonicMean {

  private HarmonicMean() {
  }

  /**
   * Harmonic mean of given numbers, or 0 if any number is 0. No check for
   * negative numbers is performed. (The harmonic mean is only defined for
   * positive numbers, but approaches 0 if one of the numbers approaches 0.)
   *
   * @param numbers
   *          Some numbers
   * @return Harmonic mean of these numbers
   */
  public static double harmonicMean(Collection<? extends Number> numbers) {
    double recSum = sumOfReciprocals(numbers);
    return 1. / recSum * numbers.size();
  }

  /**
   * Sum of the multiplicative inverses of given numbers. Returns positive
   * infinity if any number is 0.
   *
   * @param numbers
   *          Some numbers
   * @return Sum of the reciprocals of these numbers
   */
  public static double sumOfReciprocals(Iterable<? extends Number> numbers) {
    double recSum = 0.;
    for (Number n : numbers) {
      double value = n.doubleValue();
      if (value == 0) {
        return Double.POSITIVE_INFINITY;
      }
      recSum += 1. / value;
    }
    return recSum;
  }

  /**
   * Sum of the multiplicative inverses of given numbers. Returns positive
   * infinity if any number is 0.
   *
   * @param numbers
   *          Some numbers
   * @return Sum of the reciprocals of these numbers
   */
  public static double sumOfReciprocals(double... numbers) {
    double recSum = 0.;
    for (double n : numbers) {
      if (n == 0) {
        return Double.POSITIVE_INFINITY;
      }
      recSum += 1. / n;
    }
    return recSum;
  }

}
