/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.test.reports.comparator;

import org.jamesii.core.test.reports.StatisticalMeasureReport;

/**
 * This class compares test reports comprising means and variances.
 * 
 * @author Stefan Leye
 * 
 */
public class StatisticalMeasureComparator implements
    ITestReportComparator<StatisticalMeasureReport> {

  @Override
  public Double compare(StatisticalMeasureReport report1,
      StatisticalMeasureReport report2) {
    Double mean1 = report1.getMean();
    Double mean2 = report2.getMean();
    Double absMean1 = Math.abs(mean1);
    Double absMean2 = Math.abs(mean2);
    Double meanDiff;
    if (absMean1 > absMean2) {
      meanDiff = Math.abs(mean1 - mean2) / (2 * absMean1);
    } else {
      meanDiff = Math.abs(mean1 - mean2) / (2 * absMean2);
    }
    Double var1 = report1.getVariance();
    Double var2 = report2.getVariance();
    Double absVar1 = Math.abs(var1);
    Double absVar2 = Math.abs(var2);
    Double varDiff;
    if (absVar1 > absVar2) {
      varDiff = Math.abs(var1 - var2) / (2 * absVar1);
    } else {
      varDiff = Math.abs(var1 - var2) / (2 * absVar2);
    }
    return 1 - ((1 - meanDiff) * (1 - varDiff));
  }

}
