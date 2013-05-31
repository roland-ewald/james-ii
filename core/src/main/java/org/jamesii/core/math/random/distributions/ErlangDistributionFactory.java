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
 * A factory for creating ErlangDistribution objects.
 */
public class ErlangDistributionFactory extends
    DistributionFactory<ErlangDistribution> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 643702785741705369L;

  @Override
  public ErlangDistribution create(long seed) {
    return new ErlangDistribution(seed);
  }

  @Override
  public ErlangDistribution create(IRandom random) {
    return new ErlangDistribution(random);
  }

  @Override
  public ErlangDistribution create(ParameterBlock block, IRandom random) {
    if (!ParameterBlocks.hasSubBlock(block, "lambda")
        || !ParameterBlocks.hasSubBlock(block, "degreeOfFreedom")) {
      return create(random);
    }
    Double lambda = ParameterBlocks.getSubBlockValue(block, "lambda");
    Integer degOfFreedom =
        ParameterBlocks.getSubBlockValue(block, "degreeOfFreedom");
    return new ErlangDistribution(random, lambda, degOfFreedom);
  }
}
