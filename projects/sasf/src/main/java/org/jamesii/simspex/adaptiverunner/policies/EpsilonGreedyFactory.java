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
 * Factory for {@link EpsilonGreedy}.
 * 
 * @author Rene Schulz
 * 
 */
public class EpsilonGreedyFactory extends MinBanditPolicyFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = 5659923962178487033L;

  /**
   * The parameter 'epsilon' (see {@link SemiUniform} documentation). Type:
   * {@link Double}.
   */
  public static final String PARAM_EP = "EP";

  @Override
  public IMinBanditPolicy create(ParameterBlock params, Context context) {
    EpsilonGreedy eGreedy = new EpsilonGreedy();
    eGreedy.setEpsilon(params.getSubBlockValue(PARAM_EP,
        SemiUniform.DEFAULT_EPSILON));
    return eGreedy;
  }

}
