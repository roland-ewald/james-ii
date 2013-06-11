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
 * Creates {@link FDistribution}.
 * 
 * @author Felix Willud
 * 
 */
public class FDistributionFactory extends DistributionFactory<FDistribution> {

  /** Serialisation ID. */
  private static final long serialVersionUID = -8141601269110688484L;

  @Override
  public FDistribution create(long seed) {
    return new FDistribution(seed);
  }

  @Override
  public FDistribution create(IRandom random) {
    return new FDistribution(random);
  }

  @Override
  public FDistribution create(ParameterBlock block, IRandom random) {
    if (!ParameterBlocks.hasSubBlock(block, "d1")
        || !ParameterBlocks.hasSubBlock(block, "d2")) {
      return create(random);
    }

    Integer d1 = ParameterBlocks.getSubBlockValue(block, "d1");
    Integer d2 = ParameterBlocks.getSubBlockValue(block, "d2");
    return new FDistribution(random, d1, d2);
  }
}