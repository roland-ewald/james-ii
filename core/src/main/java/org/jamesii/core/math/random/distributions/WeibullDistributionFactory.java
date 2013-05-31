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
 * Creates {@link WeibullDistribution}.
 * 
 * @author Felix Willud
 * 
 */
public class WeibullDistributionFactory extends
    DistributionFactory<WeibullDistribution> {

  /** Serialisation ID. */
  private static final long serialVersionUID = 6355169429602538377L;

  @Override
  public WeibullDistribution create(long seed) {
    return new WeibullDistribution(seed);
  }

  @Override
  public WeibullDistribution create(IRandom random) {
    return new WeibullDistribution(random);
  }

  @Override
  public WeibullDistribution create(ParameterBlock block, IRandom random) {
    if (!ParameterBlocks.hasSubBlock(block, "alpha")
        || !ParameterBlocks.hasSubBlock(block, "beta")) {
      return create(random);
    }

    Double alpha = ParameterBlocks.getSubBlockValue(block, "alpha");
    Double beta = ParameterBlocks.getSubBlockValue(block, "beta");
    return new WeibullDistribution(random, alpha, beta);
  }

}
