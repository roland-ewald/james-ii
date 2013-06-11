/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.generators.mersennetwister;

import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.random.generators.plugintype.RandomGeneratorFactory;

/**
 * Handles creation of instances of the Mersenne Twister.
 * 
 * @author Johannes Rössel
 */
public class MersenneTwisterGeneratorFactory extends RandomGeneratorFactory {

  /** The serialization ID. */
  private static final long serialVersionUID = -7860890476350977919L;

  @Override
  public IRandom create(Long seed) {
    return new MersenneTwister((int) (seed & 0xffffffffL));
  }

}
