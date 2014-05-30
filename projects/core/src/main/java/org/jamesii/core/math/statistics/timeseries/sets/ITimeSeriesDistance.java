/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.timeseries.sets;

import java.util.List;

/**
 * Represents a function to compare two time series with each other.
 * 
 * @param <D>
 *          the type of the time series elements
 * @author Stefan Leye
 * @author Roland Ewald
 */
public interface ITimeSeriesDistance<D> {

  /**
   * The method to compare both time series.
   * 
   * @param timeSeries1
   *          the first time series
   * @param timeSeries2
   *          the second time series
   * @return the distance
   */
  Double compare(List<D> timeSeries1, List<D> timeSeries2);

}
