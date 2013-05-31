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
 * A factory for creating PoissonDistribution objects.
 */
public class PoissonDistributionFactory extends
    DistributionFactory<PoissonDistribution> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1186000644612094604L;

  @Override
  public PoissonDistribution create(long seed) {
    return new PoissonDistribution(seed);
  }

  @Override
  public PoissonDistribution create(IRandom random) {
    return new PoissonDistribution(random);
  }

  @Override
  public PoissonDistribution create(ParameterBlock block, IRandom random) {
    if (!ParameterBlocks.hasSubBlock(block, "lambda")) {
      return create(random);
    }

    Double lambda = ParameterBlocks.getSubBlockValue(block, "lambda");
    return new PoissonDistribution(random, lambda);
  }

}
