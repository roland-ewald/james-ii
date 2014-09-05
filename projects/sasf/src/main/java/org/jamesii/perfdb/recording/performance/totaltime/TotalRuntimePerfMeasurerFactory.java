/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.recording.performance.totaltime;

import org.jamesii.core.experiments.ComputationTaskRuntimeInformation;
import org.jamesii.core.factories.Context;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.perfdb.entities.IRuntimeConfiguration;
import org.jamesii.perfdb.recording.performance.IPerformanceMeasurer;
import org.jamesii.perfdb.recording.performance.plugintype.PerformanceMeasurerFactory;


/**
 * Factory to generate performance measurer for the total runtime.
 * 
 * @author Roland Ewald
 * 
 */
public class TotalRuntimePerfMeasurerFactory extends PerformanceMeasurerFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 6506310955972531727L;

  @Override
  public IPerformanceMeasurer<ComputationTaskRuntimeInformation> create(
      ParameterBlock parameterBlock, Context context) {
    return new TotalRuntimePerformance();
  }

  @Override
  public int supportsParameters(ParameterBlock params) {
    if (params.getSubBlockValue(PERFORMANCE_DATA) instanceof IRuntimeConfiguration) {
      return 1;
    }
    return 0;
  }

  @Override
  public String getMeasurementDescription() {
    return "Measures the overall time spent for processing the simulation";
  }

  @Override
  public String getMeasurementName() {
    return "Total Runtime Performance (in s)";
  }

}
