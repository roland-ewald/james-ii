/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.adaptiverunner.policies.plugintype;

import org.jamesii.core.factories.Context;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Base factory for {@link IMinBanditPolicy} implementations.
 * 
 * @author Roland Ewald
 * 
 */
public abstract class MinBanditPolicyFactory extends Factory<IMinBanditPolicy> {

  /** Serialisation ID. */
  private static final long serialVersionUID = -3304587681463878626L;

  /**
   * Create instance of {@link IMinBanditPolicy}.
 * @param params
   *          parameters for the policy
 * @return instance of {@link IMinBanditPolicy}
   */
  @Override
  public abstract IMinBanditPolicy create(ParameterBlock params, Context context);

}
