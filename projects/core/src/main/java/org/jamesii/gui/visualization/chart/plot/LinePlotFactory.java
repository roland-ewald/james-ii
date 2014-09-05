/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.plot;

import java.awt.Color;

import org.jamesii.core.factories.Context;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;
import org.jamesii.gui.visualization.chart.plot.plugintype.PlotFactory;

/**
 * @author Stefan Rybacki
 * 
 */
public class LinePlotFactory extends PlotFactory {

  private static final long serialVersionUID = -7867731601534279979L;

  public static final String COLOR = "color";

  public static final String STROKE = "stroke";

  @Override
  public IColoredPlot create(ParameterBlock params, Context context) {
    Color c =
        ParameterBlocks.getSubBlockValueOrDefault(params, COLOR, Color.red);

    return new LinePlot(c);
  }

}
