/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.grid;

import java.awt.Rectangle;

import org.jamesii.gui.utils.ListenerSupport;

/**
 * Abstract model class for a {@link Grid2D}. This class already handles the
 * listeners that may be added to the model. It provides the method
 * {@code fireCellChanged()} for easy notification of all registered listeners.
 * 
 * @author Johannes Rössel
 */
public abstract class AbstractGrid2DModel implements IGrid2DModel {

  /** Stores the listeners currently added to this model. */
  private ListenerSupport<IGridCellListener> listeners =
      new ListenerSupport<>();

  @Override
  public void addGridCellListener(IGridCellListener l) {
    listeners.addListener(l);
  }

  @Override
  public void removeGridCellListener(IGridCellListener l) {
    listeners.removeListener(l);
  }

  /**
   * Raises {@code cellChanged} events for each registered listener.
   * 
   * @param x
   *          The X coordinate of the changed cell.
   * @param y
   *          The Y coordinate of the changed cell.
   */
  protected final synchronized void fireCellChanged(int x, int y) {
    for (IGridCellListener l : listeners.getListeners()) {
      l.cellChanged(x, y);
    }
  }

  /**
   * Raises {@code cellRangeChanged} events for each registered listener.
   * 
   * @param r
   *          The {@code Rectangle} describing the cell range that changed.
   */
  protected final synchronized void fireCellRangeChanged(Rectangle r) {
    for (IGridCellListener l : listeners.getListeners()) {
      l.cellRangeChanged(r);
    }
  }

  /** Raises {@code dataChanged} events for each registered listener. */
  protected final synchronized void fireDataChanged() {
    for (IGridCellListener l : listeners.getListeners()) {
      l.dataChanged();
    }
  }

  /**
   * Fire bounds changed.
   */
  protected final synchronized void fireBoundsChanged() {
    for (IGridCellListener l : listeners.getListeners()) {
      l.boundsChanged(getBounds());
    }
  }
}
