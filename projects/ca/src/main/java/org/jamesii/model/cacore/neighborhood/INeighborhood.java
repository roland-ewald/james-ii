/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.cacore.neighborhood;

import java.io.Serializable;

import org.jamesii.core.model.symbolic.convert.ISymbolicModelElement;

/**
 * The Interface INeighborhood.
 * 
 * @author Stefan Rybacki
 */
public interface INeighborhood extends ISymbolicModelElement, Serializable,
    Iterable<int[]> {

  /**
   * Gets the dimensions the neighborhood was created for.
   * 
   * @return the dimensions
   */
  int getDimensions();

  /**
   * Gets the cell coordinates count.
   * 
   * @return the cell coordinates count
   */
  int getCellCount();

  /**
   * Gets the array of neighborhood states.
   * 
   * @param cellIndex
   *          the cell coordinates index
   * 
   * @return the cell coordinates
   */
  int[] getCell(int cellIndex);

  /**
   * Adds relative coordinates of a neighborhood cell. The specified array of
   * integers must contain exactly as much elements as dimensions the
   * neighborhood is created for.
   * 
   * @param cell
   *          the cell coordinates
   */
  void addCell(int... cell);

  /**
   * Helper method that can be used prior to {@link #addCell(int[])} to check
   * whether the cell coordinates are already in the neighborhood.
   * 
   * @param cell
   *          the cell coordiantes to check
   * @return {@code true} if cell coordinates are already in neighborhood
   */
  boolean containsCell(int... cell);

}
