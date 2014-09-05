/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.recording.performance.progress;

import org.jamesii.core.experiments.ComputationTaskRuntimeInformation;
import org.jamesii.core.factories.Context;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.perfdb.entities.IRuntimeConfiguration;
import org.jamesii.perfdb.recording.performance.IPerformanceMeasurer;
import org.jamesii.perfdb.recording.performance.plugintype.PerformanceMeasurerFactory;


/**
 * @author Roland Ewald
 * 
 */
public class TimeProgressPerfFactory extends PerformanceMeasurerFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 3742504758203716204L;

  @Override
  public IPerformanceMeasurer<ComputationTaskRuntimeInformation> create(
      ParameterBlock parameterBlock, Context context) {
    return new TimeProgressPerformance();
  }

  @Override
  public String getMeasurementDescription() {
    return "Simulated Time / WCT";
  }

  @Override
  public String getMeasurementName() {
    return "Simulation Time Progress per WCT";
  }

  @Override
  public int supportsParameters(ParameterBlock params) {
    if (params.getSubBlockValue(PERFORMANCE_DATA) instanceof IRuntimeConfiguration) {
      return 1;
    }
    return 0;
  }

  @Override
  public boolean isForMaximisation() {
    return true;
  }

}
