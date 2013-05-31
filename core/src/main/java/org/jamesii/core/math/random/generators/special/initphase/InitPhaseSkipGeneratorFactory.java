/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.generators.special.initphase;

import org.jamesii.SimSystem;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.random.generators.mersennetwister.MersenneTwister;
import org.jamesii.core.math.random.generators.plugintype.RandomGeneratorFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;

/**
 * Handles creation of instances of the init phase skip generator.
 * 
 * @author Jan Himmelspach
 */
public class InitPhaseSkipGeneratorFactory extends RandomGeneratorFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 7610046037240164978L;

  @Override
  public IRandom create(Long seed) {
    return new InitPhaseSkipGenerator(new MersenneTwister(
        (int) (seed & 0xffffffffL)));
  }

  /**
   * Creates a random number generator with the given parameters.
   * 
   * @param block
   *          The parameter block to use. Should contain a “PRNG” parameter.
   * @return An instance of the respective random number generator.
   */
  @Override
  public IRandom create(ParameterBlock block) {

    RandomGeneratorFactory factory = null;
    try {
      factory =
          (RandomGeneratorFactory) Class.forName(
              (String) ParameterBlocks.getSubBlockValue(block, "PRNG"))
              .newInstance();
    } catch (InstantiationException | IllegalAccessException
        | ClassNotFoundException e) {
      SimSystem.report(e);
      return null;
    }
    return new InitPhaseSkipGenerator(factory.create(block));
  }

}
