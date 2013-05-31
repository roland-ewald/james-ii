/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.axes;

/**
 * Listener interface for notifications of changes to an {@link IAxis} .
 * <p>
 * <b><font color="red">NOTE: This class is very likely to change in future
 * releases so use with care.</font></b>
 * 
 * @author Stefan Rybacki
 */
public interface IAxisListener {
  /**
   * Called when the maximum value of the given {@link IAxis} changed
   * 
   * @param axis
   *          the changed axis
   * @param old
   *          the old maximum value
   */
  void maximumChanged(IAxis axis, double old);

  /**
   * Called when the minimum value of the given {@link IAxis} changed
   * 
   * @param axis
   *          the changed axis
   * @param old
   *          the old minimum value
   */
  void minimumChanged(IAxis axis, double old);

  /**
   * General method to use whenever anything else changed within the given
   * {@link IAxis} that makes it necessary that other components that rely on
   * that axis should check its state.
   * 
   * @param axis
   *          the changed axis
   */
  void stateChanged(IAxis axis);
}
