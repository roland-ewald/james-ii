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
 * Factory for {@link EpsilonDecreasing}.
 * 
 * @author Rene Schulz
 * 
 */
public class EpsilonDecreasingFactory extends MinBanditPolicyFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = 2431491000800997633L;

  /**
   * The parameter 'epsilon' (see {@link SemiUniform} documentation). Type:
   * {@link Double}.
   */
  public static final String PARAM_EP = "EP";

  @Override
  public IMinBanditPolicy create(ParameterBlock params, Context context) {
    EpsilonDecreasing eDecreasing = new EpsilonDecreasing();
    eDecreasing.setEpsilon((double) params.getSubBlockValue(PARAM_EP,
        EpsilonDecreasing.DEFAULT_EPSILON));
    return eDecreasing;
  }

}
