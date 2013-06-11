/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.plot;

import java.awt.Color;

/**
 * @author Stefan Rybacki
 * 
 */
public interface IColoredPlot extends IPlot {

  /**
   * Sets the color.
   * 
   * @param color
   *          the new color
   */
  void setColor(Color color);

  /**
   * Gets the color.
   * 
   * @return the color
   */
  Color getColor();
}
