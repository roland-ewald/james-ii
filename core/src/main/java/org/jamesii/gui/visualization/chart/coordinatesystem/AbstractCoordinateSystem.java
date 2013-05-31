/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.coordinatesystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.gui.visualization.chart.axes.IAxis;
import org.jamesii.gui.visualization.chart.axes.LinearAxis;
import org.jamesii.gui.visualization.chart.model.IChartModel;
import org.jamesii.gui.visualization.chart.model.IChartModelListener;
import org.jamesii.gui.visualization.chart.model.ISeries;

/**
 * Basic class that should be used as base for {@link ICoordinateSystem}
 * implementations.
 * 
 * @author Stefan Rybacki
 */
public abstract class AbstractCoordinateSystem implements ICoordinateSystem,
    IChartModelListener {
  /**
   * the managed axes
   */
  private final List<IAxis> axes = new ArrayList<>();

  /**
   * mapping from axes to belonging groups
   */
  private final Map<Integer, Integer> axisToGroup = new HashMap<>();

  /**
   * mapping from axes to belonging groups
   */
  private final Map<Integer, Integer> groupToAxis = new HashMap<>();

  /**
   * mapping from axes to belonging coordinate dimensions
   */
  private final Map<Integer, Integer> axisToDimension = new HashMap<>();

  /**
   * list of axes orientations in radians
   */
  private final List<Double> orientations = new ArrayList<>();

  /**
   * the used {@link IChartModel}
   */
  private IChartModel model = null;

  // private boolean alreadyRefreshed;

  /**
   * Creates an abstract instance while setting the given {@link IChartModel}
   * 
   * @param model
   *          the chart model to use
   */
  public AbstractCoordinateSystem(IChartModel model) {
    setModel(model); // NOSONAR: setModel is NOT overridable (Sonar error?)
  }

  /**
   * Sets the chart model to use
   * 
   * @param model
   *          the model to use
   */
  @Override
  public final void setModel(IChartModel model) {
    if (model == null) {
      throw new IllegalArgumentException("model can't be null");
    }

    if (model.getDimensions() != getSupportedDimensions()) {
      throw new IllegalArgumentException(
          "model must have same dimension count as coordinate system");
    }

    if (this.model != null) {
      this.model.removeListener(this);
    }
    this.model = model;

    this.model.addListener(this);
    axes.clear();
    axisToDimension.clear();
    axisToGroup.clear();
    refreshAxes();
  }

  /**
   * Refreshes axes after model change.
   */
  private void refreshAxes() {
    // get axes count, axis to group mapping as well as axis to
    // dimension
    // mapping
    for (int i = 0; i < model.getSeriesCount(); i++) {
      for (int j = 0; j < model.getDimensions(); j++) {
        int group = model.getGroup(model.getSeries(i), j);
        if (!axisToGroup.containsValue(group)) {
          IAxis axis = new LinearAxis();
          axis.setMinimum(model.getMinValue(group).doubleValue());
          axis.setMaximum(model.getMaxValue(group).doubleValue());
          axes.add(axis);
          axisToGroup.put(axes.size() - 1, group);
          groupToAxis.put(group, axes.size() - 1);
          setAxisForDimension(axes.size() - 1, j);
        } else {
          IAxis axis = axes.get(axisToGroup.get(group));
          axis.setMinimum(model.getMinValue(group).doubleValue());
          axis.setMaximum(model.getMaxValue(group).doubleValue());
        }
      }
    }
  }

  @Override
  public final IChartModel getModel() {
    return model;
  }

  @Override
  public final int getGroupForAxis(int axisIndex) {
    return axisToGroup.get(axisIndex);
  }

  @Override
  public final int getAxisForGroup(int group) {
    return groupToAxis.get(group);
  }

  @Override
  public final int getAxesCount() {
    return axes.size();
  }

  @Override
  public final IAxis getAxis(int index) {
    return axes.get(index);
  }

  /**
   * Sets the orientation of an axis to the given angle in radians
   * 
   * @param index
   *          the axis' index
   * @param orientation
   *          the angle of orientation in radians
   */
  protected final void setOrientation(int index, double orientation) {
    orientations.set(index, orientation);
  }

  @Override
  public final void setAxis(int index, IAxis axis) {
    axes.set(index, axis);
    int group = getGroupForAxis(index);
    axis.setMaximum(model.getMaxValue(group).doubleValue());
    axis.setMinimum(model.getMinValue(group).doubleValue());
  }

  @Override
  public void setAxisForDimension(int axisIndex, int dimension) {
    axisToDimension.put(axisIndex, dimension);
  }

  @Override
  public int getDimensionForAxis(int axisIndex) {
    return axisToDimension.get(axisIndex);
  }

  @Override
  public void dataChanged() {
    refreshAxes();
  }

  @Override
  public void groupChanged(int seriesIndex, int dimension) {
    refreshAxes();
  }

  @Override
  public void seriesAdded(ISeries s) {
    refreshAxes();
  }

  @Override
  public void seriesRemoved(ISeries s) {
    refreshAxes();
  }

  @Override
  public void valueAdded(ISeries s, long valueIndex) {
    refreshAxes();
  }

  @Override
  public void valueRemoved(ISeries s, long valueIndex) {
    refreshAxes();
  }
}
