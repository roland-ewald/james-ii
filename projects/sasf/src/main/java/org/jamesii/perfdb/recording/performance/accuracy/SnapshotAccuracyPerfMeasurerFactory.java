/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.recording.performance.accuracy;

import org.jamesii.core.experiments.ComputationTaskRuntimeInformation;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.perfdb.recording.performance.IPerformanceMeasurer;
import org.jamesii.perfdb.recording.performance.plugintype.PerformanceMeasurerFactory;


/**
 * Factory for the snapshot accuracy performance measurer.
 * 
 * @author Roland Ewald
 * 
 */
public class SnapshotAccuracyPerfMeasurerFactory extends
    PerformanceMeasurerFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -6405872933202786673L;

  @Override
  public IPerformanceMeasurer<ComputationTaskRuntimeInformation> create(
      ParameterBlock parameterBlock) {
    return new SnapshotAccuracyPerformance();
  }

  @Override
  public int supportsParameters(ParameterBlock params) {
    return 0;
  }

  @Override
  public String getMeasurementDescription() {
    return "";
  }

  @Override
  public String getMeasurementName() {
    return "Accuracy measure (unfinished)";
  }

}
