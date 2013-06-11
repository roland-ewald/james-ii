/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.grid;

import java.awt.Rectangle;

/**
 * An event listener that can get updates about cell changes in a grid model.
 * 
 * @author Johannes Rössel
 */
public interface IGridCellListener {

  /**
   * Is called if a cell value in the model changes.
   * 
   * @param x
   *          The X coordinate of the changed cell.
   * @param y
   *          The Y coordinate of the changed cell.
   */
  void cellChanged(int x, int y);

  /** Is called when the complete model data changes. */
  void dataChanged();

  /**
   * Is called when the boundaries of the underlying model change.
   * 
   * @param newBounds
   *          The new boundaries.
   */
  void boundsChanged(Rectangle newBounds);

  /**
   * Is called when a range of cells changes.
   * 
   * @param r
   *          A rectangular range of cells that changed and need to be
   *          repainted.
   */
  void cellRangeChanged(Rectangle r);

}
