/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.generators.randu;

import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.random.generators.plugintype.RandomGeneratorFactory;

/**
 * Factory class for the {@link RANDU} generator.
 * 
 * @author Johannes Rössel
 */
public class RANDUGeneratorFactory extends RandomGeneratorFactory {

  /** Serial version UID. */
  private static final long serialVersionUID = 1804361882888328512L;

  @Override
  public IRandom create(Long seed) {
    return new RANDU(seed & 0xffffffffL);
  }

}
