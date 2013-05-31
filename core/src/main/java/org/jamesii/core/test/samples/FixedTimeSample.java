/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.test.samples;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple implementation of a fixed time sample.
 * 
 * @param <V>
 *          the type of the number to be sampled
 * 
 * @author Roland Ewald
 */
public class FixedTimeSample<V extends Number> implements IFixedTimeSample<V> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 8479499196734619072L;

  /** Holds sample data. */
  private final List<V> sampleData = new ArrayList<>();

  /**
   * Name of the variable associated to this sample.
   */
  private final String variableName;

  /** Sample time. Needs to be set by the class producing the sample. */
  private final double sampleSimTime;

  /** Class of the values that were sampled. */
  private final Class<V> sampleValClass;

  /**
   * Default constructor.
   * 
   * @param valueClass
   *          class of the values that were sampled
   * @param variableName
   *          the name of the variable
   * @param time
   *          time point, at which the sample has been taken
   */

  public FixedTimeSample(Class<V> valueClass, String variableName, double time) {
    sampleValClass = valueClass;
    sampleSimTime = time;
    this.variableName = variableName;
  }

  @Override
  public double getSampleSimTime() {
    return sampleSimTime;
  }

  @Override
  public List<V> getData() {
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
