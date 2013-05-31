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
 * A factory for creating ExponentialDistribution objects.
 */
public class ExponentialDistributionFactory extends
    DistributionFactory<ExponentialDistribution> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1186000644612094604L;

  /** The mu parameter of the exponential distribution. Type: {@link Double}. */
  public static final String MU = "mu";

  @Override
  public ExponentialDistribution create(long seed) {
    return new ExponentialDistribution(seed);
  }

  @Override
  public ExponentialDistribution create(IRandom random) {
    return new ExponentialDistribution(random);
  }

  @Override
  public ExponentialDistribution create(ParameterBlock block, IRandom random) {
    if (!ParameterBlocks.hasSubBlock(block, MU)) {
      return create(random);
    }

    Double mu = ParameterBlocks.getSubBlockValue(block, MU);
    return new ExponentialDistribution(random, mu);
  }

}
