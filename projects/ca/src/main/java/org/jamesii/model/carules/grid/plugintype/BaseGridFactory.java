/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.grid.plugintype;

import org.jamesii.core.factories.Context;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.model.carules.grid.ICARulesGrid;

/**
 * A factory for creating grid objects.
 * 
 * @author Jan Himmelspach
 */
public abstract class BaseGridFactory extends Factory<ICARulesGrid> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 101463223067397117L;

  /**
   * The Constant SIZE.
   */
  public static final String SIZE = "size";

  /**
   * The Constant DEFAULTSTATE.
   */
  public static final String DEFAULTSTATE = "defaultState";
  public static final String NUMBER_OF_STATES = "numberOfStates";

  /**
   * Creates a new grid object.
 * @param params
   *          the parameters
 * @return the new grid object
   */
  @Override
  public abstract ICARulesGrid create(ParameterBlock params, Context context);

  /**
   * Gets the dimension of the grid which can be created by using this factory.
   * 
   * @return the dimension
   */
  public abstract int getDimension();

}
