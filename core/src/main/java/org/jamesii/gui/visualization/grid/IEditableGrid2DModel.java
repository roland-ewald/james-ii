/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.grid;

import java.awt.Rectangle;

/**
 * An interface for editors based on {@link Grid2D} which has methods for
 * editing the model's contents.
 * 
 * @author Stefan Rybacki
 * @author Johannes Rössel
 */
public interface IEditableGrid2DModel extends IGrid2DModel {

  /**
   * Sets the value at the given coordinates.
   * 
   * @param x
   *          X coordinate.
   * @param y
   *          Y coordinate.
   * @param newValue
   *          The new value to set.
   */
  void setValueAt(int x, int y, Object newValue);

  /**
   * Sets new boundaries for the model.
   * 
   * @param newBounds
   *          The new boundaries that should be set.
   */
  void setBounds(Rectangle newBounds);

  /**
   * Clears the grid so that it is empty afterwards.
   */
  void clear();

}
