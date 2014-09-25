/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.grid.object;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.model.InvalidModelException;
import org.jamesii.model.carules.CACell;
import org.jamesii.model.carules.ICACell;
import org.jamesii.model.carules.grid.AbstractGrid;

/**
 * A three-dimensional grid.
 * 
 * @author Mathias Süß
 */
public final class Grid3D extends AbstractGrid {

  /** Store the state of each cell. */
  private ICACell[][][] cells;

  /** Store the width of the 3D-grid. */
  private int width;

  /** store the height of the 3D-grid. */
  private int height;

  /** Store the depth of the 3D-grid. */
  private int depth;

  /** Indicate if the grid was initiated. */
  private boolean isInit = false;

  /**
   * The cached hash code.
   */
  private int hashCode;

  /**
   * *****************************************************************
   * ********** Constructors ******************************************
   * ********************************.
   */

  /**
   * Create an empty instance of a Grid3D. (If use don't forget to initiate the
   * grid if all parameters are set)
   */
  public Grid3D() {
  }

  /**
   * Create an instance of a Grid3D.
   * 
   * @param size
   *          An array that specifies the size of each dimension.
   * @param defaultState
   *          THe default state each cell will be set to.
   */
  public Grid3D(int[] size, int defaultState) {
    super(defaultState);
    this.width = size[0];
    this.height = size[1];
    this.depth = size[2];

    this.initGrid();
  }

  /**
   * *****************************************************************
   * ********** public methods ****************************************
   * **********************************.
   */

  /**
   * Initialise the grid.
   */
  public void initGrid() {
    this.cells = new CACell[this.width][this.height][this.depth];

    for (int z = 0; z < this.depth; z++) {
      for (int y = 0; y < this.height; y++) {
        for (int x = 0; x < this.width; x++) {
          this.cells[z][y][x] =
              new CACell(new int[] { x, y, z }, this.getDefaultState());
        }
      }
    }
    this.isInit = true;
  }

  /**
   * Initialise the grid with the specified states.S (Attention size of the grid
   * must be set before!!!)
   * 
   * @param initStates
   *          An array of CACell.
   */
  @Override
  public void initGrid(List<ICACell> initStates) {
    if (!this.isInit) {
      initGrid();
    }

    try {
      // start initiation
      for (ICACell c : initStates) {
        int x = c.getPosition()[0];
        int y = c.getPosition()[1];
        int z = c.getPosition()[2];
        this.cells[z][y][x] = c.clone();
      }
    } catch (CloneNotSupportedException e) {
      throw new InvalidModelException(e);
    }
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
    return this.cells[coord[2]][coord[1]][coord[0]].getState();
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
    this.cells[coord[2]][coord[1]][coord[0]].setState(state);
  }

  /**
   * Get the call at the specified location.
   * 
   * @param coord
   *          the coord
   * @return the cell
   */
  @Override
  public ICACell getCell(int... coord) {
    return this.cells[coord[2]][coord[1]][coord[0]];
  }

  /**
   * Create a copy of this grid.
   * 
   * @return the grid3 d
   */
  @Override
  public Grid3D cloneGrid() {
    Grid3D ret = new Grid3D();

    ret.width = this.width;
    ret.height = this.height;
    ret.depth = this.depth;

    ret.setDefaultState(getDefaultState());

    for (int z = 0; z < this.depth; z++) {
      for (int y = 0; y < this.height; y++) {
        for (int x = 0; x < this.width; x++) {
          ret.setState(this.cells[z][y][x].getState(), x, y, z);
        }
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

    for (int z = 0; z < this.depth; z++) {
      for (int y = 0; y < this.height; y++) {
        for (int x = 0; x < this.width; x++) {
          ret.add(this.cells[z][y][x]);
        }
      }
    }

    return ret;
  }

  /**
   * Get the size in each dimension of the grid.
   * 
   * @return the size
   */
  @Override
  public int[] getSize() {
    return new int[] { this.width, this.height, this.depth };
  }

  /**
   * Set the size of each dimension of the grid.
   * 
   * @param size
   *          the size
   */
  @Override
  public void setSize(int... size) {
    this.width = size[0];
    this.height = size[1];
    this.depth = size[2];
  }

  /**
   * Calculate hash code.
   */
  private synchronized void calculateHashCode() {
    hashCode = 7;

    hashCode = 31 * hashCode + 3; // dimension
    hashCode = 31 * hashCode + width; // width
    hashCode = 31 * hashCode + height; // width
    hashCode = 31 * hashCode + depth;

    // now for all cells add state
    for (int z = 0; z < depth; z++) {
      for (int j = 0; j < height; j++) {
        for (int i = 0; i < width; i++) {
          hashCode = 31 * hashCode + getState(new int[] { i, j, z }); // store
          // cell
          // state in hash
        }
      }
    }
  }

  @Override
  public synchronized int hashCode() {
    calculateHashCode();
    return hashCode;
  }
}