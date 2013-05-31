/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.plot;

import java.awt.Graphics2D;
import java.awt.RenderingHints;

import org.jamesii.gui.visualization.chart.BasicChart;
import org.jamesii.gui.visualization.chart.model.ISeries;

/**
 * A {@link IPlot} decorator that draws a particular {@link IPlot} anti-aliased.
 * Anti-aliasing can be helpful for {@link IPlot}s that draw structures that
 * easily vanish or become unrecognisable without anti-aliasing, such as dotted
 * lines.
 * <p>
 * To use it, simply wrap the original plot in this class:
 * 
 * <pre>
 * new AntiAliasedPlot(originalPlot)
 * </pre>
 * 
 * @author Johannes Rössel
 */
public class AntiAliasedPlot implements IPlot {

  /** The {@link IPlot} instance to delegate to. */
  private IPlot plot;

  /**
   * Initialises a new instance of the {@link AntiAliasedPlot} class, decorating
   * the given plot.
   * 
   * @param plot
   *          The plot to decorate.
   */
  public AntiAliasedPlot(IPlot plot) {
    this.plot = plot;
  }

  @Override
  public void drawPlot(BasicChart chart, ISeries series, Graphics2D g, int x,
      int y, int width, int height) {
    Graphics2D gCopy = (Graphics2D) g.create();
    gCopy.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    plot.drawPlot(chart, series, gCopy, x, y, width, height);
    gCopy.dispose();
  }

  @Override
  public void drawPlotInLegend(Graphics2D g, int x, int y, int width, int height) {
    Graphics2D gCopy = (Graphics2D) g.create();
    gCopy.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    plot.drawPlotInLegend(gCopy, x, y, width, height);
    gCopy.dispose();
  }

}
