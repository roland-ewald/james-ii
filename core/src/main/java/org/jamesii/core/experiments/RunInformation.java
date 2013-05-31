/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

import org.jamesii.core.data.storage.plugintype.DataStorageFactory;
import org.jamesii.core.experiments.tasks.ComputationTaskIDObject;
import org.jamesii.core.model.InvalidModelException;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.id.IUniqueID;

/**
 * General information which will be collected for each computation task within
 * an {@link BaseExperiment}.
 * 
 * @author Jan Himmelspach
 * @author Roland Ewald
 * 
 */
public class RunInformation implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -8925973373372601870L;

  /**
   * Constant used to divide a number to shift the fraction to the next smaller
   * unit position.
   */
  private static final double SMALLERUNIT = 1000d;

  /** Time needed for creating the model (in ms). */
  private long modelCreationTime = -1;

  /** Time needed for configuring the observers (in ms). */
  private long observerConfigurationTime = -1;

  /** Time needed for creating the computation task (in ms). */
  private long computationTaskCreationTime = -1;

  /** The computation task execution time (in ms). */
  private long computationTaskRunTime = -1;

  /** The total memory of the JVM (in bytes). */
  private long totalMemory = 0;

  /** The free memory of the JVM before starting the run (in bytes). */
  private long freeMemory = 0;

  /** The estimated memory the model occupies (at startup). */
  private long modelMemory = 0;

  /** The estimated memory consumed by the computation (at startup). */
  private long computationMemory = 0;

  /**
   * Flag denoting whether the executed run was the final run of the
   * configuration.
   */
  private boolean jobDone = false;

  /**
   * Stores information about error occurring during the creation of the
   * simulation.
   */
  private String errorMsg;

  /** The cause of the failure. */
  private Throwable errorCause;

  /** The data storage to which this computation tasks output was written. */
  private Class<? extends DataStorageFactory> dataStorageFactory = null;

  /** The parameters to initialise the data storage. */
  private ParameterBlock dataStorageParams = null;

  /** The experiment ID. */
  private IUniqueID expID = null;

  /** The experiment ID. */
  private IUniqueID confID = null;

  /** The computation task ID. */
  private ComputationTaskIDObject computationTaskID = null;

  /** The response to be conveyed to the experiment steering mechanism. */
  private Map<String, Object> response = null;

  /**
   * Instantiates a new computation task runtime information object.
   * 
   * @param taskRunConfig
   *          the computation task configuration
   */
  public RunInformation(IComputationTaskConfiguration taskRunConfig) {
    expID = taskRunConfig.getExperimentID();
    confID = taskRunConfig.getConfigurationID();
    computationTaskID = taskRunConfig.getComputationTaskID();
    dataStorageFactory = taskRunConfig.getDataStorageFactoryClass();
    dataStorageParams = taskRunConfig.getDataStorageParameters();
  }

  /**
   * Instantiates a new computation task runtime information without any
   * reference to the computation task configuration. Only use for error
   * handling!
   * 
   * @param done
   *          the flag to determine whether the job is done
   */
  public RunInformation(boolean done) {
    jobDone = done;
  }

  /**
   * If failed is true this computation task failed. Thus a value of -1 for
   * {@link #modelCreationTime} , {@link #computationTaskCreationTime} or
   * {@link #computationTaskRunTime} means that the operation with the first -1
   * failed! Additionally, {@link RunInformation} {@link #errorCause} and
   * {@link RunInformation}{@link #errorMsg} are checked for null (if not, the
   * computation task is also considered to be failed). This allows to track
   * problems that occur at runtime.
   * 
   * @return true if this computation task failed
   */
  public boolean failed() {
    return (modelCreationTime == -1 || computationTaskCreationTime == -1
        || computationTaskRunTime == -1 || observerConfigurationTime == -1)
        || errorCause != null || errorMsg != null;
  }

  /**
   * Returns total time for computation task in seconds, or -1 if run failed.
   * 
   * @return total time for computation task or -1 when failed
   */
  public double getTotalRuntime() {
    if (failed()) {
      return -1;
    }
    return (modelCreationTime + computationTaskCreationTime
        + computationTaskRunTime + observerConfigurationTime)
        / SMALLERUNIT;
  }

  /**
   * Gets the total runtime in milliseconds, or -1 if run failed.
   * 
   * @return the total runtime in milliseconds or -1 when failed
   */
  public long getTotalRuntimeInMS() {
    if (failed()) {
      return -1;
    }
    return modelCreationTime + computationTaskCreationTime
        + computationTaskRunTime + observerConfigurationTime;
  }

  /**
   * Gets the experiment ID.
   * 
   * @return the experiment id
   */
  public IUniqueID getExpID() {
    return expID;
  }

  /**
   * Sets the experiment ID.
   * 
   * @param expID
   *          the new experiment id
   */
  public void setExpID(IUniqueID expID) {
    this.expID = expID;
  }

  /**
   * Gets the computation task ID object.
   * 
   * @return the computation task ID object
   */
  public ComputationTaskIDObject getComputationTaskID() {
    return computationTaskID;
  }

  /**
   * Sets the computation task ID. Warning! Setting the ID to null may result in
   * unexpected behavior w. r. t. data storages and distributed computation.
   * Each computation task needs a unique ID!
   * 
   * @param computationTaskID
   *          the new computation task ID
   */
  public void setComputationTaskID(ComputationTaskIDObject computationTaskID) {
    this.computationTaskID = computationTaskID;
  }

  /**
   * Gets the data storage factory.
   * 
   * @return the data storage factory
   */
  public Class<? extends DataStorageFactory> getDataStorageFactory() {
    return dataStorageFactory;
  }

  /**
   * Sets the data storage factory.
   * 
   * @param dataStorageFactory
   *          the new data storage factory
   */
  public void setDataStorageFactory(
      Class<? extends DataStorageFactory> dataStorageFactory) {
    this.dataStorageFactory = dataStorageFactory;
  }

  /**
   * Gets the data storage parameters.
   * 
   * @return the data storage parameters
   */
  public ParameterBlock getDataStorageParams() {
    return dataStorageParams;
  }

  /**
   * Sets the data storage parameters.
   * 
   * @param dataStorageParams
   *          the new data storage parameters
   */
  public void setDataStorageParams(ParameterBlock dataStorageParams) {
    this.dataStorageParams = dataStorageParams;
  }

  /**
   * Get execution ID, which is a String consisting of experiment ID +
   * computation task ID.
   * 
   * @return execution ID
   */
  public String getExecutionIDs() {
    return expID + "-" + computationTaskID;
  }

  /**
   * Gets the model creation time in seconds.
   * 
   * @return the modelCreationTime
   */
  public double getModelCreationTime() {
    return modelCreationTime / SMALLERUNIT;
  }

  /**
   * Gets the model creation time in milliseconds.
   * 
   * @return the modelCreationTime
   */
  public long getModelCreationTimeInMS() {
    return modelCreationTime;
  }

  /**
   * Gets the observer configuration time in seconds.
   * 
   * @return the observerConfigurationTime
   */
  public double getObserverConfigurationTime() {
    return observerConfigurationTime / SMALLERUNIT;
  }

  /**
   * Gets the observer configuration time in milliseconds.
   * 
   * @return the observerConfigurationTime
   */
  public long getObserverConfigurationTimeInMS() {
    return observerConfigurationTime;
  }

  /**
   * Gets the computation task creation time in seconds.
   * 
   * @return {@link #computationTaskCreationTime} divided by 1000 (converted to
   *         seconds)
   */
  public double getComputationTaskCreationTime() {
    return computationTaskCreationTime / SMALLERUNIT;
  }

  /**
   * Gets the computation task creation time in milliseconds.
   * 
   * @return {@link #computationTaskCreationTime}
   */
  public long getComputationTaskCreationTimeInMS() {
    return computationTaskCreationTime;
  }

  /**
   * Gets the computation task run time in seconds.
   * 
   * @return the {@link #computationTaskRunTime} divided by 1000 (converted to
   *         seconds)
   */
  public double getComputationTaskRunTime() {
    return computationTaskRunTime / SMALLERUNIT;
  }

  /**
   * Gets the computation task run time in milliseconds.
   * 
   * @return the {@link #computationTaskRunTime}
   */
  public long getComputationTaskRunTimeInMS() {
    return computationTaskRunTime;
  }

  /**
   * Stores information on failure.
   * 
   * @param errMessage
   *          the error message
   * @param errCause
   *          the error cause
   */
  public void storeFailure(String errMessage, Throwable errCause) {
    errorMsg = errMessage;
    errorCause = errCause;
  }

  /**
   * Gets the error message.
   * 
   * @return the errorMsg
   */
  public String getErrorMsg() {
    return errorMsg;
  }

  /**
   * Gets the error cause.
   * 
   * @return the errorCause
   */
  public Throwable getErrorCause() {
    return errorCause;
  }

  /**
   * Denotes whether the executed computation task was the final computation of
   * the configuration.
   * 
   * @return true if the executed run was the final run of the configuration.
   */
  public boolean isJobDone() {
    return jobDone;
  }

  /**
   * Sets the flag denoting the executed run was the final run of the
   * configuration.
   * 
   * @param jobDone
   *          - set true if the executed run was the final run of the
   *          configuration.
   */
  public void setJobDone(boolean jobDone) {
    this.jobDone = jobDone;
  }

  /**
   * Sets the model creation time.
   * 
   * @param modelCreationTime
   *          the modelCreationTime to set
   */
  protected void setModelCreationTime(long modelCreationTime) {
    this.modelCreationTime = modelCreationTime;
  }

  /**
   * Sets the observer configuration time.
   * 
   * @param observerConfigurationTime
   *          the observerConfigurationTime to set
   */
  protected void setObserverConfigurationTime(long observerConfigurationTime) {
    this.observerConfigurationTime = observerConfigurationTime;
  }

  /**
   * Sets the computation task creation time.
   * 
   * @param compTaskCreationTime
   *          the simulationCreationTime to set
   */
  protected void setComputationTaskCreationTime(long compTaskCreationTime) {
    this.computationTaskCreationTime = compTaskCreationTime;
  }

  /**
   * Sets the computation task run time.
   * 
   * @param computationTaskRunTime
   *          the computationTaskRunTime to set
   */
  protected void setComputationTaskRunTime(long computationTaskRunTime) {
    this.computationTaskRunTime = computationTaskRunTime;
  }

  /**
   * Sets the total memory.
   * 
   * @param totalMemory
   *          the totalMemory to set
   */
  protected void setTotalMemory(long totalMemory) {
    this.totalMemory = totalMemory;
  }

  /**
   * Sets the free memory.
   * 
   * @param freeMemory
   *          the freeMemory to set
   */
  protected void setFreeMemory(long freeMemory) {
    this.freeMemory = freeMemory;
  }

  /**
   * Gets the free memory.
   * 
   * @return the freeMemory
   */
  public long getFreeMemory() {
    return freeMemory;
  }

  /**
   * Gets the free memory.
   * 
   * @return the freeMemory
   */
  public long getTotalMemory() {
    return totalMemory;
  }

  /**
   * Sets the model memory.
   * 
   * @param modelMemory
   *          the modelMemory to set
   */
  protected void setModelMemory(long modelMemory) {
    this.modelMemory = modelMemory;
  }

  /**
   * Gets the model memory.
   * 
   * @return the modelMemory
   */
  public long getModelMemory() {
    return modelMemory;
  }

  /**
   * Gets the amount of used memory (in bytes).
   * 
   * @return the used memory
   */
  public long getUsedMemory() {
    return totalMemory - freeMemory;
  }

  /**
   * Sets the computation task memory.
   * 
   * @param compTaskMemory
   *          the computation task memory
   */
  protected void setComputationTaskMemory(long compTaskMemory) {
    this.computationMemory = compTaskMemory;
  }

  /**
   * Gets the computation task memory.
   * 
   * @return the {@link #computationMemory}
   */
  public long getComputationTaskMemory() {
    return computationMemory;
  }

  /**
   * Gets the response.
   * 
   * @return the response
   */
  public Map<String, Object> getResponse() {
    return response;
  }

  /**
   * Sets the response.
   * 
   * @param response
   *          the response
   */
  public void setResponse(Map<String, Object> response) {
    if (this.response != null) {
      throw new RuntimeException("response must not be set twice!");
    }
    this.response = Collections.unmodifiableMap(response);
  }

  /**
   * Get the value of the confID.
   * 
   * @return the confID
   */
  public IUniqueID getConfID() {
    return confID;
  }

  /**
   * Set the confID to the value passed via the confID attribute.
   * 
   * @param confID
   *          the confID to set
   */
  public void setConfID(IUniqueID confID) {
    this.confID = confID;
  }

}
