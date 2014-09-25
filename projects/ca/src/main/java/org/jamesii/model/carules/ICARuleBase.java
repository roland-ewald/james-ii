/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules;

import org.jamesii.model.cacore.INeighborStates;
import org.jamesii.model.cacore.neighborhood.INeighborhood;

/**
 * The Interface ICARuleBase.
 * 
 * @author Mathias Süß
 */
public interface ICARuleBase {

  /**
   * Gets the next state.
   * 
   * @param current
   *          the current
   * @param neighbors
   *          the neighbors
   * 
   * @return the next state
   */
  int getNextState(int current, INeighborStates<Integer> neighbors);

  /**
   * {@code true} if rules are of reactive nature
   * 
   * @return true, if rules have reactive nature
   */
  boolean isReactiveRules();

  /**
   * Gets the set neighborhood for the current state
   * 
   * @param current
   *          the current cell's state, this allows for state based
   *          neighborhoods
   * 
   * @return the neighborhood
   */
  INeighborhood getNeighborhood(int current);

  /**
   * Return true if torus wrapping is enabled for the current state otherwise
   * false.
   * 
   * @param current
   *          the state of the current cell, this allows for state depended
   *          torus rules
   * 
   * @return true, if checks if is torus
   */
  boolean isTorus(int current);

}
