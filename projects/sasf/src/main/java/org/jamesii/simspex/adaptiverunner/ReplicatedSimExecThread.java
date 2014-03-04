/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.adaptiverunner;

import org.jamesii.core.experiments.IComputationTaskConfiguration;
import org.jamesii.core.experiments.IExperimentExecutionController;
import org.jamesii.core.experiments.SimulationRunConfiguration;
import org.jamesii.core.experiments.TaskConfiguration;
import org.jamesii.core.experiments.taskrunner.parallel.ComputationTaskExecutionJob;
import org.jamesii.core.experiments.taskrunner.parallel.ParallelComputationTaskRunner;
import org.jamesii.core.experiments.tasks.ComputationTaskIDObject;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;
import org.jamesii.core.processor.plugintype.ProcessorFactory;
import org.jamesii.core.util.misc.Pair;

/**
 * This {@link ComputationTaskExecutionJob} holds a reference to the
 * {@link ReplicationInfo} object that corresponds to the
 * {@link TaskConfiguration} which is replicated by this execution. This is
 * necessary, as the {@link org.jamesii.core.experiments.RunInformation}, along with
 * the index identifying the selected configuration, needs to be fed back to the
 * {@link org.jamesii.simspex.adaptiverunner.policies.plugintype.IMinBanditPolicy}.
 * 
 * @author Roland Ewald
 * 
 */
public class ReplicatedSimExecThread extends ComputationTaskExecutionJob {

  /** The replication information for the given simulation configuration. */
  private final ReplicationInfo repInfo;

  /** The selection made by the replication policy for this replication. */
  private Pair<Integer, ParameterBlock> selection;

  /**
   * Default constructor.
   * 
   * @param runner
   *          the simulation runner
   * @param config
   *          the corresponding simulation configuration
   * @param execController
   *          the execution controller
   * @param replicationInfo
   *          reference to the corresponding replication information
   */
  public ReplicatedSimExecThread(ParallelComputationTaskRunner runner,
      TaskConfiguration config, IExperimentExecutionController execController,
      ReplicationInfo replicationInfo) {
    super(runner, config, execController);
    repInfo = replicationInfo;
  }

  @Override
  protected SimulationRunConfiguration createRunConfiguration(
      ComputationTaskIDObject computationTaskID) {
    IComputationTaskConfiguration runConfig;
    synchronized (getConfig()) {
      adaptConfiguration(getConfig());
      runConfig = getConfig().newComputationTaskConfiguration(computationTaskID);
    }
    return (SimulationRunConfiguration) runConfig;
  }

  /**
   * Adapts the configuration.
   * 
   * @param taskConf
   *          the task configuration
   */
  protected void adaptConfiguration(TaskConfiguration taskConf) {
    selection = repInfo.getNextOption();
    String facName = ProcessorFactory.class.getName();
    ParameterBlock execParameters =
        repInfo.getOriginalExecParameters().getCopy();
    ParameterBlock procParams =
        execParameters.hasSubBlock(facName) ? execParameters
            .getSubBlock(facName) : execParameters.addSubBlock(facName,
            (Object) null);
    execParameters.addSubBlock(facName, ParameterBlocks.mergeOnTopLevel(
        procParams, ParameterBlocks.hasSubBlock(selection.getSecondValue(),
            facName) ? selection.getSecondValue().getSubBlock(facName)
            : selection.getSecondValue()));
    taskConf.setExecParams(execParameters);
  }

  /**
   * Retrieves index of the configuration that was chosen for executing this
   * replication.
   * 
   * @return the index of the parameter block in {@link ReplicationInfo#options}
   */
  public int getOptionIndex() {
    return selection.getFirstValue();
  }

  /**
   * Get associated replication information.
   * 
   * @return associated replication information
   */
  protected ReplicationInfo getRepInfo() {
    return repInfo;
  }
}
