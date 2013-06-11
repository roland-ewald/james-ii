/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.model;

/**
 * Interface that extends {@link ISeries} and adds {@link #getValueAtX(Number)}
 * which automatically restricts the implementing series to functional behaviour
 * which means there is at most one y value for each x value.
 * 
 * @author Stefan Rybacki
 */
public interface IFunctionalXYSeries extends ISeries {

  /**
   * Gets the value at x.
   * 
   * @param x
   *          the x
   * @return the value at x
   */
  Number getValueAtX(Number x);
}
