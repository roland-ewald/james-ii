/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.serialization;

import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.random.generators.java.JavaRandom;

/**
 * Basic serialization test of the {@link JavaRandom} RNG.
 * 
 * @author Johannes Rössel
 */
public class TestJavaRandomSerialization extends TestRNGSerialization {

  @Override
  protected IRandom getRNG() {
    return new JavaRandom();
  }

}
