/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.plotter;

import java.util.List;

import org.jamesii.core.observe.INotifyingObserver;
import org.jamesii.core.observe.IObservable;
import org.jamesii.core.util.misc.Pair;

/**
 * Interface that defines the functions to be implemented by an observer so that
 * its data can be plotted.
 * 
 * @author Roland Ewald
 */
@Deprecated
public interface IPlotableObserver<E extends IObservable> extends
    INotifyingObserver<E> {

  /**
   * Get data for a certain variable. To make automatic refreshing in an
   * ObserverPlotter work, a reference to the same list should be accessed by
   * the observer to store data.
   * 
   * @param varName
   *          name of the variable
   * @return data for a certain variable
   */
  List<Pair<? extends Number, ? extends Number>> getVariableData(String varName);

  /**
   * Get list of available variables.
   * 
   * @return list of available variables
   */
  List<String> getVariableNames();

  /**
   * Get title of the plot.
   * 
   * @return title of the plot
   */
  String getPlotTitle();

  /**
   * Get names of axes (beginning with x-axis).
   * 
   * @return names of axes.
   */
  String[] getAxisNames();

  /**
   * Get unit names for the axes (same order as names array).
   * 
   * @return unit names for the axes
   */
  String[] getAxisUnits();

}
