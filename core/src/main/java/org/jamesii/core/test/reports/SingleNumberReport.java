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
 * Super class for reports containing just one Double value.
 * 
 * @author Roland Ewald
 * 
 */
public class SingleNumberReport implements ITestReport {

  /**
   * The serialization ID.
   */
  private static final long serialVersionUID = 2602868387541834127L;

  /**
   * The confidence in the test results.
   */
  private final Number value;

  /**
   * The type of the test.
   */
  private final Class<?> type;

  /**
   * The variable.
   */
  private final String variable;

  private boolean finished = false;

  /**
   * Default constructor.
   * 
   * @param variable
   *          the variable
   * @param conf
   *          the confidence in the results
   * @param type
   *          the type of the test
   */
  public SingleNumberReport(String variable, Number conf, Class<?> type,
      boolean finished) {
    this.variable = variable;
    value = conf;
    this.type = type;
    this.finished = finished;
  }

  /**
   * Confidence in the results, if this can be provided. Should be in [0,1]. -1
   * if notion of confidence does not apply for this statistic test.
   * 
   * @return the confidence in the result
   */
  public Number getValue() {
    return value;
  }

  /**
   * Gets the variable.
   * 
   * @return the variable
   */
  public String getVariable() {
    return variable;
  }

  @Override
  public String resultToString() {
    return variable + "-" + type + ": " + value + "\n";
  }

  @Override
  public Class<?> getTestType() {
    return type;
  }

  @Override
  public List<String> getInvolvedVariables() {
    List<String> result = new ArrayList<>();
    result.add(variable);
    return result;
  }

  @Override
  public boolean finished() {
    return finished;
  }

}
