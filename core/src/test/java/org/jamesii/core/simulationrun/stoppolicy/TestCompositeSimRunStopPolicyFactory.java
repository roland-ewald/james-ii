/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.simulationrun.stoppolicy;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.experiments.tasks.stoppolicy.IComputationTaskStopPolicy;
import org.jamesii.core.experiments.tasks.stoppolicy.plugintype.ComputationTaskStopPolicyFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.simulationrun.stoppolicy.CompositeCompTaskStopPolicyFactory;
import org.jamesii.core.simulationrun.stoppolicy.SimTimeStop;
import org.jamesii.core.simulationrun.stoppolicy.SimTimeStopFactory;
import org.jamesii.core.simulationrun.stoppolicy.WallClockTimeStop;
import org.jamesii.core.simulationrun.stoppolicy.WallClockTimeStopFactory;
import org.jamesii.core.util.misc.Pair;

import junit.framework.TestCase;

/**
 * Tests sub-policy instantiation in {@link CompositeCompTaskStopPolicyFactory}.
 * 
 * @author Roland Ewald
 * 
 */
public class TestCompositeSimRunStopPolicyFactory extends TestCase {

  static final Long WALLCLOCK_END = 1000L;

  public void testSubPolicyCreation() {

    ParameterBlock parameters =
        new ParameterBlock().addSubBl(
            CompositeCompTaskStopPolicyFactory.POLICY_FACTORY_LIST,
            prepareSubPolicyParameters()).addSubBl(
            ComputationTaskStopPolicyFactory.COMPTASK, new DummySimRun());

    List<IComputationTaskStopPolicy> subPolicies =
        CompositeCompTaskStopPolicyFactory.createSubPolicies(parameters);

    assertEquals(2, subPolicies.size());
    assertTrue(subPolicies.get(0) instanceof SimTimeStop);
    assertTrue(subPolicies.get(1) instanceof WallClockTimeStop);
    assertEquals(WALLCLOCK_END,
        (Long) ((WallClockTimeStop) subPolicies.get(1)).getStopTimeDelta());
  }

  /**
   * Prepares sub-policy parameters.
   * 
   * @return list of sub policy parameters
   */
  protected List<Pair<ComputationTaskStopPolicyFactory, ParameterBlock>> prepareSubPolicyParameters() {
    List<Pair<ComputationTaskStopPolicyFactory, ParameterBlock>> subPolicyParams =
        new ArrayList<>();

    Pair<ComputationTaskStopPolicyFactory, ParameterBlock> simTimeStop =
        new Pair<ComputationTaskStopPolicyFactory, ParameterBlock>(
            new SimTimeStopFactory(), new ParameterBlock());
    Pair<ComputationTaskStopPolicyFactory, ParameterBlock> wctTimeStop =
        new Pair<ComputationTaskStopPolicyFactory, ParameterBlock>(
            new WallClockTimeStopFactory(), new ParameterBlock(WALLCLOCK_END,
                WallClockTimeStopFactory.SIMEND));

    subPolicyParams.add(simTimeStop);
    subPolicyParams.add(wctTimeStop);
    return subPolicyParams;
  }

}