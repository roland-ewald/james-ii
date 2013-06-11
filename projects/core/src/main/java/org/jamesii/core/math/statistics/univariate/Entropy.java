/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.univariate;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class Entropy.
 * 
 * @author Jan Pommerenke
 */
@Deprecated
// This classes is untested, needs better documentation, and may be buggy
public final class Entropy {

  /**
   * Hidden constructor.
   */
  private Entropy() {
  }

  /**
   * This method have a result concentration of Variables between 0 and
   * log(based2)(n) The InputValues must between 0 and 1
   * 
   * @param xs
   *          list of double Values
   * @return the entropy Value of array
   */
  public static double entropy(List<? extends Number> xs) {
    int n = xs.size();

    checkInput(xs);

    double h = 0;
    double z = 0;
    double v = 0;

    for (int i = 0; i < n; i++) {
      Double value = xs.get(i).doubleValue();
      if (value != 0) {
        v = Math.log(value) / Math.log(2);
        z = value * v;
        h = h + z;
      }
    }
    return -h;
  }

  /**
   * Checks whether all numbers are in the interval of [0,1].
   * 
   * @param xs
   *          the list of numbers
   */
  private static void checkInput(List<? extends Number> xs) {
    for (Number x : xs) {
      if (x.doubleValue() < 0 || x.doubleValue() > 1) {
        throw new IllegalArgumentException("Input values must be in [0,1].");
      }
    }
  }

  /**
   * This method have a result concentration of Variables between 0 and
   * log(based2)(n) The InputValues will be normalized.
   * 
   * @param x
   *          list of double Values
   * @return the entropy Value of array
   */
  public static double normalizedEntropy(List<? extends Number> x) {
    Double min = Double.POSITIVE_INFINITY;
    Double max = Double.NEGATIVE_INFINITY;
    for (Number num : x) {
      Double doub = num.doubleValue();
      if (min.compareTo(doub) > 0.0) {
        min = doub;
      }
      if (max.compareTo(doub) < 0.0) {
        max = doub;
      }
    }
    Double range = max - min;

    // If all numbers are equal, there is no entropy
    if (range == 0) {
      return 0;
    }

    List<Double> normalizedList = new ArrayList<>();
    for (Number num : x) {
      Double doub = num.doubleValue();
      normalizedList.add((doub - min) / range);
    }
    return entropy(normalizedList);
  }
}
