/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.tasks.stoppolicy.steps;

import org.jamesii.core.experiments.tasks.IComputationTask;
import org.jamesii.core.experiments.tasks.stoppolicy.IComputationTaskStopPolicy;
import org.jamesii.core.experiments.tasks.stoppolicy.plugintype.ComputationTaskStopPolicyFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;

/**
 * A factory for creating {@link StepCountStop} objects.
 * 
 * @author Jan Himmelspach
 */
public class StepCountStopFactory extends ComputationTaskStopPolicyFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -8944548041403266176L;

  /** The Constant SIMEND. */
  public static final String TASKEND = "TASKEND";

  @Override
  public IComputationTaskStopPolicy create(ParameterBlock paramBlock) {
    IComputationTask task =
        ParameterBlocks.getSubBlockValue(paramBlock, COMPTASK);
    Long stepCount =
        ((Number) ParameterBlocks.getSubBlockValue(paramBlock, TASKEND))
            .longValue();
    if (stepCount == null) {
      stepCount = 1l;
    }
    return new StepCountStop(task, stepCount);
  }

}
