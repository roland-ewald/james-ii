/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.model;

/**
 * The Interface ISeries is used to represent a series of datapoints that can be
 * used in a {@link BasicXYChartModel}.
 * <p>
 * <b><font color="red">NOTE: This class is very likely to change in future
 * releases so use with care.</font></b>
 * 
 * @author Stefan Rybacki
 */
public interface ISeries {

  /**
   * Gets the dimensions supported in this series.
   * 
   * @return the dimensions of this series
   */
  int getDimensions();

  /**
   * Gets the value for a given dimension at a given index.
   * 
   * @param dimension
   *          the dimension of the value
   * @param index
   *          the index of the value
   * @return the value in dimension at the given index
   */
  Number getValue(int dimension, int index);

  /**
   * Gets the number of values in this series.
   * 
   * @return the number of values
   */
  int getValueCount();

  /**
   * Adds a new listener of series events to this series.
   * 
   * @param listener
   *          the listener to attach
   */
  void addSeriesListener(ISeriesListener listener);

  /**
   * Removes a previously registered series listener.
   * 
   * @param listener
   *          the listener to remove
   */
  void removeSeriesListener(ISeriesListener listener);

  /**
   * Gets the min value within this series for the given dimension. Return
   * {@link Double#POSITIVE_INFINITY} if undefined.
   * 
   * @param dimension
   *          the dimension
   * @return the min value
   */
  Number getMinValue(int dimension);

  /**
   * Gets the max value within this series for the given dimension. Return
   * {@link Double#NEGATIVE_INFINITY} if undefined.
   * 
   * @param dimension
   *          the dimension
   * @return the max value
   */
  Number getMaxValue(int dimension);

  /**
   * Gets the name of the series.
   * 
   * @return the name of the series, e.g. Temperature
   */
  String getName();
}
