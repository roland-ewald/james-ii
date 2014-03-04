/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios.ga;

import org.jamesii.core.math.random.distributions.AbstractDistribution;
import org.jamesii.core.math.random.distributions.NormalDistribution;
import org.jamesii.core.math.random.generators.IRandom;

/**
 * Data-structure for (@link GeneratePerformanceData). Represents a cluster of
 * algorithms that operate similarly well on the same problem classes, but with
 * differing standard deviation.
 * 
 * @author René Schulz, Roland Ewald
 */

public class AlgorithmCluster {

  /** The array of algorithm performance distributions. */
  private final AbstractDistribution algorithmPerformance[];

  /**
   * Instantiates a new algorithm cluster.
   * 
   * @param mean
   *          the mean
   * @param rng
   *          the RNG
   * @param numOfAlgosPerCluster
   *          the number of algorithms per cluster
   * @param deviationDistribution
   *          the deviation distribution
   */
  public AlgorithmCluster(IRandom rng, int numOfAlgosPerCluster,
      AbstractDistribution deviationDistribution) {
    algorithmPerformance = new AbstractDistribution[numOfAlgosPerCluster];
    // TODO: generalise, to be able to set arbitrary distributions!
    // algorithmPerformance[i] = new ErlangDistribution(rng, 0.5, 2);
    for (int i = 0; i < numOfAlgosPerCluster; i++) {
      algorithmPerformance[i] =
          new NormalDistribution(rng, 0, Math.abs(deviationDistribution
              .getRandomNumber()));
    }
  }

  /**
   * Gets a randomly generated algorithm performance for a given algorithm from
   * this cluster.
   * 
   * @param algo
   *          the index of the algorithm
   * 
   * @return the random performance
   */
  public double getRandomPerformance(double mean, int algo) {
    // TODO: generalise: mean + .1 * mean *
    // algorithmPerformance[algo].getRandomNumber();
    return mean + algorithmPerformance[algo].getRandomNumber();
  }
}
