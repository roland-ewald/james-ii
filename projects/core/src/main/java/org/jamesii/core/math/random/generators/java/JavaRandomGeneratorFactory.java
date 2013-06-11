/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.generators.java;

import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.random.generators.plugintype.RandomGeneratorFactory;

/**
 * Factory class for the default Java random number generator.
 * 
 * @author Jan Himmelspach
 */
public class JavaRandomGeneratorFactory extends RandomGeneratorFactory {

  /**
   * The serialisation ID.
   */
  private static final long serialVersionUID = 2454205781052476649L;

  /**
   * Creates an instance of the default random number generator with a given
   * seed.
   * 
   * @param seed
   *          The seed. Same seeds always yield the same sequence of numbers,
   *          given the exact same order of statements.
   * @return the random number generator
   */
  @Override
  public IRandom create(Long seed) {
    return new JavaRandom(seed);
  }

}
