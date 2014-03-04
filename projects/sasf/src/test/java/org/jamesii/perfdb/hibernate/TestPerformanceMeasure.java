/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.hibernate;


import org.hibernate.Session;
import org.jamesii.perfdb.hibernate.PerformanceType;
import org.jamesii.perfdb.recording.performance.plugintype.PerformanceMeasurerFactory;
import org.jamesii.perfdb.recording.performance.totaltime.TotalRuntimePerfMeasurerFactory;

/**
 * Tests {@link PerformanceType}.
 * 
 * @author Roland Ewald
 * 
 */
public class TestPerformanceMeasure extends
    TestHibernateEntity<PerformanceType> {

  /** The performance measurer factory. */
  static final Class<? extends PerformanceMeasurerFactory> PERF_MEASURER_FACTORY =
      TotalRuntimePerfMeasurerFactory.class;

  /**
   * Instantiates a new test performance measure.
   */
  public TestPerformanceMeasure() {
    super(true);
  }

  /**
   * Instantiates a new test performance measure.
   * 
   * @param s
   *          the session
   */
  public TestPerformanceMeasure(Session s) {
    super(s);
  }

  @Override
  protected void changeEntity(PerformanceType entity) {
  }

  @Override
  protected void checkEquality(PerformanceType entity) {
    assertEquals(PERF_MEASURER_FACTORY, entity.getPerformanceMeasurerFactory());
  }

  @Override
  protected PerformanceType getEntity(String name) {
    PerformanceType pm = new PerformanceType();
    pm.setName(name);
    pm.setPerformanceMeasurerFactory(PERF_MEASURER_FACTORY);
    return pm;
  }

}
