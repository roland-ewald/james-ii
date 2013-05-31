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
 * A factory for creating LaPlaceDistribution objects.
 */
public class LaPlaceDistributionFactory extends
    DistributionFactory<LaPlaceDistribution> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 6539275984986778946L;

  @Override
  public LaPlaceDistribution create(long seed) {
    return new LaPlaceDistribution(seed);
  }

  @Override
  public LaPlaceDistribution create(IRandom random) {
    return new LaPlaceDistribution(random);
  }

  @Override
  public LaPlaceDistribution create(ParameterBlock block, IRandom random) {
    if (!ParameterBlocks.hasSubBlock(block, "sigma")) {
      return create(random);
    }

    if (!ParameterBlocks.hasSubBlock(block, "mu")) {
      return create(random);
    }

    Double sigma = ParameterBlocks.getSubBlockValue(block, "sigma");
    Double mu = ParameterBlocks.getSubBlockValue(block, "mu");
    return new LaPlaceDistribution(random, sigma, mu);
  }

}
