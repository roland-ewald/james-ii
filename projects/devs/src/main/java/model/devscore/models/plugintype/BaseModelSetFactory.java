/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.models.plugintype;

import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;

import model.devscore.models.IModelSet;

/**
 * A factory for creating BaseModelSet objects.
 * 
 * @author Jan Himmelspach
 */
public abstract class BaseModelSetFactory extends Factory<IModelSet> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 101463223067397117L;

  /**
   * Creates a new BaseModelSet object.
   * 
   * @return the i model set
   */
  public abstract IModelSet createDirect();

  @Override
  public IModelSet create (ParameterBlock parameters) {
    return createDirect();
  }
  
}
