/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.coordinatesystem;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.List;

import org.jamesii.gui.visualization.chart.axes.DefaultAxisRenderer;
import org.jamesii.gui.visualization.chart.axes.IAxis;
import org.jamesii.gui.visualization.chart.axes.IAxisRenderer;
import org.jamesii.gui.visualization.chart.model.IChartModel;
import org.jamesii.gui.visualization.chart.model.ISeries;

/**
 * Very basic XY coordinate system that provides one X-Axis at dimension 0 and
 * an arbitrary number of Y-Axes distributed equally to left and right.
 * <p>
 * No renderers or special formatters are use so far.
 * <p>
 * <b><font color="red">NOTE: This class is very likely to change in future
 * releases so use with care.</font></b>
 * 
 * @author Stefan Rybacki
 */
public class CoordinateSystemXY extends AbstractCoordinateSystem {
  /**
   * The margin left and right of coordinate system.
   */
  private int marginX = 10;

  /**
   * The renderer. TODO sr137: don't use not changeable renderer
   */
  private IAxisRenderer renderer = new DefaultAxisRenderer();

  /**
   * Instantiates a new coordinate system xy.
   * 
   * @param model
   *          the chart model to use
   */
  public CoordinateSystemXY(IChartModel model) {
    super(model);
  }

  @Override
  public Dimension getMinimumSize(Graphics2D g) {
    return new Dimension(getWidthLeftAxes(g) + getWidthRightAxes(g) + 150,
        150 + (getAxesCount() > 0 ? getHeightLowerAxes(g, 0) : 0));
  }

  @Override
  public void drawCoordinateSystem(Graphics2D g, int x, int y, int width,
      int height) {
    int leftAxisPos = 0;
    int rightAxisPos = 0;

    int widthLeftAxes = getWidthLeftAxes(g);
    int widthRightAxes = getWidthRightAxes(g);

    int nHorizontalAxes = 0;
    int nVerticalAxes = 0;

    List<Double> hTicks = null;
    List<Double> vTicks = null;

    IAxis hAxis = null;
    IAxis vAxis = null;

    for (int axis = 0; axis < getAxesCount(); axis++) {
      if (getDimensionForAxis(axis) == 0) {
        nHorizontalAxes++;
      }
      if (getDimensionForAxis(axis) == 1) {
        nVerticalAxes++;
      }
    }

    for (int axis = 0; axis < getAxesCount(); axis++) {
      if (getDimensionForAxis(axis) == 0) {
        // X-AXIS
        // get the extension of left axes
        int startX = x + getWidthLeftAxes(g);

        // get the extension of right axes
        int endX = x + width - getWidthRightAxes(g);

        // draw axis from startX to endX horizontal
        int y1 = y + height - getHeightLowerAxes(g, axis);
        drawHorizontalAxis(g, axis, startX, endX, y1,
            getHeightLowerAxes(g, axis));

        if (nHorizontalAxes == 1) {
          hTicks =
              renderer.getTicksForAxis(g, getAxis(axis), startX, y, endX
                  - startX, getHeightLowerAxes(g, axis), true, false);
          hAxis = getAxis(axis);
        }
      }

      if (getDimensionForAxis(axis) == 1) {
        // Y-AXIS
        // left if axis % 2 == 0 right otherwise
        boolean leftAxis = axis % 2 != 0;
        int startY = 10; // margin
        int endY = y + height - getHeightLowerAxes(g, axis);
        if (leftAxis) {
          int w = getWidthLeftAxis(g, axis);
          leftAxisPos += w;
          int startX = x + widthLeftAxes - leftAxisPos;
          leftAxisPos += marginX;

          drawLeftAxis(g, axis, startX, w + startX, startY, endY);

          if (nVerticalAxes == 1) {
            vTicks =
                renderer.getTicksForAxis(g, getAxis(axis), startX, startY, w,
                    endY - startY, false, true);
            vAxis = getAxis(axis);
          }
        } else {
          int w = getWidthRightAxis(g, axis);
          int startX = x + width - widthRightAxes + rightAxisPos;
          rightAxisPos += w + marginX;

          drawRightAxis(g, axis, startX, w + startX, startY, endY);
        }

      }
    }

    // GradientPaint paint =
    // new GradientPaint(new Point(x, y + height), Color.LIGHT_GRAY,
    // new Point(x, y), Color.white);
    // g.setPaint(paint);

    g.setColor(Color.WHITE);
    Point plotOrigin = getPlotOrigin(g, x, y, width, height);
    Dimension plotDimension = getPlotDimension(g, x, y, width, height);
    g.fillRect(plotOrigin.x, plotOrigin.y, plotDimension.width,
        plotDimension.height);
    // g.setPaint(null);

    // draw grid
    if (hTicks != null && hAxis != null) {
      g.setColor(Color.LIGHT_GRAY);
      g.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT,
          BasicStroke.JOIN_BEVEL, 0, new float[] { 3, 3 }, 0));
      for (Double d : hTicks) {
        double v = hAxis.transform(d) * plotDimension.getWidth();
        g.drawLine((int) (plotOrigin.x + v), plotOrigin.y,
            (int) (plotOrigin.x + v), plotDimension.height + plotOrigin.y);
      }
    }

    if (vTicks != null && vAxis != null) {
      g.setColor(Color.LIGHT_GRAY);
      g.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT,
          BasicStroke.JOIN_BEVEL, 0, new float[] { 3, 3 }, 0));
      for (Double d : vTicks) {
        double v = vAxis.transform(d) * plotDimension.getHeight();
        g.drawLine(plotOrigin.x,
            (int) (plotOrigin.y + plotDimension.height - v), plotOrigin.x
                + plotDimension.width, (int) (plotOrigin.y
                + plotDimension.height - v));
      }
    }

  }

  /**
   * Draw right axis.
   * 
   * @param g
   *          the graphics context
   * @param axisIndex
   *          the axis index
   * @param startX
   *          the start x
   * @param endX
   *          the end x
   * @param startY
   *          the start y
   * @param endY
   *          the end y
   */
  private void drawRightAxis(Graphics2D g, int axisIndex, int startX, int endX,
      int startY, int endY) {
    renderer.drawAxis(g, getAxis(axisIndex), startX, startY, endX - startX,
        endY - startY, false, false);
  }

  /**
   * Draw left axis.
   * 
   * @param g
   *          the graphics context
   * @param axisIndex
   *          the axis index
   * @param startX
   *          the start x
   * @param endX
   *          the end x
   * @param startY
   *          the start y
   * @param endY
   *          the end y
   */
  private void drawLeftAxis(Graphics2D g, int axisIndex, int startX, int endX,
      int startY, int endY) {
    renderer.drawAxis(g, getAxis(axisIndex), startX, startY, endX - startX,
        endY - startY, false, true);
  }

  /**
   * Gets the width of a left axis.
   * 
   * @param g
   *          the graphics context
   * @param axisIndex
   *          the axis index
   * @return the width left axis
   */
  private int getWidthLeftAxis(Graphics2D g, int axisIndex) {
    IAxis axis = getAxis(axisIndex);
    return renderer.getWidth(g, axis, true);
  }

  /**
   * Draw horizontal axis.
   * 
   * @param g
   *          the graphics context
   * @param axisIndex
   *          the axis index
   * @param startX
   *          the start x
   * @param endX
   *          the end x
   * @param y
   *          the y
   * @param height
   *          the height
   */
  private void drawHorizontalAxis(Graphics2D g, int axisIndex, int startX,
      int endX, int y, int height) {
    renderer.drawAxis(g, getAxis(axisIndex), startX, y, endX - startX, height,
        true, false);
  }

  /**
   * Gets the height for lower axes.
   * 
   * @param g
   *          the graphics context
   * @param axisIndex
   *          the axis' index the height is requested for
   * @return the height lower axes
   */
  private int getHeightLowerAxes(Graphics2D g, int axisIndex) {
    IAxis axis = getAxis(axisIndex);
    return renderer.getHeight(g, axis, false);
  }

  // TODO sr137: optimize
  /**
   * Gets the width of all left axes together.
   * 
   * @param g
   *          the graphics context
   * @return the width left axes
   */
  private int getWidthLeftAxes(Graphics2D g) {
    int width = marginX;
    boolean leftAxisFound = false;
    for (int axis = 0; axis < getAxesCount(); axis++) {
      if (getDimensionForAxis(axis) != 0 && axis % 2 != 0) {
        width += marginX + getWidthLeftAxis(g, axis);
        leftAxisFound = true;
      }
    }
    if (leftAxisFound) {
      width -= marginX;
    }
    return width;
  }

  // TODO sr137: optimize
  /**
   * Gets the width all right axes together.
   * 
   * @param g
   *          the graphics context
   * @return the width right axes
   */
  private int getWidthRightAxes(Graphics2D g) {
    int width = marginX;
    boolean rightAxisFound = false;
    for (int axis = 0; axis < getAxesCount(); axis++) {
      if (getDimensionForAxis(axis) == 1 && axis % 2 == 0) {
        width += marginX + getWidthRightAxis(g, axis);
        rightAxisFound = true;
      }
    }
    if (rightAxisFound) {
      width -= marginX;
    }
    return width;
  }

  /**
   * Gets the width of a right axis.
   * 
   * @param g
   *          the graphics context
   * @param axisIndex
   *          the axis index
   * @return the width right axis
   */
  private int getWidthRightAxis(Graphics2D g, int axisIndex) {
    IAxis axis = getAxis(axisIndex);
    return renderer.getWidth(g, axis, false);
  }

  @Override
  public String getDimensionName(int dimension) {
    switch (dimension) {
    case 0:
      return "X-Axis";
    case 1:
      return "Y-Axis";
    }
    return null;
  }

  @Override
  public int getSupportedDimensions() {
    return 2;
  }

  @Override
  public Point2D modelToView(ISeries series, int valueIndex) {
    // get x axis for series and get y axis for series
    int xGroup = getModel().getGroup(series, 0);
    int yGroup = getModel().getGroup(series, 1);

    IAxis xAxis = getAxis(this.getAxisForGroup(xGroup));
    IAxis yAxis = getAxis(this.getAxisForGroup(yGroup));

    double x = getModel().getValue(series, 0, valueIndex).doubleValue();
    double y = getModel().getValue(series, 1, valueIndex).doubleValue();

    return new Point2D.Double(xAxis.transform(x), yAxis.transform(y));
  }

  // TODO sr137: optimize
  @Override
  public Dimension getPlotDimension(Graphics2D g, int x, int y, int width,
      int height) {
    if (getAxesCount() > 0) {
      return new Dimension(width - getWidthLeftAxes(g) - getWidthRightAxes(g)
          - 2, height - getHeightLowerAxes(g, 0) - 5); // 5 margin on top
    }
    return new Dimension(
        width - getWidthLeftAxes(g) - getWidthRightAxes(g) - 2, height - 5);
  }

  // TODO sr137: optimize
  @Override
  public Point getPlotOrigin(Graphics2D g, int x, int y, int width, int height) {
    return new Point(x + getWidthLeftAxes(g) + 1, y + 5); // 5 margin
  }

  /**
   * Note: this method is going to be changed because there should be a renderer
   * attachable to each {@link IAxis}.
   * 
   * @param renderer
   *          the renderer to set
   */
  public void setRenderer(IAxisRenderer renderer) {
    this.renderer = renderer;
  }

}
