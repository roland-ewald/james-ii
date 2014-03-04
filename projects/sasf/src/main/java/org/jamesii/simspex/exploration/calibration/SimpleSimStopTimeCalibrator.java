/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.exploration.calibration;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jamesii.SimSystem;
import org.jamesii.core.math.random.RandomSampler;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.processor.plugintype.ProcessorFactory;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.simspex.exploration.ISimSpaceExplorer;


/**
 * This is a simple {@link ISimStopTimeCalibrator}, useful for testing and
 * preliminary experiments.
 * 
 * @author Roland Ewald
 */
public class SimpleSimStopTimeCalibrator implements ISimStopTimeCalibrator {

  /** Serialisation ID. */
  private static final long serialVersionUID = 880948472554368691L;

  /** The default relative tolerance. */
  private static final double DEFAULT_RELATIVE_TOLERANCE = 0.3;

  /**
   * The default maximal number of simulation time trials until calibration will
   * be aborted and minInitSimTime will be chosen.
   */
  private static final int DEFAULT_MAX_SIM_TIME_TRIALS = 10;

  /** The default maximal wall-clock time allowed per run (in seconds). */
  private static final double DEFAULT_MAX_WCT_TIME = 60;

  /** The default maximal simulation end time. */
  private static final double DEFAULT_MAX_SIM_END_TIME = 25;

  /** The default minimal initial simulation end time (for start-up etc). */
  private static final double DEFAULT_MIN_INIT_SIM_TIME = 0.1;

  /** The default portfolio size. */
  private static final int DEFAULT_PORTFOLIO_SIZE = 3;

  /** The default desired wall-clock time per run (in seconds). */
  private static final double DEFAULT_DESIRED_WCT_TIME = 5;

  /** The default minimal simulation end time. */
  private static final double DEFAULT_MIN_SIM_END_TIME = 0.01;

  /** Size of the calibration portfolio. */
  private int portfolioSize = DEFAULT_PORTFOLIO_SIZE;

  /** The minimal initial simulation end time (for start-up etc). */
  private double minInitSimTime = DEFAULT_MIN_INIT_SIM_TIME;

  /** The minimal simulation end time. */
  private double minSimEndTime = DEFAULT_MIN_SIM_END_TIME;

  /** The desired wall-clock time per run (in seconds). */
  private double desiredWCTime = DEFAULT_DESIRED_WCT_TIME;

  /** Maximal simulation end time. */
  private double maxSimEndTime = DEFAULT_MAX_SIM_END_TIME;

  /** The maximal wall-clock time allowed per run (in seconds). */
  private double maxWCTime = DEFAULT_MAX_WCT_TIME;

  /**
   * Maximal number of simulation time trials until calibration will be aborted
   * and minInitSimTime will be chosen.
   */
  private int maxSimTimeTrials = DEFAULT_MAX_SIM_TIME_TRIALS;

  /**
   * Relative tolerance. If average runtime does not deviate more than by this
   * factor from the desired wall-clock time, calibration stops and it will be
   * accepted.
   */
  private double tolerance = DEFAULT_RELATIVE_TOLERANCE;

  /**
   * The flag to determine if the portfolio has been configured manually. It is
   * not checked anymore in this case.
   */
  private boolean portfolioFixed = false;

  /** The simulation space explorer to be used. */
  private ISimSpaceExplorer explorer;

  /** Set of configurations in portfolio. */
  private List<ParameterBlock> portfolio = new ArrayList<>();

  /** Name of the most important parameter regarding performance difference. */
  private String importantParameter = ProcessorFactory.class.getName();

  /** Runtime result storage. */
  private transient Map<ParameterBlock, Double> runtimeResults =
      new HashMap<>();

  /** Index of the next portfolio element to be chosen. */
  private transient int currentPortfolioElement = 0;

  /** Current number of simulation time changes. */
  private transient int currentSimTimeTrials = 0;

  /** Currently used simulation end time. */
  private transient double currentEndTime = minInitSimTime;

  /** Currently best simulation end time. */
  private transient double currentBestEndTime = minInitSimTime;

  /** The average wall-clock time of the currently best end time. */
  private transient double avgWCBestEndTime = Double.MAX_VALUE;

  /** Flag to signal the end of the calibration. */
  private transient boolean done = false;

  /**
   * The flag that determines whether to reset the search when maximal
   * wall-clock time has been exceeded once.
   */
  private boolean resetOnMaxWCTTimeViolation = false;

  /**
   * For bean compliance. Do not use manually.
   */
  public SimpleSimStopTimeCalibrator() {
  }

  /**
   * Default constructor.
   * 
   * @param simSpExExplorer
   *          the {@link ISimSpaceExplorer} that is in charge
   */
  public SimpleSimStopTimeCalibrator(ISimSpaceExplorer simSpExExplorer) {
    explorer = simSpExExplorer;
    checkPortfolio();
  }

  @Override
  public boolean done() {
    return done || isAborted();
  }

  /**
   * Checks if is aborted.
   * 
   * @return true, if is aborted
   */
  protected boolean isAborted() {
    return currentSimTimeTrials >= maxSimTimeTrials;
  }

  @Override
  public double getCalibratedEndTime() {
    return currentBestEndTime;
  }

  @Override
  public Pair<Pair<Double, Long>, ParameterBlock> getNextSimTime() {
    checkPortfolio();
    return new Pair<>(new Pair<>(
        currentEndTime, (long) (maxWCTime * 1000)),
        portfolio.get(currentPortfolioElement));
  }

  @Override
  public void simFinished(double duration) {

    // In case an error occurred
    if (duration < 0) {
      done = true;
      currentBestEndTime = minInitSimTime;
      return;
    }

    // If a duration is longer than allowed, adjust end time and re-start
    if (resetOnMaxWCTTimeViolation && duration > maxWCTime) {
      setCurrentEndTime(getCurrentEndTime() * maxWCTime
          / ((1 + tolerance) * duration));
      if (currentEndTime == minSimEndTime) {
        done = true;
      }
      return;
    }

    // Normal outcome: just save the result choose the next configuration
    runtimeResults.put(portfolio.get(currentPortfolioElement), duration);
    currentPortfolioElement++;

    // If we have not yet reached the final element in the portfolio, proceed
    if (currentPortfolioElement < portfolioSize) {
      return;
    }

    calculateNewStopTime();
  }

  /**
   * Calculates a new stop time.
   */
  private void calculateNewStopTime() {
    // Evaluate results, check whether calibration is done
    double avgRunTime = 0;
    for (Double runTime : runtimeResults.values()) {
      avgRunTime += runTime;
    }
    avgRunTime /= portfolioSize;

    // Test whether this end time is more suitable than the currently best one
    if (Math.abs(avgWCBestEndTime - desiredWCTime) > Math.abs(avgRunTime
        - desiredWCTime)) {
      currentBestEndTime = currentEndTime;
      avgWCBestEndTime = avgRunTime;
    }

    // If even the minimum time is too large or even maximum time is too little,
    // we stop calibration
    boolean trivialCase =
        (avgRunTime > desiredWCTime
            && Double.compare(currentEndTime, minSimEndTime) == 0 || avgRunTime < desiredWCTime
            && Double.compare(currentEndTime, maxSimEndTime) == 0);
    // Check whether calibration is finished by finding a suitable t_end
    boolean suitableEndTimeFound =
        desiredWCTime * (1 - tolerance) <= avgRunTime
            && avgRunTime <= desiredWCTime * (1 + tolerance);

    if (trivialCase || suitableEndTimeFound) {
      done = true;
    }

    setCurrentEndTime(getCurrentEndTime() * desiredWCTime / avgRunTime);
  }

  @Override
  public void setNewModelSetup(Map<String, Serializable> modelSetup) {
    // Reset simulation time change counter
    currentEndTime = minInitSimTime;
    currentBestEndTime = minInitSimTime;
    avgWCBestEndTime = Double.MAX_VALUE;
    currentSimTimeTrials = 0;
    done = false;
    // Do nothing else, this algorithm is memoryless
  }

  /**
   * Checks portfolio, re-creates it if its size is not correct anymore.
   */
  private void checkPortfolio() {
    if (portfolioSize == portfolio.size() || portfolioFixed) {
      return;
    }

    portfolio.clear();
    runtimeResults.clear();
    currentPortfolioElement = 0;

    List<ParameterBlock> combinations = explorer.getConfigurations();

    if (combinations.isEmpty() || combinations.size() <= portfolioSize) {
      portfolioSize = combinations.size();
      portfolio.addAll(combinations);
      return;
    }

    // Look for combinations having different important parameters
    Set<String> paramVals = new HashSet<>();
    for (ParameterBlock combination : combinations) {
      if (combination.getSubBlockValue(importantParameter) == null) {
        continue;
      }
      String paramVal =
          combination.getSubBlockValue(importantParameter).toString();
      if (!paramVals.contains(paramVal)) {
        portfolio.add(combination);
        paramVals.add(paramVal);
        if (portfolio.size() == portfolioSize) {
          break;
        }
      }
    }

    // Resort to random sampling for the rest of the portfolio
    IRandom random = SimSystem.getRNGGenerator().getNextRNG();
    portfolio.addAll(RandomSampler.sampleSet(portfolioSize - portfolio.size(),
        0, combinations.size(), combinations, random));
  }

  /**
   * Gets the current end time.
   * 
   * @return the current end time
   */
  public double getCurrentEndTime() {
    return currentEndTime;
  }

  /**
   * Sets the current end time. Ensures that it is in the interval
   * [minSimendTime, maxSimEndTime].
   * 
   * @param currEndTime
   *          the new current end time
   */
  public void setCurrentEndTime(double currEndTime) {
    currentEndTime =
        Math.max(minSimEndTime, Math.min(maxSimEndTime, currEndTime));
    currentSimTimeTrials++;
    currentPortfolioElement = 0;
  }

  /**
   * Gets the explorer.
   * 
   * @return the explorer
   */
  public ISimSpaceExplorer getExplorer() {
    return explorer;
  }

  /**
   * Sets the explorer.
   * 
   * @param explorer
   *          the new explorer
   */
  public void setExplorer(ISimSpaceExplorer explorer) {
    this.explorer = explorer;
  }

  /**
   * Gets the portfolio size.
   * 
   * @return the portfolio size
   */
  public int getPortfolioSize() {
    return portfolioSize;
  }

  /**
   * Sets the portfolio size.
   * 
   * @param portfolioSize
   *          the new portfolio size
   */
  public void setPortfolioSize(int portfolioSize) {
    this.portfolioSize = portfolioSize;
    checkPortfolio();
  }

  /**
   * Gets the min init sim time.
   * 
   * @return the min init sim time
   */
  public double getMinInitSimTime() {
    return minInitSimTime;
  }

  /**
   * Sets the min init sim time.
   * 
   * @param minInitSimTime
   *          the new min init sim time
   */
  public void setMinInitSimTime(double minInitSimTime) {
    this.minInitSimTime = minInitSimTime;
    minSimEndTime = minInitSimTime;
    currentEndTime = minInitSimTime;
  }

  /**
   * Gets the desired wc time.
   * 
   * @return the desired wc time
   */
  public double getDesiredWCTime() {
    return desiredWCTime;
  }

  /**
   * Sets the desired wc time.
   * 
   * @param desiredWCTime
   *          the new desired wc time
   */
  public void setDesiredWCTime(double desiredWCTime) {
    this.desiredWCTime = desiredWCTime;
  }

  /**
   * Gets the max wc time.
   * 
   * @return the max wc time
   */
  public double getMaxWCTime() {
    return maxWCTime;
  }

  /**
   * Sets the max wc time.
   * 
   * @param maxWCTime
   *          the new max wc time
   */
  public void setMaxWCTime(double maxWCTime) {
    this.maxWCTime = maxWCTime;
  }

  /**
   * Gets the portfolio.
   * 
   * @return the portfolio
   */
  public List<ParameterBlock> getPortfolio() {
    return portfolio;
  }

  /**
   * Sets the portfolio.
   * 
   * @param portfolio
   *          the new portfolio
   */
  public void setPortfolio(List<ParameterBlock> portfolio) {
    this.portfolio = portfolio;
    this.portfolioSize = this.portfolio.size();
  }

  /**
   * Gets the max sim time trials.
   * 
   * @return the max sim time trials
   */
  public int getMaxSimTimeTrials() {
    return maxSimTimeTrials;
  }

  /**
   * Sets the max sim time trials.
   * 
   * @param maxSimTimeTrials
   *          the new max sim time trials
   */
  public void setMaxSimTimeTrials(int maxSimTimeTrials) {
    this.maxSimTimeTrials = maxSimTimeTrials;
  }

  /**
   * Gets the max sim end time.
   * 
   * @return the max sim end time
   */
  public double getMaxSimEndTime() {
    return maxSimEndTime;
  }

  /**
   * Sets the max sim end time.
   * 
   * @param maxSimEndTime
   *          the new max sim end time
   */
  public void setMaxSimEndTime(double maxSimEndTime) {
    this.maxSimEndTime = maxSimEndTime;
  }

  /**
   * Checks if is reset on max wct time violation.
   * 
   * @return true, if is reset on max wct time violation
   */
  public boolean isResetOnMaxWCTTimeViolation() {
    return resetOnMaxWCTTimeViolation;
  }

  /**
   * Sets the reset on max wct time violation.
   * 
   * @param resetOnMaxWCTTimeViolation
   *          the new reset on max wct time violation
   */
  public void setResetOnMaxWCTTimeViolation(boolean resetOnMaxWCTTimeViolation) {
    this.resetOnMaxWCTTimeViolation = resetOnMaxWCTTimeViolation;
  }

  /**
   * Checks if portfolio is fixed.
   * 
   * @return the portfolioFixed
   */
  public boolean isPortfolioFixed() {
    return portfolioFixed;
  }

  /**
   * Sets the portfolio to be fixed.
   * 
   * @param portfolioFixed
   *          the portfolioFixed to set
   */
  public void setPortfolioFixed(boolean portfolioFixed) {
    this.portfolioFixed = portfolioFixed;
  }

}
