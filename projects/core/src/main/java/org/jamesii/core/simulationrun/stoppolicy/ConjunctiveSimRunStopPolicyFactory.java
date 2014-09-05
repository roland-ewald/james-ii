/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.simulationrun.stoppolicy;

import java.util.List;

import org.jamesii.core.experiments.tasks.IComputationTask;
import org.jamesii.core.experiments.tasks.stoppolicy.IComputationTaskStopPolicy;
import org.jamesii.core.factories.Context;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;
import org.jamesii.core.simulationrun.ISimulationRun;

/**
 * Factory for the {@link ConjunctiveSimRunStopPolicy}.
 * 
 * @author Roland Ewald
 * 
 */
public class ConjunctiveSimRunStopPolicyFactory extends
    CompositeCompTaskStopPolicyFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -7364245846289653229L;

  @Override
  public IComputationTaskStopPolicy<IComputationTask> create(
      ParameterBlock paramBlock, Context context) {
    ISimulationRun run = ParameterBlocks.getSubBlockValue(paramBlock, COMPTASK);
    List<IComputationTaskStopPolicy<IComputationTask>> policies =
        createSubPolicies(paramBlock);

    return new ConjunctiveSimRunStopPolicy<>(run, policies);
  }

}
