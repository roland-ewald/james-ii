/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.grid.array;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jamesii.model.cacore.neighborhood.INeighborhood;
import org.jamesii.model.carules.CACell;
import org.jamesii.model.carules.ICACell;
import org.jamesii.model.carules.grid.AbstractGrid;
import org.jamesii.model.carules.grid.ICARulesGrid;
import org.jamesii.model.carules.grid.IGrid2D;

/**
 * The Class Grid2D.
 * 
 * @author Jan Himmelspach
 */
public final class Grid2D extends AbstractGrid implements IGrid2D {

  /** The data. */
  private int[] data;

  /** The coords. */
  private int[][][] coords;

  /** The size. */
  private int[] size;

  /**
   * The cached hash code.
   */
  private int hashCode;

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

    setSize(size);

    // this.initGrid();
  }

  /**
   * Instantiates a new grid2 d.
   * 
   * @param defaultState
   *          the default state
   */
  private Grid2D(int defaultState) {
    super(defaultState);
  }

  /**
   * Instantiates a new grid2 d.
   * 
   * @param size
   *          the size
   * @param defaultState
   *          the default state
   * @param data
   *          the data
   */
  public Grid2D(int[] size, int defaultState, int[] data) {
    super(defaultState);

    this.data = data.clone();

    this.size = size.clone();

    coords = new int[size[0]][size[1]][2];

    for (int i = 0; i < size[0]; i++) {
      for (int j = 0; j < size[1]; j++) {
        coords[i][j] = new int[] { i, j };

      }
    }
  }

  @Override
  public ICARulesGrid cloneGrid() {
    int[] resData = Arrays.copyOf(data, data.length);

    // Grid2D result = new Grid2D(size,defaultState, resData);

    Grid2D result = new Grid2D(getDefaultState());

    result.size = size;
    result.data = resData;
    result.coords = coords;

    return result;
  }

  @Override
  public ICACell getCell(int... coord) {
    return new CACell(coords[coord[0]][coord[1]], data[(coord[0])
        + (coord[1] * size[0])]);
  }

  @Override
  public List<ICACell> getCellList() {
    List<ICACell> res = new ArrayList<>();
    for (int i = 0; i < size[0]; i++) {
      for (int j = 0; j < size[1]; j++) {
        res.add(new CACell(coords[i][j], data[i + (j * size[0])]));
      }
    }

    return res;
  }

  @Override
  public List<int[]> getNeighbors(INeighborhood neighborhood, boolean torus,
      int... coord) {
    return getNeighbors(neighborhood, torus, coord[0], coord[1]);
  }

  /**
   * Gets the neighbors.
   * 
   * @param neighborhood
   *          the neighborhood
   * @param torus
   *          flag indicating whether act as torus grid
   * @param x
   *          the x
   * @param y
   *          the y
   * @return the neighbors
   */
  public List<int[]> getNeighbors(INeighborhood neighborhood, boolean torus,
      int x, int y) {
    ArrayList<int[]> ret = new ArrayList<>(neighborhood.getCellCount());

    for (int i = 0; i < neighborhood.getCellCount(); i++) {
      int[] relPos = neighborhood.getCell(i);
      if (torus) {
        ret.add(coords[pMod(x + relPos[0], size[0])][pMod(y + relPos[1],
            size[1])]);
      } else {
        if (((x + relPos[0]) < 0) || (x + relPos[0] >= size[0])
            || ((y + relPos[1]) < 0) || (y + relPos[1] >= size[1])) {
          // ret.add(null); // neighbor does not exist
        } else {
          ret.add(this.coords[x + relPos[0]][y + relPos[1]]);
        }
      }
    }

    return ret;
  }

  /**
   * Calculates an always positive rest.
   * 
   * @param a
   *          the a
   * @param m
   *          if negative absolute value will be used
   * 
   * @return positive rest
   */
  private int pMod(int a, int m) {
    int absM = Math.abs(m);
    int rest = a - (int) Math.floor((double) a / absM) * absM;
    return (rest < 0 ? absM + rest : rest);
  }

  @Override
  public synchronized int[] getSize() {

    return size.clone();
  }

  @Override
  public int getState(int... coord) {

    return data[(coord[0]) + (coord[1] * size[0])];
  }

  @Override
  public void initGrid(List<ICACell> initStates) {
    // TODO Auto-generated method stub

  }

  @Override
  public synchronized void setSize(int... size) {
    // backup
    int[] oldSize = this.size;
    int[] oldData = data;

    if (oldSize == null) {
      oldSize = new int[] { 0, 0 };
    }

    this.size = size.clone();

    data = new int[(size[0]) * (size[1])];

    Arrays.fill(data, getDefaultState());

    // for (int i = 0; i < (size[0])*(size[1]); i++) {
    // data[i] = defaultState;
    // }

    coords = new int[size[0]][size[1]][2];

    for (int i = 0; i < size[0]; i++) {
      for (int j = 0; j < size[1]; j++) {
        coords[i][j] = new int[] { i, j };
      }
    }

    // now let's copy what we can save ...
    for (int i = 0; i < Math.min(oldSize[0], size[0]); i++) {
      for (int j = 0; j < Math.min(oldSize[1], size[1]); j++) {
        data[i + j * size[0]] = oldData[i + j * oldSize[0]];
      }
    }

  }

  @Override
  public void setState(int state, int... coord) {
    data[(coord[0]) + (coord[1] * size[0])] = state;
  }

  /**
   * Calculate hash code.
   */
  private synchronized void calculateHashCode() {
    hashCode = 7;

    hashCode = 31 * hashCode + 2; // dimension
    hashCode = 31 * hashCode + size[0]; // width
    hashCode = 31 * hashCode + size[1]; // width

    // now for all cells add state
    for (int j = 0; j < size[1]; j++) {
      for (int i = 0; i < size[0]; i++) {
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
