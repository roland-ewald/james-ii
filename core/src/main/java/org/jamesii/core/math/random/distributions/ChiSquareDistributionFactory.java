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

// TODO: Auto-generated Javadoc
/**
 * A factory for creating ChiSquareDistribution objects.
 */
public class ChiSquareDistributionFactory extends
    DistributionFactory<ChiSquareDistribution> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 7720563924453195874L;

  @Override
  public ChiSquareDistribution create(long seed) {
    return new ChiSquareDistribution(seed);
  }

  @Override
  public ChiSquareDistribution create(IRandom random) {
    return new ChiSquareDistribution(random);
  }

  @Override
  public ChiSquareDistribution create(ParameterBlock block, IRandom random) {
    if (!ParameterBlocks.hasSubBlock(block, "degreeOfFreedom")) {
      return create(random);
    }
    Integer degreeOfFreedom =
        ParameterBlocks.getSubBlockValue(block, "degreeOfFreedom");
    return new ChiSquareDistribution(random, degreeOfFreedom);
  }

}
