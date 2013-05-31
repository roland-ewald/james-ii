/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.axes;

import java.util.List;

import org.jamesii.gui.base.IPropertyChangeSupport;

/**
 * Interface that defines an axis that can be used in the JAMES II chart
 * component.
 * <p>
 * <b><font color="red">NOTE: This class is very likely to change in future
 * releases so use with care.</font></b>
 * 
 * @author Enrico Seib
 * @author Stefan Rybacki
 */
public interface IAxis extends IPropertyChangeSupport {

  /**
   * 
   * @return minimum value of the axis if possible (not possible in case of
   *         qualitative axis)
   */
  double getMinimum();

  /**
   * 
   * @return maximum value of the axis if possible (not possible in case of
   *         qualitative axis)
   */
  double getMaximum();

  /**
   * main method that transforms a specific value from value space into axis
   * space
   * 
   * @param value
   *          the value to transform
   * 
   * @return the given value transformed into axis space where
   *         {@link #getMinimum()} maps to 0 and {@link #getMaximum()} to 1 if
   *         the supplied value would lie below {@link #getMinimum()} a value <
   *         0 should be returned and a value > 1 should be returned in case the
   *         value lies above {@link #getMaximum()}
   */
  double transform(double value);

  /**
   * Inverts a previously done {@link #transform(double)} on a value where the
   * given value would be the result of the {@link #transform(double)} method.
   * The following would be true:
   * <p/>
   * <code>
   * <pre>
   *    if 
   *        value = transformInv(tValue)
   *    then
   *        transform(value) == tValue;
   *    and vice versa 
   * </pre> as long as the provided tValue >= 0 and <= 1
   * </code>
   * 
   * @param tValue
   *          transformed value of previous call to {@link #transform(double)}
   * @return the original value in value space
   */
  double transformInv(double tValue);

  /**
   * Sets the maximum representable value for this axis. This can be used to
   * restrict the visible values as well as to implement auto scaling. The
   * provided maximum value is mapped to 1 when calling
   * {@link #transform(double)}
   * 
   * @param max
   *          the maximum value
   */
  void setMaximum(double max);

  /**
   * Sets the minimum representable value for this axis. This can be used to
   * restrict the visible values as well as to implement auto scaling. The
   * provided minimum value is mapped to 0 when calling
   * {@link #transform(double)}
   * 
   * @param min
   *          the minimum value
   */
  void setMinimum(double min);

  /**
   * 
   * @param count
   *          number of ticks which should be returned
   * 
   * @return List of ticks, their should be count number + 1 returned ticks
   */
  List<Double> getTickMarks(int count);

  /**
   * Adds the given listener to listen for change notifications to the axis
   * 
   * @param listener
   *          the listener to add
   */
  void addAxisListener(IAxisListener listener);

  /**
   * Removes a previously registered listener
   * 
   * @param listener
   *          the listener to remove
   */
  void removeAxisListener(IAxisListener listener);
}
