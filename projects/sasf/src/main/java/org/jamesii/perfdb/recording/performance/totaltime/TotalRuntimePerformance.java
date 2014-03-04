/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.recording.performance.totaltime;

import org.jamesii.core.experiments.ComputationTaskRuntimeInformation;
import org.jamesii.perfdb.recording.performance.IPerformanceMeasurer;


/**
 * Measure for total runtime.
 * 
 * @author Roland Ewald
 */
public class TotalRuntimePerformance implements
    IPerformanceMeasurer<ComputationTaskRuntimeInformation> {

  @Override
  public double measurePerformance(ComputationTaskRuntimeInformation srti) {
    return srti.getRunInformation().failed() ? -1 : srti.getRunInformation()
        .getTotalRuntime();
  }

}
