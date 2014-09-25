/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.ca.grid;

import org.jamesii.model.ca.ICell;
import org.jamesii.model.cacore.CAState;

/**
 * Basic Grid Interface for any type of Cellular Automata
 * 
 * @author Fiete Haack
 */
public interface IGrid {

  /**
   * @return
   */
  <T> ICell<? extends CAState<T>, T> getCell(int[] coord);

}
