/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.entities;

/**
 * Interface to represent a performance measurements. A performance associates
 * the application (see {@link IApplication}) of a {@link IRuntimeConfiguration}
 * to a {@link IProblemInstance} with a concrete performance value, which is in
 * turn associated with a certain {@link IPerformanceType} (e.g., execution
 * speed).
 * 
 * @author Roland Ewald
 * 
 */
public interface IPerformance extends IIDEntity {

  /**
   * Gets the application for which the performance was measured.
   * 
   * @return the application
   */
  IApplication getApplication();

  /**
   * Sets the application for which the performance was measured.
   * 
   * @param application
   *          the new application
   */
  void setApplication(IApplication application);

  /**
   * Gets the performance type.
   * 
   * @return the performance type
   */
  IPerformanceType getPerformanceType();

  /**
   * Sets the performance type.
   * 
   * @param perfType
   *          the new performance type
   */
  void setPerformanceType(IPerformanceType perfType);

  /**
   * Gets the performance.
   * 
   * @return the performance
   */
  double getPerformance();

  /**
   * Sets the performance.
   * 
   * @param performance
   *          the new performance
   */
  void setPerformance(double performance);

}