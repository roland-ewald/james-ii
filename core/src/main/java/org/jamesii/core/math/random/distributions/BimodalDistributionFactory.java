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
 * A factory for creating BimodalDistribution objects.
 */
public class BimodalDistributionFactory extends
    DistributionFactory<BimodalDistribution> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1186000644612094604L;

  @Override
  public BimodalDistribution create(long seed) {
    return new BimodalDistribution(seed);
  }

  @Override
  public BimodalDistribution create(IRandom random) {
    return new BimodalDistribution(random);
  }

  @Override
  public BimodalDistribution create(ParameterBlock block, IRandom random) {
    if (!ParameterBlocks.hasSubBlock(block, "muFirst")
        || !ParameterBlocks.hasSubBlock(block, "muSecond")
        || !ParameterBlocks.hasSubBlock(block, "percentageSecond")
        || !ParameterBlocks.hasSubBlock(block, "startOfSecond")) {
      return create(random);
    }
    Double muFirst = ParameterBlocks.getSubBlockValue(block, "muFirst");
    Double muSecond = ParameterBlocks.getSubBlockValue(block, "muSecond");
    Double percentageSecond =
        ParameterBlocks.getSubBlockValue(block, "percentageSecond");
    Double startOfSecond =
        ParameterBlocks.getSubBlockValue(block, "startOfSecond");
    return new BimodalDistribution(random, muFirst, muSecond, percentageSecond,
        startOfSecond);
  }

}
