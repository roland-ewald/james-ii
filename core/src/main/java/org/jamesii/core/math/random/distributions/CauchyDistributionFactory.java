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
 * A factory for creating CauchyDistribution objects.
 */
public class CauchyDistributionFactory extends
    DistributionFactory<CauchyDistribution> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -258882584211059090L;

  @Override
  public CauchyDistribution create(long seed) {
    return new CauchyDistribution(seed);
  }

  @Override
  public CauchyDistribution create(IRandom random) {
    return new CauchyDistribution(random);
  }

  @Override
  public CauchyDistribution create(ParameterBlock block, IRandom random) {
    if (!ParameterBlocks.hasSubBlock(block, "t")) {
      return create(random);
    }

    if (!ParameterBlocks.hasSubBlock(block, "s")) {
      return create(random);
    }

    Double t = ParameterBlocks.getSubBlockValue(block, "t");
    Double s = ParameterBlocks.getSubBlockValue(block, "s");
    return new CauchyDistribution(random, t, s);
  }

}
