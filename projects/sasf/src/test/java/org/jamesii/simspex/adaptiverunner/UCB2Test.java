/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.adaptiverunner;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.simspex.adaptiverunner.policies.UCB2;
import org.jamesii.simspex.adaptiverunner.policies.UCB2Factory;
import org.jamesii.simspex.adaptiverunner.policies.plugintype.MinBanditPolicyFactory;


/**
 * Tests {@link UCB2}.
 * 
 * @author Roland Ewald
 * 
 */
public class UCB2Test extends TestPolicyPerformance {

  @Override
  protected Pair<MinBanditPolicyFactory, ParameterBlock> getPolicySetup() {
    return new Pair<MinBanditPolicyFactory, ParameterBlock>(new UCB2Factory(),
        new ParameterBlock());
  }

}
