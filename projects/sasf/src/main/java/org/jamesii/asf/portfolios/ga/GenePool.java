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
import org.jamesii.asf.portfolios.ga.fitness.IPortfolioFitness;
import org.jamesii.asf.portfolios.plugintype.PortfolioProblemDescription;
import org.jamesii.core.math.random.distributions.UniformDistribution;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.util.misc.Pair;


/**
 * Gene pool of {@link IIndividual} for
 * {@link GeneticAlgorithmPortfolioSelector}.
 * 
 * @author René Schulz, Roland Ewald
 * 
 */
public class GenePool implements IGenePool {

  /** The problem description. */
  private final PortfolioProblemDescription problem;

  /** The fitness function of the population. */
  private final IPortfolioFitness fitness;

  /** The mutation rate. */
  private final double mutationRate;

  /** The best individual in this pool. */
  private IIndividual bestIndividual;

  /** The random number generator to be used. */
  private IRandom rng = SimSystem.getRNGGenerator().getNextRNG();

  /** Array of individuals in the pool. */
  private IIndividual[] individuals;

  /** The sum of fitness. */
  private double sumOfFitness = 0.0;

  /** The shift (to make negative fitness values positive). */
  private double shift;

  /**
   * Public constructor for generating (populating) a new pool.
   * 
   * @param problem
   *          the portfolio selection problem
   * @param numIndividuals
   *          the number of individuals
   * @param fitness
   *          the fitness function
   * @param mutationRate
   *          the mutation rate
   * @param individualFactory
   *          the factory to create individuals
   */
  public GenePool(PortfolioProblemDescription problem, int numIndividuals,
      IPortfolioFitness fitness, double mutationRate,
      AbstractIndividualFactory individualFactory) {
    this.problem = problem;
    this.fitness = fitness;
    this.mutationRate = mutationRate;
    populatePool(numIndividuals, individualFactory);
    calculateFitness();
  }

  /**
   * Private constructor for generating a new pool with given individuals.
   * 
   * @param individuals
   *          the pool of individuals
   * @param portProbDesc
   *          the portfolio problem description
   * @param fitness
   *          the fitness function
   * @param mutationRate
   *          the mutation rate
   */
  private GenePool(IIndividual[] individuals,
      PortfolioProblemDescription portProbDesc, IPortfolioFitness fitness,
      double mutationRate) {
    this.individuals = individuals;
    this.problem = portProbDesc;
    this.mutationRate = mutationRate;
    this.fitness = fitness;
    calculateFitness();
  }

  /**
   * Calculating the fitness for each individual, the sum of all fitness and
   * determine the currently best individual.
   * 
   * ATTENTION: adds a shift value (= worst fitness) in case of negative
   * fitness, otherwise the selection for recombination will optimise in the
   * wrong direction (see {@link GenePool#sampleRandomIndividual(IIndividual)}).
   */
  private void calculateFitness() {
    sumOfFitness = 0.0;

    double worstFitness = Double.POSITIVE_INFINITY;
    double[] fitnessValues = new double[individuals.length];

    // 1st round: determine fitness by individual
    for (int i = 0; i < individuals.length; i++) {
      fitnessValues[i] =
          fitness.calculateFitness(problem,
              problem.getPerformanceData()[0].performances,
              individuals[i].getBooleanRepresentation());
      if (fitnessValues[i] < worstFitness) {
        worstFitness = fitnessValues[i];
      }
    }

    shift = worstFitness < 0 ? Math.abs(worstFitness) + 1 : 0;

    // 2nd round: if fitness values negative, shift by (i.e. add) worst fitness
    // + 1;
    for (int i = 0; i < individuals.length; i++) {
      individuals[i].setFitness(fitnessValues[i] + shift);
      sumOfFitness += individuals[i].getFitness();
      checkBestIndividual(individuals[i]);
    }
  }

  private void checkBestIndividual(IIndividual ind) {
    if (bestIndividual == null) {
      bestIndividual = ind;
      return;
    }
    if (ind.getFitness() > bestIndividual.getFitness()) {
      bestIndividual = ind;
    }
  }

  private void populatePool(int numOfIndividuals,
      AbstractIndividualFactory indFactory) {
    UniformDistribution portfolioSize =
        new UniformDistribution(rng, problem.getMinSize(),
            problem.getMaxSize() + 1);
    individuals = new IIndividual[numOfIndividuals];
    for (int i = 0; i < numOfIndividuals; i++) {
      individuals[i] =
          indFactory.createRandomIndividual(rng,
              problem.getPerformanceData()[0].performances.length,
              (int) portfolioSize.getRandomNumber());
    }
  }

  /**
   * Choose individuals out of an existing pool according to their fitness,
   * recombine them to create new individuals and mutate the generated new
   * individuals. Attention: the number of individuals in the generated pool is
   * always even. If the number of individuals in the 'old' pool is odd, the new
   * one will contain one more individual.
   * 
   * @return a new generation if individuals.
   */
  @Override
  public GenePool generateNewPool() {
    List<IIndividual> newIndividuals = new ArrayList<>();

    while (newIndividuals.size() < individuals.length) {
      // Selection.
      Pair<IIndividual, IIndividual> matingPair = chooseIndividuals();
      // Recombination.
      Pair<IIndividual, IIndividual> children =
          matingPair.getFirstValue().recombine(matingPair.getSecondValue());
      // Mutation.
      children.getFirstValue().mutate(mutationRate, problem.getMinSize(),
          problem.getMaxSize());
      children.getSecondValue().mutate(mutationRate, problem.getMinSize(),
          problem.getMaxSize());

      // Repair genomes if necessary.
      children.getFirstValue().repairGenome(problem.getMinSize(),
          problem.getMaxSize());
      children.getSecondValue().repairGenome(problem.getMinSize(),
          problem.getMaxSize());

      newIndividuals.add(children.getFirstValue());
      newIndividuals.add(children.getSecondValue());
    }

    return new GenePool(newIndividuals.toArray(new IIndividual[newIndividuals
        .size()]), problem, fitness, mutationRate);
  }

  /**
   * Choose individuals.
   * 
   * @return the pair< individual, individual>
   */
  private Pair<IIndividual, IIndividual> chooseIndividuals() {
    IIndividual one = sampleRandomIndividual(null);
    IIndividual two = sampleRandomIndividual(one);
    return new Pair<>(one, two);
  }

  /**
   * Sample random individual.
   * 
   * @param individualToAvoid
   *          an individual to avoid in the selection (e.g. because it was
   *          already chosen), set to null if all are eligible
   * 
   * @return the sampled individual
   */
  private IIndividual sampleRandomIndividual(IIndividual individualToAvoid) {
    double overallFitness =
        (sumOfFitness - (individualToAvoid == null ? 0. : individualToAvoid
            .getFitness()));

    double targetShare = rng.nextDouble();
    double currentShare = 0;
    for (IIndividual individual : individuals) {
      if (individual == individualToAvoid) {
        continue;
      }
      currentShare += (individual.getFitness() / overallFitness);
      if (targetShare <= currentShare) {
        return individual;
      }
    }
    throw new IllegalStateException(
        "SampleRandomIndividual was not able to select an individual!");
  }

  @Override
  public IIndividual getBestIndividual() {
    return bestIndividual;
  }

  @Override
  public double getBestFitness() {
    return bestIndividual.getFitness();
  }

  @Override
  public double getAvgFitness() {
    return sumOfFitness / individuals.length;
  }

  @Override
  public IIndividual[] getIndividuals() {
    return individuals;
  }

  public double getShift() {
    return shift;
  }
}
