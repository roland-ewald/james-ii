/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.utilities;

import java.awt.Dimension;
import java.awt.Graphics2D;

import org.jamesii.gui.visualization.chart.plot.IPlot;

/**
 * This interface defines additional elements that can be plugged into any chart
 * {@link IPlot} to provide additional information such as a legend or a chart
 * title.
 * <p>
 * <b><font color="red">NOTE: This class is very likely to change in future
 * releases so use with care.</font></b>
 * 
 * @author Stefan Rybacki
 */
public interface IAdditional {
  /**
   * Returns the size this additional would like to use, and can be used for
   * layouting purposes
   * 
   * @return the size the additional would prefer
   */
  Dimension getPreferredSize();

  /**
   * Draws the additional to the given graphics context at the given position.
   * with the given size Note: the size might differ from what
   * {@link #getPreferredSize()} might return.
   * 
   * @param g
   *          the graphics context to draw to
   * @param x
   *          the position to draw at (upper left corner)
   * @param y
   *          the position to draw at (upper left corner)
   * @param width
   *          the width available for drawing
   * @param height
   *          the height available for drawing
   */
  void drawAdditional(Graphics2D g, int x, int y, int width, int height);
}
