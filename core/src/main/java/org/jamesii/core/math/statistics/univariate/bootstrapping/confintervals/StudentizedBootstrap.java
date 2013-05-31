/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.univariate.bootstrapping.confintervals;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.util.misc.Pair;

public final class StudentizedBootstrap {

  /**
   * Hidden constructor.
   */
  private StudentizedBootstrap() {
  }

  /**
   * Calculates the p-1 quantil of an empirical distribution.
   * 
   * @param probability
   *          the probability p
   * @param distribution
   *          the empirical distribution
   * @return the p-1 quantil
   */
  public static double quantil(Double p,
      List<Pair<Double, Double>> distribution, double originalMean,
      double originalVariance) {
    int size = distribution.size();
    List<Double> studentizedDistribution = new ArrayList<>();
    for (int j = 0; j < size; j++) {
      double mean = distribution.get(j).getFirstValue();
      double std = Math.sqrt(distribution.get(j).getSecondValue());
      double t = (mean - originalMean) / std;
      studentizedDistribution.add(t);
    }
    int index = (int) ((1 - p) * size);
    double std = Math.sqrt(originalVariance);
    double quantil = studentizedDistribution.get(index);
    return originalMean - std * quantil;
  }
}
