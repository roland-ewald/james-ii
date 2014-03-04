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
 * Factory for {@link RewardComparison}.
 * 
 * @author Rene Schulz
 * 
 */
public class RewardComparisonFactory extends MinBanditPolicyFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = 6364775068623761854L;

  /**
   * The parameter 'alpha' (see {@link RewardComparison} documentation). Type:
   * {@link Double}.
   */
  public static final String PARAM_AL = "AL";

  /**
   * The parameter 'beta' (see {@link RewardComparison} documentation). Type:
   * {@link Double}.
   */
  public static final String PARAM_BE = "BE";

  @Override
  public IMinBanditPolicy create(ParameterBlock params) {
    RewardComparison rewComp = new RewardComparison();
    rewComp.setAlpha(params.getSubBlockValue(PARAM_AL,
        RewardComparison.DEFAULT_VAL_ALPHA));
    rewComp.setBeta(params.getSubBlockValue(PARAM_BE,
        RewardComparison.DEFAULT_VAL_BETA));
    return rewComp;
  }

}
