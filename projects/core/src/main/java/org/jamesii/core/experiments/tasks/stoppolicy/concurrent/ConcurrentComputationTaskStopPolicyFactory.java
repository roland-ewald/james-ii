/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.tasks.stoppolicy.concurrent;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.tasks.IComputationTask;
import org.jamesii.core.experiments.tasks.stoppolicy.IComputationTaskStopPolicy;
import org.jamesii.core.experiments.tasks.stoppolicy.plugintype.ComputationTaskStopPolicyFactory;
import org.jamesii.core.factories.Context;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;
import org.jamesii.core.simulationrun.ISimulationRun;

/**
 * A factory for creating {@link ConcurrentComputationTaskStopPolicy} objects.
 * 
 * @author Jan Himmelspach
 */
public class ConcurrentComputationTaskStopPolicyFactory extends
    ComputationTaskStopPolicyFactory<IComputationTask> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -6222451902152767896L;

  /** The Constant POLICY. */
  public static final String POLICY = "POLICY";

  /** The Constant POLICY_PARAMS. Used to identify the parameters for a policy. */
  public static final String POLICY_PARAMS = "POLICY_PARAMS";

  @Override
  public IComputationTaskStopPolicy<IComputationTask> create(
      ParameterBlock paramBlock, Context context) {
    ComputationTaskStopPolicyFactory<IComputationTask> policyFactory =
        ParameterBlocks.getSubBlockValue(paramBlock, POLICY);
    ISimulationRun run = ParameterBlocks.getSubBlockValue(paramBlock, COMPTASK);
    ParameterBlock innerParams =
        paramBlock.getSubBlock(POLICY_PARAMS).getCopy();
    innerParams.addSubBl(COMPTASK, run);
    IComputationTaskStopPolicy<IComputationTask> policy =
        policyFactory.create(innerParams, SimSystem.getRegistry().createContext());
    ConcurrentComputationTaskStopPolicy result =
        new ConcurrentComputationTaskStopPolicy(policy);
    result.start();
    return result;
  }

}
