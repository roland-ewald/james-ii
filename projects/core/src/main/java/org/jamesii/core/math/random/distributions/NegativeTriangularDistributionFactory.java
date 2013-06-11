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
 * A factory for creating NegativeTriangularDistribution objects.
 */
public class NegativeTriangularDistributionFactory extends
    DistributionFactory<NegativeTriangularDistribution> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1186000644612094604L;

  @Override
  public NegativeTriangularDistribution create(long seed) {
    return new NegativeTriangularDistribution(seed);
  }

  @Override
  public NegativeTriangularDistribution create(IRandom random) {
    return new NegativeTriangularDistribution(random);
  }

  @Override
  public NegativeTriangularDistribution create(ParameterBlock block,
      IRandom random) {
    if (!ParameterBlocks.hasSubBlock(block, "width")) {
      return create(random);
    }

    Double width = ParameterBlocks.getSubBlockValue(block, "width");
    return new NegativeTriangularDistribution(random, width);
  }

}
