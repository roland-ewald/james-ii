/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.ca.grid.object;

import org.jamesii.SimSystem;
import org.jamesii.core.model.InvalidModelException;
import org.jamesii.model.ca.grid.Grid3D;
import org.jamesii.model.ca.grid.ICAGrid;
import org.jamesii.model.cacore.CAState;

public class States3D extends States {

  private CAState[][][] states;

  public States3D(Grid3D grid) {
    super(grid);
  }

  @Override
  public CAState<?> getState(int[] coord) {
    return states[coord[0]][coord[1]][coord[2]];
  }

  @Override
  protected void init(ICAGrid grid) {
    states =
        new CAState[grid.getDimensions()[0]][grid.getDimensions()[1]][grid
            .getDimensions()[2]];
    int[] coord = new int[3];
    try {
      for (int x = 0; x < states.length; x++) {
        coord[0] = x;
        for (int y = 0; y < states[0].length; y++) {
          coord[1] = y;
          for (int z = 0; z < states[0][0].length; z++) {
            states[x][y][z] =
                (CAState<?>) ((Grid3D) grid).getCell(coord).getCAState()
                    .clone();
          }
        }
      }
    } catch (CloneNotSupportedException e) {
      SimSystem.report(e);
      throw new InvalidModelException(
          "At least one of the cells uses a non cloneable state!", e);
    }
  }

}
