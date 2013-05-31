/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.rnggenerator.simple;

import org.jamesii.core.math.random.rnggenerator.IRNGGenerator;
import org.jamesii.core.math.random.rnggenerator.plugintype.RNGGeneratorFactory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Handles creation of instances of the current time random seed generator.
 * 
 * @author Jan Himmelspach
 */
public class CurrentTimeRandSeedGeneratorFactory extends RNGGeneratorFactory {

  /** The serialisation ID. */
  private static final long serialVersionUID = -7860890476350977919L;

  @Override
  public IRNGGenerator create(ParameterBlock parameter) {
    return new CurrentTimeRandSeedGenerator();
  }

}
