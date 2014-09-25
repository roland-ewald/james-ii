/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.ca.grid.object;

import org.jamesii.model.ca.grid.ICAGrid;
import org.jamesii.model.cacore.CAState;

public abstract class States {

  public States(ICAGrid grid) {
    super();
    init(grid);
  }

  /**
   * 
   * @param coord
   * @return
   */
  public abstract CAState<?> getState(int[] coord);

  /**
   * 
   * @param grid
   */
  protected abstract void init(ICAGrid grid);

}
