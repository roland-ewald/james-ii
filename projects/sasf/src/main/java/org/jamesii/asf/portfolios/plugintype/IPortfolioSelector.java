/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios.plugintype;


import java.util.List;

import org.jamesii.core.util.misc.Pair;

/**
 * Interface for portfolio selection methods, i.e. methods that construct an
 * algorithm portfolio, given a certain performance metric (see
 * {@link org.jamesii.perfdb.recording.performance.IPerformanceMeasurer}) and a set of
 * eligible configurations (in form of selection trees). Note that these
 * components need to maximize OR minimize the metric, depending on its
 * semantics (e.g. accuracy: maximization, run time: minimization).
 * 
 * @author Roland Ewald
 * 
 */
public interface IPortfolioSelector {

  /**
   * Construct portfolio.
   * 
   * @param matrix
   *          of average performances (algorithm x problem)
   * @param minSize
   *          the minimal size of the portfolio
   * @param maxSize
   *          the maximal size of the portfolio
   * @param acceptableRisk
   *          risk factory, should be in [0,1] where 0 means no risk and 1 means
   *          maximum risk
   * @param maximize
   *          if true, a portfolio that maximizes performance will be searched
   *          for, otherwise portfolio with minimal performance gets selected
   * @return list with (index, weight) tuples, where weight denotes the
   *         investment to be placed on the selection tree corresponding to the
   *         index, i.e. the list contains all non-zero entries that constitute
   *         the efficient portfolio for the given risk level and performance
   *         history
   */
  List<Pair<Integer, Double>> constructPortfolio(
      PortfolioProblemDescription problemDescription);

}
