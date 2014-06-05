/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.couplings.plugintype;

import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;

import model.devscore.couplings.ICouplingSet;

/**
 * A factory for creating BaseCouplingSet objects. For each coupling set
 * implementation there has to be one factory sub classing this class.
 */
public abstract class BaseCouplingSetFactory extends Factory<ICouplingSet>  {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -1167767239433716052L;

  /**
   * Creates a new coupling set object.
   * 
   * @return the coupling set
   */
  public abstract ICouplingSet createDirect();

  @Override
  public ICouplingSet create(ParameterBlock parameters) {
    return createDirect();
  }
  
}
