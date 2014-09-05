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


/**
 * Factory for {@link SoftMaxDecreasingMix}.
 * 
 * @author Rene Schulz
 * 
 */
public class SoftMaxDecreasingMixFactory extends SoftMaxDecreasingFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = 6098458852942317787L;

  @Override
  public IMinBanditPolicy create(ParameterBlock params, Context context) {
    SoftMaxDecreasingMix sMax = new SoftMaxDecreasingMix();
    sMax.setTemperature(params.getSubBlockValue(PARAM_T, DEFAULT_SOFTMAX_T));
    return sMax;
  }

}
