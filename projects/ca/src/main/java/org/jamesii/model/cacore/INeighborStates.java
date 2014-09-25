/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.cacore;

import org.jamesii.model.cacore.neighborhood.INeighborhood;

/**
 * Very simple interface providing access to neighboring cell states by using
 * relative addressing like (-1,-1) for the cell being to the left and above of
 * the current cell.
 * 
 * @author Stefan Rybacki
 * @param <T>
 *          the actual state value used by {@link CAState} derivates as generic
 *          parameter. Basically the T in {@link CAState} <b>&lt;T&gt;</b>
 * 
 */
public interface INeighborStates<T> {

  /**
   * Gets the state for the given coordinate. Where the coordinate must be
   * relative to the current cell. Which means e.g., in 2D the cell left is
   * addressed by (-1, 0) whereas the one the right is addressed by (1, 0) etc.
   * 
   * @param coord
   *          the relative coord
   * @return the state of the neighboring cell
   */
  T getState(int... coord);

  /**
   * This is a convenience method to get the count of cells in the neighborhood
   * having the specified state.
   * 
   * @param state
   *          the state to count
   * @return the number of neighboring cells being in the specified state
   */
  int getCountOf(T state);

  /**
   * This is a convenience method to get the count of cells in the neighborhood
   * having the specified state.
   * 
   * @param state
   *          the state to count
   * @param in
   *          the neighborhood to use
   * @return the number of neighboring cells being in the specified state
   */
  int getCountOf(T state, INeighborhood in);
}
