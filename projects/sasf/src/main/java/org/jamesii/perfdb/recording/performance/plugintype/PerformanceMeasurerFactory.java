/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.recording.performance.plugintype;

import org.jamesii.core.factories.Factory;
import org.jamesii.core.factories.IParameterFilterFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.perfdb.recording.performance.IPerformanceMeasurer;


/**
 * Factory to create performance measurer.
 * 
 * @author Roland Ewald
 * 
 */
public abstract class PerformanceMeasurerFactory extends
    Factory<IPerformanceMeasurer<?>> implements IParameterFilterFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = -4178908928947597394L;

  /** The performance data to be considered. */
  public static final String PERFORMANCE_DATA = "performanceData";

  /** The parameters of the problem, that has been worked on. */
  public static final String PROBLEM_PARAMETERS = "problemParameters";

  /**
   * Get name of the measurement that the created measurer will provide.
   * 
   * @return name
   */
  public abstract String getMeasurementName();

  /**
   * Get description of the measurement that the created measurer will provide.
   * 
   * @return description
   */
  public abstract String getMeasurementDescription();

  /**
   * Creates the performance measurer.
   * 
   * @param parameterBlock
   *          the parameter block to be used for creation
   * @return the performance measurer
   */
  @Override
  public abstract IPerformanceMeasurer<?> create(ParameterBlock parameterBlock);

  /**
   * Defines whether this performance measure shall be maximised or not
   * (default).
   */
  public boolean isForMaximisation() {
    return false;
  }

  /**
   * Gets a hash to cache performance measurers by factory and parameters.
   * 
   * @param perfMeasurerFactory
   *          the performance measurer factory
   * @param perfMeasurerParams
   *          the performance measurer parameters
   * @return the performance measurer hash
   */
  public static String getPerformanceMeasurerHash(
      PerformanceMeasurerFactory perfMeasurerFactory,
      ParameterBlock perfMeasurerParams) {
    return perfMeasurerParams.toString()
        + perfMeasurerFactory.getClass().getCanonicalName();
  }

}
