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
 * A factory for creating GammaDistribution objects.
 */
public class GammaDistributionFactory extends
    DistributionFactory<GammaDistribution> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1736398275406609064L;

  @Override
  public GammaDistribution create(long seed) {
    return new GammaDistribution(seed);
  }

  @Override
  public GammaDistribution create(IRandom random) {
    return new GammaDistribution(random);
  }

  @Override
  public GammaDistribution create(ParameterBlock block, IRandom random) {
    if (!ParameterBlocks.hasSubBlock(block, "alpha")) {
      return create(random);
    }
    Integer alpha = ParameterBlocks.getSubBlockValue(block, "alpha");
    return new GammaDistribution(random, alpha);
  }

}
