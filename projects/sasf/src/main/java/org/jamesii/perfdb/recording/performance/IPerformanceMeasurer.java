/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.recording.performance;

/**
 * Interface for a performance measurer. From what kind of objects the
 * performance can be measured depends on the generic type of the measurer. In
 * general, the performance of a
 * {@link org.jamesii.perfdb.entities.IRuntimeConfiguration} when being applied to a
 * {@link org.jamesii.perfdb.entities.IProblemInstance} is measured. While the
 * application as such is represented by a
 * {@link org.jamesii.perfdb.entities.IApplication} entity, the performance of the
 * {@link org.jamesii.perfdb.entities.IRuntimeConfiguration} is measured by objects
 * implementing this interface (and will be associated with the corresponding
 * {@link org.jamesii.perfdb.entities.IApplication} entity).
 * 
 * In case of a larger-scale experiment, a performance measurer is called
 * repeatedly, i.e. it may operate in a stateful manner. This is useful, for
 * example, to cache the results of expensive performance calculations that are
 * required more than once.
 * 
 * @see org.jamesii.perfdb.entities.IApplication
 * 
 * @param <T>
 *          the type of the data structure needed to measure the performance
 * 
 * @author Roland Ewald
 */
public interface IPerformanceMeasurer<T> {

  /**
   * Measures the performance, extracts it from the given parameter.
   * 
   * @param performanceData
   *          the entity from which the performance shall be retrieved
   * @return the performance
   */
  double measurePerformance(T performanceData);

}
