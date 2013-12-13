/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.simulationrun.stoppolicy;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.experiments.tasks.IComputationTask;
import org.jamesii.core.experiments.tasks.stoppolicy.IComputationTaskStopPolicy;
import org.jamesii.core.experiments.tasks.stoppolicy.plugintype.ComputationTaskStopPolicyFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;
import org.jamesii.core.simulationrun.ISimulationRun;
import org.jamesii.core.util.misc.Pair;

/**
 * Factory for {@link DisjunctiveSimRunStopPolicy}.
 * 
 * @author Roland Ewald
 * 
 */
public class DisjunctiveSimRunStopPolicyFactory extends
    CompositeCompTaskStopPolicyFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -1587165003419760852L;

  @Override
  public IComputationTaskStopPolicy<IComputationTask> create(
      ParameterBlock paramBlock) {
    ISimulationRun run = ParameterBlocks.getSubBlockValue(paramBlock, COMPTASK);
    List<IComputationTaskStopPolicy<IComputationTask>> policies =
        createSubPolicies(paramBlock);
    return new DisjunctiveSimRunStopPolicy<IComputationTask>(run, policies);
  }

  /**
   * Configure stopping with upper limit.
   * 
   * @param simEndTime
   *          the desired simulation end time
   * @param wallClockTime
   *          the desired maximal wall clock time (in ms)
   * 
   * @return the parameter block
   */
  public static ParameterBlock configureStoppingWithUpperLimit(
      Double simEndTime, Long wallClockTime) {
    ParameterBlock factoryBlock =
        new ParameterBlock(DisjunctiveSimRunStopPolicyFactory.class.getName());
    List<Pair<ComputationTaskStopPolicyFactory<? extends IComputationTask>, ParameterBlock>> subPolicyList =
        new ArrayList<>();
    subPolicyList
        .add(new Pair<ComputationTaskStopPolicyFactory<? extends IComputationTask>, ParameterBlock>(
            new SimTimeStopFactory(), new ParameterBlock().addSubBl(
                SimTimeStopFactory.SIMEND, simEndTime)));
    subPolicyList
        .add(new Pair<ComputationTaskStopPolicyFactory<? extends IComputationTask>, ParameterBlock>(
            new WallClockTimeStopFactory(), new ParameterBlock().addSubBl(
                WallClockTimeStopFactory.SIMEND, wallClockTime)));
    factoryBlock.addSubBl(POLICY_FACTORY_LIST, subPolicyList);
    return factoryBlock;
  }
}
