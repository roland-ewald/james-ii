/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.recording.performance.progress;

import org.jamesii.core.experiments.ComputationTaskRuntimeInformation;
import org.jamesii.core.experiments.RunInformation;
import org.jamesii.perfdb.recording.performance.IPerformanceMeasurer;
import org.jamesii.simspex.util.SimulationProblemDefinition;


/**
 * Measures the amount of simulation time that could be simulated within one
 * unit of wall clock time (= one second). Can be used to compare algorithms
 * with strongly varying run time. So far, only the
 * {@link org.jamesii.core.simulationrun.stoppolicy.SimTimeStopFactory} (i.e. a fixed
 * simulation end time) is permitted.
 * 
 * @author Roland Ewald
 * 
 */
public class TimeProgressPerformance implements
    IPerformanceMeasurer<ComputationTaskRuntimeInformation> {

  @Override
  public double measurePerformance(ComputationTaskRuntimeInformation srti) {

    Double simStopTime =
        SimulationProblemDefinition.extractSimStopTime(srti
            .getSimulationRunConfiguration().getStopPolicyParameters(), srti
            .getSimulationRunConfiguration().getStopPolicyFactoryClass());

    if (simStopTime == null) {
      throw new IllegalArgumentException("No simulation end time found!");
    }

    if (simStopTime == 0) {
      return Double.MAX_VALUE;
    }
    RunInformation runInfo = srti.getRunInformation();

    if (runInfo == null || runInfo.getTotalRuntime() == -1) {
      return 0;
    }

    if (runInfo.getTotalRuntime() == 0) {
      return Double.MAX_VALUE;
    }

    return simStopTime / runInfo.getTotalRuntime();
  }
}
