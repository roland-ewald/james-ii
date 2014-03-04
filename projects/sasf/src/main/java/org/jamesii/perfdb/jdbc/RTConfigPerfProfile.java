/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.jdbc;

/**
 * This class represents a row in the performance profile view for
 * configurations.
 * 
 * @author Roland Ewald
 * 
 */
@Deprecated
public class RTConfigPerfProfile implements IRTConfigPerfProfile {

  private static final long serialVersionUID = -2433869390497541547L;

  /** The ID of the performance measurement type. */
  private final long perfID;

  /** The ID of the runtime configuration. */
  private final long configID;

  /** The minimal performance. */
  private final double minPerf;

  /** The average performance. */
  private final double avgPerf;

  /** The maximal performance. */
  private final double maxPerf;

  /** The sample size. */
  private final int sampleSize;

  /** The standard deviation. */
  private final double stdDev;

  public RTConfigPerfProfile(long confID, long pID, double minP, double avgP,
      double maxP, int sampleS, double stDev) {
    perfID = pID;
    configID = confID;
    minPerf = minP;
    avgPerf = avgP;
    maxPerf = maxP;
    sampleSize = sampleS;
    stdDev = stDev;
  }

  @Override
  public long getPerfID() {
    return perfID;
  }

  @Override
  public long getConfigID() {
    return configID;
  }

  @Override
  public double getMinPerf() {
    return minPerf;
  }

  @Override
  public double getAvgPerf() {
    return avgPerf;
  }

  @Override
  public double getMaxPerf() {
    return maxPerf;
  }

  @Override
  public int getSampleSize() {
    return sampleSize;
  }

  @Override
  public double getStdDev() {
    return stdDev;
  }
}
