/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.plot;

import java.awt.Graphics2D;

import org.jamesii.gui.visualization.chart.BasicChart;
import org.jamesii.gui.visualization.chart.model.ISeries;

/**
 * This interface is used to provide access to different plotting styles within
 * charts. For instance a plot could be used to draw a line throw data points or
 * to draw bars.
 * <p>
 * <b><font color="red">NOTE: This class is very likely to change in future
 * releases so use with care.</font></b>
 * 
 * @author Stefan Rybacki
 */
public interface IPlot {

  /**
   * Implement this method to provide a plot (lines, bars, etc. ) drawing
   * abilities
   * 
   * @param g
   *          graphics context to draw to
   * @param x
   *          the upper left corner of this component
   * @param y
   *          the upper left corner of this component
   * @param width
   *          the width of this component
   * @param height
   *          the height of this component
   * @param chart
   *          the chart the plot this drawing for
   * @param series
   *          the series to draw for
   */
  void drawPlot(BasicChart chart, ISeries series, Graphics2D g, int x, int y,
      int width, int height);

  /**
   * Implement this method to provide drawing of this plot in the specified
   * region to for legend purposes.
   * 
   * @param g
   *          the graphics context to draw on
   * @param x
   *          the upper left corner of the area to draw legend plot in
   * @param y
   *          the upper left corner of the area to draw legend plot in
   * @param width
   *          the width of the area to draw legend plot in
   * @param height
   *          the height of the area to draw legend plot in
   */
  void drawPlotInLegend(Graphics2D g, int x, int y, int width, int height);
}
