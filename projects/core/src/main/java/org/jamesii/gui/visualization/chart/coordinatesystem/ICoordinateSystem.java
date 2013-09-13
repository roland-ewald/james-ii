/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.coordinatesystem;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;

import org.jamesii.gui.visualization.chart.axes.IAxis;
import org.jamesii.gui.visualization.chart.model.IChartModel;
import org.jamesii.gui.visualization.chart.model.ISeries;

/**
 * Interface that describes a coordinate system that is used in the chart
 * component. Where it will be combined with
 * {@link org.jamesii.gui.visualization.chart.plot.IPlot} which uses the
 * coordinate system to determine item positions.
 * <p>
 * <b><font color="red">NOTE: This class is very likely to change in future
 * releases so use with care.</font></b>
 * 
 * @author Stefan Rybacki
 * @see AbstractCoordinateSystem
 */
public interface ICoordinateSystem {
  /**
   * @return number of axes used by the coordinate system to map the set
   *         {@link IChartModel}.
   */
  int getAxesCount();

  /**
   * @return the number of supported dimensions
   */
  int getSupportedDimensions();

  /**
   * Describes the dimension of the coordinate system. E.g. dimension 0 could be
   * described as x-axis where dimension 1 can be described as y-axis
   * 
   * @param dimension
   *          the dimension in question
   * @return a short description of how that dimension is mapped into the
   *         coordinate system
   */
  String getDimensionName(int dimension);

  /**
   * Maps a given axis to a specific dimension of the coordinate system. This
   * means you can say a specific group of the set {@link IChartModel} is mapped
   * to one of the available dimensions of the coordinate system.
   * 
   * @param axisIndex
   *          the axis index
   * @param dimension
   *          the dimension to map to
   */
  void setAxisForDimension(int axisIndex, int dimension);

  /**
   * Returns the group that is given by {@link IChartModel#getGroup(int, int)}
   * for a specific axis. This makes it possible to link an axis to a group
   * within the coordinate system. And additionally an axis is linked to a
   * dimension within the coordinate system using
   * {@link #setAxisForDimension(int, int)}.
   * 
   * @param axisIndex
   *          the axis index
   * @return the group the axis represents
   * 
   * @see #setAxisForDimension(int, int)
   */
  int getGroupForAxis(int axisIndex);

  /**
   * @param index
   *          the index of the axis to return
   * @return the axis for given index
   */
  IAxis getAxis(int index);

  /**
   * Sets the specified axis.
   * 
   * @param index
   *          the index of the axis to set
   * @param axis
   *          the axis to set
   */
  void setAxis(int index, IAxis axis);

  /**
   * Used to determine the plot coordinates of a given data point specified by
   * the series the data point is in and the position within the series. The
   * method gets all the dimensional needed data from the {@link IChartModel}.
   * Where 0,0 is the lower left and 1,1 the upper right corner of the plotting
   * area.
   * 
   * @param series
   *          the series the data point is in
   * @param valueIndex
   *          the index of the data point within the series
   * @return a point in view space
   */
  Point2D modelToView(ISeries series, int valueIndex);

  /**
   * 
   * @param g
   *          Graphic context
   * @param x
   *          x-coordinate of the start point of the drawing area
   * @param y
   *          y-coordinate of the start point of the drawing area
   * @param width
   *          width of the drawing area
   * @param height
   *          width of the drawing area
   */
  void drawCoordinateSystem(Graphics2D g, int x, int y, int width, int height);

  /**
   * @param axisIndex
   *          the axis in question
   * @return the dimension the given axis is used for
   */
  int getDimensionForAxis(int axisIndex);

  /**
   * @return the {@link IChartModel} assigned to this coordinate system
   */
  IChartModel getModel();

  /**
   * Returns the upper left corner in screen space where the plot region starts
   * according to coordinate system infos and the infos supplied to the
   * coordinate systems
   * {@link #drawCoordinateSystem(Graphics2D, int, int, int, int)} method.
   * 
   * @param g
   *          the graphics context
   * @param x
   *          the x value of the upper left corner of the coordinate systems
   *          area
   * @param y
   *          the y value of the upper left corner of the coordinate systems
   *          area
   * @param width
   *          the coordinate system area width
   * @param height
   *          the coordinate system area width
   * @return a point that marks the upper left corner of the plot area
   */
  Point getPlotOrigin(Graphics2D g, int x, int y, int width, int height);

  /**
   * Returns the size of the plot area in screen space where the plot region is
   * defined according to the coordinate system infos supplied to the
   * {@link #drawCoordinateSystem(Graphics2D, int, int, int, int)} method.
   * 
   * @param g
   *          the graphics context
   * @param x
   *          the x value of the upper left corner of the coordinate systems
   *          area
   * @param y
   *          the y value of the upper left corner of the coordinate systems
   *          area
   * @param width
   *          the coordinate system area width
   * @param height
   *          the coordinate system area width
   * @return the size of the plot area
   */
  Dimension getPlotDimension(Graphics2D g, int x, int y, int width, int height);

  /**
   * Returns the axis that is associated with the specified group within the
   * implementing coordinate system.
   * 
   * @param group
   *          the group the axis is requested for
   * @return the axis associated with the specified group if any, null else
   */
  int getAxisForGroup(int group);

  /**
   * Returns the minimum drawing size of the coordinate system including axes
   * and a specific plotting area size.
   * 
   * @param graphics
   *          the graphics context to draw on
   * @return the minimum size
   */
  Dimension getMinimumSize(Graphics2D graphics);

  /**
   * Sets the model to use.
   * 
   * @param model
   *          the model to use
   */
  void setModel(IChartModel model);

}
