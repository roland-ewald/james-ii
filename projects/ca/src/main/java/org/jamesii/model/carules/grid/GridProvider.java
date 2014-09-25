/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.grid;

import org.jamesii.SimSystem;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.model.carules.grid.plugintype.AbstractGridFactory;
import org.jamesii.model.carules.grid.plugintype.BaseGridFactory;

/**
 * Simple grid provider.
 *
 * @author Stefan Rybacki
 *
 */
public final class GridProvider {

  
  /**
   * Hidden constructor.
   */
  private GridProvider() {
  }

  /**
   * Creates a grid for the specified parameters.
   *
   * @param dimension the dimension of the grid to create (1D, 2D, 3D)
   * @param size the size for each dimension of the grid to create (the length
   * of the array must match the given dimension value)
   * @param defaultState the default state
   * @param numberOfStates the number of states the grid should be able to hold
   * (some grids might optimize internal representations according to that
   * value)
   * @return the i grid
   */
  public static ICARulesGrid createGrid(int dimension, int[] size,
          int defaultState, int numberOfStates) {
    ParameterBlock block = new ParameterBlock();
    block.addSubBlock(AbstractGridFactory.DIMENSION, dimension);

    BaseGridFactory factory
            = SimSystem.getRegistry().getFactory(AbstractGridFactory.class, block);

    if (factory == null) {
      return null;
    }

    block = new ParameterBlock();
    block.addSubBlock(BaseGridFactory.SIZE, size);
    block.addSubBlock(BaseGridFactory.DEFAULTSTATE, defaultState);
    block.addSubBlock(BaseGridFactory.NUMBER_OF_STATES, numberOfStates);

    return factory.create(block, SimSystem.getRegistry().createContext());
  }

  /**
   * Creates a grid for the specified parameters.
   *
   * @param factory the grid factory to use to create the grid
   * @param dimension the dimension of the grid to create (1D, 2D, 3D)
   * @param size the size for each dimension of the grid to create (the length
   * of the array must match the given dimension value)
   * @param defaultState the default state
   * @param numberOfStates the number of states the grid should be able to hold
   * (some grids might optimize internal representations according to that
   * value)
   * @return the i grid
   */
  public static ICARulesGrid createGrid(BaseGridFactory factory, int dimension,
          int[] size, int defaultState, int numberOfStates) {
    if (factory == null) {
      return null;
    }

    ParameterBlock block = new ParameterBlock();
    block.addSubBlock(BaseGridFactory.SIZE, size);
    block.addSubBlock(BaseGridFactory.DEFAULTSTATE, defaultState);
    block.addSubBlock(BaseGridFactory.NUMBER_OF_STATES, numberOfStates);

    return factory.create(block, SimSystem.getRegistry().createContext());
  }
}
