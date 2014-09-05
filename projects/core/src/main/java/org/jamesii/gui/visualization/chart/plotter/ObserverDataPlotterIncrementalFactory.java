/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.plotter;

import java.awt.Color;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.factories.Context;
import org.jamesii.core.observe.listener.IObserverListener;
import org.jamesii.core.observe.listener.plugintype.AbstractObserverListenerFactory;
import org.jamesii.core.observe.listener.plugintype.ObserverListenerFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;
import org.jamesii.gui.application.WindowManagerManager;
import org.jamesii.gui.visualization.chart.plot.IColoredPlot;
import org.jamesii.gui.visualization.chart.plot.LinePlot;
import org.jamesii.gui.visualization.chart.plot.plugintype.PlotFactory;

/**
 * Factory that creates an observer data plotter for the
 * {@link IIncrementalPlotableObserver} interface.
 * 
 * @author Stefan Rybacki
 */
public class ObserverDataPlotterIncrementalFactory extends
    ObserverListenerFactory {

  /** Serialization ID. */
  private static final long serialVersionUID = 2L;

  public static final String PLOT_TYPE = "default plot";

  @Override
  public IObserverListener create(ParameterBlock parameter, Context context) {
    IColoredPlot type = null;
    String t = ParameterBlocks.getSubBlockValue(parameter, PLOT_TYPE);
    if (t != null) {
      try {
        PlotFactory factory =
            (PlotFactory) SimSystem.getRegistry().getFactory(t);
        type = factory.create(parameter, SimSystem.getRegistry().createContext());
      } catch (Exception e) {
        SimSystem.report(Level.WARNING, "Factory could not be found: " + t, e);
      }
    }

    if (type == null) {
      type = new LinePlot(Color.red);
    }

    ObserverPlotterIncremental observerPlotter =
        new ObserverPlotterIncremental(type);

    if (WindowManagerManager.getWindowManager() != null) {
      WindowManagerManager.getWindowManager().addWindow(observerPlotter);
    }
    return observerPlotter;
  }

  @Override
  public String getDescription() {
    return "Creates a plotter for observer data.";
  }

  @Override
  public String getReadableName() {
    return "Visualisation Data Plotter";
  }

  @Override
  public int supportsParameters(ParameterBlock params) {
    if (IIncrementalPlotableObserver.class.isAssignableFrom((Class<?>) params
        .getSubBlockValue(AbstractObserverListenerFactory.OBSERVER_CLASS))) {
      return 2;
    }
    return 0;
  }

}
