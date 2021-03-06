/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.univariate.bootstrapping.confintervals;

import java.util.List;

import org.jamesii.core.util.misc.Pair;

/**
 * 
 * @author Stefan Leye
 */
public final class BasicBootstrap {

  /**
   * Hidden constructor.
   */
  private BasicBootstrap() {
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
      List<Pair<Double, Double>> distribution, double originalMean) {
    int size = distribution.size();
    int index = (int) ((1 - p) * size);
    Double quantil = distribution.get(index).getFirstValue().doubleValue();
    return 2 * originalMean - quantil;
  }
}
