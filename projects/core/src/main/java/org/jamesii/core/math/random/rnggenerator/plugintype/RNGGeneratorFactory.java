/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.rnggenerator.plugintype;

import org.jamesii.core.factories.Factory;
import org.jamesii.core.math.random.rnggenerator.IRNGGenerator;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Abstract base class for (pseudo)random number generator factories.
 * 
 * @author Jan Himmelspach
 */
public abstract class RNGGeneratorFactory extends Factory<IRNGGenerator> {

  /** Serialisation ID. */
  private static final long serialVersionUID = 3426158857216311726L;

  /**
   * Creates a random number generator with the given seed.
   * 
   * parameter seed: The seed to use. This may be truncated to accommodate for
   * RNGs with smaller seed values.
   * 
   * @param parameter
   *          the parameter
   * 
   * @return An instance of the respective random number generator.
   */
  @Override
  public abstract IRNGGenerator create(ParameterBlock parameter);

}
