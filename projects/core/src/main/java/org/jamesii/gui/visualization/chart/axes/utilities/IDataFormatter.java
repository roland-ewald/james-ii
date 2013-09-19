/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.axes.utilities;

/**
 * Simple formatter interface that if implemented provides a custom way of
 * representing for instance a label of an
 * {@link org.jamesii.gui.visualization.chart.axes.IAxis} used in an
 * {@link org.jamesii.gui.visualization.chart.axes.IAxisRenderer}.
 * <p>
 * <b><font color="red">NOTE: This class is very likely to change in future
 * releases so use with care.</font></b>
 * 
 * @author Enrico Seib
 */
public interface IDataFormatter {
  /**
   * Converts a given value into a human readable {@link String}.
   * 
   * @param value
   *          the value to convert
   * @return a human readable {@link String}
   */
  String valueToString(Number value);
}
