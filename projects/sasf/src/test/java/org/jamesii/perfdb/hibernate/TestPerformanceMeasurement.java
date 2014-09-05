/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.hibernate;


/**
 * Test for {@link Performance}.
 * 
 * @author Roland Ewald
 */
public class TestPerformanceMeasurement extends
    TestHibernateEntity<Performance> {

  /** The test performance value. */
  static final double PERFORMANCE = 0.23;

  @Override
  protected void changeEntity(Performance entity) {
    entity.setPerformance(4.2);
  }

  @Override
  protected void checkEquality(Performance entity) {
    assertEquals(PERFORMANCE, entity.getPerformance());
  }

  @Override
  protected Performance getEntity(String name) throws Exception {
    Performance pm = new Performance();
    pm.setPerformance(PERFORMANCE);
    TestPerformanceMeasure tpm = new TestPerformanceMeasure(session);
    pm.setPerformanceType(tpm.createEntity("perf measure for perf measurement "
        + name));

    TestApplication ta = new TestApplication(session);
    pm.setApplication(ta.createEntity("test application for perf measurement "
        + name));
    return pm;
  }
}
