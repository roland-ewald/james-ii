/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios.ga;


import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.asf.portfolios.ga.AbstractGenePoolFactory;
import org.jamesii.asf.portfolios.ga.AbstractIndividualFactory;
import org.jamesii.asf.portfolios.ga.GenePoolFactory;
import org.jamesii.asf.portfolios.ga.GeneticAlgorithmPortfolioSelector;
import org.jamesii.asf.portfolios.ga.IIndividual;
import org.jamesii.asf.portfolios.ga.ListIndividualFactory;
import org.jamesii.asf.portfolios.ga.abort.GenerationCountAbort;
import org.jamesii.asf.portfolios.ga.abort.ListOrAbort;
import org.jamesii.asf.portfolios.ga.abort.MaxFitnessAbort;
import org.jamesii.asf.portfolios.ga.abort.WallClockTimeAbort;
import org.jamesii.asf.portfolios.ga.fitness.BestPerformancesFitness;
import org.jamesii.asf.portfolios.ga.fitness.IPortfolioFitness;
import org.jamesii.asf.portfolios.ga.fitness.SimpleFitness;
import org.jamesii.asf.portfolios.plugintype.PortfolioPerformanceData;
import org.jamesii.asf.portfolios.plugintype.PortfolioProblemDescription;
import org.jamesii.core.math.random.distributions.UniformDistribution;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.util.StopWatch;

import junit.framework.TestCase;

/**
 * Test cases for general function of {@link GeneticAlgorithmPortfolioSelector}.
 * 
 * @author Rene Schulz
 * @author Roland Ewald
 */
public class TestGeneticAlgorithmPortfolioSelector extends TestCase {

  /** The minimum portfolio size. */
  private static final int MIN_SIZE = 3;

  /** The maximum portfolio size. */
  private static final int MAX_SIZE = 20;

  /** The number of individuals per generation. */
  private static final int NUM_OF_IND = 50;

  /** The problem matrix. */
  private Double[][] problemMatrix = generateMatrix(1000, 100, 10, 1000);

  /** The individual factory. */
  static final AbstractIndividualFactory FACTORY_INDIVIDUAL =
      new ListIndividualFactory();

  /** The gene pool factory. */
  static final AbstractGenePoolFactory FACTORY_GENE_POOL =
      new GenePoolFactory();

  /** Number of Generations in simple test. */
  static final int GEN_COUNT = 2;

  /** Percentage of null pointer in the matrix of the null test. */
  static final int NULL_PERCENTAGE = 10;

  /** The fitness for the null-test. */
  static final IPortfolioFitness NULL_FITNESS = new BestPerformancesFitness(50);

  /** The maximization fitness function */
  static final IPortfolioFitness TEST_FITNESS_MAX =
      new BestPerformancesFitness(50);

  /** The minimization fitness function. */
  static final IPortfolioFitness TEST_FITNESS_MIN =
      new BestPerformancesFitness(-50);

  /** The fitness to achieve during maximization. */
  static final double MAXIMIZE_FITNESS = 96500.0;

  /** The fitness to achieve during minimization. */
  static final double MINIMIZE_FITNESS = 20.0;

  /** Wall-clock test. */
  static final int MILLISECONDS = 3000;

  /** The problem description. */
  private PortfolioProblemDescription problemDescription;

  /** The GA-based portfolio selector. */
  private GeneticAlgorithmPortfolioSelector gaps;

  @Override
  public void setUp() {
    problemDescription =
        new PortfolioProblemDescription(new PortfolioPerformanceData(
            problemMatrix), 0, true, MIN_SIZE, MAX_SIZE);
    gaps = new GeneticAlgorithmPortfolioSelector();
    gaps.setFactoryGenePool(FACTORY_GENE_POOL);
  }

  /**
   * ListOrAbort uses: GEN_COUNT TEST_FITNESS_MAX MAXIMIZE_FITNESS MILLISECONDS.
   */

  /**
   * Check whether: the (@link GenerationCountAbort) is working correctly; the
   * number of generated Individuals in the final pool is correct; each
   * individual in the final pool is meets the MIN_SIZE-MAX_SIZE-Criterion.
   */
  public void testSimple() {
    gaps.setAbortCriterion(new GenerationCountAbort(GEN_COUNT));
    gaps.setFitness(new SimpleFitness());
    gaps.setNumIndividuals(NUM_OF_IND);
    gaps.portfolio(problemDescription);
    gaps.setIndividualFactory(FACTORY_INDIVIDUAL);

    SimSystem.report(Level.INFO,
        "SimpleTest: Achieved fitness after " + gaps.getGenerationCount()
            + " generations: " + gaps.getBestFitness());

    // TEST AbortCriterion: GenerationCountAbort.
    assertEquals("SimpleTest: GenerationCount should be " + GEN_COUNT
        + " but is " + gaps.getGenerationCount(), GEN_COUNT,
        gaps.getGenerationCount());

    // TEST Number of individuals is correct.
    IIndividual[] individuals = gaps.getCurrentPool().getIndividuals();
    assertEquals("Test is designed for even numbers of individuals.", 0,
        NUM_OF_IND % 2);
    assertTrue("SimpleTest: Number of individuals should be " + NUM_OF_IND
        + " but it is " + individuals.length, individuals.length == NUM_OF_IND);

    // TEST For each individual the number of ones is in (MIN_SIZE,MAX_SIZE).
    for (int i = 0; i < individuals.length; i++) {
      checkGenome(individuals[i].getBooleanRepresentation(), MIN_SIZE, MAX_SIZE);
    }
  }

  /**
   * Same as testSimple but there are null-pointer in the problemMatrixNull.
   */
  public void testNull() {
    IRandom rng = SimSystem.getRNGGenerator().getNextRNG();
    Double[][] problemMatrixNull =
        new Double[problemMatrix.length][problemMatrix[0].length];
    for (int i = 0; i < problemMatrix.length; i++) {
      for (int j = 0; j < problemMatrix[0].length; j++) {
        problemMatrixNull[i][j] = problemMatrix[i][j];
        if ((int) (rng.nextDouble() * 100) < NULL_PERCENTAGE) {
          problemMatrixNull[i][j] = null;
        }
      }
    }
    gaps.setAbortCriterion(new GenerationCountAbort(GEN_COUNT));
    gaps.setFitness(NULL_FITNESS);
    gaps.setNumIndividuals(NUM_OF_IND);

    gaps.portfolio(new PortfolioProblemDescription(
        new PortfolioPerformanceData(problemMatrixNull), 0, true, MIN_SIZE,
        MAX_SIZE));
    gaps.setIndividualFactory(FACTORY_INDIVIDUAL);

    SimSystem.report(Level.INFO,
        "NullTest: Achieved fitness after " + gaps.getGenerationCount()
            + " generations: " + gaps.getBestFitness());

    assertEquals("NullTest: GenerationCount should be " + GEN_COUNT + "but is "
        + gaps.getGenerationCount(), GEN_COUNT, gaps.getGenerationCount());
  }

  /**
   * Check the AbortionCriterion (@link MaxFitnessAbort) and the function of the
   * maximize-flag. To complete the test, the achieved best fitness has to be >=
   * MAXIMIZE_FITNESS. The maximize Flag is set.
   */
  public void testFixFitnessMaximize() {
    gaps.setAbortCriterion(new MaxFitnessAbort(MAXIMIZE_FITNESS));
    gaps.setFitness(TEST_FITNESS_MAX);
    gaps.setNumIndividuals(NUM_OF_IND);
    gaps.portfolio(problemDescription);

    assertTrue("Best Fitness should be " + MAXIMIZE_FITNESS + " but it is "
        + gaps.getBestFitness(), gaps.getBestFitness() >= MAXIMIZE_FITNESS);
    System.out.println("testFixFitnessMaximize: Fitness "
        + gaps.getBestFitness() + " reached in " + gaps.getGenerationCount()
        + " generations.");
  }

  /**
   * To complete the test, the achieved best fitness has to be >=
   * MINIMIZE_FITNESS. The maximize Flag is not set.
   */
  public void testFixFitnessMinimize() {
    gaps.setAbortCriterion(new MaxFitnessAbort(MINIMIZE_FITNESS));
    gaps.setFitness(TEST_FITNESS_MIN);
    gaps.setNumIndividuals(NUM_OF_IND);
    gaps.portfolio(new PortfolioProblemDescription(
        new PortfolioPerformanceData(problemMatrix), 0, false, MIN_SIZE,
        MAX_SIZE));
    gaps.setIndividualFactory(FACTORY_INDIVIDUAL);

    assertTrue("Best Fitness should be " + MINIMIZE_FITNESS + " but it is "
        + gaps.getBestFitness(), gaps.getBestFitness() >= MINIMIZE_FITNESS);
    System.out.println("testFixFitnessMinimize: Fitness "
        + gaps.getBestFitness() + " reached in " + gaps.getGenerationCount()
        + " generations.");
  }

  /**
   * Test to check the AbortionCriterion (@link WallClockTimeAbort).
   */
  public void testWallClock() {
    StopWatch watch = new StopWatch();
    gaps.setFitness(new SimpleFitness());
    gaps.setNumIndividuals(NUM_OF_IND);
    watch.start();
    gaps.setAbortCriterion(new WallClockTimeAbort(MILLISECONDS));
    gaps.portfolio(problemDescription);
    watch.stop();
    assertEquals(
        "Test should run " + MILLISECONDS + " but runs "
            + watch.elapsedMilliseconds() + ".", MILLISECONDS,
        watch.elapsedMilliseconds(), 1000);
    SimSystem.report(Level.INFO, "WallClockTest: " + gaps.getGenerationCount()
        + " generations simulated.");
  }

  /**
   * Test [@link ListOrAbort) using the GenerationCount, FixFitness and
   * WallClock Criterion.
   */
  public void testListOrAbort() {
    gaps.setFitness(TEST_FITNESS_MAX);
    gaps.setNumIndividuals(NUM_OF_IND);
    ListOrAbort abortList = new ListOrAbort();
    abortList.addCriterion(new GenerationCountAbort(GEN_COUNT));
    abortList.addCriterion(new MaxFitnessAbort(MAXIMIZE_FITNESS));

    StopWatch watch = new StopWatch();
    watch.start();

    abortList.addCriterion(new WallClockTimeAbort(MILLISECONDS));
    gaps.setAbortCriterion(abortList);
    gaps.portfolio(problemDescription);
    watch.stop();

    assertTrue(
        "ListOrAbortTest: no abortCriteria fulfilled.",
        MILLISECONDS < watch.elapsedMilliseconds()
            || GEN_COUNT == gaps.getGenerationCount()
            || gaps.getBestFitness() >= MAXIMIZE_FITNESS);
    SimSystem.report(
        Level.INFO,
        "ListOrAbortTest: Fitness " + gaps.getBestFitness() + " reached in "
            + gaps.getGenerationCount() + " generations in "
            + watch.elapsedMilliseconds() + " milliseconds.");
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
   * Checks genome.
   * 
   * @param genome
   *          the genome
   * @param min
   *          the minimal number of ones
   * @param max
   *          the maximal number of ones
   */
  private void checkGenome(boolean[] genome, int min, int max) {
    int num = 0;
    for (int i = 0; i < genome.length; i++) {
      if (genome[i]) {
        num++;
      }
    }
    assertTrue("Individual with only " + num + " Ones generated!", min <= num);
    assertTrue("Individual with " + num + " Ones generated (too much)!",
        max >= num);
  }

}
