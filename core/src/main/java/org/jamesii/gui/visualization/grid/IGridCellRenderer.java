/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.grid;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

/**
 * Interface for grid cell renderers. As grid cells may contain different types
 * of data for various models/formalisms there is probably no single correct way
 * to display them, hence the need for this.
 * 
 * @author Johannes Rössel
 */
public interface IGridCellRenderer {

  /**
   * Draws the specified cell at the given location and size.
   * 
   * @param sender
   *          The grid on which will be drawn.
   * @param g
   *          The {@link Graphics} context to draw on.
   * @param x
   *          The X coordinate of the drawing region.
   * @param y
   *          The Y coordinate of the drawing region.
   * @param width
   *          The width of the drawing region.
   * @param height
   *          The height of the drawing region.
   * @param shape
   *          the shape at position (x,y) representing the grid (e.g., a
   *          {@link Rectangle}
   * @param cellX
   *          The X coordinate of the cell in the model.
   * @param cellY
   *          The Y coordinate of the cell in the model.
   * @param value
   *          The value of the cell.
   * @param isSelected
   *          flag indicating whether the cell to render is selected
   * @param hasFocus
   *          flag indicating whether the cell to render has focus
   */
  void draw(Grid2D sender, Graphics g, int x, int y, int width, int height,
      Shape shape, int cellX, int cellY, Object value, boolean isSelected, boolean hasFocus);

  /**
   * Adds a listener that gets notified whenever the rendering of values changes
   * for this IGridCellRenderer.
   * 
   * @param l
   *          The listener to add.
   */
  void addRenderingChangedListener(IRenderingChangedListener l);

  /**
   * Removes a previously added {@link IRenderingChangedListener}.
   * 
   * @param l
   *          The listener to remove.
   */
  void removeRenderingChangedListener(IRenderingChangedListener l);
}
