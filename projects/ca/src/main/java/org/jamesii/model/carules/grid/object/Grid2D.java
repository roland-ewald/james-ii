/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.grid.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.model.InvalidModelException;
import org.jamesii.model.cacore.neighborhood.INeighborhood;
import org.jamesii.model.carules.CACell;
import org.jamesii.model.carules.ICACell;
import org.jamesii.model.carules.grid.AbstractGrid;
import org.jamesii.model.carules.grid.ICARulesGrid;
import org.jamesii.model.carules.grid.IGrid2D;

/**
 * A two-dimensional grid.
 * 
 * @author Mathias Süß
 */
public final class Grid2D extends AbstractGrid implements ICARulesGrid,
    IGrid2D, Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 7588508418267865882L;

  /**
   * ***************************************************************************
   * Variables
   * **************************************************************************.
   */

  /**
   * Store the state of each cell.
   */
  private ICACell[][] cells;

  /** Store the width of the 2D-grid. */
  private int width;

  /** store the height of the 2D-grid. */
  private int height;

  /** Indicate if the grid was initiated. */
  private boolean isInit = false;

  /**
   * The cached hash code.
   */
  private int hashCode;

  /**
   * ***************************************************************************
   * Constructors
   * **************************************************************************.
   */

  /**
   * Create an empty instance of a Grid2D. (If use don't forget to initiate the
   * grid if all parameters are set)
   */
  public Grid2D() {
    super();
  }

  /**
   * Create an instance of a Grid2D.
   * 
   * @param size
   *          An array that specifies the size of each dimension.
   * @param defaultState
   *          THe default state each cell will be set to.
   */
  public Grid2D(int[] size, int defaultState) {
    super(defaultState);
    this.width = size[0];
    this.height = size[1];

    this.initGrid();
  }

  /**
   * ***************************************************************************
   * public methods
   * **************************************************************************.
   */

  /**
   * Initialise the grid.
   */
  public void initGrid() {
    cells = new CACell[width][height];

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        cells[x][y] = new CACell(new int[] { x, y }, getDefaultState());
      }
    }
    this.isInit = true;
  }

  /**
   * Initialise the grid with the specified states. (Attention grid size must
   * set before!!)
   * 
   * @param initStates
   *          An array of CACell.
   */
  @Override
  public void initGrid(List<ICACell> initStates) {
    if (!isInit) {
      initGrid();
    }

    try {
      for (ICACell c : initStates) {

        this.cells[c.getPosition()[0]][c.getPosition()[1]] = c.clone();
      }
    } catch (CloneNotSupportedException e) {
      throw new InvalidModelException(e);
    }

  }

  /**
   * Calculate the neighbors of the specified cell.
   * 
   * @param coord
   *          The location of the cell.
   * 
   * @return A list of CACells.
   */
  @Override
  public List<int[]> getNeighbors(INeighborhood neighborhood, boolean torus,
      int... coord) {

    return getNeighbors(neighborhood, torus, coord[0], coord[1]);

    // neighbors.addAll(getNeumannNeighbors(x, y));
    //
    // if (neighborhoodType == Neighborhoods.MOORE) {
    // neighbors.addAll(getMooreNeighbors(x, y));
    // }

  }

  /**
   * Create a copy of this grid.
   * 
   * @return the grid 2d
   */
  @Override
  public Grid2D cloneGrid() {
    Grid2D ret = new Grid2D();

    ret.setDefaultState(this.getDefaultState());

    ret.setSize(new int[] { width, height });

    for (int x = 0; x < this.width; x++) {
      for (int y = 0; y < this.height; y++) {
        ret.setState(this.cells[x][y].getState(), x, y);
      }
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
    ArrayList<ICACell> ret = new ArrayList<>();

    for (int y = 0; y < this.height; y++) {
      for (int x = 0; x < this.width; x++) {
        ret.add(this.cells[x][y]);
      }
    }

    return ret;
  }

  /**
   * ***************************************************************************
   * private methods
   * **************************************************************************.
   * 
   * @param a
   *          the a
   * @param m
   *          the m
   * 
   * @return the int
   */

  /**
   * Calculates an always positive rest.
   * 
   * @param a
   * @param m
   * @return pos rest
   */
  private int pMod(int a, int m) {
    return a - (int) Math.floor((double) a / m) * m;
  }

  /**
   * Generic method to get the neighbors. Is aware of the torus flag. Is
   * independent from the type of neighborhood, is based on the neighbors list
   * which has to contain the relative coordinates of neighbors!
   * 
   * @param x
   *          coordinate of the cell the neighbors have to be determined for
   * @param y
   *          coordinate of the cell the neighbors have to be determined for
   * 
   * @return list of neighbors, not existing neighbors (non torus) are left out
   */
  public List<int[]> getNeighbors(INeighborhood neighborhood, boolean torus,
      int x, int y) {
    List<int[]> ret = new ArrayList<>(neighborhood.getCellCount());

    for (int i = 0; i < neighborhood.getCellCount(); i++) {
      int[] relPos = neighborhood.getCell(i);
      if (torus) {
        ret.add(this.cells[pMod(x + relPos[0], this.width)][pMod(y + relPos[1],
            this.height)].getPosition());
      } else {
        if (((x + relPos[0]) < 0) || (x + relPos[0] >= width)
            || ((y + relPos[1]) < 0) || (y + relPos[1] >= height)) {
          // ret.add(null); // neighbor does not exist
        } else {
          ret.add(this.cells[x + relPos[0]][y + relPos[1]].getPosition());
        }
      }
    }

    return ret;
  }

  /**
   * Return the state of the cell at the specified coordinate.
   * 
   * @param coord
   *          The coordinate of the cell of interest.
   * @return The state of the specified cell.
   */
  @Override
  public int getState(int... coord) {
    return this.cells[coord[0]][coord[1]].getState();
  }

  /**
   * Change the state of a cell.
   * 
   * @param state
   *          The new state the cell shall be set to.
   * @param coord
   *          The coordinate of the cell thats state shall be changed.
   */
  @Override
  public void setState(int state, int... coord) {
    this.cells[coord[0]][coord[1]].setState(state);
  }

  /**
   * Get the call at the specified location.
   * 
   * @param coord
   *          the coord
   * 
   * @return the cell
   */
  @Override
  public ICACell getCell(int... coord) {
    return this.cells[coord[0]][coord[1]];
  }

  /**
   * Get the size in each dimension of the grid.
   * 
   * @return The size in each dimension of the grid.
   */
  @Override
  public synchronized int[] getSize() {
    return new int[] { this.width, this.height };
  }

  /**
   * Set the size of each dimension of the grid.
   * 
   * @param size
   *          An array for the size of each dimension.
   */
  @Override
  public synchronized void setSize(int... size) {
    if ((size[0] == width) && (size[1] == height)) {
      return;
    }
    int oldW = width;
    int oldH = height;
    width = size[0];
    height = size[1];
    ICACell[][] c = cells;
    // forget everything in the grid and just make it new ...
    initGrid();
    // now let's copy what we can save ...
    for (int i = 0; i < Math.min(oldW, width); i++) {
      for (int j = 0; j < Math.min(oldH, height); j++) {
        cells[i][j] = c[i][j];
      }
    }
  }

  /**
   * Calculate hash code.
   */
  private synchronized void calculateHashCode() {
    hashCode = 7;

    hashCode = 31 * hashCode + 2; // dimension
    hashCode = 31 * hashCode + width; // width
    hashCode = 31 * hashCode + height; // width

    // now for all cells add state
    for (int j = 0; j < height; j++) {
      for (int i = 0; i < width; i++) {
        hashCode = 31 * hashCode + getState(new int[] { i, j }); // store cell
        // state in hash
      }
    }
  }

  @Override
  public synchronized int hashCode() {
    calculateHashCode();
    return hashCode;
  }
}