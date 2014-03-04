/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.adaptiverunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jamesii.core.experiments.TaskConfiguration;
import org.jamesii.core.observe.IObserver;
import org.jamesii.core.observe.Mediator;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.simspex.adaptiverunner.policies.plugintype.IMinBanditPolicy;

/**
 * This is a simple structure to maintain replication information for a given
 * {@link TaskConfiguration}.
 * 
 * @author Roland Ewald
 */
public class ReplicationInfo {

  /** Minimal number of replications that is still required. */
  private int minRemainingReps;

  /** Number of already executed replications. */
  private int executedReps = 0;

  /** List of eligible configuration options for the simulation system. */
  private final ParameterBlock[] options;

  /**
   * This history is just for debugging purposes, could be removed at some
   * point. Performance history of configurations. The performance of the
   * configuration corresponding to the i-th {@link ParameterBlock} in
   * {@link ReplicationInfo#options} will be stored at the i-th position.
   */
  private final List<List<Double>> history;

  /**
   * The simulation configuration for which the replication information is held.
   */
  private final TaskConfiguration taskConfig;

  /**
   * The policy for selecting eligible configurations for this
   * {@link TaskConfiguration}.
   */
  private final IMinBanditPolicy policy;

  /**
   * The original execution parameters, as given by the user. Will be merged
   * with the options.
   */
  private final ParameterBlock originalExecParameters;

  /**
   * Default constructor.
   * 
   * @param taskConf
   *          the task configuration for which this information is stored
   * @param configOptions
   *          list of eligible configuration options for the given simulation
   *          configuration.
   * @param minReps
   *          the minimal number of required replications
   * @param policyObservers
   *          list of observers for the policy
   * @param selectionPolicy
   *          the selection policy
   */
  public ReplicationInfo(TaskConfiguration taskConf,
      List<ParameterBlock> configOptions, int minReps,
      IMinBanditPolicy selectionPolicy,
      List<IObserver<? extends IMinBanditPolicy>> policyObservers) {
    taskConfig = taskConf;
    policy = selectionPolicy;
    options = configOptions.toArray(new ParameterBlock[configOptions.size()]);
    history = new ArrayList<>();
    minRemainingReps = minReps;
    for (int i = 0; i < options.length; i++) {
      history.add(new ArrayList<Double>());
    }

    // Add observers to newly create policy
    Mediator.create(policy);
    for (IObserver<? extends IMinBanditPolicy> observer : policyObservers) {
      policy.registerObserver(observer);
    }

    // Initialise policy in a pessimistic manner
    policy.init(options.length, minRemainingReps);

    // Keep copy of original execution parameters
    originalExecParameters = taskConfig.getExecParams().getCopy();
  }

  /**
   * Registers the performance of a single run.
   * 
   * @param option
   *          the option
   * @param performance
   *          the performance
   */
  public void registerPerformance(int option, double performance) {
    executedReps++;
    history.get(option).add(performance);
    policy.receiveReward(option, performance);
  }

  /**
   * Selects the next configuration to be used. Returns a tuple consisting of
   * the configuration's index and the configuration itself.
   * 
   * @return a pair (i,c) with i being the index of the configuration and c
   *         being the configuration itself
   */
  public Pair<Integer, ParameterBlock> getNextOption() {
    int choice = policy.nextChoice();
    return new Pair<>(choice, options[choice]);
  }

  /**
   * Quarantines a given option for this policy.
   * 
   * @param optionIndex
   *          the index of the option in {@link ReplicationInfo#options}
   */
  public void quarantine(int optionIndex) {
    policy.quarantine(optionIndex);
  }

  /**
   * Gets the min remaining reps.
   * 
   * @return the min remaining reps
   */
  public int getMinRemainingReps() {
    return minRemainingReps;
  }

  /**
   * Sets the min remaining reps.
   * 
   * @param minRemainingReps
   *          the new min remaining reps
   */
  public void setMinRemainingReps(int minRemainingReps) {
    this.minRemainingReps = minRemainingReps;
  }

  /**
   * Gets the options.
   * 
   * @return the options
   */
  public ParameterBlock[] getOptions() {
    return Arrays.copyOf(options, options.length);
  }

  /**
   * Gets the history.
   * 
   * @return the history
   */
  public List<List<Double>> getHistory() {
    return history;
  }

  /**
   * Gets the task configuration.
   * 
   * @return the task configuration
   */
  public TaskConfiguration getTaskConfig() {
    return taskConfig;
  }

  /**
   * Gets the executed reps.
   * 
   * @return the executed reps
   */
  public int getExecutedReps() {
    return executedReps;
  }

  /**
   * Gets the original exec parameters.
   * 
   * @return the original exec parameters
   */
  public ParameterBlock getOriginalExecParameters() {
    return originalExecParameters;
  }

}
