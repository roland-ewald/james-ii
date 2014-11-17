/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.grid;

import org.jamesii.core.util.collection.ListenerSupport;

/**
 * Abstract superclass for grid cell renderers that already handles listeners
 * for convenience.
 * 
 * @author Johannes Rössel
 */
public abstract class AbstractGridCellRenderer implements IGridCellRenderer {
  /** Registered listeners. */
  private ListenerSupport<IRenderingChangedListener> listeners =
      new ListenerSupport<>();

  @Override
  public void addRenderingChangedListener(IRenderingChangedListener l) {
    listeners.addListener(l);
  }

  @Override
  public void removeRenderingChangedListener(IRenderingChangedListener l) {
    listeners.removeListener(l);
  }

  /**
   * Convenience method that notifies all registered listeners that the
   * rendering has changed.
   */
  protected void fireRenderingChanged() {
    for (IRenderingChangedListener l : listeners.getListeners()) {
      l.renderingChanged();
    }
  }
}
