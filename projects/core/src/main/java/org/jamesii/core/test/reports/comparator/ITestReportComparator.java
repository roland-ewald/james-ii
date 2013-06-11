/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.test.reports.comparator;

import org.jamesii.core.test.reports.ITestReport;

/**
 * Basic interface for classes, comparing test reports.
 * 
 * @author Stefan Leye
 * 
 */
public interface ITestReportComparator<X extends ITestReport> {

  /**
   * Compares two tests and returns a double value between 0 and 1, denoting the
   * equality of them. 0 stands for absolutely equal, 1 for not equal at all.
   * 
   * @param report1
   *          the first report
   * @param report2
   *          the second report
   * @return the result of the comparison
   */
  Double compare(X report1, X report2);
}
