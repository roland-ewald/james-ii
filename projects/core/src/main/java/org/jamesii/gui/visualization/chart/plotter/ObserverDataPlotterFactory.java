/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.plotter;

import org.jamesii.core.observe.listener.IObserverListener;
import org.jamesii.core.observe.listener.plugintype.AbstractObserverListenerFactory;
import org.jamesii.core.observe.listener.plugintype.ObserverListenerFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.application.WindowManagerManager;

/**
 * Factory that creates an observer data plotter.
 * <p>
 * <b><font color="red">NOTE: This class is very likely to change or to vanish
 * in future releases so use with care.</font></b>
 * 
 * @author Roland Ewald
 * 
 */
@Deprecated
public class ObserverDataPlotterFactory extends ObserverListenerFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = 1L;

  @Override
  public IObserverListener create(ParameterBlock parameter) {
    ObserverPlotter observerPlotter = new ObserverPlotter();
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
    if (IPlotableObserver.class.isAssignableFrom((Class<?>) params
        .getSubBlockValue(AbstractObserverListenerFactory.OBSERVER_CLASS))) {
      return 2;
    }
    return 0;
  }

}
