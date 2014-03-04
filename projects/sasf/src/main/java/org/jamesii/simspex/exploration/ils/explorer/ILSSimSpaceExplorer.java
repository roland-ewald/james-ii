/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.exploration.ils.explorer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.RunInformation;
import org.jamesii.core.experiments.TaskConfiguration;
import org.jamesii.core.experiments.steering.VariablesAssignment;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.model.variables.BaseVariable;
import org.jamesii.core.model.variables.QuantitativeBaseVariable;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.perfdb.recording.selectiontrees.SelectionTreeSet;
import org.jamesii.simspex.exploration.AbstractSimSpaceExplorer;
import org.jamesii.simspex.exploration.ISimSpaceExplorer;
import org.jamesii.simspex.exploration.ils.algorithm.ParamILS;
import org.jamesii.simspex.exploration.ils.termination.ITerminationIndicator;
import org.jamesii.simspex.exploration.paramblocks.UpdateGenerator;
import org.jamesii.simspex.exploration.utils.RandomModelSetupSampler;
import org.jamesii.simspex.util.sampling.IConfigurationSampler;
import org.jamesii.simspex.util.sampling.RandomConfigurationSampler;
import org.jamesii.simspex.util.sampling.SimpleConfigurationSamplingRules;

/**
 * A simulation space explorer that uses iterated local search to explore the
 * performance space. The iterated search policy is executed in a newly created
 * thread, and communicates with the explorer via a job- and a result-queue.
 * 
 * @author Roland Ewald
 * @author Robert Engelke
 * 
 */
public class ILSSimSpaceExplorer implements ISimSpaceExplorer,
    IILSSimSpaceExplorerInformationSource {

  /** The result queue; may only contain a single element. */
  private final BlockingQueue<ExecutionResult> resultQueue =
      new ArrayBlockingQueue<>(1);

  /** The job queue; may only contain a single element. */
  private final BlockingQueue<ExecutionJob> jobQueue =
      new ArrayBlockingQueue<>(1);

  /** The configuration sampler. */
  private final IConfigurationSampler configurationSampler;

  /** The result store. */
  private final ILSResultStore resultStore = new ILSResultStore();

  /** The model setups that have been tried. */
  private final List<Map<String, Serializable>> modelSetups = new ArrayList<>();

  /** Defines the model space to be explored. */
  private final List<BaseVariable<?>> modelVariables = new ArrayList<>();

  /** The random model setup sampler. */
  private final RandomModelSetupSampler randomModelSetupSampler =
      new RandomModelSetupSampler();

  /** Runs {@link ParamILS}. */
  private final ParamILSRunner paramILSRunner;

  /** The global termination indicator. */
  private ITerminationIndicator<ILSSimSpaceExplorer> terminationIndicator;

  /** The simulation stop time to be configured. */
  private Double simStopTime = 1.;

  /** The current job to be executed. */
  private ExecutionJob currentJob;

  /** The run information of the last computed run. */
  private RunInformation lastRun;

  /**
   * Instantiates a new ILS simulation space explorer.
   * 
   * @param setups
   *          the setups to be explored
   * @param paramILS
   *          the ParamILS implementation
   * @param maximalNumRuns
   *          the maximal number of runs
   */
  public ILSSimSpaceExplorer(
      List<ParameterBlock> setups,
      ParamILS paramILS,
      Map<Class<? extends Factory<?>>, List<QuantitativeBaseVariable<?>>> parameterToSearch) {
    paramILS.setInformationSource(this);
    paramILSRunner = new ParamILSRunner(paramILS, jobQueue);
    configurationSampler =
        new RandomConfigurationSampler(new SimpleConfigurationSamplingRules(),
            setups, SimSystem.getRNGGenerator().getNextRNG(), parameterToSearch);
  }

  @Override
  public void init() {
    new Thread(paramILSRunner).start();
  }

  @Override
  public VariablesAssignment getNextVariableAssignment() {
    try {

      // Check before locking if param ILS is already done
      if (isFinished()) {
        return null;
      }

      // Retrieve job
      SimSystem.report(Level.FINEST, "Waiting for new job.");
      currentJob = jobQueue.take();
      SimSystem.report(Level.FINEST, "Received new job for configuration:\n"
          + currentJob.getConfiguration());

      // Check if param ILS provoked an error
      if (currentJob.getConfiguration() == null) {
        return null;
      }

      // Configure variable assignment accordingly
      VariablesAssignment assignment =
          UpdateGenerator.getUpdateAssignment(UpdateGenerator
              .generateUpdates(currentJob.getConfiguration()));
      assignment.putAll(getOrCreateModelSetup(currentJob.getModelSetupIndex()));
      return assignment;
    } catch (InterruptedException ex) {
      SimSystem.report(Level.SEVERE,
          "Interrupted while waiting for next job to be executed.", ex);
      paramILSRunner.getParamILS().stop();
      return null;
    }
  }

  /**
   * Gets the model setup with the given index, creates it (and all 'missing'
   * setups) if necessary.
   * 
   * @param modelSetupIndex
   *          the model setup index
   * @return the model setup for the given index
   */
  private Map<String, Serializable> getOrCreateModelSetup(int modelSetupIndex) {
    while (modelSetupIndex >= modelSetups.size()) {
      modelSetups.add(randomModelSetupSampler.sampleSetup(modelVariables));
      SimSystem.report(Level.FINE, "New problem #" + (modelSetups.size() - 1)
          + ": " + modelSetups.get(modelSetups.size() - 1));
    }
    return modelSetups.get(modelSetupIndex);
  }

  @Override
  public void executionFinished(TaskConfiguration simConfig,
      RunInformation runInformation) {
    try {
      this.lastRun = runInformation;
      if (terminationIndicator != null) {
        terminationIndicator.updateTerminationCriterion(this);
      }
      Double runtime =
          resultStore.registerExecution(currentJob,
              runInformation.getTotalRuntime());
      resultQueue.put(new ExecutionResult(runtime));
    } catch (InterruptedException e) {
      SimSystem.report(Level.SEVERE,
          "Interrupted while waiting to enqueue execution result.", e);
    }
  }

  @Override
  public List<ParameterBlock> getConfigurations() {
    return resultStore.getConfigExplorationSequence();
  }

  @Override
  public Set<BaseVariable<?>> getModelVariables() {
    return new HashSet<>(modelVariables);
  }

  @Override
  public void setModelVariables(Set<BaseVariable<?>> modelVariables) {
    this.modelVariables.clear();
    this.modelVariables.addAll(modelVariables);
  }

  @Override
  public void setConfigurations(List<ParameterBlock> configs) {
    throw new UnsupportedOperationException(
        "The list of configurations is generated via random sampling and cannot be set manually.");
  }

  @Override
  public boolean allowSubStructures() {
    return false;
  }

  @Override
  public ParameterBlock getExperimentParameters() {
    return AbstractSimSpaceExplorer.configureSimStopping(simStopTime,
        convertFromSecondsToMilliseconds());
  }

  private long convertFromSecondsToMilliseconds() {
    if (currentJob.getResourceCap() < Long.MAX_VALUE / 1000.0) {
      return (long) (currentJob.getResourceCap() * 1000);
    }
    return currentJob.getResourceCap().longValue();
  }

  // Implementation of IILSSimSpaceExplorerInformationSource

  @Override
  public ParameterBlock getInitialConfiguration() {
    return configurationSampler.getInitialConfiguration();
  }

  @Override
  public ParameterBlock getRandomNeighbour(ParameterBlock simConfig) {
    return configurationSampler.getRandomNeighbour(simConfig);
  }

  @Override
  public ParameterBlock getRandomNeighbour(ParameterBlock simConfig,
      int positionToChange) {
    return configurationSampler.getRandomNeighbour(simConfig, positionToChange);
  }

  @Override
  public List<ParameterBlock> getNeighbourhood(ParameterBlock simConfig) {
    return configurationSampler.getNeighbourhood(simConfig);
  }

  @Override
  public int getNumberOfParameters(ParameterBlock configuration) {
    return configurationSampler.getNumberOfParameters(configuration);
  }

  @Override
  public ParameterBlock restart() {
    return configurationSampler.restart();
  }

  @Override
  public double calculateCost(ParameterBlock configuration, int runNumber,
      Double resourceCap) {
    try {
      jobQueue.put(new ExecutionJob(configuration, runNumber, resourceCap));
      ExecutionResult result = resultQueue.take();
      return result.getExecutionCosts();
    } catch (InterruptedException ex) {
      SimSystem
          .report(
              Level.SEVERE,
              "Interrupted during wait for inserting job / retrieving execution results.",
              ex);
      return Double.POSITIVE_INFINITY;
    }
  }

  @Override
  public int getNumberOfRuns(ParameterBlock configuration) {
    return resultStore.getNumberOfRuns(configuration);
  }

  @Override
  public double getCost(ParameterBlock configuration, int runNumber) {
    return resultStore.getCostOfRun(configuration, runNumber);
  }

  @Override
  public double getSumOfCosts(ParameterBlock configuration, int runNumber) {
    return resultStore.getCostSumOfRuns(configuration, runNumber);
  }

  @Override
  public double getResourceCap(ParameterBlock configuration, int runNumber) {
    return resultStore.getResourceCap(configuration, runNumber);
  }

  public double getSimStopTime() {
    return simStopTime;
  }

  public void setSimStopTime(double simStopTime) {
    this.simStopTime = simStopTime;
  }

  @Override
  public boolean isFinished() {
    if (terminationIndicator != null
        && terminationIndicator.shallTerminate(this)) {
      paramILSRunner.getParamILS().stop();
    }
    return paramILSRunner.isFinished();
  }

  @Override
  public SelectionTreeSet getSelectionTreeSet() {
    return null;
  }

  public void setTerminationIndicator(
      ITerminationIndicator<ILSSimSpaceExplorer> terminationIndicator) {
    this.terminationIndicator = terminationIndicator;
  }

  /**
   * Gets the run information of the last computed run.
   * 
   * @return the last run
   */
  public RunInformation getLastRun() {
    return lastRun;
  }

  public ParamILS getParamILS() {
    return paramILSRunner.getParamILS();
  }

}

/**
 * Thread to execute {@link ParamILS}.
 * 
 * @author Roland Ewald
 * 
 */
class ParamILSRunner implements Runnable {

  /** The ParamILS instance to run. */
  private final ParamILS paramILS;

  /** The job queue to be used. */
  private final BlockingQueue<ExecutionJob> jobQueue;

  /**
   * The flag to indicate whether a severe error has occurred (lets the
   * exploration stop).
   */
  private boolean errorOccurred = false;

  ParamILSRunner(ParamILS paramILS, BlockingQueue<ExecutionJob> jobQueue) {
    this.paramILS = paramILS;
    this.jobQueue = jobQueue;
  }

  /**
   * Checks if ParamILS is finished.
   * 
   * @return true, if it is finished
   */
  public boolean isFinished() {
    return errorOccurred || paramILS.shallTerminate();
  }

  @Override
  public void run() {
    try {
      this.getParamILS().run();
    } catch (Throwable t) { // NOSONAR: catching_throwable_prevents_deadlock
      SimSystem.report(Level.SEVERE,
          "Problem in ParamILS, stopping exploration (and thread).", t);
      errorOccurred = true;
      jobQueue.add(new ExecutionJob(null, null, null));
    }
  }

  public ParamILS getParamILS() {
    return paramILS;
  }

}

/**
 * The execution results, handled by the {@link BlockingQueue}.
 * 
 * @author Roland Ewald
 */
class ExecutionResult {

  private final Double executionCosts;

  ExecutionResult(Double executionCosts) {
    this.executionCosts = executionCosts;
  }

  public Double getExecutionCosts() {
    return executionCosts;
  }
}

/**
 * The execution job, handled by the {@link BlockingQueue}.
 * 
 * @author Roland Ewald
 */
class ExecutionJob {

  private final ParameterBlock configuration;

  private final Integer modelSetupIndex;

  private final Double resourceCap;

  ExecutionJob(ParameterBlock configuration, Integer modelSetupIndex,
      Double cappingValue) {
    this.configuration = configuration;
    this.modelSetupIndex = modelSetupIndex;
    this.resourceCap = cappingValue;
  }

  public ParameterBlock getConfiguration() {
    return configuration;
  }

  public Integer getModelSetupIndex() {
    return modelSetupIndex;
  }

  public Double getResourceCap() {
    return resourceCap;
  }

}