/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios.stochsearch;


import java.util.ArrayList;
import java.util.List;

import org.jamesii.asf.portfolios.MeanVariancePortfolioUtils;
import org.jamesii.core.util.misc.Pair;


/**
 * Implements objective function for mean-variance portfolios.
 * 
 * @author Roland Ewald
 * 
 */
public class MeanVarianceObjective implements IStochSearchObjective {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 8576843095591239166L;

  @Override
  public double calcPortfolioEfficiency(double[] weights, Double[] avgPerf,
      Double[][] covMat, Double acceptableRisk) {
    return MeanVariancePortfolioUtils.calcPortfolioEfficiency(weights, avgPerf,
        covMat, acceptableRisk);
  }

  @Override
  public List<Pair<String, Double>> getDetailedFitnessDescription(
      double[] weights, Double[] avgPerf, Double[][] covMat,
      Double acceptableRisk) {
    Pair<Double, Double> fitnComp = MeanVariancePortfolioUtils
        .calcMeanAndVariance(weights, avgPerf, covMat);
    List<Pair<String, Double>> result = new ArrayList<>();
    result.add(new Pair<>("Mean", fitnComp.getFirstValue()));
    result.add(new Pair<>("Variance", fitnComp.getSecondValue()));
    return result;
  }

}
