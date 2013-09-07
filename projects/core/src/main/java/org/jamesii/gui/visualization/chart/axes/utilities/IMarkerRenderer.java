/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.axes.utilities;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Simple marker renderer interface that provides custom representations of
 * markers in a chart plot (
 * {@link org.jamesii.gui.visualization.chart.plot.IPlot}). So it might be
 * possible to have e.g. square or circle markers.
 * <p>
 * <b><font color="red">NOTE: This class is very likely to change in future
 * releases so use with care.</font></b>
 * 
 * @author Stefan Rybacki
 */
public interface IMarkerRenderer {
  /**
   * draws the marker at the given position using the given width, height and
   * color
   * 
   * @param g
   *          the graphics context to draw to
   * @param x
   *          the x position to draw to
   * @param y
   *          the y position to draw to
   * @param width
   *          the width of the marker to draw
   * @param height
   *          the height of the marker to draw
   * @param color
   *          the color to use for drawing
   */
  void draw(Graphics2D g, int x, int y, int width, int height, Color color);

}
