/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios.plugintype;

import org.jamesii.asf.portfolios.plugintype.PortfolioPerformanceData;
import org.jamesii.asf.portfolios.plugintype.PortfolioProblemDescription;

import junit.framework.TestCase;

/**
 * Tests {@link PortfolioProblemDescription}.
 * 
 * @author Roland Ewald
 * 
 */
public class TestPortfolioProblemDescription extends TestCase {

  private static final Double[][] PERF_MATRIX = new Double[][] {
      { 1., 2., 3. }, { 4., 5., 6. } };

  PortfolioProblemDescription ppd;

  @Override
  public void setUp() {
    ppd =
        new PortfolioProblemDescription(new PortfolioPerformanceData(
            PERF_MATRIX), 0, false, 1, 2);
  }

  public void testMultiplybyMinOneForMinimization() {
    Double[][] invMatrix = ppd.invert(PERF_MATRIX);
    assertEquals(invMatrix.length, PERF_MATRIX.length);
    assertEquals(invMatrix[0].length, PERF_MATRIX[0].length);

    for (int i = 0; i < invMatrix.length; i++) {
      for (int j = 0; j < invMatrix[0].length; j++) {
        assertEquals(-1 * PERF_MATRIX[i][j], invMatrix[i][j]);
      }
    }
  }
}
