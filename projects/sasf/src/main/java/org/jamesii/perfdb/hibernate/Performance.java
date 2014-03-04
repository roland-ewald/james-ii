/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.hibernate;

import org.jamesii.perfdb.entities.IApplication;
import org.jamesii.perfdb.entities.IPerformance;
import org.jamesii.perfdb.entities.IPerformanceType;

/**
 * Hibernate implementation for performance measurements.
 * 
 * @author Roland Ewald
 * 
 */
@SuppressWarnings("unused")
// Hibernate uses private functions
public class Performance extends IDEntity implements IPerformance {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 9040474249292882832L;

  /** Reference to the application that was evaluated. */
  private Application application = null;

  /** The performance measure used for this measurement. */
  private PerformanceType performanceType = null;

  /** The measured performance. */
  private double performance = 0.0;

  /**
   * Empty constructor for beans compliance.
   */
  public Performance() {
  }

  /**
   * Instantiates a new performance.
   * 
   * @param app
   *          the application
   * @param perfMeasure
   *          the performance measure
   * @param perf
   *          the performance value
   */
  public Performance(Application app, PerformanceType perfMeasure, double perf) {
    application = app;
    performanceType = perfMeasure;
    performance = perf;
  }

  @Override
  public double getPerformance() {
    return performance;
  }

  @Override
  public IPerformanceType getPerformanceType() {
    return performanceType;
  }

  @Override
  public IApplication getApplication() {
    return application;
  }

  @Override
  public void setPerformance(double perf) {
    performance = perf;
  }

  @Override
  public void setPerformanceType(IPerformanceType perfType) {
    if (!(perfType instanceof PerformanceType)) {
      throw new IllegalArgumentException();
    }
    performanceType = (PerformanceType) perfType;
  }

  @Override
  public void setApplication(IApplication app) {
    application = (Application) app;
  }

  private Application getApp() {
    return application; // NOSONAR:{used_by_hibernate}
  }

  private void setApp(Application app) {
    application = app; // NOSONAR:{used_by_hibernate}
  }

  private PerformanceType getPerfType() {
    return performanceType; // NOSONAR:{used_by_hibernate}
  }

  private void setPerfType(PerformanceType perfType) {
    performanceType = perfType; // NOSONAR:{used_by_hibernate}
  }
}
