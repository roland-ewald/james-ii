/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.ca.observe;

import java.io.PrintStream;

import org.jamesii.core.observe.Observer;
import org.jamesii.model.ca.grid.GridState;

public class CAGridObserver extends Observer<GridState> {

  static final long serialVersionUID = -9045056787135025471L;

  /**
   * The out stream can be redirected, thus printing can be done on any print
   * stream
   */
  private transient PrintStream out = System.out;

  public CAGridObserver() {
    super();
  }

  /**
   * 
   * @param stream
   */
  public CAGridObserver(PrintStream stream) {
    super();
    out = stream;
  }

  @Override
  public void update(GridState entity) {
    entity.getOwner().print(out);
  }

}
