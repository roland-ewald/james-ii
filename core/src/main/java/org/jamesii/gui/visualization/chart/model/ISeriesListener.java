/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.model;

/**
 * Listener interface that supports notification of added or removed values to a
 * specific {@link ISeries}.
 * <p>
 * <b><font color="red">NOTE: This class is very likely to change in future
 * releases so use with care.</font></b>
 * 
 * @author Stefan Rybacki
 */
public interface ISeriesListener {
  /**
   * Invoked if a value was added to the specified series.
   * 
   * @param series
   *          the series where a value was added to
   * @param index
   *          the index where the value was added
   */
  void valueAdded(ISeries series, int index);

  /**
   * Invoked if a value was removed from a specific series.
   * 
   * @param series
   *          the series where a value was removed from
   * @param index
   *          the index where the value was removed
   */
  void valueRemoved(ISeries series, int index);

  /**
   * Invoked if the name of the specific series has changed.
   * 
   * @param series
   *          the series whose name changed
   * @param oldName
   *          the old name
   */
  void nameChanged(ISeries series, String oldName);

  /**
   * Invoked if more than one value was added or removed
   * 
   * @param series
   *          the series whose data changed siginificantely
   */
  void dataChanged(ISeries series);
}
