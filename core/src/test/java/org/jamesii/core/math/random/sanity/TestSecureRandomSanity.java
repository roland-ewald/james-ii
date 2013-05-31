/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.sanity;

import java.security.SecureRandom;

import org.jamesii.core.math.random.generators.plugintype.RandomGeneratorFactory;
import org.jamesii.core.math.random.generators.securerandom.SecureRandomGeneratorFactory;
import org.jamesii.core.math.random.sanity.RNGSanityCheck;

/**
 * Basic sanity checks for the {@link SecureRandom} RNG.
 * 
 * @author Johannes Rössel
 */
public class TestSecureRandomSanity extends RNGSanityCheck {

  @Override
  protected RandomGeneratorFactory getRNGFactory() {
    return new SecureRandomGeneratorFactory();
  }

  @Override
  public void testSeedBehaviour() {
    long seed = System.currentTimeMillis();

    long[] values1 = new long[50];
    long[] values2 = new long[50];

    rng.setSeed(seed);
    for (int i = 0; i < 50; i++) {
      values1[i] = rng.next();
    }

    // sleep a while, just in case some generator uses the current time to salt
    // the seed.
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
    }

    rng.setSeed(seed);
    for (int i = 0; i < 50; i++) {
      values2[i] = rng.next();
    }

    for (int i = 0; i < 50; i++) {
      assertFalse(
          "The secure random is not allowed to produce the same sequence given the same seed",
          values1[i] == values2[i]);
    }
  }

}
