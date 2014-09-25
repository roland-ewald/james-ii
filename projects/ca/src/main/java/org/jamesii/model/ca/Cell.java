/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.ca;

import org.jamesii.core.model.InvalidModelException;
import org.jamesii.core.model.Model;
import org.jamesii.model.ca.grid.ICAGrid;
import org.jamesii.model.cacore.CAState;
import org.jamesii.model.cacore.INeighborStates;
import org.jamesii.model.cacore.neighborhood.INeighborhood;
import org.jamesii.model.cacore.neighborhood.NeumannNeighborhood;

/**
 * The Class Cell.A cellular automata works on cells arranged on a grid This
 * class is the abstract base class for all possible cellular automata cells.
 * 
 * @author Jan Himmelspach
 * 
 * @param <S>
 *          the generic type for the actual cell state
 * @param <T>
 */
public abstract class Cell<S extends CAState<T>, T> extends Model implements
    ICell<S, T> {

  private static final long serialVersionUID = -7262805274514356131L;

  /**
   * Store the position of the cell.
   */
  private int[] pos;

  /**
   * Will be set and initialized by the Grid this Cell belongs to
   */
  private int[] coordinates;

  /**
   * A reference to the grid this cell belongs to.
   */
  private ICAGrid grid = null;

  /**
   * The state of the cell. The state must be cloneable object.
   */
  protected S state;

  /** The neighborhood. */
  private INeighborhood neighborhood;

  public Cell() {
    super();
    state = createState();
  }

  /**
   * @param name
   */
  public Cell(String name) {
    super(name);
    state = createState();
  }

  /**
   * Creates the state.
   * 
   * @return the s
   */
  protected abstract S createState();

  /**
   * Gets the grid.
   * 
   * @return the grid
   */
  public final ICAGrid getGrid() {
    return grid;
  }

  /**
   * Gets the grid dimension.
   * 
   * @return the grid dimension
   */
  protected final int[] getGridDimension() {
    return grid.getDimensions();
  }

  /**
   * Gets the state.
   * 
   * @return the state
   */
  @Override
  public T getState() {
    return state.get();
  }

  /**
   * Next state. Remember that the returned state must be immutable to avoid
   * side effects with the simulator.
   * 
   * @param cellState
   *          TODO
   * @param neighborStates
   *          the neighbor states
   * 
   * @return the next state
   */
  public abstract T getNextState(T cellState, INeighborStates<T> neighborStates);

  /**
   * This method must be called after the cell has been created. And only then.
   * 
   * @param grid
   */
  public final void setGrid(ICAGrid grid) {
    if (this.grid != null) {
      throw new InvalidModelException(
          "setGrid was already called for this cell.");
    }
    this.grid = grid;
    int dim = grid.getDimensions().length;
    neighborhood = new NeumannNeighborhood(dim, null);
  }

  /**
   * Gets the neighborhood for the current cell in the current state. The
   * standard neighborhood would be Neummann, if you need a different
   * neighborhood override this method.
   * 
   * <p/>
   * Note that this method is supposed to be called for each cell in each
   * simulation step because this allows for a different neighborhood for each
   * cell in each step depending on whatever necessary, such as the current cell
   * state.
   * 
   * @return the neighborhood
   */
  public INeighborhood getNeighborhood() {
    return neighborhood;
  }

  /**
   * Sets the coordinates once.
   * 
   * @param coord
   *          the new coordinates
   */
  public void setCoordinates(int... coord) {
    if (coordinates != null) {
      throw new RuntimeException("coordinates already set.");
    }
    coordinates = coord.clone();
  }

  /**
   * Gets the coordinates.
   * 
   * @return the coordinates
   */
  public int[] getCoordinates() {
    return coordinates;
  }

  /**
   * Sets the state. Remember the newState must be immutable to avoid side
   * effects within the simulator.
   * 
   * @param newState
   *          the new state
   */
  @Override
  public void setState(T newState) {
    state.set(newState);
  }

  /**
   * Gets the CA state object.
   * 
   * @deprecated Don't use it if not necessary (it is only kept for
   *             compatibility with Grid.print and States classes and will be
   *             removed once they are refactored
   * 
   * @return the CA state object
   * 
   */
  @Deprecated
  public S getCAState() {
    return state;
  }

  /**
   * Specifies whether to use torus grid for given cell. The default return is
   * <code>false</code>. Override to change this.
   * 
   * @return true, if tours should be enabled for this cell
   */
  public boolean isTorus() {
    return false;
  }

  @Override
  public int[] getPosition() {
    return this.pos.clone();
  }

  @Override
  public void setPosition(int[] coord) {
    this.pos = coord.clone();
  }

}
