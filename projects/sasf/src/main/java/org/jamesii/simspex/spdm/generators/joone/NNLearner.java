/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.generators.joone;

/**
 * Enumeration to define learner types. See Complete Joone Guide
 * (http://www.jooneworld.com/docs/documentation.html) for documentation.
 * 
 * @author Roland Ewald
 * 
 */
public enum NNLearner {

  /** Gradient-descent learner. */
  BASIC_LEARNER,

  /** Batch-learning. */
  BATCH_LEARNER,

  /** Resilient back-propagation. */
  RPROP_LEAERNER;

  /** Default package. */
  private static final String DEF_PACKAGE = "org.joone.engine.";

  /** Default class name ending. */
  private static final String DEF_CLASS_SUFFIX = "Learner";

  @Override
  public String toString() {
    switch (this) {
    case BATCH_LEARNER:
      return DEF_PACKAGE + "Batch" + DEF_CLASS_SUFFIX;
    case RPROP_LEAERNER:
      return DEF_PACKAGE + "Rprop" + DEF_CLASS_SUFFIX;
    default:
    case BASIC_LEARNER:
      return DEF_PACKAGE + "Basic" + DEF_CLASS_SUFFIX;
    }
  }

  /**
   * Get type by a number (to support integer parameter).
   * 
   * @param index
   *          the index identifying the learner type
   * @return the corresponding learner type
   */
  public static NNLearner getLearnerType(int index) {
    return values()[index];
  }
}
