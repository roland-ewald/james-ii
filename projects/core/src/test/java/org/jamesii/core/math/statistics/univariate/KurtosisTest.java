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
public class KurtosisTest extends StatisticsTest {

  /**
   * @param name
   */
  public KurtosisTest(String name) {
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
  public void testKurtosisDouble() {

    // int[] i3 = { 1, 1, 1, 1, 1, 100 };
    // assertEquals(Kurtosis.kurtosis(i3), 6);
    //
    // double[] d3 = { 1., 1., 1., 1., 1., 100. };
    // assertEquals(6., new Analysis().compute(d3).kurtosis, EPSILON);
    //
    // //cross check
    // assertEquals(Kurtosis.kurtosis(new int[] { 42 }), new
    // Analysis().compute(new double[] { 42.0 }).kurtosis, EPSILON);
    // double[] d4 = { 1., 4., 1., 1., 2., 1. };
    // assertEquals(Kurtosis.kurtosis(new int[] { 1, 4, 1, 1, 2, 1 }), new
    // Analysis().compute(d4).kurtosis, EPSILON);
  }

}
