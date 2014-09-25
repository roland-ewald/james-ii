/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.grid;

import org.jamesii.model.cacore.INeighborStates;
import org.jamesii.model.cacore.neighborhood.INeighborhood;

/**
 * @author Stefan Rybacki
 * 
 */
class NeighborStatesImpl implements INeighborStates<Integer> {

  private boolean torus;

  private int[] cellCoord;

  private int[] dim;

  private ICARulesGrid grid;

  private INeighborhood neighborhood;

  /**
   * Instantiates a new neighbor states impl.
   * 
   * @param grid
   *          the grid
   * @param neighborhood
   *          the neighborhood
   * @param torus
   *          the torus
   * @param coords
   *          the coords
   */
  public NeighborStatesImpl(ICARulesGrid grid, INeighborhood neighborhood,
      boolean torus, int[] coords) {
    this.torus = torus;
    this.cellCoord = coords;
    this.dim = grid.getSize();
    this.grid = grid;
    this.neighborhood = neighborhood;
  }

  @Override
  public Integer getState(int... coord) {
    coord = coord.clone();
    for (int i = 0; i < coord.length; i++) {
      coord[i] += cellCoord[i];
      // special treatment for torus
      if (torus && coord[i] < 0) {
        coord[i] += dim[i];
      }
      if (torus && coord[i] >= dim[i]) {
        coord[i] -= dim[i];
      }

      // return null if outside of grid
      if (coord[i] < 0 || coord[i] >= dim[i]) {
        return null;
      }
    }

    return grid.getState(coord);
  }

  @Override
  public int getCountOf(Integer state) {
    // TODO cache results for state so counting is only done once per state
    int result = 0;
    for (int i = 0; i < neighborhood.getCellCount(); i++) {
      Integer nState = getState(neighborhood.getCell(i));
      if (state.equals(nState)) {
        result++;
      }
    }

    return result;
  }

  @Override
  public int getCountOf(Integer state, INeighborhood in) {
    throw new RuntimeException("Not Supported");
  }

}
