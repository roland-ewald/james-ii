/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.simulationrun.stoppolicy;

import org.jamesii.core.experiments.tasks.stoppolicy.IComputationTaskStopPolicy;
import org.jamesii.core.experiments.tasks.stoppolicy.plugintype.ComputationTaskStopPolicyFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;
import org.jamesii.core.simulationrun.ISimulationRun;

/**
 * A factory for creating
 * {@link org.jamesii.core.simulationrun.stoppolicy.SimTimeStop} objects.
 * 
 * @author Jan Himmelspach
 */
public class SimTimeStopFactory extends ComputationTaskStopPolicyFactory<ISimulationRun> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -887176305434302951L;

  /** The Constant SIMEND. */
  public static final String SIMEND = "SIMEND";

  @Override
  public IComputationTaskStopPolicy<ISimulationRun> create(ParameterBlock paramBlock) {
    ISimulationRun run = ParameterBlocks.getSubBlockValue(paramBlock, COMPTASK);
    Number endTime = ParameterBlocks.getSubBlockValue(paramBlock, SIMEND);
    if (endTime == null) {
      endTime = Double.POSITIVE_INFINITY;
    }
    return new SimTimeStop(endTime.doubleValue());
  }

}
