/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.grid;

import java.util.List;

import org.jamesii.model.ca.grid.IGrid;
import org.jamesii.model.cacore.INeighborStates;
import org.jamesii.model.cacore.neighborhood.INeighborhood;
import org.jamesii.model.carules.ICACell;

/**
 * @author Stefan Rybacki
 */
public interface ICARulesGrid extends IGrid, Cloneable {
  /**
   * Initialize the grid with the specified states. (Attention grid size must be
   * set before!!!)
   * 
   * @param initStates
   *          An array of objects where the object must be an integer.
   */
  void initGrid(List<ICACell> initStates);

  /**
   * Get a list of the coordinates of the neighbor cells.
   * 
   * @param neighborhood
   *          the neighborhood
   * @param torus
   *          flag indicating whether act as torus grid
   * @param coord
   *          The position of the cell from which to get the neighbors.
   * 
   * @return A list with the position of the neighbors.
   */
  List<int[]> getNeighbors(INeighborhood neighborhood, boolean torus,
      int... coord);

  /**
   * Create a copy of this grid.
   * 
   * @return the cloned grid
   */
  ICARulesGrid cloneGrid();

  /**
   * Get a list of all cells in this grid.
   * 
   * @return A list containing all cells of the grid.
   */
  List<ICACell> getCellList();

  /*****************************************************************************
   * Accessors
   ****************************************************************************/

  /**
   * Return the state of the cell at the specified position.
   * 
   * @param coord
   *          The coordinate of the cell of interest.
   * @return The state of the specified cell.
   */
  int getState(int... coord);

  /**
   * Change the state of a cell.
   * 
   * @param state
   *          The new state the cell shall be set to.
   * @param coord
   *          The coordinate of the cell that state shall be changed.
   */
  void setState(int state, int... coord);

  // /**
  // * Get the cell at the specified location.
  // *
  // * @param coord the coord
  // *
  // * @return the cell
  // */
  // @Override
  // ICACell getCell(int... coord);

  /**
   * Get the size in each dimension of the grid.
   * 
   * @return An array of the sizes of each dimension of the grid.
   */
  int[] getSize();

  /**
   * Set the size of each dimension of the grid.
   * 
   * @param size
   *          An array for the size of the each dimension.
   */
  void setSize(int... size);

  /**
   * Set the default state each cell will be set to on a pure initialization.
   * 
   * @return the default state
   */
  int getDefaultState();

  /**
   * Get the default state each cell will be set to on a pure initialization.
   * 
   * @param state
   *          the state
   */
  void setDefaultState(int state);

  /**
   * Gets the neighbor states.
   * 
   * @param neighborhood
   *          the neighborhood to use
   * @param torus
   *          flag indicating whether to use the grid as torus
   * @param coords
   *          the coords the neighborhood is requested for
   * @return the neighbor states
   */
  INeighborStates<Integer> getNeighborStates(INeighborhood neighborhood,
      boolean torus, int... coords);
}
