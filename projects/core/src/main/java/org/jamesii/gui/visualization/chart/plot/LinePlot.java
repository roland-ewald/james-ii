/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.plot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Point2D;

/**
 * Very simple line plot that can be used to build line charts.
 * 
 * @author Stefan Rybacki
 */
public class LinePlot extends AbstractPlot {
  /** The stroke that is used for drawing lines. */
  private final Stroke stroke;

  /**
   * Instantiates a new line plot.
   * 
   * @param color
   *          The color to use for drawing.
   * @param stroke
   *          The stroke to use for drawing lines. This allows dashed or dotted
   *          lines as well as setting the line width.
   */
  public LinePlot(Color color, Stroke stroke) {
    super(color);
    this.stroke = stroke;
  }

  /**
   * Instantiates a new line plot.
   * 
   * @param color
   *          The color to use for drawing.
   */
  public LinePlot(Color color) {
    this(color, new BasicStroke());
  }

  @Override
  public void drawPlotInLegend(Graphics2D g, int x, int y, int width, int height) {
    Stroke oldStroke = g.getStroke();
    g.setColor(getColor());
    g.setStroke(stroke);
    g.drawLine(x, y + height / 2, x + width, y + height / 2);
    g.setStroke(oldStroke);
  }

  @Override
  protected void drawPlotElement(Graphics2D g, boolean first, Point2D prev,
      Point2D current) {
    if (!first) {
      g.drawLine((int) prev.getX(), (int) prev.getY(), (int) current.getX(),
          (int) current.getY());
    }
  }

  @Override
  protected void prepareGraphics(Graphics2D g) {
    g.setColor(getColor());
    g.setStroke(stroke);
  }
}
