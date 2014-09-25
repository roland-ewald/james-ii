/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.ca;

import java.util.List;

import org.jamesii.core.model.IModel;

/**
 * Interface for any cellular automata implementation.
 * 
 * @author Stefan Rybacki
 * 
 */
public interface ICAModel extends IModel {

  /**
   * Gets the dimension.
   * 
   * @return the dimension
   */
  int getDimension();

  /**
   * Gets the states.
   * 
   * @return the states
   */
  List<?> getStates();

  /**
   * Gets the grid.
   * 
   * @return the grid
   */
  org.jamesii.model.ca.grid.IGrid getGrid();

}
