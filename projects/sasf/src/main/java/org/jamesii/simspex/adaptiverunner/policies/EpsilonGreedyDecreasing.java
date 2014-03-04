/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.adaptiverunner.policies;

/**
 * Stochastic gambling policy. See
 * "Finite-Time Analysis of the Multiarmed Bandit Problem" (Auer et al., 2002)
 * (it is called EpsiolonGreedy there). The probability of exploration decreases
 * as defined by the epsilon() method. Doesn't use parameter epsilon
 * (getEpsilon(), setEpsilon() will throw an exception).
 * 
 * @author Roland Ewald
 * 
 */
public class EpsilonGreedyDecreasing extends SemiUniform {

  /** Serialisation ID. */
  private static final long serialVersionUID = -914888686719501299L;

  /** The default value for c (from the paper, see class documentation). */
  static final double DEFAULT_C = 0.2;

  /** The default value for d (from the paper, see class documentation). */
  static final double DEFAULT_D = 0.5;

  /**
   * Parameter of the heuristic. It has to be > 0. The algorithm's performance
   * relies very much on c, and c is problem-dependent! The higher c, the more
   * exploration takes place.
   */
  private double c = DEFAULT_C;

  /**
   * Parameter of the heuristic. It has to be in (0,1). The higher d, the more
   * exploitation takes place (earlier).
   */
  private double d = DEFAULT_D;

  @Override
  public int nextChoice() {
    Double probExploitation = 1 - epsilon();
    int choice =
        getRandom().nextDouble() >= probExploitation ? super.getRandomIndex()
            : getBestIndex();
    changed(choice);
    return choice;
  }

  /**
   * Calculates the probability of exploration. exploration means random
   * selection of any arm in this context.
   * 
   * @return
   */
  double epsilon() {
    if (getOverallPullCount() == 0) {
      return 1;
    }
    return Math.min(1, (c * getNumOfArms()) / (d * d * getOverallPullCount()));
  }

  public double getC() {
    return c;

  }

  /**
   * C has to be > 0. Other value will be ignored.
   * 
   * @param c
   *          the new value
   */
  public void setC(double c) {
    if (c > 0) {
      this.c = c;
    }
  }

  public double getD() {
    return d;
  }

  /**
   * D has to be in (0,1). Other values will be ignored.
   * 
   * @param d
   *          the new value
   */
  public void setD(double d) {
    if (d > 0 && d < 1) {
      this.d = d;
    }
  }

  /**
   * This class doesn't use the parameter epsilon: getEpsilon and setEpsilon
   * will throw an exception.
   */
  @Override
  public void setEpsilon(double epsilon) {
    throw new UnsupportedOperationException();
  }

  @Override
  public double getEpsilon() {
    throw new UnsupportedOperationException();
  }
}
