/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.ca.grid;

import org.jamesii.model.ca.Cell;
import org.jamesii.model.ca.grid.object.States;
import org.jamesii.model.ca.grid.object.States2D;
import org.jamesii.model.cacore.CAState;

/**
 * Title: CoSA: Grid2D
 * 
 * Description: Two dimensional grid
 * 
 * Copyright: Copyright (c) 2004
 * 
 * Company: University of Rostock, Faculty of Computer Science Modeling and
 * Simulation group
 * 
 * Created on 09.06.2004
 * 
 * @author Jan Himmelspach
 * @version 1.0
 */
public abstract class Grid2D extends Grid {

  /** Serialisation ID. */
  private static final long serialVersionUID = -3803582975206186383L;

  /** The grid. */
  private Cell<?, ?>[][] grid;

  /**
   * Instantiates a new grid2 d.
   */
  public Grid2D() {
    super();

  }

  /**
   * The Constructor.
   * 
   * @param name
   *          the name
   */
  public Grid2D(String name) {
    super(name);

  }

  /**
   * Gets the cell.
   * 
   * @param coord
   *          the coord
   * 
   * @return the cell
   */
  @SuppressWarnings("unchecked")
  @Override
  public <T> Cell<? extends CAState<T>, T> getCell(int[] coord) {
    return (Cell<? extends CAState<T>, T>) grid[coord[0]][coord[1]];
  }

  @Override
  public Object getGrid() {
    return grid;
  }

  @Override
  public final States getStates() {
    return new States2D(this);
  }

  @Override
  public final void init() {
    int[] dimens = getDimensions();

    // grid = Array.newInstance(Cell.class, dimens);
    grid = new Cell[dimens[0]][dimens[1]];

    initGrid(dimens, grid);

    getState().changed();
    getState().isChangedRR();
  }

  /**
   * Creates the cells for one dimension.
   * 
   * @param dimens
   *          the dimens
   * @param theGrid
   *          the the grid
   */
  private void initGrid(int[] dimens, Cell<?, ?>[][] theGrid) {
    int[] name = new int[2];
    for (int x = 0; x < dimens[0]; x++) {
      name[0] = x;
      for (int y = 0; y < dimens[1]; y++) {
        name[1] = y;
        theGrid[x][y] = getNewCell(name);
      }
    }
  }

  // public int[][] getNeighorsOf (int[] coord) {
  // int[][] result = new
  // }

}
