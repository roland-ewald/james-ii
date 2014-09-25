/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.grid.object;

import org.jamesii.core.factories.Context;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;
import org.jamesii.core.plugins.annotations.Parameter;
import org.jamesii.core.plugins.annotations.Plugin;
import org.jamesii.model.carules.grid.ICARulesGrid;
import org.jamesii.model.carules.grid.plugintype.BaseGridFactory;

/**
 * A factory for creating ObjectGrid2D objects.
 * 
 * @author Jan Himmelspach
 */
@Plugin(
    description = "Factory for 2D object grids for CA models.",
    parameters = { @Parameter(
        name = BaseGridFactory.DEFAULTSTATE,
        type = Integer.class) })
public class ObjectGrid2DFactory extends BaseGridFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 4837726299994438354L;

  @Override
  public ICARulesGrid create(ParameterBlock params, Context context) {
    int[] size = ParameterBlocks.getSubBlockValue(params, SIZE);
    Integer defaultState =
        ParameterBlocks.getSubBlockValue(params, DEFAULTSTATE);
    return new Grid2D(size, defaultState);
  }

  @Override
  public int getDimension() {
    return 2;
  }

}
