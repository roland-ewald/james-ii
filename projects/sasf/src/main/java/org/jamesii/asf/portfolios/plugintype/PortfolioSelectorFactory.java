/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios.plugintype;

import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Base class for all factories that create {@link IPortfolioSelector}
 * instances.
 * 
 * @author Roland Ewald
 * 
 */
public abstract class PortfolioSelectorFactory extends
    Factory<IPortfolioSelector> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -7774670292642860309L;

  /**
   * Creates a portfolio selector.
   * 
   * @param params
   *          the parameters to initialize the portfolio selector
   * 
   * @return the portfolio selector
   */
  @Override
  public abstract IPortfolioSelector create(ParameterBlock params);
}
