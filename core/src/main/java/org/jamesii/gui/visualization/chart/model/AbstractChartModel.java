/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.model;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jamesii.gui.utils.ListenerSupport;

/**
 * Abstract class that provides basic implementation of {@link IChartModel} plus
 * listener notification methods.
 * 
 * @author Stefan Rybacki
 */
public abstract class AbstractChartModel implements IChartModel {
  /**
   * support for {@link IChartModelListener}s
   */
  private final ListenerSupport<IChartModelListener> listeners =
      new ListenerSupport<>();

  /**
   * The updating lock.
   */
  private final Lock updatingLock = new ReentrantLock();

  /**
   * The repaint lock.
   */
  private final Lock repaintLock = new ReentrantLock();

  /**
   * The updating flag indicating whether updating is currently running.
   */
  private boolean updating = false;

  @Override
  public synchronized void addListener(IChartModelListener listener) {
    listeners.addListener(listener);
  }

  @Override
  public synchronized void removeListener(IChartModelListener listener) {
    listeners.removeListener(listener);
  }

  /**
   * Notifies registered listeners by calling their
   * {@link IChartModelListener#dataChanged()} method.
   */
  protected final void fireDataChanged() {
    updatingLock.lock();
    try {
      if (!updating) {
        for (IChartModelListener l : listeners.getListeners()) {
          l.dataChanged();
        }
      }
    } finally {
      updatingLock.unlock();
    }
  }

  /**
   * Notifies registered listeners by calling their
   * {@link IChartModelListener#valueAdded(ISeries, long)} method.
   * 
   * @param series
   *          the series a value was added for
   * @param valueIndex
   *          the index of the value that was added
   */
  protected final void fireValueAdded(ISeries series, long valueIndex) {
    updatingLock.lock();
    try {
      if (!updating) {
        for (IChartModelListener l : listeners.getListeners()) {
          l.valueAdded(series, valueIndex);
        }
      }
    } finally {
      updatingLock.unlock();
    }
  }

  /**
   * Notifies registered listeners by calling their
   * {@link IChartModelListener#valueRemoved(ISeries, long)} method.
   * 
   * @param series
   *          the series a value was removed from
   * @param valueIndex
   *          the index of the value that was removed
   */
  protected final void fireValueRemoved(ISeries series, long valueIndex) {
    updatingLock.lock();
    try {
      if (!updating) {
        for (IChartModelListener l : listeners.getListeners()) {
          l.valueRemoved(series, valueIndex);
        }
      }
    } finally {
      updatingLock.unlock();
    }
  }

  /**
   * Notifies registered listeners by calling their
   * {@link IChartModelListener#seriesAdded(ISeries)} method.
   * 
   * @param series
   *          the series that was added
   */
  protected final void fireSeriesAdded(ISeries series) {
    updatingLock.lock();
    try {
      if (!updating) {
        for (IChartModelListener l : listeners.getListeners()) {
          l.seriesAdded(series);
        }
      }
    } finally {
      updatingLock.unlock();
    }
  }

  /**
   * Notifies registered listeners by calling their
   * {@link IChartModelListener#seriesRemoved(ISeries)} method.
   * 
   * @param series
   *          the series that was removed
   */
  protected final void fireSeriesRemoved(ISeries series) {
    updatingLock.lock();
    try {
      if (!updating) {
        for (IChartModelListener l : listeners.getListeners()) {
          l.seriesRemoved(series);
        }
      }
    } finally {
      updatingLock.unlock();
    }
  }

  /**
   * Notifies registered listeners by calling their
   * {@link IChartModelListener#seriesRemoved(ISeries)} method.
   * 
   * @param index
   *          the index of the series whose dimension group changed
   * @param dimension
   *          the dimension
   */
  protected final void fireGroupChanged(int index, int dimension) {
    updatingLock.lock();
    try {
      if (!updating) {
        for (IChartModelListener l : listeners.getListeners()) {
          l.groupChanged(index, dimension);
        }
      }
    } finally {
      updatingLock.unlock();
    }
  }

  /**
   * Call this method if you called {@link #startUpdating()} and are now
   * finished updating model data.
   */
  public final void finishedUpdating() {
    updatingLock.lock();
    try {
      updating = false;
    } finally {
      updatingLock.unlock();
    }
    fireDataChanged();
  }

  /**
   * Call this method if you start updating more than one element of the model.
   * Call {@link #finishedUpdating()} if you are done with updating.
   */
  public final void startUpdating() {
    repaintLock.lock();
    updatingLock.lock();
    repaintLock.unlock();

    try {
      updating = true;
    } finally {
      updatingLock.unlock();
    }
  }

  @Override
  public boolean repaintLock() {
    boolean result = false;
    updatingLock.lock();
    try {
      result = !updating && repaintLock.tryLock();
    } finally {
      updatingLock.unlock();
    }
    return result;
  }

  @Override
  public void repaintUnlock() {
    repaintLock.unlock();
  }
}
