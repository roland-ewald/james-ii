/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.test.samples;

import java.util.Map;

/**
 * Simple implementation of a time series sample. Holds a map containing the
 * variable names and their trajectories.
 * 
 * @param <V>
 *          the type of the sampeled data
 * 
 * @author Stefan Leye
 */
public class TrajectorySample<V extends Number> implements ITrajectorySample<V> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1059286827902658892L;

  /**
   * Holds sample data. Data for each variable should be a tree map, since the
   * time order may be important during further handling.
   */
  private final Map<Double, V> sampleData;

  /**
   * Name of the variable associated to this sample.
   */
  private final String variableName;

  /** The start time. */
  private final double startTime;

  /** The end time. */
  private final double endTime;

  /** Class of the values that were sampled. */
  private final Class<V> sampleValClass;

  /**
   * Default constructor.
   * 
   * @param sampleClass
   *          the sample class
   * @param variableName
   *          the variable name
   * @param data
   *          the data
   * @param startTime
   *          the start time
   * @param endTime
   *          the end time
   */

  public TrajectorySample(Class<V> sampleClass, String variableName,
      Map<Double, V> data, double startTime, double endTime) {
    this.sampleValClass = sampleClass;
    this.sampleData = data;
    this.startTime = startTime;
    this.endTime = endTime;
    this.variableName = variableName;
  }

  @Override
  public double getEndTime() {
    return endTime;
  }

  @Override
  public double getStartTime() {
    return startTime;
  }

  @Override
  public Map<Double, V> getData() {
    return sampleData;
  }

  @Override
  public String getVariableName() {
    return variableName;
  }

  @Override
  public Class<V> getVariableValueClass() {
    return sampleValClass;
  }

}
