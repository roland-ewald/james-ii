/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.univariate;

import org.jamesii.core.math.statistics.StatisticsTest;

/**
 * @author Jan Himmelspach *
 */
public class SkewnessTest extends StatisticsTest {

  /**
   * @param name
   */
  public SkewnessTest(String name) {
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

  /**
   * Test skewness double.
   */
  public void testSkewnessDouble() {
    // cross check
    assertEquals(Skewness.skewness(new double[] { 42.0 }), Analysis
        .compute(new double[] { 42.0 }).getSkewness(), EPSILON);
    double[] d3 = { 1., 4., 1., 1., 2., 1. };
    assertEquals(Skewness.skewness(d3), Analysis.compute(d3)
        .getSkewness(), EPSILON);
  }

}
