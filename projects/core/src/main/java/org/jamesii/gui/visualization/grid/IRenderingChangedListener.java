/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.grid;

/**
 * Interface for a listener that gets notified if the rendering of an
 * {@link IGridCellRenderer} changes. This can happen if the visual rendering of
 * certain calls depends on other data from elsewhere which gets changed and,
 * accordingly, causes a different rendering for a grid cell.
 * 
 * @author Johannes Rössel
 * 
 */
public interface IRenderingChangedListener {
  /**
   * Gets called whenever the {@link IGridCellRenderer} changes rendering for
   * values. In this case controls using the renderer need to be redrawn.
   */
  void renderingChanged();
}
