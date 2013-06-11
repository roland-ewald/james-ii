/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.distributions;

import org.jamesii.core.math.random.distributions.plugintype.DistributionFactory;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;

/**
 * A factory for creating BetaDistribution objects.
 */
public class BetaDistributionFactory extends
    DistributionFactory<BetaDistribution> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1736398275406609064L;

  @Override
  public BetaDistribution create(long seed) {
    return new BetaDistribution(seed);
  }

  @Override
  public BetaDistribution create(IRandom random) {
    return new BetaDistribution(random);
  }

  @Override
  public BetaDistribution create(ParameterBlock block, IRandom random) {
    if (!ParameterBlocks.hasSubBlock(block, "alpha")
        || !ParameterBlocks.hasSubBlock(block, "beta")) {
      return create(random);
    }
    Integer alpha = ParameterBlocks.getSubBlockValue(block, "alpha");
    Integer beta = ParameterBlocks.getSubBlockValue(block, "beta");
    return new BetaDistribution(random, alpha, beta);
  }

}
