/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.jdbc;

import org.jamesii.perfdb.jdbc.Performance;
import org.jamesii.perfdb.jdbc.PerformanceType;
import org.jamesii.perfdb.jdbc.RuntimeConfiguration;

/**
 * @author Roland Ewald
 * 
 */
@Deprecated
public class PerfMeasurementTest extends PerfDBTest<Performance> {

  public PerfMeasurementTest() throws Exception {
    super();
  }

  @Override
  public void changeEntity(Performance instance) {
    instance.setPerformance(0.23);
  }

  @Override
  public void compareRetrievedEntity(Performance expected, Performance actual) {
    assertEquals(expected.getPerformance(), actual.getPerformance());
  }

  @Override
  @Deprecated
  // Performances need Application implementation
  public Performance getEntity() throws Exception {
    RuntimeConfiguration rtConfig = (new RTConfigTest()).getConnectedEntity();
    rtConfig.create();
    PerformanceType pm = (new PerfMeasureTest()).getConnectedEntity();
    pm.create();
    return new Performance(null, pm, 0.5);
  }
}
