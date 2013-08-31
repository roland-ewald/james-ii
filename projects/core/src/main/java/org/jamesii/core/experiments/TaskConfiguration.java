/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jamesii.core.data.storage.IDataStorage;
import org.jamesii.core.data.storage.plugintype.DataStorageFactory;
import org.jamesii.core.distributed.masterserver.IMasterServer;
import org.jamesii.core.distributed.masterserver.MasterServer;
import org.jamesii.core.experiments.instrumentation.computation.IComputationInstrumenter;
import org.jamesii.core.experiments.instrumentation.computation.plugintype.ComputationInstrumenterFactory;
import org.jamesii.core.experiments.instrumentation.model.IModelInstrumenter;
import org.jamesii.core.experiments.instrumentation.model.plugintype.ModelInstrumenterFactory;
import org.jamesii.core.experiments.replication.IReplicationCriterion;
import org.jamesii.core.experiments.replication.plugintype.RepCriterionFactory;
import org.jamesii.core.experiments.tasks.ComputationTaskIDObject;
import org.jamesii.core.experiments.tasks.setup.IComputationTaskSetup;
import org.jamesii.core.experiments.tasks.setup.internalsimrun.SimulationRunSetup;
import org.jamesii.core.experiments.tasks.stoppolicy.plugintype.ComputationTaskStopPolicyFactory;
import org.jamesii.core.model.variables.BaseVariable;
import org.jamesii.core.observe.IObservable;
import org.jamesii.core.observe.IObserver;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;
import org.jamesii.core.parameters.ParameterizedFactory;
import org.jamesii.core.util.exceptions.OperationNotSupportedException;
import org.jamesii.core.util.id.IUniqueID;
import org.jamesii.core.util.id.UniqueIDGenerator;
import org.jamesii.core.util.misc.Clone;
import org.jamesii.core.util.misc.ParameterUtils;
import org.jamesii.core.util.misc.Strings;

/**
 * This class represents the a single task configuration. I.e., it describes
 * which model to compute with which parameters, how often to replicate it, how
 * to detect when a computation task can be stopped, and where to store the data
 * in.
 * 
 * If more than one execution of this configuration is necessary (as determined
 * by {@link TaskConfiguration#getReplicationCriterionFactory()}), the
 * configuration can be used to create the corresponding number of
 * {@link SimulationRunConfiguration} .
 * 
 * @author Roland Ewald
 * @author Jan Himmelspach
 * 
 *         Date: 05.06.2007
 */
public class TaskConfiguration implements Serializable,
    Comparable<TaskConfiguration> {

  /** Serialisation ID. */
  private static final long serialVersionUID = -3738204810584609050L;

  /** The experiment ID of the experiment which created the configuration. */
  private IUniqueID experimentID;

  /**
   * The unique ID of this task configuration.
   */
  private IUniqueID uniqueID = null;

  /** Number of the task configuration. */
  private int configNumber = -1;

  /**
   * The replication counter. Should help debugging strongly parallelized tasks.
   */
  private int replicationCounter = 0;

  /** Factory to create a data storage for all storage observers. */
  private ParameterizedFactory<DataStorageFactory> dataStorageFactory = null;

  /** The computation task end / stop policy factory. */
  private ParameterizedFactory<ComputationTaskStopPolicyFactory> taskStopPolicyFactory =
      null;

  /** Factory to create a replication criterion. */
  private ParameterizedFactory<RepCriterionFactory> replicationCriterionFactory = null;

  /** List of all model instrumenter factories and parameters. */
  private final Map<ModelInstrumenterFactory, ParameterBlock> modelInstrumenterFactories =
      new HashMap<>();

  /** List of all observers for the model. */
  private final List<IObserver<? extends IObservable>> modelObservers =
      new ArrayList<>();

  /** URI for the model. */
  private ParameterBlock modelReaderParams = null;

  /** Custom parameter block for model reader. */
  private ParameterBlock customReaderParams = null;

  /** Hash map of all parameters that are defined: name => value. */
  private Map<String, ?> parameters = null;

  /** List of all simulation instrumenter factories and parameters. */
  private final Map<ComputationInstrumenterFactory, ParameterBlock> computationInstrumenterFactories =
      new HashMap<>();

  /** List of all observers for the simulation. */
  private final List<IObserver<? extends IObservable>> simulationObservers =
      new ArrayList<>();

  /** Overall structure to configure execution. */
  private ParameterBlock execParams = ParameterUtils.getDefaultExecParamBlock();

  /**
   * The setup mechanism to be used to setup the computation instance. The
   * default is to use an internal setup procedure. This will be used unless the
   * setup is changed by the {@link #setSetup(IComputationTaskSetup)} method.
   */
  private IComputationTaskSetup setup = new SimulationRunSetup();

  /**
   * Reference to the master server. Will be looked up on demand, and only if
   * master server name is non-empty.
   */
  private transient IMasterServer masterServer = null;

  /** The replication criterion to be used. */
  private transient IReplicationCriterion replicationCriterion = null;

  /**
   * Constructor for bean compatibility.
   */
  public TaskConfiguration() {
  }

  /**
   * Default constructor.
   * 
   * @param taskConfigNumber
   *          the number of this task configuration
   * @param modelRWParams
   *          parameters for model reader/writer
   * @param modelParams
   *          model parameters
   * @param executionParameters
   *          parameters for the execution of the simulation
   */
  public TaskConfiguration(int taskConfigNumber, ParameterBlock modelRWParams,
      Map<String, ?> modelParams, ParameterBlock executionParameters) {
    this.configNumber = taskConfigNumber;
    this.uniqueID = UniqueIDGenerator.createUniqueID();
    this.modelReaderParams = modelRWParams;
    this.parameters = modelParams;
    this.execParams = executionParameters;
  }

  /**
   * Creates a 'plain' data storage for which only experiment ID but no task ID
   * is set. It can therefore be used to make new simulation runs visible to the
   * data storage, but cannot be used as it is to write data.
   * 
   * @return the data storage, or null if no factory and parameters are given
   */
  public IDataStorage createPlainDataStorage() {
    if (dataStorageFactory != null && dataStorageFactory.isInitialized()) {
      IDataStorage dataStorage =
          dataStorageFactory.getFactoryInstance().create(
              dataStorageFactory.getParameter());
      dataStorage.setExperimentID(experimentID);
      dataStorage.setConfigurationID(experimentID, uniqueID);
      // ExperimentInfo exi = new ExperimentInfo();
      // dataStorage.w
      // exi.setDataBase(this.g)
      // dataStorage.writeExperimentInformation(expId, exi);
      return dataStorage;
    }
    return null;
  }

  /**
   * Gets the model reader parameters.
   * 
   * @return the model reader parameters
   */
  public ParameterBlock getModelReaderParams() {
    return modelReaderParams;
  }

  /**
   * Gets the model parameters.
   * 
   * @return the model parameters
   */
  public Map<String, ?> getParameters() {
    return parameters;
  }

  /**
   * Get (deep!) copy of parameters.
   * 
   * @return copy of the parameters
   */
  protected Map<String, ?> getParametersCopy() {
    Map<String, Object> params = new HashMap<>();
    if (parameters == null) {
      return null;
    }
    for (Entry<String, ?> entry : parameters.entrySet()) {
      if (entry.getValue() instanceof BaseVariable<?>) {
        params.put(entry.getKey(),
            ((BaseVariable<?>) entry.getValue()).copyVariable());
      } else {
        params.put(entry.getKey(), entry.getValue());
      }
    }
    return params;
  }

  /**
   * Sets the model observers.
   * 
   * @param modelObservers
   *          the new model observers
   */
  public void setModelObservers(
      List<IObserver<? extends IObservable>> modelObservers) {
    this.modelObservers.clear();
    this.modelObservers.addAll(modelObservers);
  }

  /**
   * Sets the parameters.
   * 
   * @param parameters
   *          the parameters
   */
  public void setParameters(Map<String, Object> parameters) {
    this.parameters = parameters;
  }

  /**
   * Sets the replication criterion factory.
   * 
   * @param repCriterionFactory
   *          the new replication criterion factory
   */
  public void setReplicationCriterionFactory(
      ParameterizedFactory<RepCriterionFactory> repCriterionFactory) {
    this.replicationCriterionFactory = repCriterionFactory;
  }

  /**
   * Gets the replication criterion factory.
   * 
   * @return the replication criterion factory
   */
  public ParameterizedFactory<RepCriterionFactory> getReplicationCriterionFactory() {
    return replicationCriterionFactory;
  }

  /**
   * Sets the simulation observers. Will add the entries to the internal list
   * which is cleared before.
   * 
   * @param simulationObservers
   *          the new simulation observers
   */
  public void setSimulationObservers(
      List<IObserver<? extends IObservable>> simulationObservers) {
    this.simulationObservers.clear();
    this.simulationObservers.addAll(simulationObservers);
  }

  /**
   * Returns the count of allowed replications. Will instantiate a replication
   * criterion and ask whether the number of replications already executed is
   * sufficient. The list of replications has to be passed as the runInfo
   * parameter.
   * 
   * @param runInfo
   *          the run information list
   * 
   * @return the count of allowed replications; maybe 0 in case of no more
   *         replications required
   */
  public int allowedReplications(List<RunInformation> runInfo) {
    if (replicationCriterionFactory == null
        || !replicationCriterionFactory.isInitialized()) {
      throw new IllegalStateException(
          "The replication criterion is not configured properly; it is:"
              + replicationCriterionFactory);
    }
    if (replicationCriterion == null) {
      replicationCriterion =
          replicationCriterionFactory.getFactoryInstance().create(
              replicationCriterionFactory.getParameters());
    }
    return replicationCriterion.sufficientReplications(runInfo);
  }

  @Override
  public String toString() {
    return ParameterBlocks.getSubBlockValue(this.modelReaderParams, "URI")
        + ":" + Strings.dispMap(parameters);
  }

  /**
   * Gets the data storage factory.
   * 
   * @return the data storage factory
   */
  public ParameterizedFactory<DataStorageFactory> getDataStorageFactory() {
    return dataStorageFactory;
  }

  /**
   * Sets the data storage factory.
   * 
   * @param dataStorageFactory
   *          the new data storage factory
   */
  public void setDataStorageFactory(
      ParameterizedFactory<DataStorageFactory> dataStorageFactory) {
    this.dataStorageFactory = dataStorageFactory;
  }

  /**
   * Sets the model reader parameter.
   * 
   * @param modelReaderParams
   *          the new model reader parameters
   */
  public void setModelReaderParams(ParameterBlock modelReaderParams) {
    this.modelReaderParams = modelReaderParams;
  }

  /**
   * Gets the number of the configuration.
   * 
   * @return the number of the configuration
   */
  public int getConfigNumber() {
    return configNumber;
  }

  /**
   * Sets the number of the configuration.
   * 
   * @param configNumber
   *          the new number of the configuration
   */
  public void setConfigNumber(int configNumber) {
    this.configNumber = configNumber;
  }

  /**
   * Checks for data storage.
   * 
   * @return true, if successful
   */
  public boolean hasDataStorage() {
    return dataStorageFactory != null && dataStorageFactory.isInitialized();
  }

  /**
   * Gets the custom rw params.
   * 
   * @return the custom rw params
   */
  public ParameterBlock getCustomRWParams() {
    return customReaderParams;
  }

  /**
   * Sets the custom rw params.
   * 
   * @param customRWParams
   *          the new custom rw params
   */
  public void setCustomRWParams(ParameterBlock customRWParams) {
    this.customReaderParams = customRWParams;
  }

  /**
   * Initialises simulation configuration parameters with information from the
   * master server.
   * 
   * TODO: The following function should be generalized: iteration over all
   * factories, testing which requires additional information from the master
   * server (marker interface for factories), etc.
   * 
   * 
   * @param sMServer
   *          the master server
   * @param simName
   *          the sim name
   */
  public void getResourcesFromMasterServer(MasterServer sMServer, String simName) {

    // Get free database server from master server
    // if (dataStorageFactory != null && dataStorageFactory.usesMasterServer())
    // {
    //
    // DBServerInfo dbInfo = (DBServerInfo)
    // sMServer.getDbServerManagement().getFreeServer();
    // sMServer.getDbServerManagement().useFor(simName, dbInfo);
    // if (dbInfo != null)
    // dataStorageParameters.addSubBlock("dbInfo", new ParameterBlock(dbInfo));
    // else
    // throw new RuntimeException("No free database server available at '"
    // + sMServer.getName() + "'");
    // }
    // TODO this does not work, services are registered at the server and should
    // be solely
    // used/booked by the server, thus we have to postpone the resource
    // collection (if possible)

    throw new OperationNotSupportedException(
        "Database server usage not yet implemented");
  }

  /**
   * Get class of data storage factory to be used.
   * 
   * @return class of used data storage factory, null if none is used
   */
  public Class<? extends DataStorageFactory> getDataStorageFactoryClass() {
    if (dataStorageFactory == null) {
      return null;
    }
    return dataStorageFactory.getFactoryInstance().getClass();
  }

  /**
   * Gets the ID of the experiment which created this configuration.
   * 
   * @return the experiment id
   */
  public IUniqueID getExperimentID() {
    return experimentID;
  }

  /**
   * Sets the ID of the experiment which created this configuration.
   * 
   * @param experimentID
   *          the new experiment id
   */
  public void setExperimentID(IUniqueID experimentID) {
    this.experimentID = experimentID;
  }

  /**
   * Gets the execution parameters.
   * 
   * @return the execution parameters.
   */
  public ParameterBlock getExecParams() {
    return execParams;
  }

  /**
   * Sets the execution parameters.
   * 
   * @param execParams
   *          the new execution parameters.
   */
  public void setExecParams(ParameterBlock execParams) {
    this.execParams = execParams;
  }

  /**
   * Gets the simulation start time.
   * 
   * TODO: Remove, replace by function in SimRunConfig
   * 
   * @return the simulation start time
   */
  @Deprecated
  public Double getSimStartTime() {
    return execParams.getSubBlockValue(ParameterUtils.SIM_START_TIME, 0.0);
  }

  /**
   * Sets the simulation start time.
   * 
   * @param simStartTime
   *          the new simulation start time
   */
  public void setSimStartTime(Double simStartTime) {
    execParams.addSubBlock(ParameterUtils.SIM_START_TIME, simStartTime);
  }

  /**
   * Gets the simulation stop factory.
   * 
   * @return the simulation stop factory
   */
  @Deprecated
  public ComputationTaskStopPolicyFactory getSimStopFactory() {
    return taskStopPolicyFactory.getFactory();
  }

  /**
   * Gets the simulation stop parameters.
   * 
   * @return parameters for the simulation stop factory
   */
  @Deprecated
  public ParameterBlock getSimStopParameters() {
    return taskStopPolicyFactory.getParameters();
  }

  /**
   * Sets the computation task stop policy factory and its parameters.
   * 
   * @param endFactory
   *          the new computation task stop policy
   */
  public void setComputationTaskStopFactory(
      ParameterizedFactory<ComputationTaskStopPolicyFactory> endFactory) {
    taskStopPolicyFactory = endFactory;
  }

  /**
   * Sets the computation task stop policy factory and its parameters.
   * 
   * @param endFactory
   *          the new computation task stop policy
   * @param block
   */
  public void setComputationTaskStopFactory(
      ComputationTaskStopPolicyFactory endFactory, ParameterBlock block) {
    if (taskStopPolicyFactory == null) {
      taskStopPolicyFactory = new ParameterizedFactory<>();
    }
    taskStopPolicyFactory.setFactory(endFactory);
    taskStopPolicyFactory.setParameter(block);
  }

  /**
   * Defines if a master server should be used for simulation. Will return true
   * if master server name is non-empty.
   * 
   * @return true if master server should be used, otherwise false
   */
  public boolean useMasterServer() {
    return getMasterServerName().length() > 0;
  }

  /**
   * Returns name of the master server to be used.
   * 
   * @return name of the master server
   */
  public String getMasterServerName() {
    return execParams.getSubBlockValue(ParameterUtils.MASTER_SERVER_NAME);
  }

  /**
   * Gets master server. If not called before, this method will look up the
   * master server.
   * 
   * Will throw a {@link ComputationSetupException} if the master server cannot
   * be found.
   * 
   * @return reference to the master server, null if not specified or found
   */
  public IMasterServer getMasterServer() {
    String name = getMasterServerName();
    if (name != null && name.length() > 0 && masterServer == null) {

      try {
        masterServer = (IMasterServer) Naming.lookup(name);
      } catch (MalformedURLException | RemoteException | NotBoundException e) {
        throw new ComputationSetupException(
            "JAMES II - cannot find the server (" + name + ")!", e);
      }

    }
    return masterServer;
  }

  /**
   * Sets the start paused flag. If true the simulation run will be started, but
   * it will not automatically be executed - usable for step wise simulation
   * control.
   * 
   * @param value
   *          the new start paused
   */
  public void setStartPaused(boolean value) {
    execParams.getSubBlock(ParameterUtils.START_PAUSED).setValue(value);
  }

  /**
   * Sets the setup.
   * 
   * @param setup
   *          the new setup to be used for creating the instances of the
   *          computation tasks
   */
  public void setSetup(IComputationTaskSetup setup) {
    this.setup = setup;
  }

  /**
   * Gets the start paused.
   * 
   * @return the start paused
   */
  public boolean getStartPaused() {
    return execParams.<Boolean> getSubBlockValue(ParameterUtils.START_PAUSED);
  }

  /**
   * Sets the inter step delay. This value is used to pause between two
   * subsequent steps of a simulation.
   * 
   * @param value
   *          the new inter step delay
   */
  public void setInterStepDelay(long value) {
    execParams.getSubBlock(ParameterUtils.INTER_STEP_DELAY).setValue(value);
  }

  /**
   * Creates a new computation task configuration, which contains (deep) copies
   * of all parameters, to make absolutely sure that all replications are indeed
   * independent.
   * 
   * @param compTaskID
   *          the computation task id
   * 
   * @return new computation task configuration
   */
  public IComputationTaskConfiguration newComputationTaskConfiguration(
      ComputationTaskIDObject compTaskID) {
    int rep = 0;
    synchronized (this) {
      rep = ++replicationCounter;
    }
    IComputationTaskConfiguration srConfig =
        new SimulationRunConfiguration(
            this.experimentID,
            this.uniqueID,
            configNumber,
            compTaskID,
            rep,
            getParametersCopy(),
            (dataStorageFactory != null && dataStorageFactory.isInitialized()) ? dataStorageFactory
                .getFactoryInstance().getClass() : null,
            (dataStorageFactory != null) ? attemptDeepCopy(dataStorageFactory
                .getParameters()) : null,
            (taskStopPolicyFactory != null) ? taskStopPolicyFactory
                .getFactoryInstance().getClass() : null,
            (taskStopPolicyFactory != null) ? attemptDeepCopy(taskStopPolicyFactory
                .getParameter()) : null, attemptDeepCopy(modelReaderParams),
            attemptDeepCopy(customReaderParams), attemptDeepCopy(execParams),
            getMasterServer(), attemptDeepCopy(modelObservers),
            createModelInstrumenters(), attemptDeepCopy(simulationObservers),
            createComputationInstrumenters(), setup);
    return srConfig;
  }

  public void addModelInstrumenterFactory(ModelInstrumenterFactory factory,
      ParameterBlock params) {
    modelInstrumenterFactories.put(factory, params);
  }

  public void addComputationInstrumenterFactory(
      ComputationInstrumenterFactory factory, ParameterBlock params) {
    computationInstrumenterFactories.put(factory, params);
  }

  /**
   * Try to create computation instrumenter, if this has not been done yet.
   */
  private List<IComputationInstrumenter> createComputationInstrumenters() {
    List<IComputationInstrumenter> result = new ArrayList<>();
    for (Map.Entry<ComputationInstrumenterFactory, ParameterBlock> entry : computationInstrumenterFactories
        .entrySet()) {
      ParameterBlock sifParams = ParameterBlocks.newOrCopy(entry.getValue());
      IComputationInstrumenter simulationInstrumenter =
          entry.getKey().create(sifParams);
      result.add(simulationInstrumenter);
    }
    return result;
  }

  /**
   * Try to create model instrumenter, if this has not been done yet.
   */
  private List<IModelInstrumenter> createModelInstrumenters() {
    List<IModelInstrumenter> result = new ArrayList<>();
    for (Map.Entry<ModelInstrumenterFactory, ParameterBlock> entry : modelInstrumenterFactories
        .entrySet()) {
      ParameterBlock mifParams = ParameterBlocks.newOrCopy(entry.getValue());
      IModelInstrumenter modelInstrumenter = entry.getKey().create(mifParams);
      result.add(modelInstrumenter);
    }
    return result;
  }

  /**
   * This method attempts to create a deep copy of a parameter block. If the
   * attempt fails, it still creates a shallow copy and issues a warning.
   * 
   * @param pBlock
   *          the parameter block to be copied
   * 
   * @return the parameter block
   */
  private ParameterBlock attemptDeepCopy(ParameterBlock pBlock) {
    ParameterBlock result = null;
    try {
      result = Clone.cloneSerializable(pBlock);
    } catch (IOException | ClassNotFoundException e) {
      // TODO: Decide what to do
      // SimSystem.report(Level.WARNING, "Deep copy of ParameterBlock failed.",
      // t);
      return pBlock.getCopy();
    }
    return result;
  }

  /**
   * Attempts a deep copy of a list. If the attempt fails, a shallow copy is
   * created and a warning is issued.
   * 
   * @param <O>
   *          the type of list entries
   * 
   * @param list
   *          the list
   * @param errDesc
   *          the err description (reported in case of failure)
   * 
   * @return the list
   */
  @SuppressWarnings("unchecked")
  private <O> List<O> attemptDeepCopy(List<O> list) {
    List<O> newList = null;
    try {
      newList = (List<O>) Clone.cloneSerializable((Serializable) list);
    } catch (IOException | ClassNotFoundException e) {
      // TODO: Decide what to do
      // SimSystem.report(Level.WARNING, "Deep copy of ParameterBlock failed.",
      // t);
      newList = new ArrayList<>(list);
    }

    return newList;
  }

  /**
   * Checks for equal model (and model reader-) parameters.
   * 
   * @param config
   *          the task configuration to be compared
   * 
   * @return 0 if model/model-reader parameters are the same (compares their
   *         string representation)
   */

  @Override
  public int compareTo(TaskConfiguration config) {
    String myRepresentation = String.valueOf(parameters) + modelReaderParams;
    String otherRepresentation =
        String.valueOf(config.getParameters()) + config.getModelReaderParams();
    return myRepresentation.compareTo(otherRepresentation);
  }

  /**
   * Get the value of the uniqueID.
   * 
   * @return the uniqueID
   */
  public final IUniqueID getUniqueID() {
    return uniqueID;
  }

}
