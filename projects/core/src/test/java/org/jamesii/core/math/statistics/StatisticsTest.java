/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics;

import junit.framework.TestCase;

/**
 * Simple super class for statistical tests.
 * 
 * @author Roland Ewald
 * 
 */
public abstract class StatisticsTest extends TestCase {

  /**
   * Epsilon for floating point equality tests (necessary due to roundd-off
   * errors).
   */
  public static double EPSILON = 0.00001;

  /** Epsilon for less accurate approximations. */
  public static double EPSILON_LARGE = 0.01;

  public StatisticsTest() {
  }

  public StatisticsTest(String name) {
    super(name);
  }

}
