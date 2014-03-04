/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.entities;

import org.jamesii.perfdb.recording.performance.plugintype.PerformanceMeasurerFactory;

/**
 * Interface for performance types (e.g., runtime, energy consumption,
 * accuracy).
 * 
 * @author Roland Ewald
 * 
 */
public interface IPerformanceType extends INamedDBEntity {

  /**
   * Gets the performance measurer factory.
   * 
   * @return the performance measurer factory
   */
  Class<? extends PerformanceMeasurerFactory> getPerformanceMeasurerFactory();

  /**
   * Sets the performance measurer factory.
   * 
   * @param performanceMeasurerFactory
   *          the new performance measurer factory
   */
  void setPerformanceMeasurerFactory(
      Class<? extends PerformanceMeasurerFactory> performanceMeasurerFactory);

}
