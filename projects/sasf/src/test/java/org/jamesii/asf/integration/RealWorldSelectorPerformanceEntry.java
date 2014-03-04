/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.integration;

import java.util.List;

import org.jamesii.asf.registry.AlgoSelectionRegistry;


/**
 * Container for performance data of a selector, observed as deployed in the
 * {@link AlgoSelectionRegistry}.
 * 
 * 
 * @see SelectorGenerationTask
 * 
 * @author Roland Ewald
 * 
 */
public class RealWorldSelectorPerformanceEntry {

  /**
   * Brief description of the sample data that was used for evaluation.
   */
  String sampleDesc;

  /** The factory that generated this selector. */
  final String selectorGeneratorFactoryName;

  /** The predicted performance of the selector. */
  final double predictedPerformance;

  /** The execution times using the given selector. */
  List<Double> executionTimes;

  /** The sum of all execution times. */
  double execTimeSum = 0.0;

  /**
   * Instantiates a new selector performance entry.
   * 
   * @param sampleDescription
   *          the description of the sample data used for evaluation
   * @param usedPredictor
   *          the used predictor
   * @param performances
   *          the execution times per simulation problem
   * @param selGenFactoryName
   *          the name of the factory that generated this selector
   * @param predPerformance
   *          the performance predicted by SPDM
   */
  public RealWorldSelectorPerformanceEntry(String sampleDescription,
      List<Double> performances,
      String selGenFactoryName,
      double predPerformance) {
    sampleDesc = sampleDescription;
    selectorGeneratorFactoryName = selGenFactoryName;
    executionTimes = performances;
    predictedPerformance = predPerformance;

    for (Double performance : performances) {
      execTimeSum += performance;
    }
  }

  public String getSampleDesc() {
    return sampleDesc;
  }

  public void setSampleDesc(String sampleDesc) {
    this.sampleDesc = sampleDesc;
  }

  public String getSelectorGeneratorFactoryName() {
    return selectorGeneratorFactoryName;
  }

  public List<Double> getExecutionTimes() {
    return executionTimes;
  }

  public void setExecutionTimes(List<Double> executionTimes) {
    this.executionTimes = executionTimes;
  }

  public double getExecTimeSum() {
    return execTimeSum;
  }

  public void setExecTimeSum(double execTimeSum) {
    this.execTimeSum = execTimeSum;
  }

}
