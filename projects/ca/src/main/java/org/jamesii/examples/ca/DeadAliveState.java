/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.examples.ca;

import org.jamesii.model.cacore.CAState;

/**
 * Moved stuff from CAState in here... this is the most basic state variable, a
 * bool (two posible states - dead or alive)
 * 
 * @author Roland Ewald
 * 
 *         28.02.2007
 * 
 */
public class DeadAliveState extends CAState<Boolean> {

  static final long serialVersionUID = -2480451533535534487L;

  public DeadAliveState() {
    super(false);
  }

  /**
   * @return
   */
  public boolean getAlive() {
    return get();
  }

  /**
   * @param alive
   */
  public void setAlive(boolean alive) {
    set(alive);
  }

  @Override
  public String toString() {
    return getAlive() ? "O" : " ";
  }

}
