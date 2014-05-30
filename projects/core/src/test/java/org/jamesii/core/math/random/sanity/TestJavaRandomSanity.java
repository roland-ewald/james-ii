/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.sanity;

import org.jamesii.core.math.random.generators.java.JavaRandom;
import org.jamesii.core.math.random.generators.java.JavaRandomGeneratorFactory;
import org.jamesii.core.math.random.generators.plugintype.RandomGeneratorFactory;

/**
 * Basic sanity checks for the {@link JavaRandom} RNG.
 * 
 * @author Johannes Rössel
 */
public class TestJavaRandomSanity extends RNGSanityCheck {

  @Override
  protected RandomGeneratorFactory getRNGFactory() {
    return new JavaRandomGeneratorFactory();
  }

}
