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
 * Factory for {@link UCB1}.
 * 
 * @author Roland Ewald
 * 
 */
public class UCB1Factory extends MinBanditPolicyFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = 8310352085403665854L;

  @Override
  public IMinBanditPolicy create(ParameterBlock params) {
    return new UCB1();
  }

}
