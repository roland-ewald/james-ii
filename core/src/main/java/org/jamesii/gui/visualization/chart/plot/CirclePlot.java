/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.plot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

/**
 * A very simple plotter drawing circles at each data position with a predefined
 * radius.
 * 
 * @author Stefan Rybacki
 */
public class CirclePlot extends AbstractPlot {

  /**
   * The stroke to use.
   */
  private Stroke stroke;

  /**
   * The radius of the circle to draw.
   */
  private double radius;

  /**
   * Instantiates a new circle plot.
   * 
   * @param color
   *          the color to use
   * @param stroke
   *          the stroke to use
   * @param radius
   *          the radius of the circle to draw
   */
  public CirclePlot(Color color, Stroke stroke, double radius) {
    super(color);
    this.stroke = stroke;
    this.radius = radius;
  }

  @Override
  public void drawPlotInLegend(Graphics2D g, int x, int y, int width, int height) {
    Stroke oldStroke = g.getStroke();
    g.setColor(getColor());
    g.setStroke(stroke);
    g.draw(new Ellipse2D.Double(x, y, width, height));
    g.setStroke(oldStroke);
  }

  @Override
  protected void drawPlotElement(Graphics2D g, boolean first, Point2D prev,
      Point2D current) {
    g.draw(new Ellipse2D.Double(current.getX() - radius, current.getY()
        - radius, radius * 2, radius * 2));
  }

  @Override
  protected void prepareGraphics(Graphics2D g) {
    g.setColor(getColor());
    g.setStroke(stroke);
  }

}
