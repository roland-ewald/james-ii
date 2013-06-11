/*
 * The general modeling and simulation framework JAMES II. Copyright
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
 * @author Stefan Rybacki
 */
public class SkylinePlot extends AbstractPlot {

  /**
   * The stroke to use.
   */
  private Stroke stroke;

  /**
   * Instantiates a new skyline plot.
   * 
   * @param color
   *          the color to use
   * @param stroke
   *          the stroke to use
   */
  public SkylinePlot(Color color, Stroke stroke) {
    super(color);
    this.stroke = stroke;
  }

  /**
   * Instantiates a new skyline plot.
   * 
   * @param color
   *          the color to use
   */
  public SkylinePlot(Color color) {
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
          (int) prev.getY());
      g.drawLine((int) current.getX(), (int) prev.getY(), (int) current.getX(),
          (int) current.getY());
    }
  }

  @Override
  protected void prepareGraphics(Graphics2D g) {
    g.setColor(getColor());
    g.setStroke(stroke);
  }

}
