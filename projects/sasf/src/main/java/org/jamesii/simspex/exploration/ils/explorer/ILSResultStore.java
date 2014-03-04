/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.exploration.ils.explorer;

import static org.jamesii.perfdb.util.ParameterBlocks.toUniqueString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.perfdb.util.ParameterBlocks;

/**
 * Manages intermediate results required by some ParamILS implementations.
 * 
 * @author Roland Ewald
 * 
 */
public class ILSResultStore {

  /** The costs of all runs executed with a given configuration. */
  private final Map<String, List<Pair<Double, Integer>>> runCostPerConfig =
      new HashMap<>();

  /** The resource cap per configuration. */
  private final Map<String, List<Double>> resourceCapPerConfig =
      new HashMap<>();

  /** The sequence in which configurations have been explored. */
  private final List<ParameterBlock> configExplorationSequence =
      new ArrayList<>();

  /**
   * Register a single execution.
   * 
   * @param job
   *          the execution job
   * @param cost
   *          the cost of execution
   * @return the mean of this job
   */
  public Double registerExecution(ExecutionJob job, Double cost) {
    Double result = cost;
    ParameterBlock configuration = job.getConfiguration();
    int index = job.getModelSetupIndex();
    if (!isRegistered(configuration)) {
      register(configuration);
    }
    if (getNumberOfRuns(configuration) > index) {
      Pair<Double, Integer> runCost =
          lookUp(configuration, runCostPerConfig).get(index);
      result =
          calculateMean(result, runCost.getFirstValue(),
              runCost.getSecondValue());
      runCost.setFirstValue(result);
      runCost.setSecondValue(runCost.getSecondValue() + 1);
      lookUp(configuration, resourceCapPerConfig).set(index,
          job.getResourceCap());
    } else {
      lookUp(configuration, runCostPerConfig).add(
          new Pair<>(cost, 1));
      lookUp(configuration, resourceCapPerConfig).add(job.getResourceCap());
    }
    return result;
  }

  public <S> S lookUp(ParameterBlock configuarion, Map<String, S> dictionary) {
    return dictionary.get(ParameterBlocks.toUniqueString(configuarion));
  }

  private Double calculateMean(Double newCosts, Double meanCosts, Integer n) {
    return meanCosts + (newCosts - meanCosts) / n;
  }

  /**
   * Gets the number of runs executed with a configuration.
   * 
   * @param configuration
   *          the configuration
   * @return the number of runs
   */
  public int getNumberOfRuns(ParameterBlock configuration) {
    if (!isRegistered(configuration)) {
      register(configuration);
    }
    return lookUp(configuration, runCostPerConfig).size();
  }

  /**
   * Gets the cost of applying the given configuration to the n-th problem/run.
   * 
   * @param configuration
   *          the configuration
   * @param runNumber
   *          the run number (n)
   * @return the cost of the run
   */
  public double getCostOfRun(ParameterBlock configuration, int runNumber) {
    checkRegistered(configuration, runNumber);
    return lookUp(configuration, runCostPerConfig).get(runNumber)
        .getFirstValue();
  }

  /**
   * Gets the cost sum of run 1 through n.
   * 
   * @param configuration
   *          the configuration
   * @param runNumber
   *          the run number until which the cost shall be summed (n)
   * @return the cost sum of runs
   */
  public double getCostSumOfRuns(ParameterBlock configuration, int runNumber) {
    checkRegistered(configuration, runNumber);
    double costSum = 0;
    List<Pair<Double, Integer>> costs = lookUp(configuration, runCostPerConfig);
    for (int i = 0; i < runNumber; i++) {
      costSum += costs.get(i).getFirstValue();
    }
    return costSum;
  }

  /**
   * Gets the last resource cap of the n-th run for the given configuration.
   * 
   * @param configuration
   *          the configuration
   * @param runNumber
   *          the run number (n)
   * @return the resource cap
   */
  public double getResourceCap(ParameterBlock configuration, int runNumber) {
    checkRegistered(configuration, runNumber);
    return lookUp(configuration, resourceCapPerConfig).get(runNumber);
  }

  /**
   * Checks if the given configuration has already been registered.
   * 
   * @param configuration
   *          the configuration
   * @return true, if is registered
   */
  private boolean isRegistered(ParameterBlock configuration) {
    return lookUp(configuration, runCostPerConfig) != null;
  }

  /**
   * Registers a configuration.
   * 
   * @param configuration
   *          the configuration to be registered
   */
  private void register(ParameterBlock configuration) {
    runCostPerConfig.put(toUniqueString(configuration),
        new ArrayList<Pair<Double, Integer>>());
    resourceCapPerConfig.put(toUniqueString(configuration),
        new ArrayList<Double>());
    configExplorationSequence.add(configuration);
  }

  /**
   * Checks whether a run with index n has been registered for a given
   * configuration. If not at least n+1 runs are available (counting starts with
   * run #0, as usual), an unchecked exception is thrown.
   * 
   * @param configuration
   *          the configuration
   * @param runNumber
   *          the run number (n)
   */
  private void checkRegistered(ParameterBlock configuration, int runNumber) {
    checkRegistered(configuration);
    int availableRuns = lookUp(configuration, runCostPerConfig).size();
    if (runNumber > availableRuns) {
      throw new IllegalArgumentException("Can't sum over " + runNumber
          + " runs, only " + availableRuns + " are available.");
    }
  }

  /**
   * Checks whether a given configuration is registered, throws unchecked
   * exception if not.
   * 
   * @param configuration
   *          the configuration
   */
  private void checkRegistered(ParameterBlock configuration) {
    if (!isRegistered(configuration)) {
      throw new IllegalArgumentException("Configuration '" + configuration
          + "' is not registered.");
    }
  }

  /**
   * Gets the configuration exploration sequence.
   * 
   * @return the configuration exploration sequence
   */
  public List<ParameterBlock> getConfigExplorationSequence() {
    return configExplorationSequence;
  }
}
