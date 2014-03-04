/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.recording.performance.accuracy;

import org.jamesii.core.experiments.ComputationTaskRuntimeInformation;
import org.jamesii.perfdb.recording.performance.IPerformanceMeasurer;


/**
 * Measurement for accuracy of snapshots of state vectors, outputs, etc.
 * 
 * @author Roland Ewald
 * 
 */
public class SnapshotAccuracyPerformance implements
    IPerformanceMeasurer<ComputationTaskRuntimeInformation> {

  @Override
  public double measurePerformance(ComputationTaskRuntimeInformation srti) {
    throw new UnsupportedOperationException();
  }

}
