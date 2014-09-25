/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules;

import org.jamesii.core.model.Model;
import org.jamesii.model.ca.ICAModel;
import org.jamesii.model.carules.grid.GridProvider;
import org.jamesii.model.carules.grid.ICARulesGrid;
import org.jamesii.model.carules.grid.array.ArrayGrid1DFactory;
import org.jamesii.model.carules.grid.array.ArrayGrid2DFactory;
import org.jamesii.model.carules.grid.plugintype.BaseGridFactory;

import java.util.Collections;
import java.util.List;

/**
 * Abstract class for ca models handling grid creation
 * 
 * @author Stefan Rybacki
 * 
 */
public abstract class AbstractCAModel extends Model implements ICAModel {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -7213157807407813148L;

  /** The states. */
  private List<?> states;

  /** The dimensions. */
  private int dimensions;

  /** The grid. */
  private ICARulesGrid grid;

  /**
   * Instantiates a new abstract ca model.
   * 
   * @param dimensions
   *          the dimensions
   * @param states
   *          the states
   * @param initialGrid
   *          the initial grid
   * @param gridSize
   *          the grid size
   * @param factory
   *          the factory
   */
  public AbstractCAModel(int dimensions, List<?> states,
      List<ICACell> initialGrid, int[] gridSize, BaseGridFactory factory) {
    this(dimensions, states, createGrid(factory, gridSize, states, initialGrid));
  }

  /**
   * Instantiates a new abstract ca model.
   * 
   * @param dimensions
   *          the dimensions
   * @param states
   *          the states
   * @param initialGrid
   *          the initial grid
   * @param gridSize
   *          the grid size
   */
  public AbstractCAModel(int dimensions, List<?> states,
      List<ICACell> initialGrid, int[] gridSize) {
    this(dimensions, states, initialGrid, gridSize,
        getDefaultGridFactory(dimensions));
  }

  /**
   * Instantiates a new abstract ca model.
   * 
   * @param dimensions
   *          the dimensions
   * @param states
   *          the states
   * @param grid
   *          the grid
   */
  public AbstractCAModel(int dimensions, List<?> states,
      ICARulesGrid grid) {
    super();
    this.dimensions = dimensions;
    this.states = Collections.unmodifiableList(states);
    this.grid = grid;
  }

  /**
   * Creates the grid.
   * 
   * @param factory
   *          the factory
   * @param gridSize
   *          the grid size
   * @param states
   *          the states
   * @param initialGrid
   *          the initial grid
   * @return the i grid
   */
  private static ICARulesGrid createGrid(BaseGridFactory factory,
      int[] gridSize, List<?> states, List<ICACell> initialGrid) {
    ICARulesGrid result =
        GridProvider.createGrid(factory, gridSize.length, gridSize, 0,
            states.size());

    for (ICACell c : initialGrid) {
      result.setState(c.getState(), c.getPosition());
    }

    return result;
  }

  /**
   * Gets the grid parameter block for selecting the grid to be used via a
   * factory.
   * 
   * @param dim
   *          the dim
   * @return the grid parameter
   */
  private static BaseGridFactory getDefaultGridFactory(int dim) {
    switch (dim) {
    case 1:
      return new ArrayGrid1DFactory();
    case 2:
      return new ArrayGrid2DFactory();
    default:
      throw new UnsupportedOperationException(
          "Only 1d and 2d CA grids supported.");
    }
  }

  @Override
  public int getDimension() {
    return dimensions;
  }

  @Override
  public List<?> getStates() {
    return states;
  }

  @Override
  public ICARulesGrid getGrid() {
    return grid;
  }

  @Override
  public void cleanUp() {
    super.cleanUp();
    // grid = null;
    states = null;
  }
}
