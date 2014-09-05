/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.adaptiverunner.policies;

import org.jamesii.core.factories.Context;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.simspex.adaptiverunner.policies.plugintype.IMinBanditPolicy;
import org.jamesii.simspex.adaptiverunner.policies.plugintype.MinBanditPolicyFactory;


/**
 * Factory for {@link BiasedRandomSelection}.
 * 
 * @author Roland Ewald
 * 
 */
public class BiasedRandomSelectionFactory extends MinBanditPolicyFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -1660736708888183194L;

  /** The Constant BIAS_POWER. Type: {@link Double}. */
  public static final String BIAS_POWER = "biasPower";

  /**
   * The Constant name of the parameter 'Number of Initial Rounds'. Type:
   * {@link Integer}.
   */
  public static final String NUM_INIT_ROUNDS = "numberOfInitialRounds";

  @Override
  public IMinBanditPolicy create(ParameterBlock params, Context context) {
    BiasedRandomSelection brs = new BiasedRandomSelection();
    if (params != null) {
      brs.setBiasPower(params.getSubBlockValue(BIAS_POWER, 3.0));
      brs.setNumInitPhase(params.getSubBlockValue(NUM_INIT_ROUNDS, 3));
    }
    return brs;
  }

}
