/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.serialization;

import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.random.generators.mersennetwister.MersenneTwister;

/**
 * Basic serialization test of the {@link MersenneTwister} RNG.
 * 
 * @author Johannes Rössel
 */
public class TestMersenneTwisterSerialization extends TestRNGSerialization {

  @Override
  protected IRandom getRNG() {
    return new MersenneTwister();
  }

}
