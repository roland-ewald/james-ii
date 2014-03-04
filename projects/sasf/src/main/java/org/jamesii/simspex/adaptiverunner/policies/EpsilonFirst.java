/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.adaptiverunner.policies;

/**
 * After a pure exploration phase (horizon*epsilon trials finished) follows a
 * pure exploitation phase (horizon*(1-epsilon)). In the exploration phase a
 * random arm is pulled. In the exploitation phase the best arm (so far) is
 * always pulled. See: "Multi-Armed Bandit Algorithms and Empirical Evaluation"
 * by Joann`es Vermorel and Mehryar Mohri.
 * 
 * 
 * @author Rene Schulz
 * 
 */

public class EpsilonFirst extends SemiUniform {

  /** Serialisation ID. */
  private static final long serialVersionUID = -3088440505501058053L;

  @Override
  public int nextChoice() {
    int choice;
    if (getOverallPullCount() < getHorizon() * getEpsilon()) {
      // choose Random Arm in
      // exploration phase
      choice = super.getRandomIndex();
    } else {
      // after the exploration phase the best arm will
      // be chosen
      choice = getBestIndex();
    }

    changed(choice);
    return choice;
  }

}
