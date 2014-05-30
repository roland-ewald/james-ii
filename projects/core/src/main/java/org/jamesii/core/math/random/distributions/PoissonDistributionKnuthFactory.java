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

public class PoissonDistributionKnuthFactory extends
    DistributionFactory<PoissonDistributionKnuth> {

  /** Serial version UID. */
  private static final long serialVersionUID = -9004032665022902377L;

  @Override
  public PoissonDistributionKnuth create(long seed) {
    return new PoissonDistributionKnuth(seed);
  }

  @Override
  public PoissonDistributionKnuth create(IRandom random) {
    return new PoissonDistributionKnuth(random);
  }

  @Override
  public PoissonDistributionKnuth create(ParameterBlock block, IRandom random) {
    if (!ParameterBlocks.hasSubBlock(block, "lambda")) {
      return create(random);
    }

    Double lambda = ParameterBlocks.getSubBlockValue(block, "lambda");
    return new PoissonDistributionKnuth(random, lambda);
  }
}
