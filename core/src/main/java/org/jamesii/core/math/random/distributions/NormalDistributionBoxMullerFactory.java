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
public class NormalDistributionBoxMullerFactory extends
    DistributionFactory<NormalDistributionBoxMuller> {

  /** Serial version ID. */
  private static final long serialVersionUID = 3323413202348359245L;

  /** The Constant MEAN. */
  public static final String MEAN = "mean";

  /** The Constant DEVIATION. */
  public static final String DEVIATION = "deviation";

  @Override
  public NormalDistributionBoxMuller create(long seed) {
    return new NormalDistributionBoxMuller(seed);
  }

  @Override
  public NormalDistributionBoxMuller create(IRandom random) {
    return new NormalDistributionBoxMuller(random);
  }

  @Override
  public NormalDistributionBoxMuller create(ParameterBlock block, IRandom random) {
    if (!ParameterBlocks.hasSubBlock(block, MEAN)
        || !ParameterBlocks.hasSubBlock(block, DEVIATION)) {
      return create(random);
    }
    Double mean = ParameterBlocks.getSubBlockValue(block, MEAN);
    Double deviation = ParameterBlocks.getSubBlockValue(block, DEVIATION);
    return new NormalDistributionBoxMuller(random, mean, deviation);
  }

}
