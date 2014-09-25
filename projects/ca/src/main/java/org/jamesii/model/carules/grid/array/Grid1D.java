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

/**
 * The Class Grid1D.
 * 
 * @author Jan Himmelspach
 */
public final class Grid1D extends AbstractGrid {

  /** The data. */
  private int[] data;

  /** The data cells. */
  private int[][] coords;

  /** The width. */
  private int width = 0;

  /**
   * The cached hash code.
   */
  private int hashCode;

  /**
   * Instantiates a new grid1 d.
   */
  private Grid1D() {
    super();
  }

  /**
   * Instantiates a new grid1 d.
   * 
   * @param size
   *          the size
   * @param defaultState
   *          the default state
   */
  public Grid1D(int[] size, int defaultState) {
    super(defaultState);
    setSize(size);
  }

  @Override
  public ICARulesGrid cloneGrid() {
    int[] resData = Arrays.copyOf(data, data.length);

    Grid1D result = new Grid1D();

    result.width = width;
    result.data = resData;
    result.coords = coords;
    return result;
  }

  @Override
  public ICACell getCell(int... coord) {
    return new CACell(coords[coord[0]], data[coord[0]]);
  }

  @Override
  public List<ICACell> getCellList() {
    List<ICACell> res = new ArrayList<>();
    for (int i = 0; i < width; i++) {
      res.add(new CACell(coords[i], data[i]));
    }
    return res;
  }

  @Override
  public List<int[]> getNeighbors(INeighborhood neighborhood, boolean torus,
      int... coord) {

    ArrayList<int[]> neighbors =
        new ArrayList<>(neighborhood.getCellCount());

    for (int i = 0; i < neighborhood.getCellCount(); i++) {
      int[] c = neighborhood.getCell(i);
      int x = c[0] + coord[0];
      if (x < 0 && torus) {
        neighbors.add(coords[x + width]);
      }
      if (x >= width && torus) {
        neighbors.add(coords[x - width]);
      }
      if (x >= 0 && x < width) {
        neighbors.add(coords[x]);
      }
    }

    return neighbors;
  }

  @Override
  public int[] getSize() {
    return new int[] { width };
  }

  @Override
  public int getState(int... coord) {
    return data[coord[0]];
  }

  @Override
  public void initGrid(List<ICACell> initStates) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setSize(int... size) {
    width = size[0];
    coords = new int[width][];
    for (int i = 0; i < width; i++) {
      coords[i] = new int[] { i };
    }
    data = new int[width];
    Arrays.fill(data, getDefaultState());
  }

  @Override
  public void setState(int state, int... coord) {
    data[coord[0]] = state;
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
      hashCode = 31 * hashCode + getState(new int[] { i }); // store cell
      // state in hash
    }
  }

  @Override
  public synchronized int hashCode() {
    calculateHashCode();
    return hashCode;
  }

}
