/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.ca.grid;

import org.jamesii.core.model.State;
import org.jamesii.model.ca.grid.object.States;

public class GridState extends State {

  private static final long serialVersionUID = -832763828978345L;

  private final ICAGrid owner;

  /**
   * 
   * @param owner
   */
  public GridState(ICAGrid owner) {
    super();
    this.owner = owner;
  }

  /**
   * 
   * @return
   */
  public ICAGrid getOwner() {
    return owner;
  }

  /**
   * 
   * @return
   */
  public States getStates() {
    return owner.getStates();
  }

}
