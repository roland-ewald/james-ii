/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios.ga;


import java.util.ArrayList;
import java.util.List;

import org.jamesii.SimSystem;
import org.jamesii.asf.portfolios.ga.GeneticAlgorithmPortfolioSelector;
import org.jamesii.asf.portfolios.plugintype.PortfolioPerformanceData;
import org.jamesii.core.math.random.distributions.AbstractDistribution;
import org.jamesii.core.math.random.distributions.NormalDistribution;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.statistics.univariate.ArithmeticMean;


/**
 * Generates synthetic performances data to test the genetic algorithm
 * systematically. Therefore, 'algorithm clusters' are generated. Here, a
 * 'cluster' means a group of algorithms with the same mean and different
 * standard deviations for same same class of problems. The number of problem
 * classes can be adjusted as well.
 * 
 * @author René Schulz, Roland Ewald
 */

public class PerformanceDataGenerator {

  /** The number of distinct problem classes. */
  private final int numOfProblemClasses;

  /** The number of algorithm clusters. */
  private final int numOfAlgoClusters;

  /** The number of algorithms per algorithm cluster. */
  private final int numOfAlgoPerCluster;

  /** The number of problems for which data shall be generated. */
  private final int numOfProblems;

  /** The difference of the means of two following clusters. */
  private final double distanceBetweenAlgoClusters;

  /** The random number generator. */
  private final IRandom rng = SimSystem.getRNGGenerator().getNextRNG();

  /**
   * The performance-values in the matrix for portfolio selection are averaged
   * over numOfValues randomly drawn values from the performance distribution
   * (to simulate replications).
   */
  private final int numOfSimulatedReplications;

  /**
   * All generated performances will be equal or greater than
   * "minimalPerformance".
   */
  private double minimalPerformance = 0.5;

  /** The distribution of the deviations. */
  final AbstractDistribution deviationDistribution = new NormalDistribution(
      rng, 1.0, 0.5);

  /**
   * Fix value for each problem which shows the difficulty of the problem, i.e.
   * the minimal time to execution.
   */
  private double[] problemDifficulty;

  /** The number of algorithms. */
  private int numOfAlgorithms;

  /** The algorithm clusters. */
  private AlgorithmCluster[] clusters;

  /**
   * Instantiates a new performance data generator.
   * 
   * @param numOfAlgoClusters
   *          the number of clusters
   * @param numOfAlgosPerCluster
   *          the number of algorithms per cluster
   * @param numOfProblems
   *          the number of problems
   * @param distanceBetweenAlgoClusters
   *          the distances between the clusters
   * @param numProblemClasses
   *          the number problem classes
   */
  public PerformanceDataGenerator(int numOfAlgoClusters,
      int numOfAlgosPerCluster, int numOfProblems,
      double distanceBetweenAlgoClusters, int numProblemClasses,
      int numOfReplications) {
    this.numOfAlgoClusters = numOfAlgoClusters;
    this.numOfAlgoPerCluster = numOfAlgosPerCluster;
    this.numOfProblems = numOfProblems;
    this.distanceBetweenAlgoClusters = distanceBetweenAlgoClusters;
    this.numOfProblemClasses = numProblemClasses;
    this.numOfSimulatedReplications = numOfReplications;
    numOfAlgorithms = numOfAlgoClusters * numOfAlgosPerCluster;
    clusters = new AlgorithmCluster[numOfAlgoClusters];
    buildStructure();
  }

  /**
   * Instantiates a new performance data generator.
   * 
   * @param clusterNumber
   *          the cluster number
   * @param algosPerCluster
   *          the algos per cluster
   * @param problemNumber
   *          the problem number
   * @param clusterDistance
   *          the cluster distance
   * @param problemClasses
   *          the problem classes
   */
  public PerformanceDataGenerator(int clusterNumber, int algosPerCluster,
      int problemNumber, double clusterDistance, int problemClasses) {
    this(clusterNumber, algosPerCluster, problemNumber, clusterDistance,
        problemClasses, 3);
  }

  /**
   * Construct the clusters and the problemDifficulties.
   */
  private void buildStructure() {
    for (int j = 0; j < numOfAlgoClusters; j++) {
      clusters[j] =
          new AlgorithmCluster(rng, numOfAlgoPerCluster, deviationDistribution);
    }

    problemDifficulty = new double[numOfProblems];
    NormalDistribution distributionProblems =
        new NormalDistribution(rng, 0.0, 0.5 * distanceBetweenAlgoClusters);
    for (int i = 0; i < numOfProblems; i++) {
      problemDifficulty[i] = distributionProblems.getRandomNumber();
    }
  }

  /**
   * Return a randomly chosen performance-value for given algorithm and problem.
   * The performance value will be equal or greater than minimalPerformance.
   * 
   * @param algo
   *          the algorithm
   * @param problem
   *          the problem
   * 
   * @return the performance
   */
  public Double getPerformance(int algo, int problem) {
    int cluster = algo / numOfAlgoPerCluster;
    int algoNumber = algo - (cluster * numOfAlgoPerCluster);

    int clusterRelativeToProblemClass =
        (cluster + (problem % numOfProblemClasses)) % numOfAlgoClusters;
    double mean =
        distanceBetweenAlgoClusters * (clusterRelativeToProblemClass + 1)
            + problemDifficulty[problem];
    Double performance =
        clusters[cluster].getRandomPerformance(mean, algoNumber);

    if (performance < minimalPerformance) {
      performance = minimalPerformance;
    }

    return performance;
  }

  /**
   * Generate a performance-matrix to the the
   * {@link GeneticAlgorithmPortfolioSelector}.
   * 
   * @return the portfolio performance data
   */
  public PortfolioPerformanceData generateTestMatrix() {
    Double[][] matrix = new Double[numOfAlgorithms][numOfProblems];
    for (int algo = 0; algo < numOfAlgorithms; algo++) {
      for (int problem = 0; problem < numOfProblems; problem++) {
        double performance = 0.0;
        for (int i = 0; i < numOfSimulatedReplications; i++) {
          performance += getPerformance(algo, problem);
        }
        matrix[algo][problem] = performance / numOfSimulatedReplications;
      }
    }

    return new PortfolioPerformanceData(matrix);
  }

  /**
   * Calculates the difference of the performances of a given portfolio with the
   * best possible portfolio (overhead). The overhead is the sum of performance
   * differences between the optimal choice and the chosen algorithms. This will
   * be averaged over the number of chosen algorithms, to approximate the
   * expected overhead when drawing one algorithm randomly.
   * 
   * @param portfolio
   *          the portfolio-vector to compare with the optimal.
   * 
   * @return the overhead.
   */
  public double evaluatePortfolio(double[] portfolio) {

    // Checks if algorithm from cluster is selected
    boolean[] elementFromCluster = new boolean[numOfAlgoClusters];
    for (int i = 0; i < portfolio.length; i++) {
      if (portfolio[i] != 0) {
        elementFromCluster[i / numOfAlgoPerCluster] = true;
      }
    }

    // Checks the distance of nearest algorithm cluster from which the portfolio
    // contains an element, for each problem class
    List<Double> distances = new ArrayList<>();
    for (int probClass = 0; probClass < this.numOfProblemClasses; probClass++) {
      int currentCluster = (numOfAlgoClusters - probClass) % numOfAlgoClusters;
      int distance = 0;
      int counter = 0;
      while (counter < numOfAlgoClusters) {
        if (elementFromCluster[currentCluster]) {
          break;
        }
        distance++;
        counter++;
        currentCluster = (currentCluster + 1) % numOfAlgoClusters;
      }
      distances.add(distanceBetweenAlgoClusters * distance);
    }

    return ArithmeticMean.arithmeticMean(distances);
  }

  /**
   * Gets the rng.
   * 
   * @return the rng
   */
  public IRandom getRng() {
    return rng;
  }

  /**
   * Gets the num of values.
   * 
   * @return the num of values
   */
  public int getNumOfValues() {
    return numOfSimulatedReplications;
  }

  /**
   * Gets the minimal performance.
   * 
   * @return the minimal performance
   */
  public double getMinimalPerformance() {
    return minimalPerformance;
  }

  /**
   * Sets the minimal performance.
   * 
   * @param minimalPerformance
   *          the new minimal performance
   */
  public void setMinimalPerformance(double minimalPerformance) {
    this.minimalPerformance = minimalPerformance;
  }

  /**
   * Gets the deviation distribution.
   * 
   * @return the deviation distribution
   */
  public AbstractDistribution getDeviationDistribution() {
    return deviationDistribution;
  }

  /**
   * Gets the best possible performance.
   * 
   * @param problem
   * 
   * @return the best possible performance
   */
  public double getBestPossiblePerformance(int problem) {
    return distanceBetweenAlgoClusters + problemDifficulty[problem];
  }

  /**
   * Gets the num of problems.
   * 
   * @return the num of problems
   */
  public int getNumOfProblems() {
    return numOfProblems;
  }
}
