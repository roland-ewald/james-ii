/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.adaptiverunner;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.simspex.adaptiverunner.policies.BiasedRandomSelection;
import org.jamesii.simspex.adaptiverunner.policies.BiasedRandomSelectionFactory;
import org.jamesii.simspex.adaptiverunner.policies.plugintype.MinBanditPolicyFactory;


/**
 * Test for {@link BiasedRandomSelection}.
 * 
 * @author Roland Ewald
 * 
 */
public class BiasedRandomSelTest extends TestPolicyPerformance {

  @Override
  protected Pair<MinBanditPolicyFactory, ParameterBlock> getPolicySetup() {
    return new Pair<MinBanditPolicyFactory, ParameterBlock>(
        new BiasedRandomSelectionFactory(), new ParameterBlock());
  }

}
