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
 * Factory for {@link SoftMax}.
 * 
 * @author Rene Schulz
 * 
 */
public class SoftMaxFactory extends MinBanditPolicyFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = 7479158302989906559L;

  /**
   * The parameter 'temperature' (see {@link SoftMax} documentation). Type:
   * {@link Double}.
   */
  public static final String PARAM_T = "T";

  /** The default value for t. */
  static final double DEFAULT_SOFTMAX_T = 1.0;

  @Override
  public IMinBanditPolicy create(ParameterBlock params) {
    SoftMax sMax = new SoftMax();
    sMax.setTemperature(params.getSubBlockValue(PARAM_T, DEFAULT_SOFTMAX_T));
    return sMax;
  }

}
