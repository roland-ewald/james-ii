/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.sanity;

import org.jamesii.core.math.random.generators.plugintype.RandomGeneratorFactory;
import org.jamesii.core.math.random.generators.randu.RANDU;
import org.jamesii.core.math.random.generators.randu.RANDUGeneratorFactory;
import org.jamesii.core.math.random.sanity.RNGSanityCheck;

/**
 * Basic sanity checks for the {@link RANDU} RNG.
 * 
 * @author Johannes Rössel
 */
public class TestRANDUSanity extends RNGSanityCheck {

  @Override
  protected RandomGeneratorFactory getRNGFactory() {
    return new RANDUGeneratorFactory();
  }

}
