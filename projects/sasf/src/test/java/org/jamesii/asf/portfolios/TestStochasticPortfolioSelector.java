/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios;


import java.util.List;

import org.jamesii.asf.portfolios.plugintype.AbstractPortfolioSelector;
import org.jamesii.asf.portfolios.plugintype.PortfolioPerformanceData;
import org.jamesii.asf.portfolios.plugintype.PortfolioProblemDescription;
import org.jamesii.asf.portfolios.stochsearch.StochSearchPortfolioSelector;
import org.jamesii.core.math.random.generators.mersennetwister.MersenneTwister;
import org.jamesii.core.util.misc.Pair;

import junit.framework.TestCase;

/**
 * Tests for {@link StochSearchPortfolioSelector}.
 * 
 * @author Roland Ewald
 * 
 */
public class TestStochasticPortfolioSelector extends TestCase {

  /** The portfolio selector to be tested. */
  StochSearchPortfolioSelector ps;

  private static final Double[][] PERF_DATA = new Double[][] {
      { 1., .5, 1.5, 1. }, { 2.5, 2., 1.5, 1. }, { 2.5, 3.5, 3., 1. } };

  @Override
  public void setUp() {
    ps = new StochSearchPortfolioSelector(100000, new MersenneTwister());
  }

  /**
   * Tests simple portfolio construction.
   */
  public void testSimplePortfolioConstruction() {

    List<Pair<Integer, Double>> portfolio =
        ps.constructPortfolio(new PortfolioProblemDescription(
            new PortfolioPerformanceData(PERF_DATA), 1, false, 1, 2));
    assertTrue(portfolio.size() == 1 || portfolio.size() == 2);

    double weightSum = 0;
    for (Pair<Integer, Double> pElem : portfolio) {
      assertTrue(pElem.getSecondValue() >= 0 && pElem.getSecondValue() <= 1.0);
      assertTrue(pElem.getFirstValue() >= 0
          && pElem.getFirstValue() < PERF_DATA[0].length);
      weightSum += pElem.getSecondValue();
    }

    assertTrue(Math.abs(1 - weightSum) <= AbstractPortfolioSelector.EPSILON);

    // This is a stochastic result (although *very* likely) - re-run if check
    // fails
    PERF_DATA[0] = new Double[] { .5, .5, .5, .5 };
    portfolio =
        ps.constructPortfolio(new PortfolioProblemDescription(
            new PortfolioPerformanceData(PERF_DATA), 1, true, 1, 2));
    assertEquals(1, portfolio.size());
    assertEquals(2, portfolio.get(0).getFirstValue().intValue());

    portfolio =
        ps.constructPortfolio(new PortfolioProblemDescription(
            new PortfolioPerformanceData(PERF_DATA), 1, false, 1, 2));
    assertEquals(1, portfolio.size());
    assertEquals(0, portfolio.get(0).getFirstValue().intValue());

  }
}
