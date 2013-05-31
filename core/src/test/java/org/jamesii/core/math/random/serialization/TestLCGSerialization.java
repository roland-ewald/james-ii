/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.serialization;

import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.random.generators.lcg.LCG;
import org.jamesii.core.math.random.serialization.TestRNGSerialization;

/**
 * Basic serialization test of the {@link LCG} RNG.
 * 
 * @author Johannes Rössel
 */
public class TestLCGSerialization extends TestRNGSerialization {

  @Override
  protected IRandom getRNG() {
    return new LCG(123L);
  }

}
