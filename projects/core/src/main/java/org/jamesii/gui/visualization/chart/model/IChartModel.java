/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.model;

import org.jamesii.gui.visualization.chart.BasicChart;
import org.jamesii.gui.visualization.chart.axes.IAxis;
import org.jamesii.gui.visualization.chart.coordinatesystem.ICoordinateSystem;
import org.jamesii.gui.visualization.chart.plot.IPlot;

/**
 * The interface providing data information to the {@link ICoordinateSystem} and
 * {@link IPlot} implementations for further use in custom charts or
 * {@link BasicChart}.
 * <p>
 * <b><font color="red">NOTE: This class is very likely to change in future
 * releases so use with care.</font></b>
 * 
 * @author Enrico Seib
 * @author Stefan Rybacki
 */
// TODO sr137: provide a way to group updates so for instance if I
// have 5 series and will update all 5 of them there is no need to
// send 5 updated events but one in the end
public interface IChartModel {
  /**
   * adds a listener to the chart model
   * 
   * @param listener
   *          the listener to add
   */
  void addListener(IChartModelListener listener);

  /**
   * Removes a previously registered listener
   * 
   * @param listener
   *          the listener to remove
   */
  void removeListener(IChartModelListener listener);

  /**
   * @return the number of series in the data set
   */
  int getSeriesCount();

  /**
   * Gets the series at the given index.
   * 
   * @param index
   *          the index of the requested series
   * @return the requested series
   */
  ISeries getSeries(int index);

  /**
   * @return the number dimensions (must be the same for all series)
   */
  int getDimensions();

  /**
   * Returns the value of a series for a given dimension and a given index
   * 
   * @param series
   *          the series
   * @param dimension
   *          the dimension to return
   * @param valueIndex
   *          the index of the value
   * @return the value
   */
  Number getValue(ISeries series, int dimension, int valueIndex);

  /**
   * Returns the number of values available for the given series
   * 
   * @param series
   *          the series
   * @return the value count
   */
  int getValueCount(ISeries series);

  /**
   * This method is used to group dimension of each series together. That means
   * different series can be mapped onto the same {@link IAxis} if they are in
   * the same group.
   * <p>
   * Example:<br/>
   * There are 4 series with 2 dimensions each:
   * <ul>
   * <li>series 1: temperature Rostock over the year 2008</li>
   * <li>series 2: temperature Berlin over the year 2008</li>
   * <li>series 3: rain Rostock over the year 2008</li>
   * <li>series 4: rain Berlin over the year 2008</li>
   * </ul>
   * <p>
   * We now want to have:
   * <ul>
   * <li>one axis for the time (year 2008)</li>
   * <li>one axis for temperature</li>
   * <li>one axis for rain</li>
   * </ul>
   * So we define the following groups:
   * <ul>
   * <li><b>0</b> for all time dimensions for all series</li>
   * <li><b>1</b> for the temperature dimensions of series 1 and 2</li>
   * <li><b>2</b> for the rain dimensions of series 3 and 4</li>
   * </ul>
   * That means {@link #getGroup(ISeries, int)} returns as follows when using
   * specified parameters:
   * <table>
   * <tr>
   * <td>series</td>
   * <td>dimension</td>
   * <td>result</td>
   * </tr>
   * <tr>
   * <td>0</td>
   * <td>0</td>
   * <td>0</td>
   * </tr>
   * <tr>
   * <td>1</td>
   * <td>0</td>
   * <td>0</td>
   * </tr>
   * <tr>
   * <td>2</td>
   * <td>0</td>
   * <td>0</td>
   * </tr>
   * <tr>
   * <td>3</td>
   * <td>0</td>
   * <td>0</td>
   * </tr>
   * <tr>
   * <td>0</td>
   * <td>1</td>
   * <td>1</td>
   * </tr>
   * <tr>
   * <td>1</td>
   * <td>1</td>
   * <td>1</td>
   * </tr>
   * <tr>
   * <td>2</td>
   * <td>1</td>
   * <td>2</td>
   * </tr>
   * <tr>
   * <td>3</td>
   * <td>1</td>
   * <td>2</td>
   * </tr>
   * </table>
   * This results in three groups hence three axes.
   * 
   * @param series
   *          the series index
   * @param dimension
   *          the dimension index
   * @return the group the dimension of the given series belongs to
   */
  int getGroup(ISeries series, int dimension);

  /**
   * Returns the name of a specific group that is returned by
   * {@link #getGroup(ISeries, int)}. This name might be used as axis label.
   * 
   * @param group
   *          the group
   * @return a name for the group
   */
  String getGroupName(int group);

  /**
   * @param group
   *          the group the minimum value is requested for
   * @return the minimum value for the given group
   */
  Number getMinValue(int group);

  /**
   * @param group
   *          the group the maximum value is requested for
   * @return the maximum value for the given group
   */
  Number getMaxValue(int group);

  /**
   * To provide consistency during usage of model while repainting this method
   * is called prior to the repainting process is later on unlocked by
   * {@link #repaintUnlock()}. It is crucial that this method and
   * {@link #repaintUnlock()} are implemented correctly so that while repaint
   * lock was and returned {@code true} which means the lock was acquired
   * successfully no changes to the model whatsoever should occur until
   * {@link #repaintUnlock()} is called.
   * <p/>
   * Example usage:<br/>
   * <code>
   * <pre>
   *   if (model.repaintLock()) {
   *    try {
   *      chart.repaint();  //assuming that repaint does not postpone the actual rendering
   *    } finally {
   *      model.repaintUnlock();
   *    }
   *   }
   * </pre>
   * </code>
   * 
   * @return true, if successful acquired the lock
   */
  boolean repaintLock();

  /**
   * To provide consistency during usage of model while repainting this method
   * is called after {@link #repaintLock()} was called and returned {@code true}
   * . It is crucial that this method and {@link #repaintLock()} are implemented
   * correctly so that while repaint lock was and returned {@code true} which
   * means the lock was acquired successfully no changes to the model whatsoever
   * should occur until {@link #repaintUnlock()} is called.
   */
  void repaintUnlock();

  /**
   * Returns the name of a specific series. This name might be used as legend
   * label.
   * 
   * @param series
   *          the series
   * @return the series name for the specified index
   */
  String getSeriesName(ISeries series);
}
