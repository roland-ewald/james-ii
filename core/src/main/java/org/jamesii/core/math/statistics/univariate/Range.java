/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.univariate;

import java.util.Arrays;
import java.util.List;

/**
 * The Class Range. The range is the smallest interval in which all values fall.
 * This class provides a number of different methods to compute the range. Some
 * of them require the list of values to be sorted, others sort them on their
 * own or work on the list in directly O(n) searching for min and max. Created
 * on 22.02.2005
 * 
 * @author Jan Pommerenke
 * 
 */
public final class Range {

  /**
   * Hidden constructor.
   */
  private Range() {
  }

  /**
   * Computes the range of values in the passed array.
   * 
   * @param x
   *          SORTED (descending) array of double Values
   * @return the range double Value, is the width of variation
   */
  public static double range(List<? extends Number> x) {
    return x.get(0).doubleValue() - x.get(x.size() - 1).doubleValue();
  }

  /**
   * Computes the range of values in the passed array. Searches for min and max
   * in O(1 * n), and finally computes the range.
   * 
   * @param x
   *          unsorted array of double Values
   * @return the range double Value, is the width of variation
   */
  public static double rangeF(List<? extends Number> x) {
    double min = Double.POSITIVE_INFINITY;
    double max = Double.NEGATIVE_INFINITY;
    for (int i = 0; i < x.size(); i++) {
      double val = x.get(i).doubleValue();
      if (Double.compare(min, val) > 0) {
        min = val;
      }
      if (Double.compare(max, val) < 0) {
        max = val;
      }
    }
    return max - min;
  }

  /**
   * Computes the range of values in the passed array.
   * 
   * @param x
   *          SORTED (descending) array of double Values
   * @return the range double Value, is the width of variation
   */
  public static double range(double[] x) {
    return x[0] - x[x.length - 1];
  }

  /**
   * Computes the range of values in the passed array. Sorts the array (in
   * place) in an ascending order.
   * 
   * @param x
   *          unsorted array of double Values
   * @return the range double Value, is the width of variation
   */
  public static double rangeS(double[] x) {
    Arrays.sort(x);
    return x[x.length - 1] - x[0];
  }

  /**
   * Computes the range of values in the passed array. Searches for min and max
   * in O(1 * n), and finally computes the range.
   * 
   * @param x
   *          unsorted array of double Values
   * @return the range double Value, is the width of variation
   */
  public static double rangeF(double[] x) {
    double min = Double.POSITIVE_INFINITY;
    double max = Double.NEGATIVE_INFINITY;
    for (int i = 0; i < x.length; i++) {
      double val = x[i];
      if (Double.compare(min, val) > 0) {
        min = val;
      }
      if (Double.compare(max, val) < 0) {
        max = val;
      }
    }
    return max - min;
  }

  /**
   * Computes the range of values in the passed array.
   * 
   * @param x
   *          SORTED (descending) array of float Values
   * @return the range float Value, is the width of variation
   */
  public static float range(float[] x) {
    return x[0] - x[x.length - 1];
  }

  /**
   * Computes the range of values in the passed array. Sorts the array (in
   * place) in an ascending order.
   * 
   * @param x
   *          unSORTED array of float Values
   * @return the range float Value, is the width of variation
   */
  public static float rangeS(float[] x) {
    Arrays.sort(x);
    return x[x.length - 1] - x[0];
  }

  /**
   * Computes the range of values in the passed array.
   * 
   * @param x
   *          SORTED (descending) array of int Values
   * 
   * @return the range int Value, is the width of variation
   */
  public static int range(int[] x) {
    return x[0] - x[x.length - 1];
  }

  /**
   * Computes the range of values in the passed array. Sorts the array (in
   * place) in an ascending order.
   * 
   * @param x
   *          unSORTED array of int Values
   * @return the range int Value, is the width of variation
   */
  public static int rangeS(int[] x) {
    Arrays.sort(x);
    return x[x.length - 1] - x[0];
  }

  /**
   * Computes the range of values in the passed array. Searches for min and max
   * in O(1 * n), and finally computes the range.
   * 
   * @param x
   *          (un)sorted array of integer Values
   * @return the range double Value, is the width of variation
   */
  public static int rangeF(int[] x) {
    int min = Integer.MAX_VALUE;
    int max = Integer.MIN_VALUE;
    for (int i = 0; i < x.length; i++) {
      int val = x[i];
      if (min > val) {
        min = val;
      }
      if (max < val) {
        max = val;
      }
    }
    return max - min;
  }

  /**
   * Computes the range of values in the passed array.
   * 
   * @param x
   *          SORTED (descending) array of long Values
   * 
   * @return the range double Value, is the width of variation
   */
  public static double range(long[] x) {
    return x[0] - x[x.length - 1];
  }

  /**
   * Computes the range of values in the passed array. Sorts the array (in
   * place) in an ascending order.
   * 
   * @param x
   *          unSORTED array of long Values
   * @return the range double Value, is the width of variation
   */
  public static double rangeS(long[] x) {
    Arrays.sort(x);
    return x[x.length - 1] - x[0];
  }
}
