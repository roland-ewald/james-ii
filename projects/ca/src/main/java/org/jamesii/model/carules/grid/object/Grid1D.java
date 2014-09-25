/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.grid.object;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.model.InvalidModelException;
import org.jamesii.model.cacore.neighborhood.INeighborhood;
import org.jamesii.model.carules.CACell;
import org.jamesii.model.carules.ICACell;
import org.jamesii.model.carules.grid.AbstractGrid;

/**
 * A one-dimensional grid.
 * 
 * @author Mathias Süß
 */
public final class Grid1D extends AbstractGrid {

  /**
   * ***************************************************************************
   * Variables
   * **************************************************************************.
   */

  /**
   * Store the state of each cell.
   */
  private ICACell[] cells;

  /** Store the width of the 1D-grid. */
  private int width;

  /** Store the default value of the cells. */
  private int defaultState;

  /** Indicate if the grid was initiated. */
  private boolean isInit = false;

  /**
   * The cached hash code.
   */
  private int hashCode = 0;

  /**
   * ***************************************************************************
   * Constructors
   * **************************************************************************.
   */

  /**
   * Create an empty instance of a Grid1D. (If use don't forget to initiate the
   * grid if all parameters are set)
   */
  public Grid1D() {
    this(new int[] { 10 }, 0);
  }

  /**
   * Create an instance of a 1D-grid with the specified size.
   * 
   * @param size
   *          The count of cells in the first dimension.
   * @param defaultState
   *          THe default state each cell will be set to.
   */
  public Grid1D(int[] size, int defaultState) {
    this.width = size[0];

    this.defaultState = defaultState;

    this.initGrid();
  }

  /**
   * ***************************************************************************
   * public methods
   * **************************************************************************.
   */

  /**
   * Initialise the grid with the default state.
   */
  public void initGrid() {
    this.cells = new CACell[this.width];

    for (int i = 0; i < this.width; i++) {
      this.cells[i] = new CACell(new int[] { i }, this.defaultState);
    }

    this.isInit = true;
    calculateHashCode();
  }

  /**
   * Initialise the grid with the specified states. (Attention grid size must be
   * set before!!!)
   * 
   * @param initStates
   *          An array of objects where the object must be an integer.
   */
  @Override
  public void initGrid(List<ICACell> initStates) {

    if (!this.isInit) {
      initGrid();
    }

    // Must be a one dimensional ArrayList so the containing objects are all
    // integers!!
    try {
      for (ICACell<?> c : initStates) {

        this.cells[c.getPosition()[0]] = c.clone();
      }
    } catch (CloneNotSupportedException e) {
      throw new InvalidModelException(e);
    }

    calculateHashCode();
  }

  /**
   * Get a list of the coordinates of the neighbor cells.
   * 
   * @param coord
   *          The position of the cell from which to get the neighbors.
   * 
   * @return A list with the position of the neighbors.
   */
  @Override
  public List<int[]> getNeighbors(INeighborhood neighborhood, boolean torus,
      int... coord) {

    ArrayList<int[]> neighbors =
        new ArrayList<>(neighborhood.getCellCount());

    for (int i = 0; i < neighborhood.getCellCount(); i++) {
      int[] c = neighborhood.getCell(i);
      int x = c[0] + coord[0];
      if (x < 0 && torus) {
        neighbors.add(cells[x + width].getPosition());
      }
      if (x >= width && torus) {
        neighbors.add(cells[x - width].getPosition());
      }
      if (x >= 0 && x < width) {
        neighbors.add(cells[x].getPosition());
      }
    }

    return neighbors;
  }

  /**
   * Create a copy of this grid.
   * 
   * @return the grid1 d
   */
  @Override
  public Grid1D cloneGrid() {
    Grid1D ret = new Grid1D();

    ret.setSize(new int[] { width });

    ret.defaultState = this.defaultState;

    for (int i = 0; i < this.width; i++) {
      ret.setState(this.cells[i].getState(), new int[] { i });
    }

    return ret;
  }

  /**
   * Get a list of all cells in this grid.
   * 
   * @return A list contains all cells of the grid.
   */
  @Override
  public List<ICACell> getCellList() {
    List<ICACell> ret = new ArrayList<>();

    for (int i = 0; i < this.width; i++) {
      ret.add(this.cells[i]);
    }

    return ret;
  }

  /**
   * Return the state of the cell at coordinate coord[0].
   * 
   * @param coord
   *          The coordinate of the cell of interest.
   * @return The state of the specified cell.
   */
  @Override
  public int getState(int... coord) {
    return this.cells[coord[0]].getState();
  }

  /**
   * Change the state of a cell.
   * 
   * @param state
   *          The new state the cell shall be set to.
   * @param coord
   *          The coordinate of the cell that state shall be changed.
   */
  @Override
  public void setState(int state, int... coord) {
    this.cells[coord[0]].setState(state);
    calculateHashCode();
  }

  /**
   * Get the cell at the specified location.
   * 
   * @param coord
   *          the coord
   * 
   * @return the cell
   */
  @Override
  public ICACell getCell(int... coord) {
    return this.cells[coord[0]];
  }

  /**
   * Get the size in each dimension of the grid.
   * 
   * @return The size of the first dimension of the grid.
   */
  @Override
  public int[] getSize() {
    return new int[] { this.width };
  }

  /**
   * Set the size of each dimension of the grid.
   * 
   * @param size
   *          An one-dimensional array for the size of the first dimension.
   */
  @Override
  public void setSize(int... size) {
    if (size[0] == width) {
      return;
    }
    int newWidth = size[0];
    // if the size is modified we need to create or delete the cells
    CACell[] c = new CACell[newWidth];
    if (newWidth < width) {
      System.arraycopy(cells, 0, c, 0, newWidth);
    }
    if (newWidth > width) {
      System.arraycopy(cells, 0, c, 0, width);
      for (int i = width; i < newWidth; i++) {
        c[i] = new CACell(new int[] { i }, this.defaultState);
      }

    }
    cells = c;
    this.width = newWidth;
    calculateHashCode();
  }

  /**
   * Calculate hash code.
   */
  private synchronized void calculateHashCode() {
    hashCode = 7;

    hashCode = 31 * hashCode + 1; // dimension
    hashCode = 31 * hashCode + width; // width

    // now for all cells add state
    for (int i = 0; i < width; i++) {
      hashCode = 31 * hashCode + getState(new int[] { i }); // store cell state
      // in hash
    }
  }

  @Override
  public synchronized int hashCode() {
    return hashCode;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    return obj.hashCode() == hashCode();
  }

}