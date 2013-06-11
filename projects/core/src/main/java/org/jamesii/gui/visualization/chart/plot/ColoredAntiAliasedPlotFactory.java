/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.plot;

import java.awt.Color;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;
import org.jamesii.gui.visualization.chart.plot.plugintype.PlotFactory;

/**
 * @author Stefan Rybacki
 * 
 */
public class ColoredAntiAliasedPlotFactory extends PlotFactory {

  private static final long serialVersionUID = -7042568341731470506L;

  public static final String PLOT = "plot";

  @Override
  public IColoredPlot create(ParameterBlock params) {
    String t = ParameterBlocks.getSubBlockValue(params, PLOT);
    IColoredPlot p = null;

    if (t != null) {
      try {
        PlotFactory factory =
            (PlotFactory) SimSystem.getRegistry().getFactory(t);
        p = factory.create(params.getSubBlock(PLOT));
      } catch (Exception e) {
        SimSystem.report(Level.WARNING, "Factory could not be loaded: " + t, e);
      }
    }

    if (p == null) {
      p = new LinePlot(Color.red);
    }

    return new ColoredAntiAliasedPlot(p);
  }

}
