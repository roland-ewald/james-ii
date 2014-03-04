/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios.stochsearch;


import java.util.List;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.asf.portfolios.MeanVariancePortfolioUtils;
import org.jamesii.asf.portfolios.plugintype.AbstractPortfolioSelector;
import org.jamesii.asf.portfolios.plugintype.PortfolioProblemDescription;
import org.jamesii.core.math.random.RandomSampler;
import org.jamesii.core.math.random.distributions.UniformDistribution;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.util.misc.Pair;


/**
 * Trivial solution to the portfolio selection problem. Samples uniformly from
 * the space of eligible portfolio vectors.
 * 
 * @author Roland Ewald
 * 
 */
public class StochSearchPortfolioSelector extends AbstractPortfolioSelector {

  /** The interval at which an info will be printed about the progress. */
  private static final int ITERATION_REPORT_INTERVAL = 1000;

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -7701301009792911233L;

  /** Stores the current optimum. */
  private double[] currentOptimum = null;

  /** The value of the objective function for the current optimum. */
  private double optimumObjective;

  /** The number of iterations to be done. */
  private final int iterations;

  /** The randomizer to generate the samples. */
  private final IRandom random;

  /** The uniform distribution to generate random samples from the search space. */
  private final UniformDistribution uDist;

  /** The objective function to be used. */
  private IStochSearchObjective objectiveFunction = new MeanVarianceObjective();

  /**
   * Instantiates a new stochastic search portfolio selector.
   * 
   * @param sampleSize
   *          the sample size
   * @param rng
   *          the random number generator to be used
   */
  public StochSearchPortfolioSelector(int sampleSize, IRandom rng) {
    iterations = sampleSize;
    random = rng;
    uDist = new UniformDistribution(random);
  }

  @Override
  public double[] portfolio(PortfolioProblemDescription problem) {

    currentOptimum = null;
    optimumObjective = Double.NEGATIVE_INFINITY;

    Pair<Double[], Double[][]> avgPerfCov =
        MeanVariancePortfolioUtils.getAvgAndCovFromPerformances(problem
            .getPerformancesForMaximization(0));
    Double[] avgPerf = avgPerfCov.getFirstValue();
    Double[][] covMat = avgPerfCov.getSecondValue();

    for (int i = 0; i < iterations; i++) {

      double[] sample =
          generateRandomSample(avgPerf.length, problem.getMinSize(),
              problem.getMaxSize());
      double objective =
          objectiveFunction.calcPortfolioEfficiency(sample, avgPerf, covMat,
              problem.getAcceptableRisk());

      if (optimumObjective < objective) {
        SimSystem.report(Level.INFO, "Found new best objective:" + objective);
        SimSystem.report(Level.INFO, "Lambda:" + problem.getAcceptableRisk());
        List<Pair<String, Double>> fitnComps =
            objectiveFunction.getDetailedFitnessDescription(sample, avgPerf,
                covMat, problem.getAcceptableRisk());
        for (Pair<String, Double> comp : fitnComps) {
          SimSystem.report(Level.INFO,
              comp.getFirstValue() + ":\t" + comp.getSecondValue());
        }
        optimumObjective = objective;
        currentOptimum = sample;
      }

      if (i % ITERATION_REPORT_INTERVAL == 0) {
        SimSystem.report(Level.INFO, ITERATION_REPORT_INTERVAL
            + "-th iteration:" + i);
      }
    }

    return currentOptimum;
  }

  /**
   * Generate random portfolio.
   * 
   * @param overallLength
   *          the overall length of the portfolio vector
   * @param minSize
   *          the minimal number of non-zero elements
   * @param maxSize
   *          the maximal number of non-zero elements
   * 
   * @return a random portfolio
   */
  private double[] generateRandomSample(int overallLength, int minSize,
      int maxSize) {

    int size = minSize + random.nextInt(maxSize - minSize + 1);
    double result[] = new double[overallLength];
    int[] nonZeroElems =
        RandomSampler.sampleUnique(size, overallLength, random);

    double currentSum = 0.0;
    uDist.setLowerBorder(0);
    uDist.setUpperBorder(1);
    for (int i = 0; i < size - 1; i++) {
      double randWeight = uDist.getRandomNumber();
      result[nonZeroElems[i]] = randWeight;
      currentSum += randWeight;
      uDist.setUpperBorder(1 - currentSum);
    }

    // Sum up to 1
    result[nonZeroElems[size - 1]] = 1 - currentSum;
    return result;
  }

  public IStochSearchObjective getObjectiveFunction() {
    return objectiveFunction;
  }

  public void setObjectiveFunction(IStochSearchObjective objectiveFunction) {
    this.objectiveFunction = objectiveFunction;
  }

  public double[] getCurrentOptimum() {
    return currentOptimum;
  }

  public double getOptimumObjective() {
    return optimumObjective;
  }
}
