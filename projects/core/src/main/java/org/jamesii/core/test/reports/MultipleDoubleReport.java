/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.test.reports;

import java.util.List;

/**
 * Super class for reports containing more than one double value.
 * 
 * @author Sven Kluge
 * 
 */
public class MultipleDoubleReport implements ITestReport {

  /**
   * The serialization ID.
   */
  private static final long serialVersionUID = 1237296952310982349L;

  /**
   * The test values.
   */
  private final double[] values;

  /**
   * The type of the test.
   */
  private final Class<?> type;

  /**
   * The variables to be tested.
   */
  private final List<String> variables;

  /**
   * Default constructor.
   * 
   * @param variables
   *          the names of the variables
   * @param values
   *          the corresponding values
   * @param type
   *          the type of the test
   */
  public MultipleDoubleReport(List<String> variables, double[] values,
      Class<?> type) {
    this.values = values;
    this.variables = variables;
    this.type = type;
  }

  /**
   * Get the value from the array with the index n.
   * 
   * @param n
   *          the index
   * @return the value at the index
   */
  public Double getValue(int n) {
    return values[n];
  }

  /**
   * Get the variable from the array with the index n
   * 
   * @param n
   *          index
   * @return name of the variable
   */
  public String getVariable(int n) {
    return variables.get(n);
  }

  @Override
  public Class<?> getTestType() {
    return type;
  }

  @Override
  public String resultToString() {
    String s = "";
    for (int i = 0; i < values.length; i++) {
      s += variables.get(i) + "-" + type + ": " + values[i] + "\n";
    }
    return s;
  }

  @Override
  public List<String> getInvolvedVariables() {
    return variables;
  }

  @Override
  public boolean finished() {
    return false;
  }

}
