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
 * A factory for creating {@link WallClockTimeStop} objects.
 * 
 * @author Jan Himmelspach
 */
public class WallClockTimeStopFactory extends ComputationTaskStopPolicyFactory<ISimulationRun> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -6222451902152767896L;

  /** The parameter to set the wall-clock time (in milliseconds). */
  public static final String SIMEND = "SIMEND";

  @Override
  public IComputationTaskStopPolicy<ISimulationRun> create(ParameterBlock paramBlock) {
    ISimulationRun run = ParameterBlocks.getSubBlockValue(paramBlock, COMPTASK);
    Number endTimeDelta = ParameterBlocks.getSubBlockValue(paramBlock, SIMEND);
    if (endTimeDelta == null) {
      endTimeDelta = 1l;
    }
    return new WallClockTimeStop(endTimeDelta.longValue());
  }

}
