/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.adaptiverunner;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.simspex.adaptiverunner.policies.UCB1Tuned;
import org.jamesii.simspex.adaptiverunner.policies.UCB1TunedFactory;
import org.jamesii.simspex.adaptiverunner.policies.plugintype.MinBanditPolicyFactory;


/**
 * Tests {@link UCB1Tuned}.
 * 
 * @author Roland Ewald
 * 
 */
public class UCB1TunedTest extends TestPolicyPerformance {

  @Override
  protected Pair<MinBanditPolicyFactory, ParameterBlock> getPolicySetup() {
    return new Pair<MinBanditPolicyFactory, ParameterBlock>(
        new UCB1TunedFactory(), new ParameterBlock());
  }

}
