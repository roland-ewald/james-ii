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
 * Factory for {@link SoftMaxDecreasing}.
 * 
 * @author Rene Schulz
 * 
 */
public class SoftMaxDecreasingFactory extends SoftMaxFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = -9208232890757661659L;

  @Override
  public IMinBanditPolicy create(ParameterBlock params, Context context) {
    SoftMaxDecreasing sMax = new SoftMaxDecreasing();
    sMax.setTemperature(params.getSubBlockValue(PARAM_T, DEFAULT_SOFTMAX_T));
    return sMax;
  }

}
