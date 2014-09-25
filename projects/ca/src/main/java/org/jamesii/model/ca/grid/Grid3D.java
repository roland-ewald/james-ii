/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.ca.grid;

import org.jamesii.model.ca.Cell;
import org.jamesii.model.ca.grid.object.States;
import org.jamesii.model.ca.grid.object.States3D;
import org.jamesii.model.cacore.CAState;

/**
 * The Class Grid3D. Three dimensional grid
 * 
 * @author Jan Himmelspach
 */
public abstract class Grid3D extends Grid {

  private static final long serialVersionUID = 5423461823683657534L;

  /** The grid. */
  private Cell<?, ?>[][][] grid;

  /**
   * Instantiates a new grid3 d.
   */
  public Grid3D() {
    super();

  }

  /**
   * The Constructor.
   * 
   * @param name
   *          the name
   */
  public Grid3D(String name) {
    super(name);

  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> Cell<? extends CAState<T>, T> getCell(int[] coord) {
    return (Cell<? extends CAState<T>, T>) grid[coord[0]][coord[1]][coord[2]];
  }

  @Override
  public Object getGrid() {
    return grid;
  }

  @Override
  public final States getStates() {
    return new States3D(this);
  }

}
