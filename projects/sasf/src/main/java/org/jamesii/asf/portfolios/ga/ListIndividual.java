/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios.ga;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jamesii.core.math.random.RandomSampler;
import org.jamesii.core.math.random.distributions.BinomialDistribution;
import org.jamesii.core.math.random.distributions.UniformDistribution;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.util.misc.Pair;

/**
 * Class of {@link IIndividual} using {@link List} for representing the genome.
 * The method repairGenome() is not necessary because mutation and recombination
 * generate only valid individuals.
 * 
 * @author René Schulz, Roland Ewald
 */
public class ListIndividual extends Individual {

  /** The genome of the individual. */
  private List<Integer> genome = new ArrayList<>();

  /** The length of the boolean-representation of the genome. */
  private int length;

  /**
   * Instantiates a new list individual.
   * 
   * @param genome
   *          the genome
   * @param len
   *          the length
   * @param rng
   *          the random number generator
   */
  public ListIndividual(List<Integer> genome, int len, IRandom rng) {
    super(rng);
    this.genome = genome;
    this.length = len;
  }

  @Override
  public void generateRandomGenome(int portfolioSize, int len) {
    this.length = len;
    genome = new ArrayList<>();
    int[] genes = RandomSampler.sampleUnique(portfolioSize, length, getRNG());
    for (int gene : genes) {
      genome.add(gene);
    }
  }

  /**
   * Mutation: select random element from list, choose new asset index and
   * replace old one. So the generated individual meets the size constraints.
   * 
   * @param mutationRate
   *          the mutation rate
   * @param minSize
   *          the min size
   * @param maxSize
   *          the max size
   */
  @Override
  public void mutate(double mutationRate, int minSize, int maxSize) {
    BinomialDistribution mutationNumberDistribution =
        new BinomialDistribution(getRNG(), mutationRate, length);
    int numOfMutations =
        (int) Math.round(mutationNumberDistribution.getRandomNumber());
    if (numOfMutations > genome.size()) {
      numOfMutations = genome.size();
    }
    int[] mutationPositions =
        RandomSampler.sampleUnique(numOfMutations, genome.size(), getRNG());

    // Index+1 signifies the deletion of the algorithm at the mutation position
    // (if omitted the while loop below may run infinitely, in case an
    // individual representing a complete portfolio should mutate)
    for (int mutation : mutationPositions) {
      mutate(mutation, minSize, maxSize);
    }

    clearRemovedItemsFromGenome();
  }

  /**
   * Clears removed items from genome. These are marked by null.
   */
  private void clearRemovedItemsFromGenome() {
    for (int i = genome.size() - 1; i >= 0; i--) {
      if (genome.get(i) == null) {
        genome.remove(i);
      }
    }
  }

  /**
   * Executes Mutation.
   * 
   * @param mutation
   *          the position of the element to be mutated
   * @param minSize
   *          the minimal size of the list
   * @param maxSize
   *          the maximal size of the list
   */
  private void mutate(int mutation, int minSize, int maxSize) {

    // Avoid trivial case
    if (minSize == length) {
      return;
    }

    int removeOption = checkIfRemovalPossible(minSize, maxSize);
    int newIndex = getNewRandomIndex(removeOption);
    while (genome.contains(newIndex)) {
      newIndex = getNewRandomIndex(removeOption);
    }

    // Set null if algorithm at this place shall be removed
    genome.set(mutation, (newIndex < length) ? newIndex : null);
  }

  /**
   * Checks if a removal due to mutation possible. An algorithm may only be
   * removed if the size is not fixed and the minimum size is exceeded.
   * 
   * @param minSize
   *          the minimal size of the list
   * @param maxSize
   *          the maximal size of the list
   * 
   * @return either one (removal is OK) or 0 (no removal is possible)
   */
  private int checkIfRemovalPossible(int minSize, int maxSize) {
    boolean minSized = (genome.size() == minSize);
    boolean fixedSized = (minSize == maxSize);
    return (minSized || fixedSized) ? 0 : 1;
  }

  /**
   * Gets a new random index.
   * 
   * @param removeOption
   *          the remove option
   * 
   * @return the new random index
   */
  private int getNewRandomIndex(int removeOption) {
    return (int) (getRNG().nextDouble() * length + removeOption);
  }

  /**
   * Recombination: 1) find duplicates 2) use those in both child lists 3)
   * normal recombination (assuming empty entries at the end of the smaller
   * list). So the generated individuals meet the size constraints.
   * 
   * @param partner
   *          the partner
   * 
   * @return the pair< i individual, i individual>
   */
  @Override
  public Pair<IIndividual, IIndividual> recombine(IIndividual partner) {

    if (!(partner instanceof ListIndividual)) {
      throw new IllegalArgumentException("Partner of type "
          + partner.getClass() + " are not supported.");
    }

    ListIndividual realPartner = (ListIndividual) partner;
    List<Integer> newGenome1 = new ArrayList<>();
    List<Integer> newGenome2 = new ArrayList<>();
    List<Integer> genome1WithoutDuplicates = new ArrayList<>();
    List<Integer> genome2WithoutDuplicates = new ArrayList<>();

    // Find duplicates
    Set<Integer> genomeHash = new HashSet<>(genome);
    for (Integer i : realPartner.genome) {
      if (genomeHash.contains(i)) {
        newGenome1.add(i);
        newGenome2.add(i);
      } else {
        genome2WithoutDuplicates.add(i);
      }
    }

    genome1WithoutDuplicates.addAll(genome);
    genome1WithoutDuplicates.removeAll(newGenome1);

    int maxSize =
        Math.max(genome1WithoutDuplicates.size(),
            genome2WithoutDuplicates.size());

    int crossoverPoint =
        (int) (new UniformDistribution(getRNG(), 0, maxSize)).getRandomNumber();
    Pair<List<Integer>, List<Integer>> crossedGenome =
        recombineGenomes(crossoverPoint, maxSize, genome1WithoutDuplicates,
            genome2WithoutDuplicates);
    newGenome1.addAll(crossedGenome.getFirstValue());
    newGenome2.addAll(crossedGenome.getSecondValue());
    ListIndividual child1 = new ListIndividual(newGenome1, length, getRNG());
    ListIndividual child2 = new ListIndividual(newGenome2, length, getRNG());
    return new Pair<IIndividual, IIndividual>(child1, child2);
  }

  /**
   * Recombines two genomes with one-point crossover at a given point.
   * 
   * @param crossoverPoint
   *          the crossover point
   * @param maxSize
   *          the maximal size of the lists
   * @param oldGenome1
   *          the first parent's genome
   * @param oldGenome2
   *          the second parent's genome
   * 
   * @return two recombined child genomes
   */
  protected static Pair<List<Integer>, List<Integer>> recombineGenomes(
      int crossoverPoint, int maxSize, List<Integer> oldGenome1,
      List<Integer> oldGenome2) {
    List<Integer> genome1 = new ArrayList<>();
    List<Integer> genome2 = new ArrayList<>();
    for (int i = 0; i < crossoverPoint; i++) {
      if (i < oldGenome1.size()) {
        genome1.add(oldGenome1.get(i));
      }
      if (i < oldGenome2.size()) {
        genome2.add(oldGenome2.get(i));
      }
    }
    for (int i = crossoverPoint; i < maxSize; i++) {
      if (i < oldGenome2.size()) {
        genome1.add(oldGenome2.get(i));
      }
      if (i < oldGenome1.size()) {
        genome2.add(oldGenome1.get(i));
      }
    }
    return new Pair<>(genome1, genome2);
  }

  @Override
  public boolean[] getBooleanRepresentation() {
    boolean[] representation = new boolean[length];
    for (int gene : genome) {
      representation[gene] = true;
    }
    return representation;
  }

  @Override
  public int numOfChosenAlgo() {
    return genome.size();
  }
}
