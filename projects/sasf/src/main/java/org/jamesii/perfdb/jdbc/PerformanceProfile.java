/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.jdbc;

import org.jamesii.perfdb.entities.IPerformance;
import org.jamesii.perfdb.entities.IPerformanceType;
import org.jamesii.perfdb.entities.IProblemDefinition;

/**
 * A performance profile holds statistical information regarding the performance
 * of the simulation system for a given problem, and with respect to a certain
 * performance measure.
 * 
 * @author Roland Ewald
 */
@Deprecated
public class PerformanceProfile implements IPerformaneProfile {

  /** Serialisation ID. */
  private static final long serialVersionUID = 5465310291066503133L;

  /** The simulation problem for which the performance statistics are collected. */
  private IProblemDefinition problemDefinition;

  /** The performance measure to be used. */
  private IPerformanceType performanceMeasure;

  /** Maximum performance. */
  private IPerformance max;

  /** Minimum performance. */
  private IPerformance min;

  /** Number of performance measurements for this problem and of this kind. */
  private int sampleSize;

  /** Mean performance. */
  private double mean;

  /** Standard deviation. */
  private double stdDeviation;

  /**
   * Instantiates a new performance profile.
   * 
   * @param simProblem
   *          the sim problem
   * @param perfMeasure
   *          the perf measure
   * @param maxPerf
   *          the max perf
   * @param minPerf
   *          the min perf
   * @param sampSize
   *          the samp size
   * @param mn
   *          the mn
   * @param stdDev
   *          the std dev
   */
  public PerformanceProfile(IProblemDefinition simProblem,
      IPerformanceType perfMeasure, IPerformance maxPerf, IPerformance minPerf,
      int sampSize, double mn, double stdDev) {
    problemDefinition = simProblem;
    performanceMeasure = perfMeasure;
    max = maxPerf;
    min = minPerf;
    sampleSize = sampSize;
    mean = mn;
    stdDeviation = stdDev;
  }

  protected PerformanceProfile() {

  }

  @Override
  public IProblemDefinition getSimulationProblem() {
    return problemDefinition;
  }

  @Override
  public void setSimulationProblem(IProblemDefinition problemDefinition) {
    this.problemDefinition = problemDefinition;
  }

  @Override
  public IPerformanceType getPerformanceMeasure() {
    return performanceMeasure;
  }

  @Override
  public void setPerformanceMeasure(IPerformanceType performanceMeasure) {
    this.performanceMeasure = performanceMeasure;
  }

  @Override
  public IPerformance getMax() {
    return max;
  }

  @Override
  public void setMax(IPerformance max) {
    this.max = max;
  }

  @Override
  public IPerformance getMin() {
    return min;
  }

  @Override
  public void setMin(IPerformance min) {
    this.min = min;
  }

  @Override
  public int getSampleSize() {
    return sampleSize;
  }

  @Override
  public void setSampleSize(int sampleSize) {
    this.sampleSize = sampleSize;
  }

  @Override
  public double getMean() {
    return mean;
  }

  @Override
  public void setMean(double mean) {
    this.mean = mean;
  }

  @Override
  public double getStdDeviation() {
    return stdDeviation;
  }

  @Override
  public void setStdDeviation(double stdDeviation) {
    this.stdDeviation = stdDeviation;
  }

}