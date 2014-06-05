/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.couplings;

import model.devscore.couplings.plugintype.BaseCouplingSetFactory;

/**
 * A factory for creating SimpleCouplingSet objects.
 */
public class SimpleCouplingSetFactory extends BaseCouplingSetFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1122671413949129695L;

  @Override
  public ICouplingSet createDirect() {
    return new SimpleCouplingSet();
  }

}
