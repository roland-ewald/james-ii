/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.adaptiverunner;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.simspex.adaptiverunner.policies.EpsilonGreedyDecreasing;
import org.jamesii.simspex.adaptiverunner.policies.EpsilonGreedyDecreasingFactory;
import org.jamesii.simspex.adaptiverunner.policies.plugintype.MinBanditPolicyFactory;


/**
 * Tests {@link EpsilonGreedyDecreasing}.
 * 
 * @author Roland Ewald
 * 
 */
public class EpsilonGreedyDecreasingTest extends TestPolicyPerformance {

  @Override
  protected Pair<MinBanditPolicyFactory, ParameterBlock> getPolicySetup() {
    return new Pair<MinBanditPolicyFactory, ParameterBlock>(
        new EpsilonGreedyDecreasingFactory(), new ParameterBlock());
  }

}
