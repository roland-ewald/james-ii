/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb;

import java.util.List;
import java.util.Map;

import org.jamesii.perfdb.entities.IApplication;
import org.jamesii.perfdb.entities.IPerformance;
import org.jamesii.perfdb.entities.IPerformanceType;
import org.jamesii.perfdb.recording.performance.plugintype.PerformanceMeasurerFactory;

/**
 * Interface of data base for performance measurement data.
 * 
 * @author Roland Ewald
 * 
 */
public interface IMeasureDatabase {

  /**
   * Get all types of performance that are registered in the database.
   * 
   * @return list of performance types
   */
  List<IPerformanceType> getAllPerformanceTypes();

  /**
   * Returns performance measure for given factory class.
   * 
   * @param measurerClass
   *          the class of the performance measurer factory
   * @return the performance type object, or null if not existing
   */
  IPerformanceType getPerformanceType(
      Class<? extends PerformanceMeasurerFactory> measurerClass);

  /**
   * Creates new performance type with these properties (if it does not yet
   * exist).
   * 
   * @param typeName
   *          the name of the performance type
   * @param typeDesc
   *          a description of the performance type
   * @param measurerClass
   *          the factory class to create a performance measurer
   * @return a (maybe newly created) performance type
   */
  IPerformanceType newPerformanceType(String typeName, String typeDesc,
      Class<? extends PerformanceMeasurerFactory> measurerClass);

  /**
   * Creates new performance entry for given performance type and application.
   * 
   * @param application
   *          the application (object representing a tuple [c,i,h]- i.e., using
   *          configuration c on simulation problem instance i with hardware
   *          setup h)
   * @param performanceMeasure
   *          the performance type
   * @param performance
   *          the actual performance
   * @return the performance entry that was written to the database
   */
  IPerformance newPerformance(IApplication application,
      IPerformanceType performanceMeasure, double performance);

  /**
   * Get all performance data for a given application.
   * 
   * @param application
   *          the application
   * @return list of performances associated with this application
   */
  List<IPerformance> getAllPerformances(IApplication application);

  /**
   * Get all performance data for a given {@link IPerformanceType}. The data is
   * returned as a map from application IDs ({@link IApplication#getID()}) to
   * the actual performance measurements.
   * 
   * @param perfType
   *          the performance type to be used
   * @return the map application id => performance
   */
  Map<Long, Double> getAllPerformancesMap(IPerformanceType perfType);

  /**
   * Get the performance of an {@link IApplication} with respect to a specific
   * {@link IPerformanceType}.
   * 
   * @param application
   *          the application
   * @param perfType
   *          the desired performance type
   * @return the application's performance of that kind, null if not available
   */
  IPerformance getPerformance(IApplication application,
      IPerformanceType perfType);
}
