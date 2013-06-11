/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.univariate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The Class Frequencies. Class for getting frequencies, used values, ...
 * Created on 25.02.2005
 * 
 * @author Jan Himmelspach
 */
public final class Frequencies {

  /**
   * Hidden constructor.
   */
  private Frequencies() {
  }

  /**
   * Find out which double values are used in the given array and return them as
   * new double array.
   * 
   * @param x
   *          the x
   * 
   * @return the values
   */
  public static double[] getValues(double[] x) {
    Map<Double, Object> vals = new HashMap<>();

    for (int i = 0; i < x.length; i++) {
      vals.put(x[i], null);
    }

    double[] result = new double[vals.size()];

    Iterator<Double> it = vals.keySet().iterator();
    int i = 0;
    while (it.hasNext()) {
      result[i++] = it.next();
    }
    return result;
  }

  /**
   * Find out which integer values are used in the given array and return them
   * as new integer array.
   * 
   * @param x
   *          array of integer values
   * 
   * @return the values
   */
  public static int[] getValues(int[] x) {
    Map<Integer, Object> vals = new HashMap<>();

    for (int i = 0; i < x.length; i++) {
      vals.put(x[i], null);
    }

    int[] result = new int[vals.size()];

    Iterator<Integer> it = vals.keySet().iterator();
    int i = 0;
    while (it.hasNext()) {
      result[i++] = it.next();
    }
    return result;
  }

  /**
   * Find out which double values are used in the given array how often and
   * return this in the given hasmap
   * 
   * @param x
   *          array of double values
   * @param valFreq
   *          (empty!) non null hash map
   */
  public static void getValuesAndFrequencies(double[] x,
      Map<Double, Integer> valFreq) {

    for (int i = 0; i < x.length; i++) {
      if (valFreq.containsKey(x[i])) {
        int j = valFreq.get(x[i]) + 1;
        valFreq.put(x[i], j);
      } else {
        valFreq.put(x[i], 1);
      }
    }

  }

  /**
   * Find out which integer values are used in the given array how often and
   * return this in the given hasmap
   * 
   * @param x
   *          array of integer values
   * @param valFreq
   *          (empty!) non null hash map
   */
  public static void getValuesAndFrequencies(int[] x,
      Map<Integer, Integer> valFreq) {

    for (int i = 0; i < x.length; i++) {
      if (valFreq.containsKey(x[i])) {
        int j = valFreq.get(x[i]) + 1;
        valFreq.put(x[i], j);
      } else {
        valFreq.put(x[i], 1);
      }
    }

  }

}
