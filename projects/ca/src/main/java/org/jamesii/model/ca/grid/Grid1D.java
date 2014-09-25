/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
/**
 * Title:        CoSA: Grid1D
 * Description:  One dimensional grid
 * Copyright:    Copyright (c) 2004
 * Company:      University of Rostock, Faculty of Computer Science
 *               Modeling and Simulation group
 * Created on 09.06.2004
 * @author       Jan Himmelspach
 * @version      1.0
 */
package org.jamesii.model.ca.grid;

import org.jamesii.model.ca.Cell;
import org.jamesii.model.ca.grid.object.States;
import org.jamesii.model.ca.grid.object.States1D;
import org.jamesii.model.cacore.CAState;

public abstract class Grid1D extends Grid {

  private static final long serialVersionUID = -2300678203576733741L;

  private Cell<?, ?>[] grid;

  private int width = 10;

  public Grid1D() {
    super();

  }

  /**
   * @param name
   */
  public Grid1D(String name) {
    super(name);

  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> Cell<? extends CAState<T>, T> getCell(int[] coord) {
    return (Cell<? extends CAState<T>, T>) grid[coord[0]];
  }

  @Override
  public Object getGrid() {
    return grid;
  }

  @Override
  public final States getStates() {
    return new States1D(this);
  }

  @Override
  public final void init() {
    int[] dimens = getDimensions();

    grid = new Cell[dimens[0]];
    int[] d = new int[1];
    d[0] = dimens[0];

    initGrid(d, grid);

    getState().changed();
    getState().isChangedRR();
  }

  /**
   * Creates the cells for one dimension
   * 
   * @param dimens
   */
  private void initGrid(int[] dimens, Cell<?, ?>[] theGrid) {
    int[] name = new int[1];
    for (int x = 0; x < dimens[0]; x++) {
      name[0] = x;

      theGrid[x] = getNewCell(name);
    }
  }

  /**
   * @return the width
   */
  public int getWidth() {
    return width;
  }

  /**
   * @param width
   *          the width to set
   */
  public void setWidth(int width) {
    this.width = width;
  }

}
