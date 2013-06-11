/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.test.reports.comparator;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.test.reports.StatisticalMeasureReport;
import org.jamesii.core.test.reports.comparator.plugintype.AbstractTestReportComparatorFactory;
import org.jamesii.core.test.reports.comparator.plugintype.TestReportComparatorFactory;

/**
 * Factory for comparator, comparing statistical measure reports.
 * 
 * @author Stefan Leye
 * 
 */
public class StatisticalMeasureComparatorFactory extends
    TestReportComparatorFactory {

  /**
   * The serialization ID
   */
  private static final long serialVersionUID = -8616442331873218794L;

  @Override
  public ITestReportComparator create(ParameterBlock params) {
    return new StatisticalMeasureComparator();
  }

  @Override
  public int supportsParameters(ParameterBlock params) {
    if (params != null
        && StatisticalMeasureReport.class.isAssignableFrom((Class<?>) params
            .getSubBlockValue(AbstractTestReportComparatorFactory.REPORT_TYPE))) {
      return 1;
    }
    return 0;
  }
}
