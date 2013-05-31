/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.plot;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import org.jamesii.gui.visualization.chart.BasicChart;
import org.jamesii.gui.visualization.chart.model.ISeries;

/**
 * For testing only, yet.
 * 
 * @author Stefan Rybacki
 */
public class TwoSeriesAreaLinePlot implements IPlot {
  private ISeries otherSeries;

  private Paint paint;

  public TwoSeriesAreaLinePlot(ISeries otherSeries, Paint paint) {
    this.otherSeries = otherSeries;
    this.paint = paint;
  }

  @Override
  public void drawPlot(BasicChart chart, ISeries series, Graphics2D g, int x,
      int y, int width, int height) {
    // create a polygon shape
    Polygon p = new Polygon();
    for (int i = 0; i < chart.getModel().getValueCount(series); i++) {
      Point2D coord = chart.getCoordinateSystem().modelToView(series, i);
      p.addPoint(x + (int) (coord.getX() * width), y
          + (int) (coord.getY() * height));
    }

    for (int i = chart.getModel().getValueCount(otherSeries) - 1; i >= 0; i--) {
      Point2D coord = chart.getCoordinateSystem().modelToView(otherSeries, i);
      p.addPoint(x + (int) (coord.getX() * width), y
          + (int) (coord.getY() * height));
    }

    // draw polygon
    Paint old = g.getPaint();
    g.setPaint(paint);
    g.fill(p);
    g.setPaint(old);
  }

  @Override
  public void drawPlotInLegend(Graphics2D g, int x, int y, int width, int height) {
    Paint old = g.getPaint();
    g.setPaint(paint);
    // TODO scale paint?!
    g.fill(new Rectangle(x, y, width, height));
    g.setPaint(old);
  }

}
