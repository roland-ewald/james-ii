/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.univariate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * @author Arne Bittig
 * @date 29.04.2013
 */
public final class MinMedianMeanMax {

  private MinMedianMeanMax() {
  }

  /**
   * @param numbers
   *          Array of numbers (will be sorted!)
   * @return min, median, mean, max, variance, non-zero amount
   */
  public static List<Number> minMedianMeanMaxStdAmountMutating(int[] numbers) {
    Arrays.sort(numbers);
    int size = numbers.length;
    double median;
    if (size % 2 == 1) {
      median = numbers[size / 2];
    } else {
      median = 0.5 * (numbers[size / 2 - 1] + numbers[size / 2]);
    }
    long sum = 0L;
    for (int n : numbers) {
      sum += n;
    }
    double mean = (double) sum / size;
    double var = 0;
    for (int n1 : numbers) {
      double dev = -mean + n1;
      var += dev * dev;
    }
    var /= size;
    return Arrays.asList(new Number[] { numbers[0], median, mean,
        numbers[size - 1], Math.sqrt(var), size });
  }

  /**
   * @param numbers
   *          Array of numbers (will be sorted!)
   * @return min, median, mean, max, variance, amount
   */
  public static List<Number> minMedianMeanMaxStdAmountMutating(double[] numbers) {
    Arrays.sort(numbers);
    int size = numbers.length;
    double median;
    if (size % 2 == 1) {
      median = numbers[size / 2];
    } else {
      median = 0.5 * (numbers[size / 2 - 1] + numbers[size / 2]);
    }
    double sum = 0.;
    for (double n : numbers) {
      sum += n;
    }
    double mean = sum / size;
    double var = 0;
    for (double n1 : numbers) {
      double dev = -mean + n1;
      var += dev * dev;
    }
    var /= size;
    return Arrays.asList(new Number[] { numbers[0], median, mean,
        numbers[size - 1], Math.sqrt(var), size });
  }

  /**
   * @param numbers
   *          List of numbers (will be sorted!)
   * @return min, median, mean, max, variance, amount
   */
  public static <N extends Number & Comparable<? super N>> List<Double> minMedianMeanMaxStdAmountMutating(
      List<N> numbers) {
    Collections.sort(numbers);
    int size = numbers.size();
    double median;
    if (size % 2 == 1) {
      median = numbers.get(size / 2).doubleValue();
    } else {
      median =
          0.5 * (numbers.get(size / 2 - 1).doubleValue() + numbers
              .get(size / 2).doubleValue());
    }
    double sum = 0.;
    for (N n : numbers) {
      sum += n.doubleValue();
    }
    double mean = sum / size;
    double var = 0;
    for (N n1 : numbers) {
      double dev = -mean + n1.doubleValue();
      var += dev * dev;
    }
    var /= size;
    return Arrays.asList(numbers.get(0).doubleValue(), median, mean, numbers
        .get(size - 1).doubleValue(), Math.sqrt(var), (double) size);
  }

  /**
   * Minimum, median, mean, maximum, variance and amount of non-zero elements in
   * given array (all 0 list if no non-zero elements)
   *
   * @param numbers
   *          Number array
   * @return see above
   */
  public static List<Number> nonZeroMinMedianMeanMaxStdAmount(int[] numbers) {
    List<Integer> lst = new ArrayList<>(numbers.length);
    long sum = 0L;
    for (int n : numbers) {
      if (n != 0) {
        sum += n;
        lst.add(n);
      }
    }
    int size = lst.size();
    if (size == 0) {
      return Arrays.asList(new Number[] { 0, 0, 0, 0, 0, 0 });
    }
    double mean = (double) sum / size;
    double var = 0;
    for (int n1 : lst) {
      double dev = -mean + n1;
      var += dev * dev;
    }
    var /= size;

    Collections.sort(lst);
    double median;
    if (size % 2 == 1) {
      median = lst.get(size / 2);
    } else {
      median = 0.5 * (lst.get(size / 2 - 1) + lst.get(size / 2));
    }
    return Arrays.asList(new Number[] { lst.get(0), median, mean,
        lst.get(size - 1), Math.sqrt(var), size });

  }

  /**
   * Minimum, median, mean, maximum, variance and amount of elements in a
   * collection of value->number of occurrences pairs. Involves creation and
   * filling of a temporary sorted map with the content of the input for the
   * median calculation.
   *
   * @param map
   * @return minimum, median, mean, maximum, variance and amount (6-el. double
   *         array)
   */
  public static List<? extends Number> minMedianMeanMaxStdAmount(
      Map<? extends Number, Integer> map) {
    NavigableMap<Number, Integer> sortedMap = new TreeMap<>();

    double min = Double.POSITIVE_INFINITY;
    double max = Double.NEGATIVE_INFINITY;
    double sum = 0.;
    int amount = 0;
    for (Map.Entry<? extends Number, Integer> e : map.entrySet()) {
      sortedMap.put(e.getKey(), e.getValue());
      double key = e.getKey().doubleValue();
      int numOcc = e.getValue();
      amount += numOcc;
      sum += key * numOcc;
      if (min > key) {
        min = key;
      }
      if (max < key) {
        max = key;
      }
    }
    double mean = sum / amount;

    double median = Double.NaN;
    double variance = 0.;
    int numProcessed = 0;
    double medianPos = 0.5 * amount;
    boolean medianAtBoundary = false;
    for (Map.Entry<? extends Number, Integer> e : sortedMap.entrySet()) {
      double key = e.getKey().doubleValue();
      int numOcc = e.getValue();

      double dev = key - mean;
      variance += dev * dev * numOcc;

      numProcessed += numOcc;
      if (medianAtBoundary) {
        median = (median + key) / 2;
        medianAtBoundary = false;
      } else if (Double.isNaN(median) && numProcessed >= medianPos) {
        median = key;
        medianAtBoundary = numProcessed == medianPos;
      }
    }
    variance /= amount;
    return Arrays.asList(min, median, mean, max, Math.sqrt(variance), amount);
  }
}
