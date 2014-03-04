/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.adaptiverunner.policies;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;
import org.jamesii.simspex.adaptiverunner.policies.plugintype.IMinBanditPolicy;
import org.jamesii.simspex.adaptiverunner.policies.plugintype.MinBanditPolicyFactory;


/**
 * Factory for {@link EpsilonGreedyDecreasing}.
 * 
 * @author Roland Ewald
 */
public class EpsilonGreedyDecreasingFactory extends MinBanditPolicyFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = 958903234910485437L;

  /**
   * The parameter 'c' (see {@link EpsilonGreedyDecreasing} documentation).
   * Type: {@link Double}.
   */
  public static final String PARAM_C = "C";

  /**
   * The parameter 'd' (see {@link EpsilonGreedyDecreasing} documentation). Type
   * {@link Double}.
   */
  public static final String PARAM_D = "D";

  @Override
  public IMinBanditPolicy create(ParameterBlock params) {
    EpsilonGreedyDecreasing eGreedy = new EpsilonGreedyDecreasing();
    setupPolicy(eGreedy, params);
    return eGreedy;
  }

  /**
   * Sets up policy.
   * 
   * @param params
   *          the parameters
   * @param policy
   *          the policy
   */
  protected void setupPolicy(EpsilonGreedyDecreasing policy,
      ParameterBlock params) {
    ParameterBlock parameters = ParameterBlocks.newOrCopy(params);

    policy.setC(parameters.getSubBlockValue(PARAM_C,
        EpsilonGreedyDecreasing.DEFAULT_C));
    policy.setD(parameters.getSubBlockValue(PARAM_D,
        EpsilonGreedyDecreasing.DEFAULT_D));
  }

}
