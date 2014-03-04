/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.exploration.ils.algorithm;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.simspex.exploration.ils.termination.ITerminationIndicator;
import org.jamesii.simspex.exploration.ils.termination.ParamILSTerminationSimpleCounter;


/**
 * Focused Iterated Local Search (ILS) algorithm to solve the 'Algorithm
 * Configuration Problem'.
 * <p/>
 * From "ParamILS: An automatic algorithm configuration framework" by: F.
 * Hutter, H. Hoos, K. Leyton-Brown, T. Stützle Journal of Artificial
 * Intelligence Research, Vol. 36 (2009), pp. 267-306. <a
 * href="http://dx.doi.org/10.1613/jair.2861">doi:10.1613/jair.2861</a>
 * <p/>
 * This is one possibility of implementing the ParamILS algorithm. It uses a
 * better function which calculates the estimated cost for different
 * ParameterConfigurations based on an adaptable amount of test cases.
 * 
 * 
 * @author Robert Engelke
 */
public class FocusedILS extends ParamILS {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -1835448121432403229L;

  /** The Constant DEFAULT_EPSILON. */
  private static final double DEFAULT_EPSILON = 0.0;

  /** The epsilon parameter. */
  private double epsilon;

  /** Counts the number of algorithm runs performed since last improvement step. */
  private int bonusRuns;

  /**
   * Instantiates a new focused ILS.
   */
  public FocusedILS() {
    this(new ParamILSTerminationSimpleCounter());
  }

  /**
   * Instantiates a new focused ils.
   * 
   * @param termination
   *          the termination condition
   */
  public FocusedILS(ITerminationIndicator<ParamILS> termination) {
    super(termination);
    bonusRuns = 0;
    epsilon = DEFAULT_EPSILON;
  }

  @Override
  public boolean better(ParameterBlock config1, ParameterBlock config2) {
    bonusRuns++;
    int runsConfig1 = getInformationSource().getNumberOfRuns(config1);
    int runsConfig2 = getInformationSource().getNumberOfRuns(config2);
    ParameterBlock minConfiguration, maxConfiguration;
    if (runsConfig1 <= runsConfig2) {
      minConfiguration = config1;
      maxConfiguration = config2;
      if (runsConfig1 == runsConfig2) {
        bonusRuns++;
      }
    } else {
      minConfiguration = config2;
      maxConfiguration = config1;
    }
    // while not one configuration is dominating the other run through the test
    // cases
    int i;
    do {
      i = getInformationSource().getNumberOfRuns(minConfiguration) + 1;
      objective(maxConfiguration, i, getMaxExecutionTime());
      objective(minConfiguration, i,
          getConsistentEstimator(maxConfiguration, i));

    } while (!(dominates(config1, config2,
        getConsistentEstimator(maxConfiguration, i)) || dominates(config2,
        config1, getConsistentEstimator(maxConfiguration, i))));

    if (dominates(config1, config2, getConsistentEstimator(maxConfiguration, i))) {
      objective(config1, runsConfig1 + bonusRuns, getMaxExecutionTime());
      bonusRuns = 0;
      return true;
    }
    return false;
  }

  /**
   * Checks whether one ParameterConfiguration dominates the other.
   * ParameterConfiguration1 P1 is dominating ParameterConfiguration2 P2 if the
   * number of training instances of ParameterConfiguration1 N(P1) >= N(P2) and
   * the N(P2)-th consistent estimator of P1 <= the N(P2)-th consistent
   * estimator of P2.
   * 
   * @param config1
   *          ParameterConfiguration1
   * @param config2
   *          ParameterConfiguration2
   * @return true, if config1 dominates config2
   */
  public boolean dominates(ParameterBlock config1, ParameterBlock config2,
      double bound) {
    if (getInformationSource().getNumberOfRuns(config1) < getInformationSource()
        .getNumberOfRuns(config2)) {
      return false;
    }
    return objective(config1, getInformationSource().getNumberOfRuns(config2),
        bound) * getEpsilonMultiplier() <= objective(config2,
          getInformationSource().getNumberOfRuns(config2), bound);
  }

  /**
   * Gets the epsilon.
   * 
   * @return the epsilon
   */
  public double getEpsilon() {
    return epsilon;
  }

  /**
   * Sets the epsilon for stochastic experiments.
   * 
   * @param eps
   *          the new epsilon
   */
  public void setEpsilon(double eps) {
    epsilon = eps;
  }

  /**
   * Gets the epsilon multiplier.
   * 
   * @return the epsilon multiplier
   */
  public double getEpsilonMultiplier() {
    return epsilon + 1;
  }

}
