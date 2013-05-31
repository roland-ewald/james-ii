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

/**
 * A factory for creating UniformDistribution objects.
 */
public class UniformDistributionFactory extends
    DistributionFactory<UniformDistribution> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1186000644612094604L;

  /** The Constant PARAM_LOWER_BORDER. */
  public static final String PARAM_LOWER_BORDER = "lowerborder";

  /** The Constant PARAM_UPPER_BORDER. */
  public static final String PARAM_UPPER_BORDER = "upperborder";

  @Override
  public UniformDistribution create(long seed) {
    return new UniformDistribution(seed);
  }

  @Override
  public UniformDistribution create(IRandom random) {
    return new UniformDistribution(random);
  }

  @Override
  public UniformDistribution create(ParameterBlock block, IRandom random) {
    return new UniformDistribution(random, block.getSubBlockValue(
        PARAM_LOWER_BORDER, 0.0), block.getSubBlockValue(PARAM_LOWER_BORDER,
        1.0));
  }

}
