/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jamesii.core.util.misc.Pair;
import org.jamesii.gui.visualization.chart.model.aggregator.IAggregator;

/**
 * This is a basic xy series that represents a number of series aggregated into
 * a single one by using the {@link IAggregator} implementations. It is expected
 * to work well with dimension 0 monotonic {@link ISeries}es. Results might vary
 * from expected when this condition is not meet.
 * <p/>
 * <b>Not working yet!!!</b>
 * 
 * @author Stefan Rybacki
 */
@Deprecated
public final class AggregatedXYSeries extends AbstractSeries implements
    ISeriesListener {

  /**
   * The serieses that should be aggregated.
   */
  private final List<IFunctionalXYSeries> serieses = new ArrayList<>();

  /**
   * The aggregator to use.
   */
  private IAggregator aggregator;

  /**
   * The cached maximum value for each dimension.
   */
  private final double[] max = new double[2];

  /**
   * The cached min value for each dimension.
   */
  private final double[] min = new double[2];

  /**
   * The cached aggregated values.
   */
  private final List<Pair<Number, Number>> cachedValues = new ArrayList<>();

  /**
   * Instantiates a new aggregated xy series.
   * 
   * @param name
   *          the name of the series
   * @param aggregator
   *          the aggregator to use when aggregating supplied
   *          {@link IFunctionalXYSeries}s
   */
  public AggregatedXYSeries(String name, IAggregator aggregator) {
    super(name);
    setAggregator(aggregator);
  }

  @Override
  public int getDimensions() {
    return 2;
  }

  @Override
  protected Number maxValue(int dimension) {
    return max[dimension];
  }

  @Override
  protected Number minValue(int dimension) {
    return min[dimension];
  }

  @Override
  public Number getValue(int dimension, int index) {
    if (dimension == 0) {
      return cachedValues.get(index).getFirstValue();
    }
    return cachedValues.get(index).getSecondValue();
  }

  @Override
  public int getValueCount() {
    return cachedValues.size();
  }

  /**
   * Sets the aggregator to use
   * 
   * @param aggregator
   *          the aggregator to set
   */
  public synchronized void setAggregator(IAggregator aggregator) {
    this.aggregator = aggregator;
    calculateMinMax();
    fireDataChanged();
  }

  /**
   * Returns the aggregator to use
   * 
   * @return the aggregator
   */
  public synchronized IAggregator getAggregator() {
    return aggregator;
  }

  /**
   * Adds the specified series.
   * 
   * @param series
   *          the series to add
   */
  public synchronized void addSeries(IFunctionalXYSeries series) {
    if (series.getDimensions() != getDimensions()) {
      return;
    }
    serieses.add(series);
    series.addSeriesListener(this);
    calculateValues(Double.NEGATIVE_INFINITY);
    calculateMinMax();
    fireDataChanged();
  }

  /**
   * Removes the series.
   * 
   * @param series
   *          the series to remove
   */
  public synchronized void removeSeries(IFunctionalXYSeries series) {
    series.removeSeriesListener(this);
    serieses.remove(series);
    calculateValues(Double.NEGATIVE_INFINITY);
    calculateMinMax();
    fireDataChanged();
  }

  /**
   * Helper method that calculate min and max.
   */
  private void calculateMinMax() {
    // get min and max for dimension 0
    min[0] = Double.POSITIVE_INFINITY;
    max[0] = Double.NEGATIVE_INFINITY;
    min[1] = Double.POSITIVE_INFINITY;
    max[1] = Double.NEGATIVE_INFINITY;

    for (Pair<Number, Number> p : cachedValues) {
      max[0] = Math.max(p.getFirstValue().doubleValue(), max[0]);
      min[0] = Math.min(p.getFirstValue().doubleValue(), min[0]);

      max[1] = Math.max(p.getSecondValue().doubleValue(), max[1]);
      min[1] = Math.min(p.getSecondValue().doubleValue(), min[1]);
    }
  }

  /**
   * Calculates the cached aggregated values.
   * 
   * @param startAt
   *          the starting value on "x-axis"
   */
  private synchronized void calculateValues(double startAt) {
    int[] indexes = new int[serieses.size()];
    Arrays.fill(indexes, 0);

    // clear values greater than startAt
    for (int i = 0; i < cachedValues.size(); i++) {
      if (cachedValues.get(cachedValues.size() - i - 1).getFirstValue()
          .doubleValue() > startAt) {
        cachedValues.remove(cachedValues.size() - i - 1);
      } else {
        break;
      }
    }

    double x = startAt;

    boolean finished = false;

    while (!finished) { // update indexes
      double m = Double.POSITIVE_INFINITY;

      finished = true;

      // TODO sr137: include x values within epsilon or better yet
      // advance at least a min-max depended epsilon
      for (int i = 0; i < serieses.size(); i++) {
        IFunctionalXYSeries s = serieses.get(i);
        if (s.getValueCount() <= indexes[i]) {
          continue;
        }

        finished = false;

        if (s.getValue(0, indexes[i]).doubleValue() <= x) {
          indexes[i]++;
        }

        if (s.getValueCount() <= indexes[i]) {
          continue;
        }

        // get min x value from all indexes
        m = Math.min(m, s.getValue(0, indexes[i]).doubleValue());
      }

      if (!finished) {
        x = m;

        List<Number> values = new ArrayList<>(serieses.size());
        for (IFunctionalXYSeries s : serieses) {
          Number n = s.getValueAtX(x);
          if (n != null) {
            values.add(s.getValueAtX(x));
          }
        }

        if (values != null && values.size() > 0) {
          cachedValues.add(new Pair<Number, Number>(x, aggregator
              .aggregate(values)));
        }
      }
    }

    // // get min and max of x values
    // double mi = Double.POSITIVE_INFINITY;
    // double ma = Double.NEGATIVE_INFINITY;
    //
    // for (ISeries s : serieses) {
    // mi = Math.min(mi, s.getMinValue(0).doubleValue());
    // ma = Math.max(ma, s.getMaxValue(0).doubleValue());
    // }
    //
    // int res = 250;
    //
    // for (int i = 0; i < res; i++) {
    // List<Number> values = new ArrayList<Number>(serieses.size());
    //
    // x = mi + (ma - mi) * i / res;
    // for (IFunctionalXYSeries s : serieses) {
    // Number n = s.getValueAtX(x);
    // if (n != null)
    // values.add(s.getValueAtX(x));
    // }
    //
    // if (values != null
    // && (values.size() >= serieses.size()))
    // cachedValues.add(new Pair<Number, Number>(x, aggregator
    // .aggregate(values)));
    //
    // }

  }

  @Override
  public void nameChanged(ISeries series, String oldName) {
  }

  @Override
  public void valueAdded(ISeries series, int index) {
    calculateValues(series.getValue(0, index).doubleValue());
    calculateMinMax();
    fireDataChanged();
  }

  @Override
  public void valueRemoved(ISeries series, int index) {
    dataChanged(series);
  }

  @Override
  public void dataChanged(ISeries series) {
    calculateValues(Double.NEGATIVE_INFINITY);
    calculateMinMax();
    fireDataChanged();
  }

}
