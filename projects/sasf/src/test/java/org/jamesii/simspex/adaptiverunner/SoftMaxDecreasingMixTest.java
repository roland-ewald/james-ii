/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.adaptiverunner;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.simspex.adaptiverunner.policies.SoftMaxDecreasingMix;
import org.jamesii.simspex.adaptiverunner.policies.SoftMaxDecreasingMixFactory;
import org.jamesii.simspex.adaptiverunner.policies.plugintype.MinBanditPolicyFactory;


/**
 * Tests {@link SoftMaxDecreasingMix}.
 * 
 * @author Rene Schulz
 * 
 */
public class SoftMaxDecreasingMixTest extends TestPolicyPerformance {

  @Override
  protected Pair<MinBanditPolicyFactory, ParameterBlock> getPolicySetup() {
    return new Pair<MinBanditPolicyFactory, ParameterBlock>(
        new SoftMaxDecreasingMixFactory(), new ParameterBlock());
  }

}
