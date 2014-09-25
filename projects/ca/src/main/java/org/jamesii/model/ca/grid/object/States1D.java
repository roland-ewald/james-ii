/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.ca.grid.object;

import org.jamesii.SimSystem;
import org.jamesii.core.model.InvalidModelException;
import org.jamesii.model.ca.grid.Grid1D;
import org.jamesii.model.ca.grid.ICAGrid;
import org.jamesii.model.cacore.CAState;

/**
 * The Class States1D.
 */
public class States1D extends States {

  /** The states. */
  private CAState[] states;

  /**
   * Instantiates a new states1 d.
   * 
   * @param grid
   *          the grid
   */
  public States1D(Grid1D grid) {
    super(grid);
  }

  @Override
  public CAState<?> getState(int[] coord) {
    return states[coord[0]];
  }

  @Override
  protected void init(ICAGrid grid) {
    states = new CAState[grid.getDimensions()[0]];
    int[] coord = new int[1];
    try {
      for (coord[0] = 0; coord[0] < states.length; coord[0]++) {
        states[coord[0]] =
            (CAState<?>) ((Grid1D) grid).getCell(coord).getCAState().clone();
      }
    } catch (CloneNotSupportedException e) {
      SimSystem.report(e);
      throw new InvalidModelException(
          "At least one of the cells uses a non cloneable state!", e);
    }

  }

}
