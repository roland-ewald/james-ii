/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.plot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;
import org.jamesii.gui.visualization.chart.plot.plugintype.PlotFactory;

/**
 * @author Stefan Rybacki
 * 
 */
public class SkylinePlotFactory extends PlotFactory {

  private static final long serialVersionUID = 2634666544915693102L;

  public static final String COLOR = "color";

  public static final String STROKE = "stroke";

  @Override
  public IColoredPlot create(ParameterBlock params) {
    Color c =
        ParameterBlocks.getSubBlockValueOrDefault(params, COLOR, Color.red);
    Stroke s =
        ParameterBlocks.getSubBlockValueOrDefault(params, STROKE,
            new BasicStroke());

    return new SkylinePlot(c, s);
  }

}
