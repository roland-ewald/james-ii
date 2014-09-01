/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios.ga;


import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.asf.portfolios.ga.abort.GenerationCountAbort;
import org.jamesii.asf.portfolios.ga.abort.IAbortCriterion;
import org.jamesii.asf.portfolios.ga.fitness.IPortfolioFitness;
import org.jamesii.asf.portfolios.ga.fitness.SimpleFitness;
import org.jamesii.asf.portfolios.plugintype.AbstractPortfolioSelector;
import org.jamesii.asf.portfolios.plugintype.PortfolioPerformanceData;
import org.jamesii.asf.portfolios.plugintype.PortfolioProblemDescription;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.core.util.misc.Strings;


/**
 * Selection based on a genetic algorithm.
 * 
 * @author René Schulz, Roland Ewald
 * 
 */
public class GeneticAlgorithmPortfolioSelector extends
    AbstractPortfolioSelector {

  /** The default number of generations counted until the algorithm terminates. */
  private static final int DEFAULT_GENERATION_COUNT = 10;

  /** The default number of individuals. */
  private static final int DEFAULT_NUM_INDIVIDUALS = 100;

  /** The default mutation rate for the GA. */
  private static final double DEFAULT_MUTATION_RATE = 0.001;

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1777632973901841566L;

  /** The output file for debugging info. */
  static final String DEBUG_OUTPUT_FILE = "./GA_Fitness.dat";

  /** The fitnessFunction. */
  private IPortfolioFitness fitness = new SimpleFitness();

  /** The abort criterion. */
  private IAbortCriterion abortCriterion = new GenerationCountAbort(
      DEFAULT_GENERATION_COUNT);

  /** The number of individuals. */
  private int numIndividuals = DEFAULT_NUM_INDIVIDUALS;

  /** The mutation rate for the GA. */
  private double mutationRate = DEFAULT_MUTATION_RATE;

  /** The generation count. */
  private int generationCount = 0;

  /** The best found portfolio so far. */
  private boolean[] bestIndividual = null;

  /** The best fitness. */
  private Double bestFitness = null;

  /** Factory for generating the current pool */
  private AbstractGenePoolFactory factoryGenePool = new GenePoolFactory();

  /** The current pool. */
  private transient IGenePool currentPool = null;

  /** Save the average performance of each generation */
  private List<Double> avgFitnesses = new ArrayList<>();

  /** Save the best performance of each generation */
  private List<Double> bestFitnesses = new ArrayList<>();

  /** The factory to be used to create the individuals of the first generation. */
  private AbstractIndividualFactory individualFactory =
      new ListIndividualFactory();

  @Override
  public double[] portfolio(PortfolioProblemDescription problem) {

    Double[][] performances = problem.getPerformancesForMaximization(0);
    PortfolioProblemDescription problemGA =
        new PortfolioProblemDescription(new PortfolioPerformanceData(
            performances), problem.getAcceptableRisk(),
            problem.getMaximizationFlags()[0], problem.getMinSize(),
            problem.getMaxSize());

    currentPool =
        factoryGenePool.create(problemGA, numIndividuals, fitness,
            mutationRate, individualFactory);
    updateResults();
    generationCount = 1;

    while (!abortCriterion.abort(this)) {
      IIndividual bestInd = currentPool.getBestIndividual();
      currentPool = currentPool.generateNewPool();
      updateResults();
      generationCount++;
      reportState(problem, performances, bestInd);
    }

    outputInFile(problem);
    return createPortfolioVector();
  }

  /**
   * Report current state of the GA to the logging mechanism of
   * {@link SimSystem}.
   * 
   * @param problem
   *          the problem
   * @param performances
   *          the performances
   * @param bestInd
   *          the best individual
   */
  private void reportState(PortfolioProblemDescription problem,
      Double[][] performances, IIndividual bestInd) {
    SimSystem.report(Level.INFO, "Generation: " + generationCount);
    SimSystem.report(Level.INFO,
        "Best fitness: " + bestFitnesses.get(bestFitnesses.size() - 1)
            + "\t avg fitness:" + avgFitnesses.get(avgFitnesses.size() - 1));
    SimSystem.report(Level.INFO, "Details:");
    List<Pair<String, Double>> desc =
        this.fitness.getDetailedFitnessDescription(problem, performances,
            bestInd.getBooleanRepresentation());
    for (Pair<String, Double> d : desc) {
      SimSystem.report(Level.INFO,
          d.getFirstValue() + ":\t" + d.getSecondValue());
    }
  }

  /**
   * Output best and average fitness and a few other parameter in text-file.
   */
  private void outputInFile(PortfolioProblemDescription problem) {
    BufferedWriter out = null;
    try {
      out =
          new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
              DEBUG_OUTPUT_FILE)));
      out.append(resultDescForProblem(problem));
      out.newLine();
    } catch (IOException e) {
      SimSystem.report(Level.SEVERE, "Problems writing debugging info to '"
          + DEBUG_OUTPUT_FILE + "'", e);
    } finally {
      if (out != null) {
        try {
          out.close();
        } catch (IOException e) {
          SimSystem.report(Level.SEVERE, "Could not close file '"
              + DEBUG_OUTPUT_FILE + "'", e);
        }
      }
    }
  }

  /**
   * Creates result description for the given problem.
   * 
   * @param problem
   *          the problem
   * @return the string representation of the results
   */
  private String resultDescForProblem(PortfolioProblemDescription problem) {
    StringBuilder s = new StringBuilder();
    s.append("BestFitness" + "\t" + "AverageFitness\n");
    for (int i = 0; i < avgFitnesses.size(); i++) {
      s.append(bestFitnesses.get(i));
      s.append('\t');
      s.append(avgFitnesses.get(i));
      s.append('\n');
    }
    s.append("\nNumberOfIndividuals: ");
    s.append(numIndividuals);
    s.append("\nGeneration: ");
    s.append(generationCount);
    s.append("\nMutationRate: ");
    s.append(getMutationRate());
    s.append("\nBestFitness: ");
    s.append(bestFitness);
    s.append("\nNumOfAlgorithm: ");
    s.append(problem.getPerformanceData()[0].performances.length);
    s.append("\nNumOfProblems: ");
    s.append(problem.getPerformanceData()[0].performances[0].length);
    s.append("\nMaximize: ");
    s.append(Strings.dispArray(problem.getMaximizationFlags()));
    s.append("\nRisk: ");
    s.append(problem.getAcceptableRisk());
    s.append("\nMinimalSize: ");
    s.append(problem.getMinSize());
    s.append("\nMaximalSize: ");
    s.append(problem.getMaxSize());
    s.append('\n');
    return s.toString();
  }

  private void updateResults() {
    // Store average and best fitness of the current pool.
    bestFitnesses.add(currentPool.getBestFitness());
    avgFitnesses.add(currentPool.getAvgFitness());
    // Update best reached fitness.
    if (bestFitness == null || bestFitness < currentPool.getBestFitness()) {
      bestFitness = currentPool.getBestFitness();
      bestIndividual =
          currentPool.getBestIndividual().getBooleanRepresentation();
    }
  }

  private double[] createPortfolioVector() {
    double[] vector = new double[bestIndividual.length];
    int sum = 0;
    for (int i = 0; i < bestIndividual.length; i++) {
      if (bestIndividual[i]) {
        sum++;
      }
    }
    for (int i = 0; i < vector.length; i++) {
      if (bestIndividual[i]) {
        vector[i] = 1.0 / sum;
      }
    }
    return vector;
  }

  public IAbortCriterion getAbortCriterion() {
    return abortCriterion;
  }

  public void setAbortCriterion(IAbortCriterion abortCriterion) {
    this.abortCriterion = abortCriterion;
  }

  public int getGenerationCount() {
    return generationCount;
  }

  public IPortfolioFitness getFitness() {
    return fitness;
  }

  public void setFitness(IPortfolioFitness fitness) {
    this.fitness = fitness;
  }

  public int getNumIndividuals() {
    return numIndividuals;
  }

  public void setNumIndividuals(int numIndividuals) {
    this.numIndividuals = numIndividuals;
  }

  public double getMutationRate() {
    return mutationRate;
  }

  public void setMutationRate(double mutationRate) {
    this.mutationRate = mutationRate;
  }

  public Double getBestFitness() {
    return bestFitness;
  }

  public AbstractGenePoolFactory getFactoryGenePool() {
    return factoryGenePool;
  }

  public void setFactoryGenePool(AbstractGenePoolFactory factoryGenePool) {
    this.factoryGenePool = factoryGenePool;
  }

  public AbstractIndividualFactory getIndividualFactory() {
    return individualFactory;
  }

  public void setIndividualFactory(AbstractIndividualFactory individualFactory) {
    this.individualFactory = individualFactory;
  }

  public IGenePool getCurrentPool() {
    return currentPool;
  }

}
