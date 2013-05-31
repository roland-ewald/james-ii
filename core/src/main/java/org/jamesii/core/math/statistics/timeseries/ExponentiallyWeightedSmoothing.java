/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.timeseries;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to smooth a time series using a running mean with exponential weights.
 * 
 * @author Stefan Leye
 * 
 */
public final class ExponentiallyWeightedSmoothing {

  /**
   * Hidden constructor.
   */
  private ExponentiallyWeightedSmoothing() {
  }

  public static List<Double> smoothing(List<? extends Number> timeSeries,
      Double smoothingFactor) {
    List<Double> result = new ArrayList<>();
    if (!timeSeries.isEmpty()) {
      double last = timeSeries.get(0).doubleValue();
      for (int i = 1; i < timeSeries.size(); i++) {
        double newValue = timeSeries.get(i).doubleValue();
        last = newValue * smoothingFactor + last * (1 - smoothingFactor);
        result.add(last);
      }
    }
    return result;
  }
}
