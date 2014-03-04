/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios.ga.fitness;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.asf.portfolios.plugintype.PortfolioProblemDescription;


/**
 * Fitness measure that aims at capturing the aspects of a portfolio that are
 * important for adaptive replication. Takes into account the user's risk
 * aversion.
 * 
 * @author Roland Ewald
 * @author René Schulz
 * 
 */
public class ASRFitness extends AbstractPortfolioFitness {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -6624789432669509589L;

  @Override
  public double calculateFitness(PortfolioProblemDescription problem,
      Double[][] performances, boolean[] individual) {

    // Retrieve all relevant performance arrays
    List<Object> perfArrays = new ArrayList<>();
    for (int algo = 0; algo < performances.length; algo++) {
      if (!(individual[algo])) {
        continue;
      }
      perfArrays.add(performances[algo]);
    }
    int numOfAlgorithms = perfArrays.size();
    Double[][] relevantPerformance = new Double[perfArrays.size()][];
    for (int i = 0; i < numOfAlgorithms; i++) {
      relevantPerformance[i] = (Double[]) perfArrays.get(i);
    }

    // Sum over all/maximal performance
    double averagePerformanceSum = 0;
    double bestPerformanceSum = 0;
    for (int prob = 0; prob < relevantPerformance[0].length; prob++) {
      double bestPerformanceForProblem = Double.NEGATIVE_INFINITY;
      double performanceSum = 0;
      for (int algo = 0; algo < numOfAlgorithms; algo++) {
        if (relevantPerformance[algo][prob] > bestPerformanceForProblem) {
          bestPerformanceForProblem = relevantPerformance[algo][prob];
        }
        performanceSum += relevantPerformance[algo][prob];
      }
      bestPerformanceSum += bestPerformanceForProblem;
      averagePerformanceSum += (performanceSum / numOfAlgorithms);
    }

    // Apply linear interpolation between risky and non-risky case
    return (problem.getAcceptableRisk() * bestPerformanceSum + (1 - problem
        .getAcceptableRisk()) * averagePerformanceSum)
        / performances.length;
  }
}
