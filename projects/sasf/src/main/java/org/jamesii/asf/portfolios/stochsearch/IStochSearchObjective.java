/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios.stochsearch;


import java.io.Serializable;
import java.util.List;

import org.jamesii.core.util.misc.Pair;

/**
 * Simple interface for objective functions that can be plugged into the
 * {@link StochSearchPortfolioSelector}. Apart from that its function is
 * analogous to {@link org.jamesii.asf.portfolios.ga.fitness.IPortfolioFitness}.
 * 
 * @author Roland Ewald
 * 
 */
public interface IStochSearchObjective extends Serializable {

  /**
   * Calculate portfolio efficiency.
   * 
   * @param weights
   *          the weights of the algorithms
   * @param avgPerf
   *          the avgerage performances
   * @param covMat
   *          the covariance matrix
   * @param acceptableRisk
   *          the acceptable risk
   * @return the result
   */
  double calcPortfolioEfficiency(double[] weights, Double[] avgPerf,
      Double[][] covMat, Double acceptableRisk);

  /**
   * Gets the detailed fitness description.
   * 
   * @param weights
   *          the weights
   * @param avgPerf
   *          the avg perf
   * @param covMat
   *          the cov mat
   * @param acceptableRisk
   *          the acceptable risk
   * @return the detailed fitness description
   */
  List<Pair<String, Double>> getDetailedFitnessDescription(double[] weights,
      Double[] avgPerf, Double[][] covMat, Double acceptableRisk);

}
