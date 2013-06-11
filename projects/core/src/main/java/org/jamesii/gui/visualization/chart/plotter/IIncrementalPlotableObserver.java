/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.plotter;

import java.awt.Color;
import java.util.List;

import org.jamesii.core.observe.INotifyingObserver;
import org.jamesii.core.observe.IObservable;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.gui.visualization.chart.plot.IPlot;

/**
 * Interface that defines the functions to be implemented by an observer so that
 * its data can be plotted.
 * 
 * @author Stefan Rybacki
 */
public interface IIncrementalPlotableObserver<E extends IObservable> extends
    INotifyingObserver<E> {

  /**
   * Get current data for a certain variable. The resulting pair of data is
   * added to the already collected values of previous calls. So there is no
   * need to hold and provide all data at all times but only the most recent
   * value. This method is called each time it is passed to the update function
   * of an observer.
   * <p/>
   * Return {@code null} if no data is available for variable or the resulting
   * pair of numbers didn't change.
   * 
   * @param varName
   *          name of the variable
   * @return data for a certain variable or {@code null} if no change in both
   *         first second value or if no data is available for variable
   */
  Pair<? extends Number, ? extends Number> getCurrentData(String varName);

  /**
   * Get list of available variables. Called before hand
   * {@link #getCurrentData(String)} is called for all variable names.
   * 
   * @return list of available variables
   */
  List<String> getVariableNames();

  /**
   * Get title of the plot. This method is only called once by each observer for
   * initialization purposes.
   * 
   * @return title of the plot
   */
  String getPlotTitle();

  /**
   * Get names of axes. This method is only called once by each observer for
   * initialization purposes.
   * 
   * @return names of axes.
   */
  List<String> getAxisNames();

  /**
   * Get unit names for the axes (same order as names array). This method is
   * only called once by each observer for initialization purposes.
   * 
   * @return unit names for the axes
   */
  List<String> getAxisUnits();

  /**
   * Allows to specify a minimum value for x and y axis if known. This prevents
   * the plotter to calculate its own min value.
   * 
   * @return the min value for x and y axis, specify null for one or both if
   *         unknown
   */
  Pair<? extends Number, ? extends Number> getMin();

  /**
   * Allows to specify a maximum value for x and y axis if known. This prevents
   * the plotter to calculate its own max value.
   * 
   * @return the max value for x and y axis, specify null for one or both if
   *         unknown
   */
  Pair<? extends Number, ? extends Number> getMax();

  /**
   * To provide a custom plotter for a given variable implement this method.
   * Returning <code>null</code> will use the default plotter and color.
   * 
   * @param name
   *          the name of the variable
   * @param color
   *          the standard color to use (but can be ignore if color management
   *          is done by this plotable)
   * @return the plot to use for the given variable name, or <code>null</code>
   *         if standard
   * 
   * @see #getVariableNames()
   */
  IPlot getPlotFor(String name, Color color);

  /**
   * @param varName
   *          name of the variable
   * @return <code>true</code> if values are increasing in X-dimension (usually
   *         the case when dealing with time-series), <code>false</code> else
   */
  boolean isMonotonIncreasingX(String varName);
}
