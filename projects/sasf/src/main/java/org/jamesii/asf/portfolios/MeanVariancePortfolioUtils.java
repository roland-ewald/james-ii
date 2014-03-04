/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios;

import org.jamesii.core.math.Matrix;
import org.jamesii.core.math.statistics.BivariateDataAnalysis;
import org.jamesii.core.math.statistics.univariate.ArithmeticMean;
import org.jamesii.core.util.misc.Pair;

/**
 * Simple helper class that provides functions to select mean-variance
 * portfolios.
 * 
 * @author Roland Ewald
 * 
 */
public final class MeanVariancePortfolioUtils {
  
  /**
   * Should not be instantiated.
   */
  private MeanVariancePortfolioUtils() {
  }

  /**
   * Calculation to identify the efficiency of the portfolio given by the
   * weights. The (convex quadratic-programming w/ constraints) - formula is
   * (1/2)*w^T*Cov*w - q*R^T*w, where w are the weights of the portfolio, R are
   * the expected returns and Cov is the co-variance matrix. Since the case of
   * maximization is considered here, we multiply the expression by -1 and
   * arrive at q*R^T*w - (1/2)*w^T*Cov*w.
   * 
   * 
   * @param weights
   *          the vector of weights, characterising the portfolio
   * @param avgPerf
   *          the average performances, i.e. the expected returns
   * @param covMat
   *          the covariance matrix (Cov)
   * @param acceptableRisk
   *          the acceptable risk (q)
   * 
   * @return the suitability of the portfolio
   */
  public static double calcPortfolioEfficiency(double[] weights,
      Double[] avgPerf, Double[][] covMat, Double acceptableRisk) {
    Pair<Double, Double> fitnComp = calcMeanAndVariance(weights, avgPerf, covMat);
    return acceptableRisk * fitnComp.getFirstValue() - (1 - acceptableRisk)
        * fitnComp.getSecondValue();
  }

  /**
   * Calculates expected mean and variance of a given portfolio.
   * 
   * @param weights
   *          the weights of the portfolio
   * @param avgPerf
   *          the average performances of the assets
   * @param covMat
   *          the covariance matrix of the assets
   * @return the mean and variance
   */
  public static Pair<Double, Double> calcMeanAndVariance(double[] weights,
      Double[] avgPerf, Double[][] covMat) {
    Matrix w = new Matrix(weights);
    Matrix s = new Matrix(covMat);
    Matrix r = new Matrix(avgPerf);
    return new Pair<>(r.transpose().mult(w).getElement(0, 0), w
        .transpose().mult(s.mult(w)).getElement(0, 0));
  }

  /**
   * Gets the avg and cov from performances.
   * 
   * @param performances
   *          the performances
   * 
   * @return the avg and cov from performances
   */
  public static Pair<Double[], Double[][]> getAvgAndCovFromPerformances(
      Double[][] performances) {
    Double[] avgPerf = calculateAvgPerformances(performances);
    Double[][] covMatrix = calculateCovarianceMatrix(performances);
    return new Pair<>(avgPerf, covMatrix);
  }

  /**
   * Calculate average performance per algorithm.
   * 
   * @param performances
   *          the performances
   * 
   * @return the array of average performances
   */
  private static Double[] calculateAvgPerformances(Double[][] performances) {
    Double[] avgPerformances = new Double[performances.length];
    for (int algoIndex = 0; algoIndex < avgPerformances.length; algoIndex++) {
      avgPerformances[algoIndex] = ArithmeticMean
          .arithmeticMean(performances[algoIndex]);
    }
    return avgPerformances;
  }

  /**
   * Calculate covariance matrix.
   * 
   * @param performances
   *          the performances
   * 
   * @return the covariance matrix
   */
  private static Double[][] calculateCovarianceMatrix(Double[][] performances) {
    return BivariateDataAnalysis.covarianceMatrix(performances);
  }

}
