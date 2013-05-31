/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.plot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import org.jamesii.gui.visualization.chart.BasicChart;
import org.jamesii.gui.visualization.chart.model.IFunctionalXYSeries;
import org.jamesii.gui.visualization.chart.model.ISeries;

/**
 * Abstract class that can be used to implement new {@link IColoredPlot} or
 * {@link IPlot} classes. It already calculates the screen coordinates of each
 * data point and simply calls
 * {@link #drawPlotElement(Graphics2D, boolean, Point2D, Point2D)} with them.
 * 
 * @author Stefan Rybacki
 */
public abstract class AbstractPlot implements IColoredPlot {

  /** The color to use. */
  private Color color;

  /**
   * Instantiates a new abstract plot.
   * 
   * @param color
   *          the color to use
   */
  public AbstractPlot(Color color) {
    this.color = color;
  }

  /**
   * @return <code>true</code>, if data points that lie closer than
   *         {@link #minPixelDistance()} in screen space to the last drawn data
   *         point should be omitted in the drawing process. Return
   *         <code>false</code>, if all data points should be drawn.
   * 
   */
  protected boolean skipCloseDataPoints() {
    return true;
  }

  /**
   * Specifies the minimum pixels distance of two data points used to determine
   * if a data point is skipped during drawing.
   * 
   * @see #skipCloseDataPoints()
   * @return the minimum distance of data points in pixels
   */
  protected int minPixelDistance() {
    return 3;
  }

  @Override
  public void drawPlot(BasicChart chart, ISeries series,
      Graphics2D g, int x,
      int y, int width, int height) {
    prepareGraphics(g);
    if (series.getValueCount() == 0) {
      return;
    }

    Point2D coord = chart.getCoordinateSystem().modelToView(series, 0);
    Point2D current =
        new Point2D.Double(x + coord.getX() * width, y + height - coord.getY()
            * height);
    Point2D oldT = current;

    boolean binarySearch = series.getValueCount() > width * minPixelDistance();

    // TODO sr137: this only applies to a coordinate system where values get
    // closer to each other the closer they are on dimension one
    if (series instanceof IFunctionalXYSeries && skipCloseDataPoints()
        && binarySearch) {
      int l = 1;
      int size = series.getValueCount();
      int r = size;
      int distSq = minPixelDistance() * minPixelDistance();
      // draw first element
      drawPlotElement(g, true, oldT, oldT);

      boolean draw = false;

      while (true) {
        // find next element using binary search
        int i = l + (r - l) / 2;

        if (i >= size) {
          break;
        }

        if (i == l) {
          draw = true;
        }

        coord = chart.getCoordinateSystem().modelToView(series, i);
        current =
            new Point2D.Double(x + coord.getX() * width, y + height
                - coord.getY() * height);

        // check whether pixel can be drawn
        if (draw
            || ((current.getX() - oldT.getX()) * (current.getX() - oldT.getX()) + (current
                .getY() - oldT.getY()) * (current.getY() - oldT.getY())) < distSq) {
          drawPlotElement(g, false, oldT, current);
          l = i + 1;
          r = size;
          oldT = current;
          draw = false;
        } else {
          r = i;
        }
      }
    } else {
      for (int i = 0; i < chart.getModel().getValueCount(series); i++) {
        coord = chart.getCoordinateSystem().modelToView(series, i);

        current =
            new Point2D.Double(x + coord.getX() * width, y + height
                - coord.getY() * height);

        drawPlotElement(g, i == 0, oldT, current);
        oldT = current;
      }
    }
  }

  /**
   * Draw plot element.
   * 
   * @param g
   *          the graphics context to draw on
   * @param first
   *          flag indicating whether the given current point is the first of
   *          the data series (which means there is no prev value)
   * @param prev
   *          the previous value in screen space if first is <code>false</code>
   * @param current
   *          the current value in screen space
   */
  protected abstract void drawPlotElement(Graphics2D g, boolean first,
      Point2D prev, Point2D current);

  protected abstract void prepareGraphics(Graphics2D g);

  @Override
  public void setColor(Color color) {
    this.color = color;
  }

  @Override
  public Color getColor() {
    return color;
  }

}
