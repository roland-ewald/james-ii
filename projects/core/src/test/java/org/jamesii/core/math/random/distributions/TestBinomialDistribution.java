/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.distributions;

import org.jamesii.core.math.random.distributions.BinomialDistribution;
import org.jamesii.core.math.statistics.StatisticsTest;

/**
 * Tests {@link BinomialDistribution}.
 * 
 * @see BinomialDistribution
 * 
 * @author Roland Ewald
 * 
 */
public class TestBinomialDistribution extends StatisticsTest {

  /** The number of coin tosses. */
  private static final int NUM_COIN_TOSSES = 4;

  /** The probability of head. */
  private static final double PROB_HEAD = 0.25;

  /** The constant explanation text. */
  static final String TEST_EXPLANATION = "Probability of coin toss (P(tail)="
      + (1 - PROB_HEAD) + ", P(head)= " + PROB_HEAD + "), ";

  public void testDistributionFunction() {
    assertEquals(TEST_EXPLANATION + "0 times head.", 0.32,
        BinomialDistribution.probability(NUM_COIN_TOSSES, 0, PROB_HEAD),
        EPSILON_LARGE);
    assertEquals(TEST_EXPLANATION + "1 times head.", 0.42,
        BinomialDistribution.probability(4, 1, 0.25), EPSILON_LARGE);
    assertEquals(TEST_EXPLANATION + "4 times head.", 0.004,
        BinomialDistribution.probability(4, 4, 0.25), EPSILON_LARGE);

    assertEquals(TEST_EXPLANATION + " <= 3 times head",
        1. - BinomialDistribution.probability(4, 4, 0.25),
        BinomialDistribution.distribution(4, 3, 0.25), EPSILON_LARGE);
  }
}
