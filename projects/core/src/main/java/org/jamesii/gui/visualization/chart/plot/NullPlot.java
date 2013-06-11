/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.plot;

import java.awt.Graphics2D;

import org.jamesii.gui.visualization.chart.BasicChart;
import org.jamesii.gui.visualization.chart.model.ISeries;

/**
 * This plot does not plot at all. This can be use together with e.g.,
 * {@link TwoSeriesAreaLinePlot} to hide the second series because it is already
 * covered by that plot.
 * 
 * @author Stefan Rybacki
 */
public class NullPlot implements IPlot {

  @Override
  public void drawPlot(BasicChart chart, ISeries series, Graphics2D g, int x,
      int y, int width, int height) {
  }

  @Override
  public void drawPlotInLegend(Graphics2D g, int x, int y, int width, int height) {
  }

}
