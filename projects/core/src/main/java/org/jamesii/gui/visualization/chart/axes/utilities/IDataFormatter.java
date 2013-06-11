/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.axes.utilities;

import org.jamesii.gui.visualization.chart.axes.IAxis;
import org.jamesii.gui.visualization.chart.axes.IAxisRenderer;

/**
 * Simple formatter interface that if implemented provides a custom way of
 * representing for instance a label of an {@link IAxis} used in an
 * {@link IAxisRenderer}.
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
