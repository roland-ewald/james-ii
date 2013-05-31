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
 * Test report comprising a list of reports.
 * 
 * @author Stefan Leye
 * 
 */
public class MultiTestReport implements ITestReport {

  /**
   * The serialization ID.
   */
  private static final long serialVersionUID = 785550632565223680L;

  /**
   * The list of test reports.
   */
  private final List<ITestReport> reports;

  /**
   * The type of the test.
   */
  private final Class<?> type;

  /**
   * The variables to be tested.
   */
  private final List<String> variables = new ArrayList<>();

  /**
   * Default constructor.
   * 
   * @param reports
   *          the list of reports
   * @param type
   *          the type of the test
   */
  public MultiTestReport(List<ITestReport> reports, Class<?> type) {
    this.reports = reports;
    for (ITestReport report : reports) {
      for (String var : report.getInvolvedVariables()) {
        if (!variables.contains(var)) {
          variables.add(var);
        }
      }
    }
    this.type = type;
  }

  @Override
  public List<String> getInvolvedVariables() {
    return variables;
  }

  @Override
  public Class<?> getTestType() {
    return type;
  }

  /**
   * Gets the reports.
   * 
   * @return the reports
   */
  public List<ITestReport> getReports() {
    return reports;
  }

  @Override
  public String resultToString() {
    String result = "";
    for (ITestReport report : reports) {
      result += report.toString() + "\n";
    }
    return result;
  }

  @Override
  public boolean finished() {
    return false;
  }

}
