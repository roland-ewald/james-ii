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
 * Factory for {@link Pursuit}.
 * 
 * @author Rene Schulz
 * 
 */
public class PursuitFactory extends MinBanditPolicyFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = 7291277870752383171L;

  /**
   * The parameter 'beta' (see {@link Pursuit} documentation). Type:
   * {@link Double}.
   */
  public static final String PARAM_BE = "BE";

  @Override
  public IMinBanditPolicy create(ParameterBlock params, Context context) {
    Pursuit pur = new Pursuit();
    pur.setBeta(params.getSubBlockValue(PARAM_BE, Pursuit.DEFAULT_VAL_BETA));
    return pur;
  }

}
