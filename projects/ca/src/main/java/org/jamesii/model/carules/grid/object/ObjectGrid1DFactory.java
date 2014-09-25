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
 * A factory for creating ArrayGrid1D objects.
 * 
 * @author Jan Himmelspach
 */
@Plugin(
    description = "Factory for 1D object grids for CA models.",
    parameters = { @Parameter(
        name = BaseGridFactory.DEFAULTSTATE,
        type = Integer.class) })
public class ObjectGrid1DFactory extends BaseGridFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 4681703538699517797L;

  @Override
  public ICARulesGrid create(ParameterBlock params, Context context) {
    int[] size = ParameterBlocks.getSubBlockValue(params, SIZE);
    Integer defaultState =
        ParameterBlocks.getSubBlockValue(params, DEFAULTSTATE);
    return new Grid1D(size, defaultState);
  }

  @Override
  public int getDimension() {
    return 1;
  }

}
