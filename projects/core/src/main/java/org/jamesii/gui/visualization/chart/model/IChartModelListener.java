/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.model;

/**
 * Listener interface that can react on changes to an {@link IChartModel}.
 * <p>
 * <b><font color="red">NOTE: This class is very likely to change in future
 * releases so use with care.</font></b>
 * 
 * @author Stefan Rybacki
 */
public interface IChartModelListener {
  /**
   * notifies the listener that a new series was introduced at given index to
   * the model
   * 
   * @param series
   *          the new series
   */
  void seriesAdded(ISeries series);

  /**
   * notifies the listener that a series was removed from the model
   * 
   * @param series
   *          the removed series
   */
  void seriesRemoved(ISeries series);

  /**
   * notifies the listener that a value for a given series has been added
   * 
   * @param series
   *          the series
   * @param valueIndex
   *          the new value index
   */
  void valueAdded(ISeries series, long valueIndex);

  /**
   * notifies the listener that a value for a given series has been removed from
   * the model
   * 
   * @param series
   *          the series
   * @param valueIndex
   *          the index of the removed value
   */
  void valueRemoved(ISeries series, long valueIndex);

  /**
   * notifies the listener that most or all data and series have changed in the
   * model
   */
  void dataChanged();

  /**
   * Notifies the listener that the group affiliation for a dimension of a
   * specific series changed.
   * 
   * @param seriesIndex
   *          the series index
   * @param dimension
   *          the dimension
   */
  void groupChanged(int seriesIndex, int dimension);

}
