/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.ca.grid;

import java.io.PrintStream;

import org.jamesii.core.model.IModel;
import org.jamesii.model.ca.grid.object.States;

/**
 * 
 * @author Jan Himmelspach
 * 
 */
public interface ICAGrid extends IModel, IGrid {

  /**
   * @return
   */
  int[] getDimensions();

  /**
   * @return
   */
  Object getGrid();

  /**
   * @param cell
   * @return
   */
  int[] getNeighbours(int[] cell);

  /**
   * 
   * @return
   */
  GridState getState();

  /**
   * @return
   */
  States getStates();

  void print();

  /**
   * 
   * @param stream
   */
  void print(PrintStream stream);

  /**
   * @return
   */
  double timeAdvance();

}
