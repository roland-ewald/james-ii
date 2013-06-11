/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.timeseries;

import java.util.List;

import org.jamesii.core.math.statistics.univariate.ArithmeticMean;

/**
 * Class to calculate the autocovariance function for a given time-series
 * 
 * @author Stefan Leye
 * 
 */
public final class AutoCovariance {

  /**
   * Hidden constructor.
   */
  private AutoCovariance() {
  }

  /**
   * Calculates the autocovariance for a given time-series and a given lag.
   * 
   * @param timeSeries
   *          the time-series
   * @param lag
   *          the distance between the compared values
   * @return the autocovariance
   */
  public static double autoCovariance(List<? extends Number> timeSeries, int lag) {
    Double mean = ArithmeticMean.arithmeticMean(timeSeries);
    Double covariance = 0.0;
    for (int i = 0; i < timeSeries.size() - lag; i++) {
      covariance +=
          ((timeSeries.get(i).doubleValue() - mean)
              * (timeSeries.get(i + lag).doubleValue() - mean) / timeSeries
              .size());
    }
    return covariance;
  }

}
