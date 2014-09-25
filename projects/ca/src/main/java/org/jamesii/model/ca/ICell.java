/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.ca;

import java.io.Serializable;

import org.jamesii.core.model.IModel;
import org.jamesii.model.cacore.CAState;

public interface ICell<S extends CAState<T>, T> extends IModel, Cloneable,
    Serializable {

  /**
   * Set the state of the cell.
   * 
   * @param state
   *          the state
   */
  void setState(T state);

  /**
   * Get the state of the cell.
   * 
   * @return
   */
  T getState();

  /**
   * Get the position of the cell.
   * 
   * @return the position
   */
  int[] getPosition();

  /**
   * Set the position of the cell.
   * 
   * @param coord
   *          the coord
   */
  void setPosition(int[] coord);

  // /**
  // * @param neighboutStates
  // */
  // void nextState(States neighbourStates);

}
