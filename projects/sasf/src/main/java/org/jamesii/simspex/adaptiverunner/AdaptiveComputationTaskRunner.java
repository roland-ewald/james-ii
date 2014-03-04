/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.adaptiverunner;

import java.net.URI;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.asf.registry.AlgoSelectionRegistry;
import org.jamesii.asf.registry.failuredetection.FailureDescription;
import org.jamesii.core.data.IURIHandling;
import org.jamesii.core.experiments.ComputationTaskRuntimeInformation;
import org.jamesii.core.experiments.RunInformation;
import org.jamesii.core.experiments.TaskConfiguration;
import org.jamesii.core.experiments.taskrunner.parallel.ComputationTaskExecutionJob;
import org.jamesii.core.experiments.taskrunner.parallel.ParallelComputationTaskRunner;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.observe.IObserver;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.perfdb.recording.selectiontrees.SelectionTreeSet;
import org.jamesii.simspex.adaptiverunner.policies.plugintype.AbstractMinBanditPolicyFactory;
import org.jamesii.simspex.adaptiverunner.policies.plugintype.IMinBanditPolicy;
import org.jamesii.simspex.adaptiverunner.policies.plugintype.MinBanditPolicyFactory;
import org.jamesii.simspex.util.SelTreeSetCreation;

/**
 * Variant of the {@link ParallelComputationTaskRunner} that uses a
 * zero-knowledge reinforcement learning approach using multi-armed bandit
 * policies, for efficient handling of replicated tasks.
 * 
 * @author Roland Ewald
 */
public class AdaptiveComputationTaskRunner extends
    ParallelComputationTaskRunner {

  /**
   * Maps task configurations to information that needs to be stored for
   * adaptive re-configuration.
   */
  private Map<TaskConfiguration, ReplicationInfo> repInfos =
      new HashMap<>();

  /** The bandit policy factory to be used. */
  private final MinBanditPolicyFactory policyFactory;

  /**
   * Parameters for the policy creation by
   * {@link AdaptiveComputationTaskRunner#policyFactory}.
   */
  private final ParameterBlock policyParameters;

  /** List of observers that shall be associated with the policy that is used. */
  private List<IObserver<? extends IMinBanditPolicy>> policyObservers =
      new ArrayList<>();

  /** List of factory names that shall be ignored. */
  private List<String> blackList = new ArrayList<>();

  /**
   * This is a list of predefined configurations. If this is not set, a
   * {@link SelectionTreeSet} will be created to generate all possible
   * configurations. This list can be used to restrict the adaption to a certain
   * subset, or portfolio, of configurations.
   */
  private List<ParameterBlock> predefinedConfigurations = null;

  /**
   * Default constructor from {@link ParallelComputationTaskRunner}.
   * 
   * @param serverName
   *          name of the master server
   * @param maxThreads
   *          number of threads to use
   * @param banditPolicyFactory
   *          the factory to create selection policies
   * @throws RemoteException
   */
  public AdaptiveComputationTaskRunner(int maxThreads,
      MinBanditPolicyFactory banditPolicyFactory, ParameterBlock policyParams)
      throws RemoteException {
    super(maxThreads);
    // Set policy factory
    if (banditPolicyFactory == null) {
      policyFactory =
          SimSystem.getRegistry().getFactory(
              AbstractMinBanditPolicyFactory.class, null);
    } else {
      policyFactory = banditPolicyFactory;
    }
    policyParameters = policyParams;
  }

  @Override
  protected ComputationTaskExecutionJob scheduleJob(TaskConfiguration simConfig) {
    if (!configRegistered(simConfig)) {
      initConfig(simConfig);
    }
    return super.scheduleJob(simConfig);
  }

  /**
   * Initialises adaptive replication for given simulation configuration.
   * 
   * @param taskConfig
   *          the task configuration
   */
  protected void initConfig(TaskConfiguration taskConfig) {

    // TODO: Check if user already pre-defined a single parameter block. If this
    // is
    // partly done, match the eligible configurations and filter accordingly.

    // If there are pre-defined configurations, use them instead of
    // automatically created ones
    List<ParameterBlock> options =
        predefinedConfigurations == null ? createEligibleOptions(taskConfig)
            : predefinedConfigurations;

    if (options == null) {
      return;
    }

    SimSystem.report(Level.INFO,
        "Adaptive Task Runner: Having " + options.size() + " options.");
    repInfos.put(taskConfig, new ReplicationInfo(taskConfig, options,
        taskConfig.allowedReplications(getRunInfos().get(taskConfig)),
        policyFactory.create(policyParameters), policyObservers));
  }

  /**
   * Analyse parameter structure: generate a selection tree set and let it
   * create all eligible configurations.
   * 
   * @param simConfig
   *          the corresponding simulation configuration
   * @return a list of eligible (config) options
   */
  protected List<ParameterBlock> createEligibleOptions(
      TaskConfiguration simConfig) {

    SelectionTreeSet treeSet = null;
    try {
      treeSet =
          SelTreeSetCreation.createSelectionTreeSet(simConfig
              .getCustomRWParams(), (URI) simConfig.getModelReaderParams()
              .getSubBlockValue(IURIHandling.URI), simConfig.getParameters());
    } catch (Exception ex) {
      SimSystem.report(Level.SEVERE, "Creation of selectiont ree set failed.",
          ex);
    }

    if (treeSet == null) {
      return null;
    }

    // Enumerate all (non-parametric) combination of algorithm factories that
    // are eligible
    treeSet.generateFactoryCombinations(blackList);
    return treeSet.getFactoryCombinations();
  }

  /**
   * Checks if a certain simulation configuration has already been registered.
   * 
   * @param simConfig
   *          the simulation configuration
   * @return
   */
  protected boolean configRegistered(TaskConfiguration simConfig) {
    return repInfos.containsKey(simConfig);
  }

  @Override
  protected ComputationTaskExecutionJob createTaskExecJob(
      TaskConfiguration simConfig) {
    return new ReplicatedSimExecThread(this, simConfig,
        getExperimentController(simConfig), repInfos.get(simConfig));
  }

  @Override
  public synchronized void runExecuted(ComputationTaskExecutionJob thread,
      ComputationTaskRuntimeInformation taskRTI, TaskConfiguration taskConfig,
      RunInformation results) {
    super.runExecuted(thread, taskRTI, taskConfig, results);

    // TODO: Generalise performance measurement here
    ReplicatedSimExecThread repThread = (ReplicatedSimExecThread) thread; // NOSONAR:{by_design}
    ReplicationInfo repInfo = repThread.getRepInfo();

    // If run time is negative, there has been an error and this setup should
    // not be used again!
    double runtime = results.getTotalRuntime();
    if (runtime < 0) {
      quarantine(repInfo, repThread.getOptionIndex(), results);
    } else {
      repInfo.registerPerformance(repThread.getOptionIndex(), runtime);
    }

    // TODO: Check/re-set min reps, remove from repInfos when simconf exec
    // finished
  }

  /**
   * Quarantines a configuration option identified by its index. This is
   * necessary if a configuration turns out to be not functional (i.e., it
   * throws Exceptions etc.).
   * 
   * @param repInfo
   *          the replication info for the simulation configuration that caused
   *          the error
   * @param optionIndex
   *          the system configuration option that caused the error
   * @param results
   *          runtime results of the failed configuration
   */
  protected void quarantine(ReplicationInfo repInfo, int optionIndex,
      RunInformation results) {
    repInfo.quarantine(optionIndex);

    // :REGISTRY:
    if (SimSystem.getRegistry() instanceof AlgoSelectionRegistry) {

      TaskConfiguration src = repInfo.getTaskConfig();

      // Avoid copying all observers etc., just the basics for reproducibility
      TaskConfiguration shallowCopy =
          new TaskConfiguration(src.getConfigNumber(),
              src.getModelReaderParams(), src.getParameters(),
              src.getExecParams());

      // TODO: this operation is not guaranteed to identify all components
      // that actually have been used (-> a selection tree) - this would be
      // something the simulation system will need to *return* at some point,
      // and which up until now only can be recorded by the PerfDBRecorder.
      Set<Class<? extends Factory<?>>> failedFactories =
          ((AlgoSelectionRegistry) SimSystem.getRegistry())
              .lookupFactories(repInfo.getOptions()[optionIndex]);

      ((AlgoSelectionRegistry) SimSystem.getRegistry())
          .reportFailure(new FailureDescription(failedFactories, shallowCopy,
              new Exception(results.getErrorMsg(), results.getErrorCause())));
    }
  }

  public List<IObserver<? extends IMinBanditPolicy>> getPolicyObservers() {
    return policyObservers;
  }

  public void setPolicyObservers(
      List<IObserver<? extends IMinBanditPolicy>> policyObservers) {
    this.policyObservers = policyObservers;
  }

  public void setBlackList(List<String> blList) {
    blackList = blList;
  }

  public List<ParameterBlock> getPredefinedConfigurations() {
    return predefinedConfigurations;
  }

  public void setPredefinedConfigurations(
      List<ParameterBlock> predefinedConfigurations) {
    this.predefinedConfigurations = predefinedConfigurations;
  }

  public List<String> getBlackList() {
    return blackList;
  }
}