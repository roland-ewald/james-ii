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
 * A factory for creating CamelDistribution objects.
 */
public class CamelDistributionFactory extends
    DistributionFactory<CamelDistribution> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 5957115478101694577L;

  @Override
  public CamelDistribution create(long seed) {
    return new CamelDistribution(seed);
  }

  @Override
  public CamelDistribution create(IRandom random) {
    return new CamelDistribution(random);
  }

  @Override
  public CamelDistribution create(ParameterBlock block, IRandom random) {
    if (!ParameterBlocks.hasSubBlock(block, "humps")
        || !ParameterBlocks.hasSubBlock(block, "humpHitPercentage")
        || !ParameterBlocks.hasSubBlock(block, "humpDomain")) {
      return create(random);
    }
    Integer humps = ParameterBlocks.getSubBlockValue(block, "humps");
    Double humpHitPercentage =
        ParameterBlocks.getSubBlockValue(block, "humpHitPercentage");
    Double humpDomain = ParameterBlocks.getSubBlockValue(block, "humpDomain");
    return new CamelDistribution(random, humps, humpHitPercentage, humpDomain);
  }

}
