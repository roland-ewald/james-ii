/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.univariate;

import org.jamesii.core.math.statistics.univariate.QuartilRange;

import junit.framework.TestCase;

/**
 * @author Jan Himmelspach *
 */
public class QuartilRangeTest extends TestCase {

  /**
   * @param name
   */
  public QuartilRangeTest(String name) {
    super(name);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testQuartiles() {

    int[] it = new int[] { 2, 2, 2, 3, 4, 4, 5 };
    double q = QuartilRange.quartilRangeNumber(it);
    assertTrue(Double.compare(q, 2.) == 0);

    int[] it2 = new int[] { 2, 2, 2, 3, 4, 4, 5, 5 };
    double q2 = QuartilRange.quartilRangeNumber(it2);
    assertTrue(Double.compare(q2, 3.) == 0);

  }

}
