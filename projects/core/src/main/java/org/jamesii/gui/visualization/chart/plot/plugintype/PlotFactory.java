/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.plot.plugintype;

import org.jamesii.core.factories.Context;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.visualization.chart.plot.IColoredPlot;

/**
 * @author Stefan Rybacki
 * 
 */
public abstract class PlotFactory extends Factory<IColoredPlot> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -3039426107252686102L;

  @Override
  public abstract IColoredPlot create(ParameterBlock paramBlock, Context context);

}
