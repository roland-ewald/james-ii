/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.axes;

import java.awt.Graphics2D;
import java.util.List;

/**
 * Simple renderer interface for rendering {@link IAxis}s in a chart and to
 * provide custom rendering of those.
 * <p>
 * <b><font color="red">NOTE: This class is very likely to change in future
 * releases so use with care.</font></b>
 * 
 * @author Stefan Rybacki
 */
public interface IAxisRenderer {
  /**
   * draws the axis component
   * <p>
   * Note: this method signature is very very likely to change
   * 
   * @param g
   *          the graphics context to draw to
   * @param axis
   *          the axis to be drawn
   * @param x
   *          the origins x position (where the origin lies at the axis'
   *          {@link IAxis#getMinimum()} position)
   * @param y
   *          the origins y position
   * @param width
   * @param height
   * @param horizontal
   * @param leftLabels
   */
  void drawAxis(Graphics2D g, IAxis axis, int x, int y, int width, int height,
      boolean horizontal, boolean leftLabels);

  /**
   * Gets the width the renderer would need to render the provided {@link IAxis}
   * in the given graphics context including labels. This method is particular
   * important for vertical axes and only called for those.
   * <p>
   * Note: this method signature is very very likely to change
   * 
   * @param g
   *          the graphics context
   * @param axis
   *          the axis the width is requested for
   * @param labelsLeft
   *          indicated whether the labels should be placed left
   * @return the width needed for rendering the given vertical axis
   */
  int getWidth(Graphics2D g, IAxis axis, boolean labelsLeft);

  /**
   * Gets the height the renderer would need to render the provided
   * {@link IAxis} in the given graphics context including labels. This method
   * is particular important for horizontal axes and only called for those.
   * <p>
   * Note: this method signature is very very likely to change
   * 
   * @param g
   *          the graphics context
   * @param axis
   *          the axis the height is requested for
   * @param labelsTopside
   *          indicated whether the labels should be placed topside
   * @return the height needed for rendering the given vertical axis
   */
  int getHeight(Graphics2D g, IAxis axis, boolean labelsTopside);

  /**
   * Gets the positions of ticks a renderer would use in
   * {@link #drawAxis(Graphics2D, IAxis, int, int, int, int, boolean, boolean)}
   * if any.
   * <p>
   * Note: this method signature is very very likely to change
   * 
   * @param g
   *          the graphics context to draw to
   * @param axis
   *          the axis to be drawn
   * @param x
   *          the origins x position (where the origin lies at the axis'
   *          {@link IAxis#getMinimum()} position)
   * @param y
   *          the origins y position
   * @param width
   * @param height
   * @param horizontal
   * @param leftLabels
   */
  List<Double> getTicksForAxis(Graphics2D g, IAxis axis, int x, int y,
      int width,
      int height, boolean horizontal, boolean leftLabels);

}
