/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.generators.securerandom;

import java.security.SecureRandom;

import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.random.generators.plugintype.RandomGeneratorFactory;

/** Constructs instances of the {@link SecureRandom} class wrapper. */
public class SecureRandomGeneratorFactory extends RandomGeneratorFactory {

  /** Serial version ID. */
  private static final long serialVersionUID = -4467473474467839615L;

  @Override
  public IRandom create(Long seed) {
    return new SecureRandomWrapper(seed);
  }
}
