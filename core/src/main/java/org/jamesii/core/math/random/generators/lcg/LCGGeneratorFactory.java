/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.generators.lcg;

import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.random.generators.plugintype.RandomGeneratorFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;

/**
 * Factory class for the {@link LCG} random number generator.
 * 
 * @author Johannes Rössel
 */
public class LCGGeneratorFactory extends RandomGeneratorFactory {

  /** Serial version ID. */
  private static final long serialVersionUID = -8044330003140051144L;

  @Override
  public IRandom create(Long seed) {
    return new LCG((int) (seed & 0xffffffffL));
  }

  @Override
  public IRandom create(ParameterBlock block) {
    Long seed = ParameterBlocks.getSubBlockValue(block, "seed");
    Long multiplier = LCG.DEFAULTMULTIPLIER; // e.g. using 2 instead of the
                                             // given value
    // is a bad idea
    Long addend = LCG.DEFAULTADDEND;
    Long mask = LCG.DEFAULTMASK;

    if (ParameterBlocks.hasSubBlock(block, "multiplier")) {
      multiplier = ParameterBlocks.getSubBlockValue(block, "multiplier");
    }

    if (ParameterBlocks.hasSubBlock(block, "addend")) {
      addend = ParameterBlocks.getSubBlockValue(block, "addend");
    }

    if (ParameterBlocks.hasSubBlock(block, "mask")) {
      mask = ParameterBlocks.getSubBlockValue(block, "mask");
    }

    return new LCG((int) (seed & 0xffffffffL), multiplier, addend, mask);
  }

}
