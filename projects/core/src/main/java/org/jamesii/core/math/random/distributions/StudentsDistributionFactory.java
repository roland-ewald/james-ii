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
 * A factory for creating StudentsDistribution objects.
 */
public class StudentsDistributionFactory extends
    DistributionFactory<StudentsDistribution> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 3325231953538448212L;

  @Override
  public StudentsDistribution create(long seed) {
    return new StudentsDistribution(seed);
  }

  @Override
  public StudentsDistribution create(IRandom random) {
    return new StudentsDistribution(random);
  }

  @Override
  public StudentsDistribution create(ParameterBlock block, IRandom random) {
    if (!ParameterBlocks.hasSubBlock(block, "degreeOfFreedom")) {
      return create(random);
    }
    Integer degreeOfFreedom =
        ParameterBlocks.getSubBlockValue(block, "degreeOfFreedom");
    return new StudentsDistribution(random, degreeOfFreedom);
  }

}
