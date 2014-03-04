/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.jdbc;

import org.jamesii.perfdb.jdbc.PerformanceType;
import org.jamesii.perfdb.recording.performance.totaltime.TotalRuntimePerfMeasurerFactory;

/**
 * @author Roland Ewald
 * 
 */
@Deprecated
public class PerfMeasureTest extends PerfDBTest<PerformanceType> {

  public PerfMeasureTest() throws Exception {
    super();
  }

  @Override
  public void changeEntity(PerformanceType instance) {
    instance.setName("new name");
    instance.setDescription("new description");
  }

  @Override
  public void compareRetrievedEntity(PerformanceType expected,
      PerformanceType actual) {
    assertEquals(expected.getName(), actual.getName());
    assertEquals(expected.getDescription(), actual.getDescription());
  }

  @Override
  public PerformanceType getEntity() throws Exception {
    return new PerformanceType("Performance measure",
        "Description of performance measure",
        TotalRuntimePerfMeasurerFactory.class);
  }

}
