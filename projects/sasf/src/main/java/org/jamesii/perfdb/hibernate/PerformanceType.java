/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.hibernate;

import org.jamesii.perfdb.entities.IPerformanceType;
import org.jamesii.perfdb.recording.performance.plugintype.PerformanceMeasurerFactory;

/**
 * Hibernate implementation of a performance type.
 * 
 * @author Roland Ewald
 * 
 */
public class PerformanceType extends NamedDBEntity implements IPerformanceType {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 770801639872187022L;

  /** Reference to performance measurer factory. */
  private Class<? extends PerformanceMeasurerFactory> performanceMeasurerFactory;

  /**
   * Empty constructor for beans compliance.
   */
  public PerformanceType() {
  }

  /**
   * Instantiates a new performance type.
   * 
   * @param typeName
   *          the type name
   * @param typeDesc
   *          the type description
   * @param measurerClass
   *          the measurer class
   */
  public PerformanceType(String typeName, String typeDesc,
      Class<? extends PerformanceMeasurerFactory> measurerClass) {
    super(typeName, typeDesc);
    performanceMeasurerFactory = measurerClass;
  }

  @Override
  public Class<? extends PerformanceMeasurerFactory> getPerformanceMeasurerFactory() {
    return performanceMeasurerFactory;
  }

  @Override
  public void setPerformanceMeasurerFactory(
      Class<? extends PerformanceMeasurerFactory> pmFactory) {
    performanceMeasurerFactory = pmFactory;
  }

}
