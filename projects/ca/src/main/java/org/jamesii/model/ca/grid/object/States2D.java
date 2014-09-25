/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.ca.grid.object;

import org.jamesii.SimSystem;
import org.jamesii.core.model.InvalidModelException;
import org.jamesii.model.ca.grid.Grid2D;
import org.jamesii.model.ca.grid.ICAGrid;
import org.jamesii.model.cacore.CAState;

public class States2D extends States {

  private CAState[][] states;

  public States2D(Grid2D grid) {
    super(grid);
  }

  /**
   * @param coord
   * @return
   */
  @Override
  public CAState<?> getState(int[] coord) {
    return states[coord[0]][coord[1]];
  }

  /**
   * @param grid
   */
  @Override
  protected void init(ICAGrid grid) {
    states = new CAState[grid.getDimensions()[0]][grid.getDimensions()[1]];
    int[] coord = new int[2];
    try {

      for (int x = 0; x < states.length; x++) {
        coord[0] = x;
        for (int y = 0; y < states[0].length; y++) {
          coord[1] = y;
          // FIXME why not just storing the actual state rather cloning the
          // state
          // object (now it doubles the memory footprint)
          states[x][y] =
              (CAState<?>) ((Grid2D) grid).getCell(coord).getCAState().clone();
        }
      }
    } catch (CloneNotSupportedException e) {
      SimSystem.report(e);
      throw new InvalidModelException(
          "At least one of the cells uses a non cloneable state!", e);
    }
  }

}
