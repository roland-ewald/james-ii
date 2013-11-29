/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.univariate;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Static methods for computing the median.
 * 
 * Created on 28.01.2005
 * 
 * @author Jan Pommerenke
 * @version 1.0 history 18.02.2005 Jan Himmelspach Added double, float, and long
 *          medians, added header, added JAVA Doc, fixed computation of median
 */
public final class Median {

  private Median() {
  }

  /**
   * Median of a collection of numbers. A copy of the collection will be created
   * and sorted. Time complexity thus corresponds to that of the used sorting
   * method (ComparableTimSort via {@link Arrays#sort(Object[])}).
   * 
   * @author Arne Bittig
   * 
   * @param numbers
   * @return median of these numbers
   */
  public static <N extends Number & Comparable<N>> double median(
      Collection<N> numbers) {
    N[] arr = (N[]) numbers.toArray();
    Arrays.sort(arr);
    int size = arr.length;
    if (size % 2 == 0) {
      int sBy2 = size / 2;
      return (arr[sBy2 - 1].doubleValue() + arr[sBy2].doubleValue()) / 2.;
    } else {
      return arr[size / 2].doubleValue();
    }

  }

  /**
   * The median of a sorted list of values is, if number of elements is - odd:
   * list[(n+1) div 2] - even: (list[n div 2]+list[n div 2 + 1]) div 2
   * 
   * @param x
   *          SORTED arraylist of double values
   * @return the median value of the sorted arraylist
   */
  public static Double medianFromSorted(List<Double> x) {
    int n = x.size();

    if (n == 0) {
      return null;
    }

    Double median;
    if (n % 2 == 0) { // even!
      median = (x.get(n / 2 - 1) + x.get(n / 2)) / 2;
    } else { // odd!
      median = x.get((n + 1) / 2 - 1);
    }
    return median;
  }

  /**
   * The median of a sorted list of values is, if number of elements is - odd:
   * list[(n+1) div 2] - even: (list[n div 2]+list[n div 2 + 1]) div 2
   * 
   * @param x
   *          SORTED array of double values
   * @return the median value of the sorted array
   */
  public static double medianFromSorted(double[] x) {
    int n = x.length;
    double median;

    if (n % 2 == 0) { // even!
      median = (x[n / 2 - 1] + x[n / 2]) / 2;
    } else { // odd!
      median = x[(n + 1) / 2 - 1];
    }

    return median;
  }

  /**
   * The median of a sorted list of values is, if number of elements is - odd:
   * list[(n+1) div 2] - even: (list[n div 2]+list[n div 2 + 1]) div 2
   * 
   * @param x
   *          SORTED array of float values
   * @return the median value of the sorted array
   */
  public static float medianFromSorted(float[] x) {
    int n = x.length;
    float median;

    if (n % 2 == 0) { // even!
      median = (x[n / 2 - 1] + x[n / 2]) / 2;
    } else { // odd!
      median = x[(n + 1) / 2 - 1];
    }

    return median;
  }

  /**
   * The median of a sorted list of values is, if number of elements is - odd:
   * list[(n+1) div 2] - even: (list[n div 2]+list[n div 2 + 1]) div 2
   * 
   * @param x
   *          SORTED array of integer values
   * @return the median value of the sorted array
   */
  public static double medianFromSorted(int[] x) {
    int n = x.length;
    double median;
    if (n % 2 == 0) { // even!
      // array based on 0 .. n - 1, thus we need n/2 and n/2-1
      int j = (n / 2);
      int l = j - 1;

      median = ((double) x[l] + (double) x[j]) / 2;
    } else { // odd!
      median = x[(n + 1) / 2 - 1];
    }
    return median;
  }

  /**
   * The median of a sorted list of values is, if number of elements is - odd:
   * list[(n+1) div 2] - even: (list[n div 2]+list[n div 2 + 1]) div 2
   * 
   * @param x
   *          SORTED array of long values
   * @return the median value of the sorted array
   */
  public static double medianFromSorted(long[] x) {
    int n = x.length;
    double median;
    if (n % 2 == 0) { // even!
      median = (x[n / 2 - 1] + x[n / 2]) / 2.0;
    } else { // odd!
      median = x[(n + 1) / 2 - 1];
    }

    return median;
  }
}
