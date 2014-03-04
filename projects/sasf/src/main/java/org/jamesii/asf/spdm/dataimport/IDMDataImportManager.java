/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.spdm.dataimport;

/**
 * Interface for components that manage the import of simulation performance
 * data for data mining.
 * 
 * @param <T>
 *          the type of the performance tuple
 * @author Roland Ewald
 */
public interface IDMDataImportManager<T extends PerformanceTuple> {

  /**
   * Get performance tuple.
   * 
   * @return list of performance tuples
   */
  PerformanceDataSet<T> getPerformanceData();

}
