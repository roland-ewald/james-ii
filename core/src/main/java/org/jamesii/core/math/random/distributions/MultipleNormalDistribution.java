/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.distributions;

import org.jamesii.core.math.random.generators.IRandom;

// TODO: Auto-generated Javadoc
/**
 * simsim.utils.distributions.MutlipleNormalDistribution -
 * 
 * Defines a (value) distribution between a lower and an upper border. The
 * values are concentrated on certain points that divide the range between lower
 * and upper border. Each distribution around one of those points will have the
 * characteristics of a normal distribution with a certain deviation and the
 * point itself as mean value.
 * 
 * Created on 11:52:42 13.07.2005, added to SimSim on 07/11/2005
 * 
 * @author Roland Ewald
 */
public class MultipleNormalDistribution extends AbstractDistribution {

  /** <code>serialVersionUID</code>. */
  private static final long serialVersionUID = 3545514019344758835L;

  /** Deviation (for all normal distributions). */
  private double deviation = 0.1;

  /** Overall lower border. */
  private double lowerBorder = 0;

  /** Array of normal distributions to generate the variables. */
  private AbstractNormalDistribution[] normalDistribution = null;

  /** Counter used to use the normal distributions round-robin. */
  private int numberCount = 0;

  /** Defines the number of concentration points. */
  private int numOfSegments = 3;

  /** Overall upper border. */
  private double upperBorder = 1;

  /**
   * Factory constructor.
   * 
   * @param seed
   *          the seed
   */
  public MultipleNormalDistribution(long seed) {
    super(seed);
    if (isInitializable()) {
      reset();
    }
  }

  /**
   * Factory constructor.
   * 
   * @param rand
   *          the rand
   */
  public MultipleNormalDistribution(IRandom rand) {
    super(rand);
    if (isInitializable()) {
      reset();
    }
  }

  /**
   * Default constructor.
   * 
   * @param rand
   *          the rand
   * @param lBorder
   *          the l border
   * @param uBorder
   *          the u border
   * @param dev
   *          the dev
   * @param nSegments
   *          the n segments
   */
  public MultipleNormalDistribution(IRandom rand, double lBorder,
      double uBorder, double dev, int nSegments) {

    super(rand);

    this.lowerBorder = lBorder;
    this.upperBorder = uBorder;
    this.deviation = dev;
    this.numOfSegments = nSegments;

    if (isInitializable()) {
      reset();
    }
  }

  /**
   * Get deviation.
   * 
   * @return deviation
   */
  public double getDeviation() {
    return this.deviation;
  }

  /**
   * Get number of segments (each has a concentration point in its middle).
   * 
   * @return int number of segments
   */
  public int getNumOfSegments() {
    return this.numOfSegments;
  }

  @Override
  public double getRandomNumber() {
    double returnValue =
        this.normalDistribution[(this.numberCount++) % this.numOfSegments]
            .getRandomNumber();
    return returnValue;
  }

  @Override
  public AbstractDistribution getSimilar(IRandom newRandomizer) {
    return new MultipleNormalDistribution(newRandomizer, lowerBorder,
        upperBorder, deviation, numOfSegments);
  }

  /**
   * Test whether the normal distributions can be initialized.
   * 
   * @return boolean true, if it can be initialized, otherwise false
   */
  private boolean isInitializable() {
    return (this.numOfSegments > 0 && this.deviation >= 0
        && this.lowerBorder >= 0 && this.upperBorder > this.lowerBorder);
  }

  /**
   * Initialize normal distributions.
   */
  protected final void reset() {

    if (!isInitializable()) {
      return;
    }

    double meanSteps =
        (this.upperBorder - this.lowerBorder) / (2 * this.numOfSegments);

    this.normalDistribution =
        new AbstractNormalDistribution[this.numOfSegments];

    for (int i = 0; i < this.numOfSegments; i++) {
      this.normalDistribution[i] =
          new NormalDistribution(this.getRandom(), this.lowerBorder
              + (2 * i + 1) * meanSteps, this.deviation);
    }

  }

  /**
   * Set deviation.
   * 
   * @param deviation
   *          the deviation
   */
  public void setDeviation(double deviation) {
    this.deviation = deviation;
    if (isInitializable()) {
      reset();
    }
  }

  /**
   * Set lower border.
   * 
   * @param lowerBorder
   *          the lower border
   */
  public void setLowerBorder(double lowerBorder) {
    this.lowerBorder = lowerBorder;
    if (isInitializable()) {
      reset();
    }
  }

  /**
   * Set number of segments.
   * 
   * @param numOfSegments
   *          the num of segments
   */
  public void setNumOfPartitions(int numOfSegments) {
    this.numOfSegments = numOfSegments;
    if (isInitializable()) {
      reset();
    }
  }

  /**
   * Set lower border.
   * 
   * @param upperBorder
   *          the upper border
   */
  public void setUpperBorder(double upperBorder) {
    this.upperBorder = upperBorder;
    if (isInitializable()) {
      reset();
    }
  }
}
