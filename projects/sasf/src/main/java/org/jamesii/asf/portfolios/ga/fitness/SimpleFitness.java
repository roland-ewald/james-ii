/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios.ga.fitness;

import org.jamesii.asf.portfolios.plugintype.PortfolioProblemDescription;

/**
 * Primitive fitness function for test cases. Sum of average performances of all
 * selected algorithms divided by the number of selected algorithms.
 * 
 * 
 * @author René Schulz, Roland Ewald
 */

public class SimpleFitness extends AbstractPortfolioFitness {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -5312915673449712122L;

  @Override
  public double calculateFitness(PortfolioProblemDescription problem,
      Double[][] performances, boolean[] individual) {
    double fitness = 0;
    int numOfAlgorithm = 0;
    for (int i = 0; i < individual.length; i++) {
      if (individual[i]) {
        numOfAlgorithm++;
        double avgPerformance = 0;
        for (int j = 0; j < performances[i].length; j++) {
          if (performances[i][j] != null) {
            avgPerformance += performances[i][j];
          }
        }
        avgPerformance = avgPerformance / performances[i].length;
        fitness += avgPerformance;
      }
    }
    fitness = fitness / numOfAlgorithm;
    return fitness;
  }

}
