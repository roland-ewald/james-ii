/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.generators.plugintype;

import org.jamesii.core.factories.Factory;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.random.rnggenerator.IRNGGenerator;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;

/**
 * Abstract base class for (pseudo)random number generator factories.
 * 
 * @author Jan Himmelspach
 */
public abstract class RandomGeneratorFactory extends Factory<IRandom> {

  /** Serialisation ID. */
  private static final long serialVersionUID = 3825374770686281006L;

  /**
   * Creates a random number generator with the given seed.
   * 
   * @param seed
   *          The seed to use. This may be truncated to accommodate for RNGs
   *          with smaller seed values.
   * @return An instance of the respective random number generator.
   */
  public abstract IRandom create(Long seed);

  /**
   * Creates a random number generator with the given parameters.
   * 
   * @param block
   *          The parameter block to use. Should contain a “seed” parameter as
   *          well as generator-specific ones.
   * @return An instance of the respective random number generator.
   */
  @Override
  public IRandom create(ParameterBlock block) {
    Long seed =
        ParameterBlocks.getSubBlockValue(block,
            AbstractRandomGeneratorFactory.SEED);
    return create(seed);
  }

}
