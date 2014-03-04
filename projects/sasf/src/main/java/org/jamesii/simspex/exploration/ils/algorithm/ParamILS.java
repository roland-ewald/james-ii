/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.exploration.ils.algorithm;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jamesii.SimSystem;
import org.jamesii.core.base.Entity;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.perfdb.util.ParameterBlocks;
import org.jamesii.simspex.exploration.ils.explorer.IILSSimSpaceExplorerInformationSource;
import org.jamesii.simspex.exploration.ils.observation.ParamILSMessage;
import org.jamesii.simspex.exploration.ils.observation.ParamILSMessageType;
import org.jamesii.simspex.exploration.ils.termination.ITerminationIndicator;
import org.jamesii.simspex.exploration.ils.termination.ParamILSTerminationSimpleCounter;

/**
 * Parameterized Iterated Local Search (ILS) algorithm to solve the 'Algorithm
 * Configuration Problem'.
 * 
 * <p/>
 * From "ParamILS: An automatic algorithm configuration framework" by: F.
 * Hutter, H. Hoos, K. Leyton-Brown, T. Stützle Journal of Artificial
 * Intelligence Research, Vol. 36 (2009), pp. 267-306. <a
 * href="http://dx.doi.org/10.1613/jair.2861">doi:10.1613/jair.2861</a>
 * <p/>
 * 
 * Concrete implementations such as {@link BasicILS} and {@link FocusedILS} only
 * differ in the function <code>better</code>; this class provides the
 * algorithm. Due to this only the better function and the calculation of the
 * costs have to be implemented manually.
 * 
 * @author Robert Engelke
 * 
 */
public abstract class ParamILS extends Entity {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -2338957511067498828L;

  /** The maximum execution time per run (in seconds). */
  protected static final double DEFAULT_MAX_SECONDS_EXEC_TIME = 60;

  /** The default value for {@link #restartProbability}. */
  private static final double DEFAULT_RESTART_PROBABILITY = 0.05;

  /** The default value for {@link #boundMultiplier}. */
  private static final double DEFAULT_BOUND_MULTIPLIER = 2.0;

  /** The default value for {@link #changesPerRun}. */
  private static final int DEFAULT_CHANGES = 3;

  /** The default value for {@link #minResourceCap}. */
  private static final double DEFAULT_MIN_RESOURCE_CAP = 10.;

  /** The Constant MAX_POSSIBLE_OBJECTIVE. */
  private static final double MAX_POSSIBLE_OBJECTIVE = 100000.;

  /** The information source. */
  private IILSSimSpaceExplorerInformationSource informationSource;

  /** Defines which capping strategy is being used. */
  private boolean aggressiveCapping = false;

  /** The maximal execution time per simulation run. */
  private double maxExecutionTime = DEFAULT_MAX_SECONDS_EXEC_TIME;

  /** The configuration with best result. */
  private ParameterBlock minimumConfiguration;

  /**
   * Multiplier used for calculating the bound and the resourceCap for the runs
   * of the ParameterConfigruation when using capping strategies.
   */
  private double boundMultiplier = DEFAULT_BOUND_MULTIPLIER;

  /** Maximum time used for one execution. */
  private double minResourceCap = DEFAULT_MIN_RESOURCE_CAP;

  /** The random number generator. */
  private final IRandom rng;

  /**
   * Indicates the restart probability. This parameter is called 'p_{restart}'
   * in the Hutter et al. paper.
   */
  private double restartProbability = DEFAULT_RESTART_PROBABILITY;

  /**
   * Indicates the number of parameter changes in every run. This parameter is
   * called 's' in the Hutter et al. paper.
   */
  private int changesPerRun = DEFAULT_CHANGES;

  /** Indicates whether to stop the run. */
  private Boolean stopRun = false;

  /** The termination indicator. */
  private final ITerminationIndicator<ParamILS> terminationIndicator;

  /**
   * Creates instance with default termination indicator.
   */
  public ParamILS() {
    this(new ParamILSTerminationSimpleCounter());
  }

  /**
   * Instantiates a new param ILS with the given termination indicator.
   * 
   * @param termIndicator
   *          the termination indicator
   */
  public ParamILS(ITerminationIndicator<ParamILS> termIndicator) {
    rng = SimSystem.getRNGGenerator().getNextRNG();
    terminationIndicator = termIndicator;
  }

  /**
   * Tries to calculate the best configuration by iteratively improve the values
   * of the TaskConfiguration.
   * 
   * <p/>
   * See algorithm 1 in Hutter et al. paper
   */
  public void run() {

    ParameterBlock thetaILS = initializeSearch();

    // Main search loop
    while (!shallTerminate()) {
      terminationIndicator.updateTerminationCriterion(this);

      // "Perturbation"
      ParameterBlock theta = thetaILS;
      for (int i = 0; i < changesPerRun; i++) {
        theta = getInformationSource().getRandomNeighbour(theta);
      }

      changed(new ParamILSMessage(ParamILSMessageType.ParameterAdjusting,
          theta, 0.));

      // "Basic Local Search"
      theta = iterativeFirstImprovement(theta);

      // Acceptance / random restarts
      if (better(theta, thetaILS)) {
        thetaILS = theta;
      }
      if (rng.nextDouble() <= restartProbability) {
        thetaILS = getInformationSource().restart();
        changed(new ParamILSMessage(ParamILSMessageType.Restart, thetaILS, 0.));
      }
    }
  }

  /**
   * Initializes the search. See first part of algorithm 1 in Hutter et al.
   * paper.
   * 
   * @return the theta_{ils}, i.e. the configuration with which to start in the
   *         main loop
   */
  protected ParameterBlock initializeSearch() {
    ParameterBlock theta0 = getInformationSource().getInitialConfiguration();
    // initial configuration is the best one found before search started
    setMinimumConfiguration(theta0);

    // try to find a good starting Configuration by iteratively changing a
    // parameter of the default configuration to a random value
    for (int i = 0; i < getInformationSource().getNumberOfParameters(theta0); i++) {
      ParameterBlock theta =
          getInformationSource().getRandomNeighbour(theta0, i);
      if (better(theta, theta0)) {
        theta0 = theta;
      }
    }
    return iterativeFirstImprovement(theta0);
  }

  /**
   * This method is used to iterate over the neighborhood until a local optimum
   * is reached.
   * 
   * @param configuration
   *          beginning of the iteration
   * @return a local optimum
   */
  private ParameterBlock iterativeFirstImprovement(ParameterBlock configuration) {
    ParameterBlock neighbourConfiguration;
    ParameterBlock config = configuration;
    // neighbours already calculated will be ignored to prevent circles
    Set<String> visitedNeighbours = new HashSet<>();
    do {
      // update the currently best configuration
      neighbourConfiguration = config;
      if (!visitedNeighbours.contains(ParameterBlocks
          .toUniqueString(neighbourConfiguration))) {
        visitedNeighbours.add(ParameterBlocks
            .toUniqueString(neighbourConfiguration));
        List<ParameterBlock> neighbourhood =
            getInformationSource().getNeighbourhood(neighbourConfiguration);
        // iterating over the neighborhood of the actual best
        // configuration
        for (ParameterBlock nextNeighbour : neighbourhood) {
          if (better(nextNeighbour, config)) {
            config = nextNeighbour;
            changed(new ParamILSMessage(
                ParamILSMessageType.IterativeImprovement, config,
                getConsistentEstimator(config, getInformationSource()
                    .getNumberOfRuns(config)), getConsistentEstimator(config,
                    getInformationSource().getNumberOfRuns(config))));
          }
        }
      }
    } while (config != neighbourConfiguration);
    return config;
  }

  /**
   * Indicates whether the termination criteria was already reached.
   * 
   * @return true, if successful
   */
  public synchronized boolean shallTerminate() {
    return terminationIndicator.shallTerminate(this) || stopRun;
  }

  /**
   * Checks whether first configuration is better than second configuration.
   * 
   * @param config1
   *          the config1
   * @param config2
   *          the config2
   * @return points of config1 <= points of config2
   */
  public abstract boolean better(ParameterBlock config1, ParameterBlock config2);

  /**
   * Objective to be calculated.
   * 
   * @param configuration
   *          the configuration which is tested
   * @param numRuns
   *          the number of runs being processed
   * @param initialBound
   *          the initial bound
   * @return the consistent estimator for n runs
   */
  public double objective(ParameterBlock configuration, int numRuns,
      double initialBound) {
    maintainInvariant(configuration, numRuns);
    double bound = calculateBound(initialBound, numRuns);
    return calculateConsistentEstimator(configuration, numRuns, bound);
  }

  /**
   * Maintain the minimum configuration. If necessary it adds runs to the
   * minimum configuration.
   * 
   * @param configuration
   *          the configuration
   * @param numRuns
   *          the number of runs which should have been calculated already
   */
  private void maintainInvariant(ParameterBlock configuration, int numRuns) {
    if (!ParameterBlocks.equal(configuration, minimumConfiguration)
        && getInformationSource().getNumberOfRuns(minimumConfiguration) < numRuns) {
      objective(minimumConfiguration, numRuns, getMaxExecutionTime());
      changed(new ParamILSMessage(ParamILSMessageType.MinimumMaintained,
          minimumConfiguration, getConsistentEstimator(minimumConfiguration,
              numRuns)));
    }
  }

  /**
   * Calculate consistent estimator for given configuration and first n runs.
   * 
   * @param configuration
   *          the configuration
   * @param n
   *          the number of runs needed to be calculated
   * @param bound
   *          the bound which should not be exceeded in average
   * @return the consistent estimator for run n
   */
  private double calculateConsistentEstimator(ParameterBlock configuration,
      int n, double bound) {
    double sumOfCosts = 0;
    for (int i = 0; i < n; i++) {
      double cost =
          calculateCosts(configuration, i,
              calculateResourceCap(n, bound, sumOfCosts));
      sumOfCosts += cost;
      if (cost >= getMaxExecutionTime() || sumOfCosts > bound * n) {
        changed(new ParamILSMessage(ParamILSMessageType.PerformanceEstimation,
            configuration, MAX_POSSIBLE_OBJECTIVE));
        return MAX_POSSIBLE_OBJECTIVE + (n + 1 - i);
      }
    }
    changed(new ParamILSMessage(ParamILSMessageType.PerformanceEstimation,
        configuration, sumOfCosts / n));
    // checking whether configuration is better then minimum
    if (checkMinimumConfiguration(sumOfCosts / n, n)) {
      setMinimumConfiguration(configuration);
    }
    return sumOfCosts / n;
  }

  /**
   * Calculates a new bound.
   * 
   * @param bound
   *          the actual bound
   * @param n
   *          the n-th run
   * @return the new bound
   */
  private double calculateBound(double bound, int n) {
    if (!isAggressiveCapping()) {
      return bound;
    }
    return Math.min(bound,
        getBoundMultiplier() * getConsistentEstimator(minimumConfiguration, n));
  }

  /**
   * Checks whether this configuration is better then the overall best
   * configuration.
   * 
   * @param configurationConsistenceEstimator
   *          the consistence estimator proofed of being better than the one of
   *          the minimum
   * @param n
   *          the n
   * @return true, if configuration better than actual minimum
   */
  private boolean checkMinimumConfiguration(
      double configurationConsistenceEstimator, int n) {
    return (n == getInformationSource().getNumberOfRuns(minimumConfiguration) && configurationConsistenceEstimator < getConsistentEstimator(
        minimumConfiguration, n));
  }

  /**
   * Calculate costs. If the configuration already ran the i-th training
   * instance it will proof whether the conditions (resourceCap) of the run
   * changed. If the changes are in a way not changing the result of the data
   * already calculated this data will be used.
   * 
   * @param configuration
   *          the configuration which is tested
   * @param i
   *          the indicator which training instance is used
   * @param resourceCap
   *          the time after which the algorithm will stop calculating the
   *          result
   * @return the costs of the configuration
   */
  private double calculateCosts(ParameterBlock configuration, int i,
      double resourceCap) {
    // a run is not successful when the runtime is greater or equal
    // to the resourceCap
    if (getInformationSource().getNumberOfRuns(configuration) > i
        && !hasResourceCapEffect(configuration, i, resourceCap)) {
      return getInformationSource().getCost(configuration, i);
    }

    return getInformationSource().calculateCost(configuration, i, resourceCap);
  }

  /**
   * Checks if the new resource cap may lead to other results. That is the case
   * when having a greater resource cap than in the nonsucceeded runs before, or
   * when having a smaller resource cap in the succeeded runs before.
   * 
   * @param configuration
   *          the configuration
   * @param i
   *          the i-th run
   * @param resourceCap
   *          the new cap time
   * @return true, if capping time may effect result
   */
  private boolean hasResourceCapEffect(ParameterBlock configuration, int i,
      double resourceCap) {
    return (getInformationSource().getResourceCap(configuration, i) < resourceCap && getInformationSource()
        .getCost(configuration, i) >= getInformationSource().getResourceCap(
        configuration, i))
        || (getInformationSource().getResourceCap(configuration, i) > resourceCap && getInformationSource()
            .getCost(configuration, i) < getInformationSource().getResourceCap(
            configuration, i));
  }

  /**
   * Calculates the consistent estimator of the runNumber for a given
   * configuration.
   * 
   * @param configuration
   *          the configuration
   * @param runNumber
   *          the number of the run
   * @return the consistent estimator
   */
  public double getConsistentEstimator(ParameterBlock configuration,
      int runNumber) {
    if (runNumber <= 0) {
      throw new IllegalArgumentException();
    }
    if (getInformationSource().getNumberOfRuns(configuration) < runNumber) {
      return getMaxExecutionTime();
    }
    return getInformationSource().getSumOfCosts(configuration, runNumber)
        / runNumber;
  }

  /**
   * Calculate resource cap.
   * 
   * @param n
   *          the n
   * @param bound
   *          the bound
   * @param sumOfCosts
   *          the sum of costs
   * @return the double
   */
  public double calculateResourceCap(int n, double bound, double sumOfCosts) {
    return Math.max(getMinResourceCap(), n * bound - sumOfCosts);
  }

  /**
   * Gets the minimum configuration.
   * 
   * @return the minimum configuration
   */
  public ParameterBlock getMinimumConfiguration() {
    return minimumConfiguration;
  }

  /**
   * Sets the minimum configuration.
   * 
   * @param minimumConfiguration
   *          the new minimum configuration
   */
  private void setMinimumConfiguration(ParameterBlock minimumConfiguration) {
    if (!shallTerminate()
        && getInformationSource().getNumberOfRuns(minimumConfiguration) != 0) {
      changed(new ParamILSMessage(
          ParamILSMessageType.MinimumChanged,
          minimumConfiguration,
          getConsistentEstimator(minimumConfiguration, getInformationSource()
              .getNumberOfRuns(minimumConfiguration)),
          getConsistentEstimator(this.minimumConfiguration,
              getInformationSource().getNumberOfRuns(this.minimumConfiguration))));
    }
    this.minimumConfiguration = minimumConfiguration;
  }

  /**
   * Gets the random number generator.
   * 
   * @return the random number generator
   */
  public IRandom getR() {
    return rng;
  }

  /**
   * Sets whether aggressive capping is used. Aggressive Capping is an option to
   * further improve the resource cap a parameter get within runtime.
   * 
   * @param capping
   *          the new aggressive capping
   */
  public void setAggressiveCapping(boolean capping) {
    this.aggressiveCapping = capping;
  }

  /**
   * Checks if the aggressive capping strategy is used.
   * 
   * @return true, if is aggressive capping
   */
  public boolean isAggressiveCapping() {
    return aggressiveCapping;
  }

  /**
   * Sets the bound multiplier. It is used to calculate the resource bound for
   * the tests of the configurations based on the overall minimum configuration
   * found.
   * 
   * @param boundMultiplier
   *          the new bound multiplier
   */
  public void setBoundMultiplier(double boundMultiplier) {
    this.boundMultiplier = boundMultiplier;
  }

  /**
   * Gets the bound multiplier.
   * 
   * @return the bound multiplier
   */
  public double getBoundMultiplier() {
    return boundMultiplier;
  }

  /**
   * Gets the minimum cap time.
   * 
   * @return the minimum cap time
   */
  public double getMinResourceCap() {
    return minResourceCap;
  }

  /**
   * Sets the minimum resource cap. This is the minimum resource a configuration
   * gets for a test case.
   * 
   * @param minResourceCap
   *          the new minimum resource cap
   */
  public void setMinResourceCap(double minResourceCap) {
    this.minResourceCap = minResourceCap;
  }

  /**
   * Gets the information source.
   * 
   * @return the information source
   */
  public IILSSimSpaceExplorerInformationSource getInformationSource() {
    return informationSource;
  }

  /**
   * Sets the information source. It's the source of information about runtime
   * of configurations,... for the ILS algorithm.
   * 
   * @param informationSource
   *          the new information source
   */
  public void setInformationSource(
      IILSSimSpaceExplorerInformationSource informationSource) {
    this.informationSource = informationSource;
  }

  /**
   * Gets the restart probability.
   * 
   * @return the restart probability
   */
  public double getRestart() {
    return restartProbability;
  }

  /**
   * Gets the changes per run.
   * 
   * @return the changes per run
   */
  public int getChangesPerRun() {
    return changesPerRun;
  }

  /**
   * Sets the restart probability.
   * 
   * @param restart
   *          the new restart probability
   */
  public void setRestart(double restart) {
    this.restartProbability = restart;
  }

  /**
   * Sets the number of changes per run.
   * 
   * @param changesPerRun
   *          the new number of changes per run
   */
  public void setChangesPerRun(int changesPerRun) {
    this.changesPerRun = changesPerRun;
  }

  /**
   * Gets the max possible objective.
   * 
   * @return the max possible objective
   */
  public static double getMaxPossibleObjective() {
    return MAX_POSSIBLE_OBJECTIVE;
  }

  /**
   * Indicates to stop the algorithm.
   */
  public synchronized void stop() {
    stopRun = true;
  }

  /**
   * Get the value of the maxExecutionTime.
   * 
   * @return the maxExecutionTime
   */
  public double getMaxExecutionTime() {
    return maxExecutionTime;
  }

  /**
   * Set the maxExecutionTime to the value passed via the maxExecutionTime
   * attribute.
   * 
   * @param maxExecutionTime
   *          the maxExecutionTime to set
   */
  public void setMaxExecutionTime(double maxExecutionTime) {
    this.maxExecutionTime = maxExecutionTime;
  }
}
