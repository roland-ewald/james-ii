/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios.ga;


import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.math.random.RandomSampler;
import org.jamesii.core.math.random.distributions.BinomialDistribution;
import org.jamesii.core.math.random.distributions.UniformDistribution;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.util.misc.Pair;

/**
 * An individual for the {@link IGenePool} using a boolean array for
 * representing the genome.
 * 
 * @author René Schulz, Roland Ewald
 * 
 */
public class BooleanIndividual extends Individual {

  /** The genome of the individual. */
  private boolean[] genome;

  /**
   * Instantiates a new individual.
   * 
   * @param genome
   *          the genome
   * @param random
   *          the random number generator to be used
   */
  public BooleanIndividual(boolean[] genome, IRandom random) {
    super(random);
    this.genome = genome;
  }

  /**
   * Generate random genome for an individual.
   * 
   * @param length
   *          the size of the hole genome
   * @param portfolioSize
   *          the size of the portfolio
   * @param rng
   *          the random-generator to be used
   * 
   */
  @Override
  public void generateRandomGenome(int length, int portfolioSize) {
    int remainingPortfolioSize = portfolioSize;
    boolean[] newGenome = new boolean[length];
    UniformDistribution randomPosition =
        new UniformDistribution(getRNG(), 0, newGenome.length);
    while (remainingPortfolioSize > 0) {
      int position = (int) randomPosition.getRandomNumber();
      if (!newGenome[position]) {
        newGenome[position] = true;
        remainingPortfolioSize--;
      }
    }
    this.genome = newGenome;
  }

  /**
   * Mutate the genome of an individual.
   * 
   * @param mutationRate
   *          the probability of an mutation
   * 
   */
  @Override
  public void mutate(double mutationRate, int minSize, int maxSize) {
    BinomialDistribution mutationNumberDistribution =
        new BinomialDistribution(getRNG(), mutationRate, genome.length);
    int numOfMutations =
        (int) Math.round(mutationNumberDistribution.getRandomNumber());
    int[] mutationPositions =
        RandomSampler.sampleUnique(numOfMutations, genome.length, getRNG());
    for (int mutationPosition : mutationPositions) {
      genome[mutationPosition] = !genome[mutationPosition];
    }
  }

  /**
   * Checks whether the genome still complies to size constraints and repairs it
   * if check is failed.
   * 
   */
  @Override
  public void repairGenome(int minSize, int maxSize) {
    int portfolioSize = numOfChosenAlgo();
    if (portfolioSize < minSize) {
      expandPortfolioRandomly(minSize - portfolioSize);
    } else if (portfolioSize > maxSize) {
      reducePortfolioRandomly(portfolioSize - maxSize);
    }
  }

  /**
   * Reduces (randomly) the number of 'ones' in the genome by the given count.
   * 
   * @param genome
   *          the genome
   * @param count
   *          the count of ones that shall be removed
   */
  private void reducePortfolioRandomly(int count) {
    List<Integer> positionsWithOnes = new ArrayList<>();
    for (int i = 0; i < genome.length; i++) {
      if (genome[i]) {
        positionsWithOnes.add(i);
      }
    }
    List<Integer> posOfOnesForRemoval =
        RandomSampler.sampleSet(count, positionsWithOnes, getRNG());
    for (Integer posForRemoval : posOfOnesForRemoval) {
      genome[posForRemoval] = false;
    }
  }

  /**
   * Increases (randomly) the number of 'ones' in the genome by the given count.
   * 
   * @param count
   *          the count of ones that shall be added
   */
  private void expandPortfolioRandomly(int count) {
    List<Integer> positionsWithZeros = new ArrayList<>();
    for (int i = 0; i < genome.length; i++) {
      if (!genome[i]) {
        positionsWithZeros.add(i);
      }
    }
    List<Integer> posOfZerosForSwitching =
        RandomSampler.sampleSet(count, positionsWithZeros, getRNG());
    for (Integer posForSwitch : posOfZerosForSwitching) {
      genome[posForSwitch] = true;
    }
  }

  /**
   * Counts the number of chosen algorithms (ones) in a genome.
   * 
   * @return the number of ones
   */
  @Override
  public int numOfChosenAlgo() {
    int ones = 0;
    for (int i = 0; i < genome.length; i++) {
      if (genome[i]) {
        ones++;
      }
    }
    return ones;
  }

  /**
   * Recombine the genome of two mating individuals.
   * 
   * @return a pair of new individuals
   */
  @Override
  public Pair<IIndividual, IIndividual> recombine(IIndividual partner) {
    int crossoverPoint =
        (int) (new UniformDistribution(getRNG(), 0, genome.length))
            .getRandomNumber();
    Pair<boolean[], boolean[]> newGenomes =
        recombineGenomes(crossoverPoint, genome,
            partner.getBooleanRepresentation());
    return new Pair<IIndividual, IIndividual>(new BooleanIndividual(
        newGenomes.getFirstValue(), getRNG()), new BooleanIndividual(
        newGenomes.getSecondValue(), getRNG()));
  }

  /**
   * Recombines two genomes with one-point crossover at a given point.
   * 
   * @param crossover
   *          the crossover point
   * @param genomeParentA
   *          the first parent's genome
   * @param genomeParentB
   *          the second parent's genome
   * @return two recombined child genomes
   */
  protected static Pair<boolean[], boolean[]> recombineGenomes(int crossover,
      boolean[] genomeParentA, boolean[] genomeParentB) {
    return new Pair<>(copyGenome(crossover, genomeParentA,
        genomeParentB), copyGenome(crossover, genomeParentB, genomeParentA));
  }

  /**
   * Create new genome of form [genomeA (start -> crossover point) + genomeB
   * (crossover point -> end)].
   * 
   * @param crossover
   *          the crossover point
   * @param genomeA
   *          the first genome
   * @param genomeB
   *          the second genome
   * @return the resulting genome
   */
  private static boolean[] copyGenome(int crossover, boolean[] genomeA,
      boolean[] genomeB) {
    boolean[] result = new boolean[genomeA.length];
    System.arraycopy(genomeA, 0, result, 0, crossover);
    System.arraycopy(genomeB, crossover, result, crossover, result.length
        - crossover);
    return result;
  }

  @Override
  public boolean[] getBooleanRepresentation() {
    return genome;
  }

}
