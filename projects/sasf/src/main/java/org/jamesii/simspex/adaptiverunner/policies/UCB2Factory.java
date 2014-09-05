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
 * Factory for {@link UCB2}.
 * 
 * @author Roland Ewald
 * 
 */
public class UCB2Factory extends MinBanditPolicyFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = -7965539965404213494L;

  /** The default parameter value for alpha. */
  public static final double DEFAULT_VAL_ALPHA = 0.001;

  /**
   * Parameter alpha for the heuristic (see {@link UCB2} documentation). Type:
   * {@link Double}.
   */
  public static final String ALPHA = "alpha";

  @Override
  public IMinBanditPolicy create(ParameterBlock params, Context context) {
    UCB2 ucb2 = new UCB2();
    ucb2.setAlpha(params.getSubBlockValue(ALPHA, DEFAULT_VAL_ALPHA));
    return ucb2;
  }

}
