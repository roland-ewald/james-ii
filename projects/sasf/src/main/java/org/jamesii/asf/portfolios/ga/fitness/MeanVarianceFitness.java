/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios.ga.fitness;

import org.jamesii.asf.portfolios.MeanVariancePortfolioUtils;
import org.jamesii.asf.portfolios.plugintype.PortfolioProblemDescription;
import org.jamesii.core.util.misc.Pair;


/**
 * The calculates the mean-variance fitness of an individual.
 * 
 * @see MeanVariancePortfolioUtils
 * 
 * @author Roland Ewald
 */
public class MeanVarianceFitness extends AbstractPortfolioFitness {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -4358029730978595284L;

  /** The previous performances. */
  private Double[][] previousPerformances;

  /** The average performances. */
  private Double[] averagePerformances;

  /** The covariance matrix. */
  private Double[][] covMatrix;

  @Override
  public double calculateFitness(PortfolioProblemDescription problem,
      Double[][] performances, boolean[] genome) {

    // Check reference, since performances should stay the same...
    if (previousPerformances != performances) {
      previousPerformances = performances;
      Pair<Double[], Double[][]> avgPerfCov = MeanVariancePortfolioUtils
          .getAvgAndCovFromPerformances(performances);
      averagePerformances = avgPerfCov.getFirstValue();
      covMatrix = avgPerfCov.getSecondValue();
    }

    double[] weights = new double[genome.length];
    for (int i = 0; i < genome.length; i++) {
      if (genome[i]) {
        weights[i] = 1.0 / genome.length;
      }
    }

    return MeanVariancePortfolioUtils.calcPortfolioEfficiency(weights,
        averagePerformances, covMatrix, problem.getAcceptableRisk());
  }

}
