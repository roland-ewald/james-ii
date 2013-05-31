/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.distributions;

import org.jamesii.core.math.random.generators.IRandom;

/**
 * 
 * RoundingUniformDistribution -
 * 
 * @author Roland Ewald
 * 
 */
public class RoundingUniformDistribution extends UniformDistribution {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 4590441906188074931L;

  /** The counter. */
  private int counter = 0;

  /**
   * Array of distinctions
   */
  private double[] distinctions;

  /**
   * Number of distinctions
   */
  private int numOfDistinctions;

  /**
   * Standard constructor
   * 
   * @param randomizer
   * @param lowerBorder
   * @param upperBorder
   * @param numOfDecimalDistinctions
   */
  public RoundingUniformDistribution(IRandom randomizer, double lowerBorder,
      double upperBorder, int numOfDecimalDistinctions) {
    super(randomizer, lowerBorder, upperBorder);
    setNumOfDecimalDistinctions(numOfDecimalDistinctions);
  }

  /**
   * Fill distinctions.
   * 
   * @param distincts
   *          the distinctions
   * @param lBorder
   *          the lower border
   * @param uBorder
   *          the upper border
   * @param numOfDistinctions
   *          the num of distinctions
   */
  private void fillDistinctions(double[] distincts, double lBorder,
      double uBorder, int numOfDistinctions) {

    double myValue = lBorder + (uBorder - lBorder) / 2;
    distincts[counter] = myValue;
    counter++;

    if (numOfDistinctions == 1) {
      return;
    }

    int nod = numOfDistinctions - 1;

    int leftNumOfDistinctions = (int) Math.ceil(nod / (double) 2);
    int rightNumOfDistinctions = nod - leftNumOfDistinctions;

    fillDistinctions(distincts, lBorder, myValue, leftNumOfDistinctions);

    if (rightNumOfDistinctions > 0) {
      fillDistinctions(distincts, myValue, uBorder, rightNumOfDistinctions);
    }

  }

  /**
   * @return Returns the numOfDecimalDistinctions.
   */
  public int getNumOfDecimalDistinctions() {
    return numOfDistinctions;
  }

  /**
   * @see org.jamesii.core.math.random.distributions.UniformDistribution#getRandomNumber()
   */
  @Override
  public double getRandomNumber() {

    double val = distinctions[this.getRandom().nextInt(distinctions.length)];
    // System.out.println("UR:" + val);
    return val;
  }

  /**
   * Overridden method
   * 
   * @see org.jamesii.core.math.random.distributions.UniformDistribution#getSimilar()
   */
  @Override
  public AbstractDistribution getSimilar(IRandom random) {
    return new RoundingUniformDistribution(random, getLowerBorder(),
        getUpperBorder(), getNumOfDecimalDistinctions());
  }

  /**
   * 
   * @param numOfDistinctions
   */
  public final void setNumOfDecimalDistinctions(int numOfDistinctions) {

    if (numOfDistinctions < 0) {
      return;
    }

    this.numOfDistinctions = numOfDistinctions;

    distinctions = new double[numOfDistinctions + 2];

    // double difference = upperBorder - lowerBorder;
    // int roundingFactor = (int) Math.pow(10.0, Math.ceil(Math
    // .log10(numOfDistinctions)));
    // for (int i = 0; i <= numOfDistinctions + 1; i++) {
    // distinctions[i] = lowerBorder + i / (double) (numOfDistinctions + 1)
    // * difference;
    // distinctions[i] = Math.round(roundingFactor * distinctions[i])
    // / (double) roundingFactor;
    // }

    distinctions[0] = getLowerBorder();
    distinctions[1] = getUpperBorder();
    counter = 2;
    if (numOfDistinctions > 0) {
      fillDistinctions(distinctions, getLowerBorder(), getUpperBorder(),
          numOfDistinctions);
    }
  }

  /**
   * 
   * Overridden method
   * 
   * @see org.jamesii.core.math.random.distributions.UniformDistribution#toString()
   */
  @Override
  public String toString() {
    return "Ur(" + getLowerBorder() + "," + getUpperBorder() + ","
        + numOfDistinctions + ")";
  }

}
