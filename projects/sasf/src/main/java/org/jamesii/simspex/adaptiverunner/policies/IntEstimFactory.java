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
 * Factory for {@link IntEstim}.
 * 
 * @author Rene Schulz
 * 
 */
public class IntEstimFactory extends MinBanditPolicyFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = 4798454535460780479L;

  /**
   * The parameter 'alpha' (see {@link IntEstim} documentation). Type:
   * {@link Double}.
   */
  public static final String PARAM_A = "A";

  /**
   * The parameter 'startDeviation' (see {@link IntEstim} documentation). Type:
   * {@link Integer}.
   */
  public static final String PARAM_D = "D";

  /** The default value for parameter a. */
  public static final double DEFAULT_VAL_A = 0.05;

  /** The default value for parameter d. */
  public static final int DEFAULT_VAL_D = 20;

  @Override
  public IMinBanditPolicy create(ParameterBlock params) {
    IntEstim iEst = new IntEstim();
    iEst.setAlpha(params.getSubBlockValue(PARAM_A, DEFAULT_VAL_A));
    iEst.setStartDeviation(params.getSubBlockValue(PARAM_D, DEFAULT_VAL_D));
    return iEst;
  }

}
