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
 * @author Roland Ewald
 * 
 *         27.04.2007
 * 
 */
public class BinomialDistributionFactory extends
    DistributionFactory<BinomialDistribution> {

  /**
   * Serialisation ID
   */
  private static final long serialVersionUID = 260466686998160835L;

  @Override
  public BinomialDistribution create(long seed) {
    return new BinomialDistribution(seed);
  }

  @Override
  public BinomialDistribution create(IRandom random) {
    return new BinomialDistribution(random);
  }

}
