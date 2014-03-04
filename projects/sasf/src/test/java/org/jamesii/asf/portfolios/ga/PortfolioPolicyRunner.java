/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios.ga;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.simspex.adaptiverunner.policies.plugintype.IMinBanditPolicy;


/**
 * Run {@link org.jamesii.simspex.adaptiverunner.policies.plugintype.IMinBanditPolicy} with
 * given portfolios.
 * 
 * @author René Schulz, Roland Ewald
 * 
 */

public class PortfolioPolicyRunner {

  /** The used policy. */
  private final IMinBanditPolicy policy;

  /** The performanceGenerator used to create the portfolio. */
  private final PerformanceDataGenerator generator;

  /**
   * Store the number of the algorithm in the generator for each arm of the
   * policy.
   */
  private final List<Integer> indexMap = new ArrayList<>();

  /**
   * Stores the overhead for each round (= (sum of runtime policy)/(round *
   * bestPerformance)).
   */
  private List<Double> overhead = new ArrayList<>();

  /** The list of rewards. */
  private List<Double> rewards = new ArrayList<>();

  /** The number of arms. */
  private final int numOfArms;

  /** The current round. */
  private int currentRound = 0;

  /** The sum of rewards. */
  private Double sumOfRewards = 0.0;

  /**
   * Instantiates a new portfolio policy runner.
   * 
   * @param policy
   *          the policy
   * @param portfolio
   *          the portfolio
   * @param generator
   *          the generator
   */
  public PortfolioPolicyRunner(IMinBanditPolicy policy, double[] portfolio,
      PerformanceDataGenerator generator) {
    this.policy = policy;
    this.generator = generator;
    for (int i = 0; i < portfolio.length; i++) {
      if (portfolio[i] != 0.0) {
        indexMap.add(i);
      }
    }
    numOfArms = indexMap.size();
  }

  /**
   * Run policy.
   * 
   * @param horizon
   *          the horizon
   * @param problem
   *          number of the problem (from PerformanceDataGenerator).
   */
  public void runPolicy(int horizon, int problem) {
    policy.init(numOfArms, horizon);
    double bestPerformance = generator.getBestPossiblePerformance(problem);
    while (currentRound < horizon) {
      int arm = policy.nextChoice();
      double reward = generator.getPerformance(indexMap.get(arm), problem);
      policy.receiveReward(arm, reward);
      sumOfRewards += reward;
      rewards.add(reward);
      currentRound++;
      overhead.add((sumOfRewards / (currentRound * bestPerformance)) - 1);
    }
  }

  public Double[] getOverhead() {
    return overhead.toArray(new Double[0]);
  }

  public Double[] getReward() {
    return rewards.toArray(new Double[0]);
  }

  public Double getSumOfRewards() {
    return sumOfRewards;
  }

}
