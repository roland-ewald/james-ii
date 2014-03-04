/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios.ga.fitness;

import org.jamesii.asf.portfolios.plugintype.PortfolioProblemDescription;

/**
 * FitnessFunction: The sum of the HIGHEST performance (in the portfolio) for
 * each problem minus the number of chosen algorithms * correction (fitness may
 * be < 0!). When minimizing correction should be a negative value.
 * 
 * @author René Schulz, Roland Ewald
 */

public class BestPerformancesFitness extends AbstractPortfolioFitness {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -5425101523063816588L;

  /** The fitness correction per algorithm. */
  private final int correctionPerAlgorithm;

  /**
   * Instantiates a new best performances fitness.
   * 
   * @param correctionPerAlgorithm
   *          the correction per algorithm
   */
  public BestPerformancesFitness(int correctionPerAlgorithm) {
    this.correctionPerAlgorithm = correctionPerAlgorithm;
  }

  @Override
  public double calculateFitness(PortfolioProblemDescription problem,
      Double[][] performances, boolean[] individual) {

    double fitness = 0;
    int numOfAlgorithm = 0;

    // Stores the best performances of an in the individual selected algorithm
    // for each problem.
    Double[] bestSelectedPerformances = null;

    for (int algo = 0; algo < performances.length; algo++) {
      if (!(individual[algo])) {
        continue;
      }
      numOfAlgorithm++;
      if (bestSelectedPerformances == null) {
        bestSelectedPerformances = performances[algo];
        continue;
      }
      for (int prob = 0; prob < performances[0].length; prob++) {
        if (performances[algo][prob] == null) {
          continue;
        }
        if (bestSelectedPerformances[prob] == null) {
          bestSelectedPerformances[prob] = performances[algo][prob];
          continue;
        }
        if (performances[algo][prob] > bestSelectedPerformances[prob]) {
          bestSelectedPerformances[prob] = performances[algo][prob];
        }
      }
    }

    // Sum up the best performances for all problems.
    for (int i = 0; i < bestSelectedPerformances.length; i++) {
      if (bestSelectedPerformances[i] == null) {
        continue;
      }
      fitness += bestSelectedPerformances[i];
    }
    fitness -= (numOfAlgorithm * correctionPerAlgorithm);
    return fitness;
  }
}
