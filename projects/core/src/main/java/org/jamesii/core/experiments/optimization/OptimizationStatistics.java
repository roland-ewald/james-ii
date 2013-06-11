/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jamesii.core.util.collection.list.SortedList;
import org.jamesii.core.util.sorting.SortOrder;

/**
 * Base class describing the status of the optimisation progress.
 * 
 * @author Arvid Schwecke
 */
public class OptimizationStatistics implements Serializable {

  /** Number of best configurations to be saved. */
  private static final int MAX_BEST_CONFIGS = 30;

  /** Serialisation ID. */
  private static final long serialVersionUID = -5587286676157744572L;

  /** List of best configurations. */
  private SortedList<ConfigurationInfos> bestConfigs = new SortedList<>(
      MAX_BEST_CONFIGS, SortOrder.DESCENDING);

  /** Number of configurations found in storage. */
  private int foundInStorage;

  /**
   * Result of the simulations for one configuration, including the time needed
   * for simulation. See {@link org.jamesii.core.data.experiment.ExperimentInfo}
   * .
   */
  private ConfigurationInfos lastResults = null;

  /** Number of all configurations done so far. */
  private int totalConfigurationRuns = 0;

  /** Time of all simulation runs done so far. */
  private double totalOptimizationTime = 0;

  /** Number of all simulation runs done so far. This is includes replications. */
  private int totalSimulationRuns = 0;

  /** Number of constraints that have been violated. */
  private int violatedPostConstraints;

  /** Number of pre-constraints that have been violated. */
  private int violatedPreConstraints;

  /** The evaluated solutions. */
  private List<ConfigurationInfos> evaluatedSolutions = new ArrayList<>();

  /**
   * Add new configuration to statistics.
   * 
   * @param lastConfigurationResults
   *          the last configuration results
   * @param representativeValue
   *          the representative value
   */
  public void configurationDone(ConfigurationInfos lastConfigurationResults,
      Map<String, Double> representativeValue) {

    lastResults = lastConfigurationResults;

    if (lastConfigurationResults == null) {
      return;
    }

    if (lastConfigurationResults.isStorageUse()) {
      foundInStorage++;
    } else {
      totalConfigurationRuns++;
      totalSimulationRuns += lastResults.getRunCount();
      totalOptimizationTime += lastResults.getRunTime();
    }
    lastConfigurationResults.setRepresentativeObjective(representativeValue);
    bestConfigs.add(lastConfigurationResults);
    evaluatedSolutions.add(lastConfigurationResults);
  }

  /**
   * Gets the last configuration results.
   * 
   * @return the lastSimulationResult
   */
  public ConfigurationInfos getLastConfigurationResults() {
    return lastResults;
  }

  /**
   * Gets the number of storage hits.
   * 
   * @return the foundInStorage
   */
  public int getNumOfStorageHits() {
    return foundInStorage;
  }

  /**
   * Gets the number of violated constraints.
   * 
   * @return the hitConstraints
   */
  public int getNumOfViolatedConstraints() {
    return violatedPostConstraints + violatedPreConstraints;
  }

  /**
   * Gets the simulation runs of current configuration.
   * 
   * @return the simulationRunsOfCurrentConfiguration
   */
  public int getSimulationRunsOfCurrentConfiguration() {
    if (lastResults != null) {
      return lastResults.getRunCount();
    }
    return 0;
  }

  /**
   * Gets the simulation time spend for current configuration.
   * 
   * @return the simulationTimeSpendForCurrentConfiguration
   */
  public double getSimulationTimeSpendForCurrentConfiguration() {
    if (lastResults != null) {
      return lastResults.getRunTime();
    }
    return 0;
  }

  /**
   * Gets the total configuration runs.
   * 
   * @return the totalConfigurationRuns
   */
  public int getTotalConfigurationRuns() {
    return totalConfigurationRuns;
  }

  /**
   * Gets the total optimisation time.
   * 
   * @return the totalOptimizationTime
   */
  @Deprecated
  public double getTotalOptimizationTime() {
    return totalOptimizationTime;
  }

  /**
   * Gets the total simulation runs.
   * 
   * @return the totalSimulationRuns
   */
  public int getTotalSimulationRuns() {
    return totalSimulationRuns;
  }

  /**
   * Needs to be called when post-constraints are violated.
   */
  public void postConstraintsViolated() {
    this.violatedPostConstraints++;

  }

  /**
   * Needs to be called when pre-constraints are violated.
   */
  public void preConstraintsViolated() {
    this.violatedPreConstraints++;
  }

  @Override
  public String toString() {
    String result;
    result = "simulations: " + getTotalSimulationRuns() + "\n";
    result =
        result + "total configurations: " + getTotalConfigurationRuns() + "\n";
    result =
        result + "configurations hit constraints: "
            + getNumOfViolatedConstraints() + "\n";
    result =
        result + "configurations found in storage: " + getNumOfStorageHits()
            + "\n";
    result =
        result + "total simulation time: " + getTotalOptimizationTime() + "\n";
    result = result + "********* best found solutions ***************" + "\n";

    for (int i = 0; i < bestConfigs.size(); i++) {
      result = result + bestConfigs.get(i) + "\n";
    }

    return result;
  }

  /**
   * Gets the best configurations.
   * 
   * @return the best configurations
   */
  public SortedList<ConfigurationInfos> getBestConfigs() {
    return bestConfigs;
  }

  /**
   * @return the evaluatedSolutions
   */
  public List<ConfigurationInfos> getEvaluatedSolutions() {
    return evaluatedSolutions;
  }

}
