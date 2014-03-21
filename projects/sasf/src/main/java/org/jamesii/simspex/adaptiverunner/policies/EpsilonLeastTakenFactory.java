/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.adaptiverunner.policies;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.simspex.adaptiverunner.policies.plugintype.IMinBanditPolicy;
import org.jamesii.simspex.adaptiverunner.policies.plugintype.MinBanditPolicyFactory;


/**
 * Factory for {@link EpsilonLeastTaken}.
 * 
 * @author Rene Schulz
 * 
 */
public class EpsilonLeastTakenFactory extends MinBanditPolicyFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = 8469210787035123114L;

  /**
   * The parameter 'epsilon' (see {@link SemiUniform} documentation). Type:
   * {@link Double}.
   */
  public static final String PARAM_EP = "EP";

  @Override
  public IMinBanditPolicy create(ParameterBlock params) {
    EpsilonLeastTaken eLeastTaken = new EpsilonLeastTaken();
    eLeastTaken.setEpsilon(params.getSubBlockValue(PARAM_EP,
        SemiUniform.DEFAULT_EPSILON));
    return eLeastTaken;
  }

}