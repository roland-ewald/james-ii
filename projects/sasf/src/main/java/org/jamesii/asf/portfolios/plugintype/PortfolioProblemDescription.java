/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios.plugintype;


import java.util.logging.Level;

import org.jamesii.SimSystem;

/**
 * The Class PortfolioProblemDescription.
 * 
 * @author Roland Ewald
 */
public class PortfolioProblemDescription {

  /** The performance data. One array in the element per performance metric. */
  private final PortfolioPerformanceData[] performanceData;

  /** The minimal size of an eligible portfolio. */
  private final int minSize;

  /** The maximal size of an eligible portfolio. */
  private final int maxSize;

  /** The acceptable risk. */
  private final double acceptableRisk;

  /**
   * Flags to determine whether maximization or minimization shall be done (per
   * performance metric).
   */
  private final boolean maximizationFlags[];

  /**
   * Instantiates a new portfolio problem description. Simplified constructor
   * for single-dimensional data.
   * 
   * @param perfData
   *          the performance data
   * @param acceptableRisk
   *          the acceptable risk
   * @param maximize
   *          the maximize
   * @param minSize
   *          the minimal size
   * @param maxSize
   *          the max size
   */
  public PortfolioProblemDescription(PortfolioPerformanceData perfData,
      double acceptableRisk, boolean maximize, int minSize, int maxSize) {
    this(new PortfolioPerformanceData[] { perfData }, acceptableRisk,
        new boolean[] { maximize }, minSize, maxSize);
  }

  /**
   * Creates portfolio problem.
   * 
   * @param perfData
   *          performance data
   * @param acceptableRisk
   *          risk factor, should be in [0,1] where 0 means no risk and 1 means
   *          maximum risk
   * @param maximize
   *          if true, a portfolio that maximizes performance will be searched
   *          for, otherwise portfolio with minimal performance gets selected
   * @param minSize
   *          the minimal size of the portfolio
   * @param maxSize
   *          the maximal size of the portfolio
   * 
   */
  public PortfolioProblemDescription(PortfolioPerformanceData[] perfData,
      double acceptableRisk, boolean[] maximize, int minSize, int maxSize) {
    this.performanceData = perfData;
    this.acceptableRisk = acceptableRisk;
    this.maximizationFlags = maximize;

    checkPerfDataDimensions(perfData);

    // Check whether minSize <= maxSize <= numOfAlgorithm
    if (maxSize < 1 || minSize < 1) {
      throw new IllegalArgumentException(
          "Both min and max size have to be >= 1.");
    }

    if (maxSize > perfData[0].performances.length) {
      this.maxSize = perfData[0].performances.length;
      SimSystem.report(Level.WARNING,
          "PortfolioProblemDescription: max size was"
              + " greater than the number of algortihms, has been reset to:"
              + this.maxSize);
    } else {
      this.maxSize = maxSize;
    }

    if (minSize > this.maxSize) {
      this.minSize = this.maxSize;
      SimSystem.report(Level.INFO, "PortfolioProblemDescription: min size was"
          + " greater than max size, has been reset to:" + this.maxSize);
    } else {
      this.minSize = minSize;
    }
  }

  /**
   * Checks the dimensions of the performance data.
   * 
   * @param perfData
   *          the performance data
   */
  private void checkPerfDataDimensions(PortfolioPerformanceData[] perfData) {
    if (perfData.length == 0) {
      throw new IllegalArgumentException("No performance data is given.");
    }
    int numOfAlgos = perfData[0].performances.length;
    for (int i = 1; i < perfData.length; i++) {
      if (numOfAlgos != perfData[i].performances.length) {
        throw new IllegalArgumentException(
            "Number of algorithms in performance data " + i
                + " is not equal to that at index 0!");
      }
    }
  }

  /**
   * Gets the performances for maximisation.
   * 
   * @param index
   *          the index of the performance data
   * @return the performances for maximisation
   */
  public Double[][] getPerformancesForMaximization(int index) {
    if (maximizationFlags[index]) {
      return performanceData[index].performances;
    }
    return invert(performanceData[index].performances);
  }

  /**
   * Inverts the performance data. In other words, creates new matrix with all
   * original values being multiplied by -1.
   * 
   * @param performances
   *          the old performances matrix
   * 
   * @return the new performance matrix
   */
  protected Double[][] invert(Double[][] performances) {
    if (performances.length == 0) {
      return performances;
    }
    int rows = performances.length;
    int cols = performances[0].length;
    Double[][] invertedMatrix = new Double[rows][cols];

    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        invertedMatrix[i][j] = -1 * performances[i][j];
      }
    }

    return invertedMatrix;
  }

  public int getMinSize() {
    return minSize;
  }

  public int getMaxSize() {
    return maxSize;
  }

  public PortfolioPerformanceData[] getPerformanceData() {
    return performanceData;
  }

  public double getAcceptableRisk() {
    return acceptableRisk;
  }

  public boolean[] getMaximizationFlags() {
    return maximizationFlags;
  }
}
