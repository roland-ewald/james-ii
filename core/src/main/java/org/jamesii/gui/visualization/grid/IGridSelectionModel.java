/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.grid;

import java.awt.Rectangle;

/**
 * The Interface IGridSelectionModel.
 */
public interface IGridSelectionModel {

  /**
   * Clear selection.
   */
  void clearSelection();

  /**
   * Checks if is selected.
   * 
   * @param x
   *          the x
   * @param y
   *          the y
   * 
   * @return true, if is selected
   */
  boolean isSelected(int x, int y);

  /**
   * Checks if is selection empty.
   * 
   * @return true, if is selection empty
   */
  boolean isSelectionEmpty();

  /**
   * Gets the selection bounding box.
   * 
   * @return the selection bounding box
   */
  Rectangle getSelectionBoundingBox();

  /**
   * Adds the selected cell range.
   * 
   * @param x1
   *          the x1
   * @param y1
   *          the y1
   * @param x2
   *          the x2
   * @param y2
   *          the y2
   */
  void addSelectedCellRange(int x1, int y1, int x2, int y2);

  /**
   * Removes the selected cell range.
   * 
   * @param x1
   *          the x1
   * @param y1
   *          the y1
   * @param x2
   *          the x2
   * @param y2
   *          the y2
   */
  void removeSelectedCellRange(int x1, int y1, int x2, int y2);

  /**
   * Adds the grid selection listener.
   * 
   * @param l
   *          the l
   */
  void addGridSelectionListener(IGridSelectionListener l);

  /**
   * Removes the grid selection listener.
   * 
   * @param l
   *          the l
   */
  void removeGridSelectionListener(IGridSelectionListener l);

}
