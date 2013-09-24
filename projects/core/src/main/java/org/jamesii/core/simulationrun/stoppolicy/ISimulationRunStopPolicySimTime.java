/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.simulationrun.stoppolicy;

import org.jamesii.core.experiments.tasks.IComputationTask;
import org.jamesii.core.experiments.tasks.stoppolicy.IComputationTaskStopPolicy;

/**
 * The Interface ISimulationRunStopPolicySimTime.
 * 
 * Some simulation algorithms might be in need of an explicit end time. For
 * these simulation end policies are required which are capable of returning an
 * explicit simulation time end time value.
 * 
 * @author Jan Himmelspach
 */
public interface ISimulationRunStopPolicySimTime<TimeBase extends Comparable<TimeBase>, T extends IComputationTask>
    extends IComputationTaskStopPolicy<T> {

  /**
   * Gets the estimated end time (simulation time). Depending on the
   * implementing class the return value might change between subsequent calls
   * to this method.
   * 
   * @return the estimated end time
   */
  TimeBase getEstimatedEndTime();

}
