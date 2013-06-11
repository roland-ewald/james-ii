/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.distributions;

import org.jamesii.core.math.random.distributions.plugintype.DistributionFactory;
import org.jamesii.core.math.random.generators.IRandom;

/**
 * The Class MultipleNormalDistributionFactory.
 * 
 * @author Roland Ewald
 * 
 *         27.04.2007
 */
public class MultipleNormalDistributionFactory extends
    DistributionFactory<MultipleNormalDistribution> {

  /** Serialization ID. */
  private static final long serialVersionUID = 6403586277210510972L;

  @Override
  public MultipleNormalDistribution create(long seed) {
    return new MultipleNormalDistribution(seed);
  }

  @Override
  public MultipleNormalDistribution create(IRandom random) {
    return new MultipleNormalDistribution(random);
  }

}
