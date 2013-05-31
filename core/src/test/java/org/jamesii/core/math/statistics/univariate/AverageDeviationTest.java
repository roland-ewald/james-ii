/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.univariate;

import org.jamesii.core.math.statistics.StatisticsTest;
import org.jamesii.core.math.statistics.univariate.ArithmeticMean;
import org.jamesii.core.math.statistics.univariate.AverageDeviation;

/**
 * Tests the {@link AverageDeviation} class.
 * 
 * @author Jan Himmelspach
 */
public class AverageDeviationTest extends StatisticsTest {

  /**
   * Tests the {@link AverageDeviation#averageDeviationDouble(double[])} method.
   */
  public void testAverageDeviationDouble() {
    double[] vals = new double[] { 1.0, 1.0, 1.0, 1.0, 1.0 };
    assertEquals(0.0, AverageDeviation.averageDeviationDouble(vals));
    vals = new double[] { 1.1, 1.1, 0.9, .9 };
    System.out.println(ArithmeticMean.arithmeticMean(vals));
    assertEquals(0.1, AverageDeviation.averageDeviationDouble(vals), EPSILON);
  }

  /** Tests the {@link AverageDeviation#averageDeviationInt(int[])} method. */
  public void testAverageDeviationInt() {
    int[] vals = new int[] { 1, 1, 1, 1, 1 };
    assertEquals(0.0, AverageDeviation.averageDeviationInt(vals));
    vals = new int[] { 2, 2, 0, 0 };
    System.out.println(ArithmeticMean.arithmeticMean(vals));
    assertEquals(1.0, AverageDeviation.averageDeviationInt(vals));
  }

}
