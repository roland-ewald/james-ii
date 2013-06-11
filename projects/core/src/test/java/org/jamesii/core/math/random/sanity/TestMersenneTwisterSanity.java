package org.jamesii.core.math.random.sanity;

import org.jamesii.core.math.random.generators.mersennetwister.MersenneTwister;
import org.jamesii.core.math.random.generators.mersennetwister.MersenneTwisterGeneratorFactory;
import org.jamesii.core.math.random.generators.plugintype.RandomGeneratorFactory;

/**
 * Basic sanity checks for the {@link MersenneTwister} RNG.
 * 
 * @author Johannes Rössel
 */
public class TestMersenneTwisterSanity extends RNGSanityCheck {

  @Override
  protected RandomGeneratorFactory getRNGFactory() {
    return new MersenneTwisterGeneratorFactory();
  }

}
