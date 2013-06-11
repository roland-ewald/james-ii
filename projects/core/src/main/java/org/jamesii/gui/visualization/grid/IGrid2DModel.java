/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.grid;

import java.awt.Rectangle;

/**
 * Model interface for a 2D grid.
 * 
 * @author Johannes Rössel
 */
public interface IGrid2DModel {

  /**
   * Retrieves the value at the given coordinates.
   * 
   * @param x
   *          X coordinate.
   * @param y
   *          Y coordinate.
   * @return The value at the given coordinates.
   */
  Object getValueAt(int x, int y);

  /**
   * Returns the position and dimensions of the grid where negative x and y
   * coordinates are allowed.
   * 
   * @return the position and dimensions of the grid as {@link Rectangle}
   */
  Rectangle getBounds();

  /**
   * Adds a {@link IGridCellListener} to this model. The listeners should be
   * notified if a cell's content changes.
   * 
   * @param l
   *          The listener to add.
   */
  void addGridCellListener(IGridCellListener l);

  /**
   * Removes a {@link IGridCellListener} from this model.
   * 
   * @param l
   *          The listener to remove.
   */
  void removeGridCellListener(IGridCellListener l);

}
