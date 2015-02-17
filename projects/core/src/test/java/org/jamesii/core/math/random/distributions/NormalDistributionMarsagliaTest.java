package org.jamesii.core.math.random.distributions;

import org.jamesii.core.math.random.generators.IRandom;

/**
 * @author Arne Bittig
 * @date 21.11.2014
 */
public class NormalDistributionMarsagliaTest extends AbstractNormalDistributionTest{

  @Override
  protected AbstractNormalDistribution getDistribution(IRandom rand,
      double mean, double dev) {
    return new NormalDistribution(rand, mean, dev);
  }
}
