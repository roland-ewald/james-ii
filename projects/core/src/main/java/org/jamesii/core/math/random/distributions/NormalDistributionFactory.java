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
 * A factory for creating NormalDistribution objects.
 */
public class NormalDistributionFactory extends
    DistributionFactory<NormalDistribution> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1186000644612094604L;

  /** The Constant MEAN. */
  public static final String MEAN = "mean";

  /** The Constant DEVIATION. */
  public static final String DEVIATION = "deviation";

  @Override
  public NormalDistribution create(long seed) {
    return new NormalDistribution(seed);
  }

  @Override
  public NormalDistribution create(IRandom random) {
    return new NormalDistribution(random);
  }

  @Override
  public NormalDistribution create(ParameterBlock block, IRandom random) {
    if (!ParameterBlocks.hasSubBlock(block, MEAN)
        || !ParameterBlocks.hasSubBlock(block, DEVIATION)) {
      return create(random);
    }
    Double mean = ParameterBlocks.getSubBlockValue(block, MEAN);
    Double deviation = ParameterBlocks.getSubBlockValue(block, DEVIATION);
    return new NormalDistribution(random, mean, deviation);
  }

}
