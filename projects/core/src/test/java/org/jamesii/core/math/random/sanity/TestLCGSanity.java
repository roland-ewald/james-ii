/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.sanity;

import org.jamesii.core.math.random.generators.lcg.LCG;
import org.jamesii.core.math.random.generators.lcg.LCGGeneratorFactory;
import org.jamesii.core.math.random.generators.plugintype.RandomGeneratorFactory;

/**
 * Basic sanity checks for the {@link LCG} RNG.
 * 
 * @author Johannes Rössel
 */
public class TestLCGSanity extends RNGSanityCheck {

  @Override
  protected RandomGeneratorFactory getRNGFactory() {
    return new LCGGeneratorFactory();
  }

}
