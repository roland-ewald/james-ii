/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.couplings;

import model.devscore.couplings.plugintype.BaseCouplingSetFactory;

/**
 * A factory for creating CouplingSet objects.
 */
public class CouplingSetFactory extends BaseCouplingSetFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -8875633142668796971L;

  @Override
  public ICouplingSet createDirect() {
    return new CouplingSet();
  }

}
