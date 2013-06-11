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
 * A factory for creating InverseGaussianDistribution objects.
 */
public class InverseGaussianDistributionFactory extends
    DistributionFactory<InverseGaussianDistribution> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1186000644612094604L;

  /** The Constant MEAN. */
  public static final String MEAN = "mean";

  /** The Constant DEVIATION. */
  public static final String LAMBDA = "lambda";

  @Override
  public InverseGaussianDistribution create(long seed) {
    return new InverseGaussianDistribution(seed);
  }

  @Override
  public InverseGaussianDistribution create(IRandom random) {
    return new InverseGaussianDistribution(random);
  }

  @Override
  public InverseGaussianDistribution create(ParameterBlock block, IRandom random) {
    if (!ParameterBlocks.hasSubBlock(block, MEAN)
        || !ParameterBlocks.hasSubBlock(block, LAMBDA)) {
      return create(random);
    }
    Double mean = ParameterBlocks.getSubBlockValue(block, MEAN);
    Double lambda = ParameterBlocks.getSubBlockValue(block, LAMBDA);
    return new InverseGaussianDistribution(random, mean, lambda);
  }

}
