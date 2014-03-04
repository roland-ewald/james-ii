/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios.plugintype;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.asf.portfolios.MeanVariancePortfolioUtils;
import org.jamesii.core.base.Entity;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.core.util.misc.Strings;

// TODO: Auto-generated Javadoc
/**
 * Super class for all portfolio selectors. Handles the compliance to
 * min/maxSize by either removing entries with least influence/investment (in
 * case maxSize is violated) or by adding random configurations (in case minSize
 * is violated).
 * 
 * @author Roland Ewald
 * 
 */
public abstract class AbstractPortfolioSelector extends Entity implements
    IPortfolioSelector {

  /**
   * Compares portfolio representations by weight (the second element of the
   * tuple).
   */
  private static final class ComparatorByWeight implements
      Comparator<Pair<Integer, Double>>, Serializable {

    private static final long serialVersionUID = -7274768224575785636L;

    @Override
    public int compare(Pair<Integer, Double> o1, Pair<Integer, Double> o2) {
      return o1.getSecondValue().compareTo(o2.getSecondValue());
    }
  }

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 7611042511900060735L;

  /** Accuracy of summation. */
  public static final double EPSILON = 1e-13;

  /**
   * Construct portfolio.
   * 
   * @param problem
   *          the problem
   * @return the list
   */
  @Override
  public List<Pair<Integer, Double>> constructPortfolio(
      PortfolioProblemDescription problem) {

    List<Pair<Integer, Double>> portfolio = check(portfolio(problem));

    Pair<Double[], Double[][]> avgCov =
        MeanVariancePortfolioUtils.getAvgAndCovFromPerformances(problem
            .getPerformanceData()[0].performances);

    if (portfolio.size() > problem.getMaxSize()) {
      reducePortfolio(portfolio, problem.getMaxSize());
    } else if (portfolio.size() < problem.getMinSize()) {
      extendPortfolio(portfolio, problem.getMinSize(), avgCov.getFirstValue());
    }

    return portfolio;
  }

  /**
   * Extend portfolio to minSize. Non-efficient assets are added randomly, with
   * a weight of 1/minSize. Old assets weights are rescaled accordingly.
   * 
   * @param portfolio
   *          the portfolio to be extended
   * @param minSize
   *          the minimal size
   * @param avgPerf
   *          average performance for each available assset
   */
  protected void extendPortfolio(List<Pair<Integer, Double>> portfolio,
      int minSize, Double[] avgPerf) {

    HashSet<Integer> notInPort = new HashSet<>(avgPerf.length);
    for (int i = 0; i < avgPerf.length; i++) {
      notInPort.add(i);
    }
    for (Pair<Integer, Double> pElem : portfolio) {
      notInPort.remove(pElem.getFirstValue());
    }

    // Generate random list of assets that could be added
    List<Integer> notInP = new ArrayList<>(notInPort);
    Collections.shuffle(notInP);

    // Add new assets
    double randAssetW = 1.0 / minSize;
    int pSize = portfolio.size();
    for (int i = 0; i < (minSize - pSize); i++) {
      portfolio.add(new Pair<>(notInP.get(i), randAssetW));
    }

    // Re-scale weights of old assets
    double scale = pSize / (double) minSize;
    for (int i = 0; i < pSize; i++) {
      Pair<Integer, Double> pElem = portfolio.get(i);
      pElem.setSecondValue(pElem.getSecondValue() * scale);
    }
  }

  /**
   * Reduce portfolio to maxSize. Sorts all assets in ascending weight order,
   * removes those with lowest weights and re-scales weights of the others, so
   * that the sum of weights still equals to 1.
   * 
   * @param portfolio
   *          the portfolio to be reduced
   * @param maxSize
   *          the maximal size
   */
  protected void reducePortfolio(List<Pair<Integer, Double>> portfolio,
      int maxSize) {

    Collections.sort(portfolio, new ComparatorByWeight());

    double weightRemoved = 0;
    for (int i = 0; i < (portfolio.size() - maxSize); i++) {
      weightRemoved += portfolio.remove(0).getSecondValue();
    }

    double scale = 1.0 / (1.0 - weightRemoved);
    for (Pair<Integer, Double> pElem : portfolio) {
      pElem.setSecondValue(pElem.getSecondValue() * scale);
    }

  }

  /**
   * Construct a portfolio.
   * 
   * @param problem
   *          the problem
   * @return the investments per asset for *minimal* performance
   */
  protected abstract double[] portfolio(PortfolioProblemDescription problem);

  /**
   * Checks the eligibility of the returned portfolio, e.g. if all elements are
   * in [0,1]. Counts non-zero elements (= size of the portfolio) and returns
   * them (incl. their weight).
   * 
   * @param results
   *          the results
   * 
   * @return the list (index,weight) of the portfolio, i.e. of all non-zero
   *         elements
   */
  protected List<Pair<Integer, Double>> check(double[] results) {
    double sum = 0.0;
    List<Pair<Integer, Double>> portfolio = new ArrayList<>();
    boolean negVals = false;

    // Count non-zero elements
    for (int i = 0; i < results.length; i++) {
      sum += results[i];
      negVals |= (results[i] < 0);
      if (results[i] != 0) {
        portfolio.add(new Pair<>(i, results[i]));
      }
    }

    if (negVals) {
      SimSystem.report(Level.WARNING, "PortfolioConstructor returned negative weights: "
      + Strings.dispArray(results));
    }
    if (Math.abs(sum - 1) >= EPSILON) {
      SimSystem.report(Level.WARNING, "PortfolioConstructor returned weight vector with sum > 1: "
      + Strings.dispArray(results));
    }

    return portfolio;
  }

}
