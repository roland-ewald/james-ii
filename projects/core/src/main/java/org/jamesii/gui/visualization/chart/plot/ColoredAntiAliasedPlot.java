/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.plot;

import java.awt.Color;

/**
 * @author Stefan Rybacki
 * 
 */
public class ColoredAntiAliasedPlot extends AntiAliasedPlot implements
    IColoredPlot {

  /** The plot to wrap. */
  private IColoredPlot plot;

  public ColoredAntiAliasedPlot(IColoredPlot plot) {
    super(plot);
    this.plot = plot;
  }

  @Override
  public void setColor(Color color) {
    plot.setColor(color);
  }

  @Override
  public Color getColor() {
    return plot.getColor();
  }

}
