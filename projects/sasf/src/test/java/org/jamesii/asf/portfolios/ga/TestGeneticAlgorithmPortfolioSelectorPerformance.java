/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios.ga;

import org.jamesii.SimSystem;
import org.jamesii.asf.portfolios.ga.AbstractGenePoolFactory;
import org.jamesii.asf.portfolios.ga.AbstractIndividualFactory;
import org.jamesii.asf.portfolios.ga.BooleanIndividualFactory;
import org.jamesii.asf.portfolios.ga.GenePoolFactory;
import org.jamesii.asf.portfolios.ga.GeneticAlgorithmPortfolioSelector;
import org.jamesii.asf.portfolios.ga.abort.GenerationCountAbort;
import org.jamesii.asf.portfolios.ga.abort.IAbortCriterion;
import org.jamesii.asf.portfolios.ga.fitness.BestPerformancesFitness;
import org.jamesii.asf.portfolios.ga.fitness.IPortfolioFitness;
import org.jamesii.asf.portfolios.plugintype.PortfolioPerformanceData;
import org.jamesii.asf.portfolios.plugintype.PortfolioProblemDescription;
import org.jamesii.core.math.random.distributions.UniformDistribution;
import org.jamesii.core.math.random.generators.IRandom;

import junit.framework.TestCase;

/**
 * Simple test case to test the performance of a
 * GeneticAlgorithmPortfolioSelector.
 * 
 * @author Rene Schulz
 * @author Roland Ewald
 */
public class TestGeneticAlgorithmPortfolioSelectorPerformance extends TestCase {

  /** The mutation rate. */
  static final double MUTATION_RATE = 0.01;

  /** Minimal size of portfolio. */
  static final int MIN_SIZE = 1;

  /** Maximal size of portfolio. */
  static final int MAX_SIZE = 10;

  /** The number of individuals. */
  static final int NUM_OF_IND = 30;

  /** The factory to create individuals. */
  static final AbstractIndividualFactory FACTORY_INDIVIDUAL =
      new BooleanIndividualFactory();

  /** The gene pool factory. */
  static final AbstractGenePoolFactory FACTORY_GENE_POOL =
      new GenePoolFactory();

  /** The maximize flag. */
  static final boolean MAXIMIZE = true;

  /** The fitness function to be used. */
  static final IPortfolioFitness FITNESS = new BestPerformancesFitness(1);

  /** The abort criterion. */
  static final IAbortCriterion ABORT = new GenerationCountAbort(500);

  /** The problem matrix. */
  Double[][] problemMatrix = generateMatrix(1000, 100, 10, 100);

  /**
   * Test performance of GA-based selector.
   */
  public void testPerformance() {

    PerformanceDataGenerator generator =
        new PerformanceDataGenerator(50, 3, 200, 3, 3);
    PortfolioPerformanceData perfData = generator.generateTestMatrix();

    System.out.println("Number of Algorithm: " + perfData.performances.length);
    System.out
        .println("Number of Problems: " + perfData.performances[0].length);
    printMatrix(perfData.performances);

    GeneticAlgorithmPortfolioSelector gaps =
        new GeneticAlgorithmPortfolioSelector();
    gaps.setAbortCriterion(ABORT);
    gaps.setFitness(FITNESS);
    gaps.setNumIndividuals(NUM_OF_IND);
    gaps.setMutationRate(MUTATION_RATE);
    gaps.setFactoryGenePool(FACTORY_GENE_POOL);
    gaps.setIndividualFactory(FACTORY_INDIVIDUAL);
    double[] vector =
        gaps.portfolio(new PortfolioProblemDescription(perfData, 0, MAXIMIZE,
            MIN_SIZE, MAX_SIZE));

    System.out.println("PerformanceTest finished.");
    System.out.println("Vector:");
    for (int i = 0; i < vector.length; i++) {
      System.out.println(vector[i]);
    }
    System.out.println();
    System.out.println(generator.evaluatePortfolio(vector));
  }

  /**
   * Generates automatically a performances matrix.
   * 
   * @param numOfAlgo
   *          number of algorithms in the matrix (0 = random value in (10,1000).
   * @param numOfProb
   *          number of problems in the matrix (0 = random value in (10,1000).
   * @param minPerformance
   *          minimal possible performance in the matrix.
   * @param maxPerformance
   *          maximal possible performance in the matrix.
   * @return a randomly generated performance matrix.
   */
  protected Double[][] generateMatrix(int numOfAlgo, int numOfProb,
      int minPerformance, int maxPerformance) {
    IRandom rng = SimSystem.getRNGGenerator().getNextRNG();
    if (numOfAlgo == 0) {
      numOfAlgo = (int) (991 * rng.nextDouble() + 10);
    }
    if (numOfProb == 0) {
      numOfProb = (int) (991 * rng.nextDouble() + 10);
    }
    UniformDistribution u =
        new UniformDistribution(rng, minPerformance, maxPerformance + 1);
    Double[][] matrix = new Double[numOfAlgo][numOfProb];
    for (Double[] mat : matrix) {
      for (int i = 0; i < mat.length; i++) {
        mat[i] = u.getRandomNumber();
      }
    }
    return matrix;
  }

  /**
   * Prints the matrix.
   * 
   * @param matrix
   *          the matrix
   */
  void printMatrix(Double[][] matrix) {
    System.out.println("Matrix");
    double avgPerformance;
    double[] performancePerProblem = new double[matrix[0].length];
    for (Double[] mat : matrix) {
      System.out.println();
      avgPerformance = 0.0;
      for (int i = 0; i < mat.length; i++) {
        System.out.print(mat[i] + " | ");
        avgPerformance += mat[i];
        performancePerProblem[i] += mat[i];
        if (i == mat.length - 1) {
          avgPerformance = avgPerformance / mat.length;
          System.out.print("average Performance: " + avgPerformance);
        }
      }
    }
    System.out.println();
    for (int i = 0; i < matrix[0].length; i++) {
      System.out
          .print("avg: " + performancePerProblem[i] / matrix.length + "|");
    }
    System.out.println();
  }
}
