/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios.ga;

import org.jamesii.asf.portfolios.plugintype.IPortfolioSelector;
import org.jamesii.asf.portfolios.plugintype.PortfolioSelectorFactory;
import org.jamesii.core.factories.Context;
import org.jamesii.core.parameters.ParameterBlock;


/**
 * Factory for {@link GeneticAlgorithmPortfolioSelector}.
 * 
 * @author René Schulz, Roland Ewald
 * 
 */
public class GeneticAlgorithmPortfolioSelectorFactory extends
    PortfolioSelectorFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -4151620312951133884L;

  // TODO Make parameterizable: rng, abortion criterion, #individuals, mutation
  // rate, etc.

  @Override
  public IPortfolioSelector create(ParameterBlock params, Context context) {
    return new GeneticAlgorithmPortfolioSelector();
  }

}
