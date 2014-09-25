/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules;

import org.jamesii.model.ca.ICell;
import org.jamesii.model.cacore.CAState;

/**
 * Interface for any cellular cell implementation that can be used.
 * 
 * @author Stefan Rybacki
 * @param <S>
 * @param <T>
 */
public interface ICACell<S extends CAState<Integer>> extends ICell<S, Integer> {

  /**
   * Get the state of the cell.
   * 
   * @return the state
   */
  @Override
  Integer getState();

  /**
   * Set the state of the cell.
   * 
   * @param state
   *          the state
   */
  @Override
  void setState(Integer state);

  /**
   * Clones this object and is mandatory to satisfy the {@link Cloneable}
   * interface.
   * 
   * @return the iCA cell
   */
  ICACell<S> clone() throws CloneNotSupportedException;
}
