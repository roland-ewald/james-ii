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
 * Factory for {@link IntEstimDec}.
 * 
 * @author Rene Schulz
 * 
 */
public class IntEstimDecFactory extends IntEstimFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = -6476897139220741421L;

  /**
   * The parameter 'alpha' (see {@link IntEstimDec} documentation). Type:
   * {@link Double}.
   */
  public static final String PARAM_A = "A";

  /**
   * The parameter 'startDeviation' (see {@link IntEstimDec} documentation).
   * Type: {@link Integer}.
   */
  public static final String PARAM_D = "D";

  @Override
  public IMinBanditPolicy create(ParameterBlock params, Context context) {
    IntEstimDec iEst = new IntEstimDec();
    iEst.setAlpha(params.getSubBlockValue(PARAM_A, DEFAULT_VAL_A));
    iEst.setStartDeviation(params.getSubBlockValue(PARAM_D, DEFAULT_VAL_D));
    return iEst;
  }

}
