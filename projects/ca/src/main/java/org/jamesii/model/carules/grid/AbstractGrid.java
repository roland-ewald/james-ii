/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.grid;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.model.cacore.INeighborStates;
import org.jamesii.model.cacore.neighborhood.INeighborhood;

/**
 * The Class AbstractGrid.
 * 
 * @author Jan Himmelspach
 * @author Stefan Rybacki
 */
public abstract class AbstractGrid implements ICARulesGrid, Cloneable {

  /** Store the default value of the cells. */
  private int defaultState;

  /**
   * Instantiates a new abstract grid.
   */
  public AbstractGrid() {
    super();
  }

  /**
   * Instantiates a new abstract grid.
   */
  public AbstractGrid(int defaultState) {
    super();
    this.defaultState = defaultState;
  }

  @Override
  public abstract ICARulesGrid cloneGrid();

  @Override
  public final void setDefaultState(int state) {
    this.defaultState = state;
  }

  @Override
  public final int getDefaultState() {
    return this.defaultState;
  }

  @Override
  public abstract int hashCode();

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof ICARulesGrid)) {
      return false;
    }
    return obj.hashCode() == hashCode();
  }

  @Override
  public List<int[]> getNeighbors(INeighborhood neighborhood, boolean torus,
      int... coord) {
    List<int[]> result = new ArrayList<>(neighborhood.getCellCount());

    // dimensions of the grid
    int[] size = getSize();

    // for each neighborhood cell
    for (int i = 0; i < neighborhood.getCellCount(); i++) {
      // position to add to result if inside grid boundaries
      int[] pos = new int[neighborhood.getDimensions()];
      // the relative position of neighbor cell
      int[] c = neighborhood.getCell(i);

      boolean valid = true;
      for (int j = 0; j < neighborhood.getDimensions(); j++) {
        pos[j] = coord[j] + c[j];

        // check for grid boundaries
        if (!torus && pos[j] < 0 || pos[j] >= size[j]) {
          valid = false;
          break;
        }

        // correct position if torus
        if (torus && pos[j] < 0) {
          pos[j] += size[j];
        }

        if (torus && pos[j] >= size[j]) {
          pos[j] -= size[j];
        }
      }

      if (valid) {
        result.add(pos);
      }
    }

    return result;
  }

  @Override
  public INeighborStates<Integer> getNeighborStates(INeighborhood neighborhood,
      boolean torus, int... coords) {
    return new NeighborStatesImpl(this, neighborhood, torus, coords);
  }
}
