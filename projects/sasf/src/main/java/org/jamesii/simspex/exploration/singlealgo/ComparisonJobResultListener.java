/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.exploration.singlealgo;


import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.perfdb.entities.IApplication;
import org.jamesii.perfdb.entities.IPerformanceType;
import org.jamesii.perfdb.entities.IProblemInstance;
import org.jamesii.perfdb.recording.performance.plugintype.PerformanceMeasurerFactory;
import org.jamesii.perfdb.util.ParameterBlocks;
import org.jamesii.simspex.gui.IPerfRecorderListener;


/**
 * Simple execution listener that records the results of the algorithmic change.
 * 
 * @author Roland Ewald
 * 
 */
public class ComparisonJobResultListener implements IPerfRecorderListener {

  /** The comparison result to be calculated. */
  private final ComparisonResult results;

  /**
   * The RTC comparator that was used for defining the job. Holds performance
   * information on old configurations.
   */
  private final RTCComparator comparator;

  /**
   * The mapping between the string representation of a parameter block and its
   * corresponding configuration's ID.
   */
  private Map<String, Long> paramsConfigMap = new HashMap<>();

  /** The performance type of interest. */
  private IPerformanceType perfType;

  /** The performance type factory. */
  private PerformanceMeasurerFactory perfTypeFac = null;

  /**
   * Instantiates a new comparison job result listener.
   * 
   * @param compJob
   *          the comparison job
   */
  public ComparisonJobResultListener(ComparisonJob job,
      IPerformanceType performanceType) {
    results = job.getComparisonResult();
    comparator = results.getRTCComparator();
    perfType = performanceType;

    for (Entry<Long, ParameterBlock> pb : job.getConfigs().entrySet()) {
      if (paramsConfigMap.containsKey(pb.getValue().toString())) {
        throw new IllegalArgumentException(
            "The configuration list contains the block '"
                + pb.getValue().toString() + "' twice!");
      }
      paramsConfigMap.put(ParameterBlocks.toUniqueString(pb.getValue()),
          pb.getKey());
    }
  }

  @Override
  public void performanceRecorded(IProblemInstance probInst,
      IApplication application,
      Map<PerformanceMeasurerFactory, Double> performances) {

    String execParams =
        ParameterBlocks.toUniqueString(application.getRuntimeConfiguration()
            .getSelectionTree().toParamBlock());

    Long configID = paramsConfigMap.get(execParams);
    if (configID == null) {
      throw new IllegalStateException("Could not find config ID for:"
          + execParams);
    }

    if (perfTypeFac == null) {
      for (PerformanceMeasurerFactory fac : performances.keySet()) {
        if (perfType.getPerformanceMeasurerFactory().equals(fac.getClass())) {
          perfTypeFac = fac;
          break;
        }
      }
    }
    Double performance = performances.get(perfTypeFac);

    if (performance == null) {
      throw new IllegalStateException("Could not performance of type :"
          + perfType);
    }

    results.registerPerformance(configID, performance);
  }

  protected RTCComparator getComparator() {
    return comparator;
  }
}
