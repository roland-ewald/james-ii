/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
/**
 * Title:        CoSA: Grid
 * Description:	 The cells computed by a cellular automata are arranged
 * 							 on a "grid". This is the base class for all possible cell arrangements.
 * Copyright:    Copyright (c) 2004
 * Company:      University of Rostock, Faculty of Computer Science
 *               Modeling and Simulation group
 * Created on 08.06.2004
 * @author       Jan Himmelspach
 * @version      1.0
 */
package org.jamesii.model.ca.grid;

import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.Arrays;

import org.jamesii.SimSystem;
import org.jamesii.core.model.Model;
import org.jamesii.model.ca.Cell;
import org.jamesii.model.ca.grid.object.States;
import org.jamesii.model.cacore.CAState;

public abstract class Grid extends Model implements ICAGrid {

  private static final long serialVersionUID = -1441240091817953922L;

  /**
   * @param onObject
   */
  @SuppressWarnings("unchecked")
  public static void print(Object onObject, PrintStream stream) {

    if (onObject instanceof Cell) {
      // if the object is a cell we'll simply have to compute the next
      // state
      // of it
      stream.print(((Cell<? extends CAState<?>, ?>) onObject).getCAState()
          .toString());
    } else {
      stream.println("");
      // if the object is not a cell it is an array
      for (int i = 0; i < Array.getLength(onObject); i++) {
        // here we "slice" the actual array into pieces and call the
        // doStep method for each array - piece
        // which may be either another array or an instance of a Cell
        // descendant class
        print(Array.get(onObject, i), stream);
      }
    }

  }

  private GridState state;

  public Grid() {
    super();
    state = new GridState(this);
  }

  /**
   * @param name
   */
  public Grid(String name) {
    super(name);
    state = new GridState(this);
  }

  /**
   * This method must be overwritten in descendant classes. A call to this
   * method (with valid coordinates) will return the cell at the given position
   * in the grid
   * 
   * @param coord
   *          integer array of coordinates (2,2) or (3,4,5) as examples
   * @return The cell at coord in the grid
   */
  @Override
  public abstract <T> Cell<? extends CAState<T>, T> getCell(int[] coord);

  /**
   * This method must be overwritten in descendant classes
   * 
   * @return The class type of the cells of this grid
   */
  public abstract Class<? extends Cell<?, ?>> getCellClass();

  /**
   * This method must be overwritten in descendant classes
   * 
   * @return an integer array containing the dimensions
   */
  @Override
  public abstract int[] getDimensions();

  /**
   * This method must be overwritten in descendant classes
   * 
   * @return the grid as object
   */
  @Override
  public abstract Object getGrid();

  @Override
  public abstract int[] getNeighbours(int[] cell);

  /**
   * Returns a new cell for the grid
   * 
   * @param coord
   */
  protected Cell<? extends CAState<?>, ?> getNewCell(int[] coord) {
    Cell<? extends CAState<?>, ?> cell = null;
    try {
      cell = getCellClass().newInstance();
      cell.setGrid(this);
      cell.setName(Arrays.toString(coord));
      cell.setCoordinates(coord);
      initCell(cell);
      cell.init();

    } catch (Exception re) {
      SimSystem.report(re);
    }
    return cell;
  }

  /**
   * 
   * @return
   */
  @Override
  public GridState getState() {
    return state;
  }

  /**
   * This method must be overwritten in descendant classes
   * 
   * @return a grid containing a copy of the states of all cells of this grid
   */
  @Override
  public abstract States getStates();

  /**
   * Can be used for initializing a newly created cell of a cellular automata
   * 
   * @param cell
   */
  public void initCell(Cell<? extends CAState<?>, ?> cell) {

  }

  @Override
  public void print() {
    print(System.out);
  }

  /**
   * 
   * @param stream
   */
  @Override
  public void print(PrintStream stream) {
    print(getGrid(), stream);
  }

  /**
   * This method can be overwritten in descendant classes if the "time" shall
   * not be increased constantly by one
   * 
   * @return the span to be added to the current time after which the follower
   *         state shall be computed
   */
  @Override
  public double timeAdvance() {
    return 1.0;
  }

}
