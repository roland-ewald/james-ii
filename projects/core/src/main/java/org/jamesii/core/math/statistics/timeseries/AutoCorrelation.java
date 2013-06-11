/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.timeseries;

import java.util.List;

/**
 * Class to calculate the autocorrelation function for a given time-series
 * 
 * @author Stefan Leye
 * 
 */
public final class AutoCorrelation {

  /**
   * Hidden constructor.
   */
  private AutoCorrelation() {
  }

  /**
   * Calculates the autocorrelation for a given time-series and a given lag.
   * 
   * @param timeSeries
   *          the time-series
   * @param lag
   *          the distance between the compared values
   * @return the autocorrelation
   */
  public static double autoCorrelation(List<? extends Number> timeSeries,
      int lag) {
    return AutoCovariance.autoCovariance(timeSeries, lag)
        / AutoCovariance.autoCovariance(timeSeries, 0);
  }

}
