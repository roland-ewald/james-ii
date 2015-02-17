package org.jamesii.core.math.random.distributions;

import org.jamesii.core.math.random.generators.IRandom;

/**
 * @author Arne Bittig
 * @date 21.11.2014
 */
public class NormalDistributionBoxMullerTest extends
    AbstractNormalDistributionTest {

  @Override
  protected AbstractNormalDistribution getDistribution(IRandom rand,
      double mean, double dev) {
    return new NormalDistributionBoxMuller(rand, mean, dev);
  }
}
