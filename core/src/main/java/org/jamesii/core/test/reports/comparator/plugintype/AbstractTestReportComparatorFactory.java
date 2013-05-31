/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.test.reports.comparator.plugintype;

import org.jamesii.core.factories.AbstractFilteringFactory;

/**
 * Abstract factory for comparators, comparing test reports.
 * 
 * @author Stefan Leye
 * 
 */
public class AbstractTestReportComparatorFactory extends
    AbstractFilteringFactory<TestReportComparatorFactory> {

  /**
   * The serialization ID.
   */
  private static final long serialVersionUID = 5902823672603744320L;

  /**
   * Tag for the type of the reports to be compared.
   */
  public static final String REPORT_TYPE = "reportType";

}