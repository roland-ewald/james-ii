/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.grid.bitPacked;

import org.jamesii.core.factories.Context;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;
import org.jamesii.core.plugins.annotations.Parameter;
import org.jamesii.core.plugins.annotations.Plugin;
import org.jamesii.model.carules.grid.ICARulesGrid;
import org.jamesii.model.carules.grid.plugintype.BaseGridFactory;

/**
 * A factory for creating ArrayGrid2D objects.
 * 
 * @author Jan Himmelspach
 */
@Plugin(
    description = "Factory for bit packed grids for CA models. Every Dimension.",
    parameters = {
        @Parameter(name = BaseGridFactory.DEFAULTSTATE, type = Integer.class),
        @Parameter(
            name = BaseGridFactory.NUMBER_OF_STATES,
            type = Integer.class) })
public class BitPackedGridFactory extends BaseGridFactory {

  /**
   * 
   */

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 3082968834651698869L;

  @Override
  public ICARulesGrid create(ParameterBlock params, Context context) {
    int[] size = ParameterBlocks.getSubBlockValue(params, SIZE);
    Integer defaultState =
        ParameterBlocks.getSubBlockValue(params, DEFAULTSTATE);
    Integer numberOfStates =
        ParameterBlocks.getSubBlockValue(params, NUMBER_OF_STATES);
    if (size.length == 2) {
      return new BitPackedGrid2D(size, defaultState, numberOfStates);
    }
    return new BitPackedGrid(size, defaultState, numberOfStates);
  }

  @Override
  public int getDimension() {
    return 2;
  }

}
