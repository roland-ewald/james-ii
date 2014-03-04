/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios.stochsearch;

import org.jamesii.SimSystem;
import org.jamesii.asf.portfolios.plugintype.IPortfolioSelector;
import org.jamesii.asf.portfolios.plugintype.PortfolioSelectorFactory;
import org.jamesii.core.math.random.generators.plugintype.AbstractRandomGeneratorFactory;
import org.jamesii.core.math.random.generators.plugintype.RandomGeneratorFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.ParameterUtils;


/**
 * Factory for the {@link StochSearchPortfolioSelector}.
 * 
 * @author Roland Ewald
 * 
 */
public class StochSearchPortfolioSelFactory extends PortfolioSelectorFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 3547627745752143139L;

  /**
   * Parameter to set the size of the random sample, default is 10000. Type:
   * {@link Integer}.
   */
  public static final String SAMPLE_SIZE = "sampleSize";

  /** The default sample size. */
  private static final int DEFAULT_SAMPLE_SIZE = 10000;

  @Override
  public IPortfolioSelector create(ParameterBlock params) {
    ParameterBlock rngParameters =
        ParameterUtils.getFactorySubBlock(params, RandomGeneratorFactory.class);
    RandomGeneratorFactory mbFac =
        SimSystem.getRegistry().getFactory(
            AbstractRandomGeneratorFactory.class, rngParameters);
    return new StochSearchPortfolioSelector(params.getSubBlockValue(
        SAMPLE_SIZE, DEFAULT_SAMPLE_SIZE), mbFac.create(rngParameters));
  }

}
