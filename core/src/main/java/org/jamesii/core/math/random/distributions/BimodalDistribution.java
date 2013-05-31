/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.distributions;

import org.jamesii.core.math.random.generators.IRandom;

/**
 * Number generator for a split distribution. Creates random number which are
 * (with a certain probability either in a first or a second hump). the width of
 * these humps is given by muFirst and muSecond. The width of the second
 * interval should be smaller as startOfSecond, which is the start of the second
 * interval.
 * 
 * e.g.
 * 
 * ---- <br>
 * ---- _<br>
 * ---- _<br>
 * 
 * Do not modify the default parameter values! These are the values typically
 * used for evaluating the performance of event queues.
 * 
 * @author Jan Himmelspach
 */
public class BimodalDistribution extends AbstractDistribution {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 904477680973829180L;

  /** stretch factor of the first interval. */
  private double muFirst = 0.95238;

  /** stretch factor of the second interval. */
  private double muSecond = 0.95238;

  /** the number of numbers which shall be in the second interval. */
  private double percentageSecond = 0.1;

  /** start of the second interval. */
  private double startOfSecond = 9.5238;

  /**
   * Instantiates a new bimodal distribution.
   * 
   * @param seed
   *          the seed
   */
  public BimodalDistribution(long seed) {
    super(seed);
  }

  /**
   * Instantiates a new bimodal distribution.
   * 
   * @param random
   *          the random
   */
  public BimodalDistribution(IRandom random) {
    super(random);
  }

  /**
   * Instantiates a new bimodal distribution.
   * 
   * @param random
   *          the random
   * @param muFirst
   *          the mu first
   * @param muSecond
   *          the mu second
   * @param percentageSecond
   *          the percentage second
   * @param startOfSecond
   *          the start of second
   */
  public BimodalDistribution(IRandom random, double muFirst, double muSecond,
      double percentageSecond, double startOfSecond) {
    this(random);
    this.muFirst = muFirst;
    this.muSecond = muSecond;
    this.percentageSecond = percentageSecond;
    this.startOfSecond = startOfSecond;
  }

  @Override
  public double getRandomNumber() {
    double rand = getRandom().nextDouble();
    if (rand < percentageSecond) {
      rand = startOfSecond + muSecond * rand;
    } else {
      rand = muFirst * rand;
    }
    return rand;
  }

  @Override
  public AbstractDistribution getSimilar(IRandom newRandomizer) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String toString() {
    return "B(" + muFirst + "," + percentageSecond + "," + startOfSecond + ","
        + muSecond + ")";
  }

}
