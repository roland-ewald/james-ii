/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.rnggenerator.pre;

import java.util.List;

import org.jamesii.core.factories.Context;
import org.jamesii.core.math.random.rnggenerator.IRNGGenerator;
import org.jamesii.core.math.random.rnggenerator.plugintype.RNGGeneratorFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;
import org.jamesii.core.util.Hook;

/**
 * Handles creation of instances of the pre defined seeds random generator
 * generator.
 * 
 * @author Jan Himmelspach
 */
public class PreDefinedSeedsRandGeneratorFactory extends RNGGeneratorFactory {

  /** The serialisation ID. */
  private static final long serialVersionUID = 7139050094433501180L;

  public static final String SEEDS = "Seeds";

  public static final String HOOK = "Hook";

  @Override
  public IRNGGenerator create(ParameterBlock parameter, Context context) {
    return new PreDefinedSeedsRandGenerator(
        ParameterBlocks.<List<Long>> getSubBlockValue(parameter, SEEDS),
        ParameterBlocks.<Hook<PreDefinedSeedsRandGenerator>> getSubBlockValue(
            parameter, HOOK));
  }
}
