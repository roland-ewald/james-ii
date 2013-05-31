/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.test.reports;

import java.util.ArrayList;
import java.util.List;

/**
 * Test report, holding mean and variance of a set of observations on a specific
 * variable.
 * 
 * @author Stefan Leye
 * 
 */
public class StatisticalMeasureReport implements ITestReport {

  /**
   * The serialization ID.
   */
  private static final long serialVersionUID = 5634297054983973900L;

  /**
   * The arithmetic mean.
   */
  private final Double mean;

  /**
   * The variance.
   */
  private final Double variance;

  /**
   * The size of the sample.
   */
  private final int size;

  /**
   * The type of the test.
   */
  private final Class<?> type;

  /**
   * The variable which has been tested.
   */
  private final String variable;

  /**
   * Default constructor.
   * 
   * @param variable
   *          the variable
   * @param type
   *          the type of the test
   * @param size
   *          the size of the sample
   * @param mean
   *          the mean
   * @param variance
   *          the variance
   */
  public StatisticalMeasureReport(String variable, double mean,
      double variance, Class<?> type, int size) {
    this.variable = variable;
    this.mean = mean;
    this.variance = variance;
    this.type = type;
    this.size = size;
  }

  /**
   * Gets the size of the sample.
   * 
   * @return the size
   */
  public int getSize() {
    return size;
  }

  @Override
  public List<String> getInvolvedVariables() {
    List<String> result = new ArrayList<>();
    result.add(variable);
    return result;
  }

  @Override
  public Class<?> getTestType() {
    return type;
  }

  @Override
  public String resultToString() {
    return variable + "-" + type + "-mean: " + mean + "-variance: " + variance;
  }

  /**
   * Get the mean.
   * 
   * @return the mean
   */
  public Double getMean() {
    return mean;
  }

  /**
   * Get the variance.
   * 
   * @return the variance
   */
  public Double getVariance() {
    return variance;
  }

  @Override
  public boolean finished() {
    return false;
  }

}
