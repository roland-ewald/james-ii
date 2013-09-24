/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.tasks.stoppolicy;

import org.jamesii.core.experiments.tasks.IComputationTask;
import org.jamesii.core.experiments.tasks.stoppolicy.plugintype.ComputationTaskStopPolicyFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;

/**
 * Factory for {@link EmptyStopCondition}.
 * 
 * @author Jan Himmelspach
 * 
 */
public class EmptyStopConditionStopPolicyFactory extends
    ComputationTaskStopPolicyFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -5064362321174780171L;

  @Override
  public IComputationTaskStopPolicy create(ParameterBlock paramBlock) {
    IComputationTask task =
        ParameterBlocks.getSubBlockValue(paramBlock, COMPTASK);
    return new EmptyStopCondition();
  }

}
