/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.adaptiverunner.policies;

import org.jamesii.core.factories.Context;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;
import org.jamesii.simspex.adaptiverunner.policies.plugintype.IMinBanditPolicy;


/**
 * Factory for the {@link EpsilonGreedyDecrInit} strategy.
 * 
 * @author Roland Ewald
 * 
 */
public class EpsilonGreedyDecrInitFactory extends
    EpsilonGreedyDecreasingFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 8138749875021217345L;

  /** The parameter name to set the minimal number of trials per setup. */
  public static final String MIN_NUM_TRIALS = "minNumOfTrials";

  /** The default minimum number of trials. */
  private static final int DEFAULT_MIN_TRIALS = 1;

  @Override
  public IMinBanditPolicy create(ParameterBlock params, Context context) {
    EpsilonGreedyDecrInit egdi =
        new EpsilonGreedyDecrInit(ParameterBlocks.getSubBlockValueOrDefault(
            params, MIN_NUM_TRIALS, DEFAULT_MIN_TRIALS));
    setupPolicy(egdi, params);
    return egdi;
  }

}
