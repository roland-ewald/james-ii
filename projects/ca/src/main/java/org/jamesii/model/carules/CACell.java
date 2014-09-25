/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules;

import org.jamesii.core.model.Model;
import org.jamesii.model.cacore.CAState;

/**
 * The Class CACell. This class is used to represent each cell in the grid of
 * the cellular automata. Each instance should only be used for one cell, and a
 * cell should belong to one grid at most.
 * 
 * @author Mathias Süß
 * @author Jan Himmelspach
 * @param <S>
 */
public final class CACell<S extends CAState<Integer>> extends Model implements
    ICACell<S> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -3853562547869237707L;

  /**
   * ***************************************************************************
   * Variables
   * **************************************************************************.
   */

  /**
   * Store the position of the cell.
   */
  private int[] pos;

  /** Store the state of the cell. */
  private int state;

  /**
   * ***************************************************************************
   * Constructors
   * **************************************************************************.
   */

  /**
   * Create an instance of an empty cell.
   */
  public CACell() {
  }

  /**
   * Create a cell with it's position and state.
   * 
   * @param coord
   *          The position of the cell in the grid.
   * @param state
   *          The state of the cell.
   */
  public CACell(int[] coord, int state) {
    this.pos = coord.clone();
    this.state = state;
  }

  @Override
  public CACell clone() {
    return new CACell(this.pos, this.state);
  }

  @Override
  public int[] getPosition() {
    return this.pos.clone();
  }

  @Override
  public void setPosition(int[] coord) {
    this.pos = coord.clone();
  }

  @Override
  public Integer getState() {
    return this.state;
  }

  @Override
  public void setState(Integer state) {
    this.state = state;
  }
}
