/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.data.storage.IDataStorage;
import org.jamesii.core.data.storage.plugintype.DataStorageFactory;
import org.jamesii.core.distributed.allocation.plugintype.AbstractSimulationResourceAllocatorFactory;
import org.jamesii.core.distributed.allocation.plugintype.ISimulationResourceAllocator;
import org.jamesii.core.distributed.allocation.plugintype.SimulationResourceAllocatorFactory;
import org.jamesii.core.distributed.masterserver.IMasterServer;
import org.jamesii.core.experiments.instrumentation.computation.IComputationInstrumenter;
import org.jamesii.core.experiments.instrumentation.model.IModelInstrumenter;
import org.jamesii.core.experiments.tasks.ComputationTaskIDObject;
import org.jamesii.core.experiments.tasks.setup.IComputationTaskSetup;
import org.jamesii.core.experiments.tasks.stoppolicy.plugintype.ComputationTaskStopPolicyFactory;
import org.jamesii.core.model.IModel;
import org.jamesii.core.observe.IObservable;
import org.jamesii.core.observe.IObserver;
import org.jamesii.core.observe.IStoringObserver;
import org.jamesii.core.observe.Mediator;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.serialization.IConstructorParameterProvider;
import org.jamesii.core.serialization.SerialisationUtils;
import org.jamesii.core.simulationrun.ISimulationRun;
import org.jamesii.core.util.id.IUniqueID;
import org.jamesii.core.util.misc.ParameterUtils;

/**
 * This class contains all information that is required to execute a single
 * simulation run.
 * 
 * @author Roland Ewald
 */
public class SimulationRunConfiguration implements
    IComputationTaskConfiguration {
  static {
    SerialisationUtils.addDelegateForConstructor(
        SimulationRunConfiguration.class,
        new IConstructorParameterProvider<SimulationRunConfiguration>() {
          @Override
          public Object[] getParameters(SimulationRunConfiguration srConfig) {
            Object[] params =
                new Object[] { srConfig.getExperimentID(), srConfig.getID(),
                    srConfig.getNumber(), srConfig.getComputationTaskID(),
                    srConfig.getComputationTaskConfigNumber(),
                    srConfig.getParameters(),
                    srConfig.getDataStorageFactoryClass(),
                    srConfig.getDataStorageParameters(),
                    srConfig.getStopPolicyFactoryClass(),
                    srConfig.getStopPolicyParameters(),
                    srConfig.getAbsModelReaderFactoryParams(),
                    srConfig.getModelReaderParams(), srConfig.getExecParams(),
                    srConfig.getMasterServer(), srConfig.getModelObservers(),
                    srConfig.getModelInstrumenters(),
                    srConfig.getComputationTaskObservers(),
                    srConfig.getSimulationInstrumenters(), srConfig.setup };
            return params;
          }
        });
  }

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -7967368126145287139L;

  /** The experiment id. */
  private final IUniqueID experimentID;

  /** The unique configuration id. */
  private final IUniqueID configurationID;

  /** The computation task id. */
  private final ComputationTaskIDObject taskID;

  /**
   * The task configuration number, unique for the experiment it has been
   * created for.<br/>
   * Note: Should/Could maybe be replaced by unique ID with an additional
   * counter.
   */
  private final int configurationNumber;

  /**
   * The computation task configuration number, unique for given computation
   * task configuration. Should be replaced by unique ID with an additional
   * counter.
   */
  private final int computationTaskConfigNumber;

  /** Model instrumenters to be used. */
  private final transient List<IModelInstrumenter> modelInstrumenters;

  /** List of all observers for the model. */
  private final transient List<IObserver<? extends IObservable>> modelObservers;

  /** Hash map of all parameters that are defined: name => value. */
  private final Map<String, ?> parameters;

  /** Simulation instrumenters to be used. */
  private final transient List<IComputationInstrumenter> computationTaskInstrumenters;

  /** List of all observers for the computation task. */
  private final transient List<IObserver<? extends IObservable>> computationTaskObservers;

  /** Factory class of the data storage used by all storage observers. */
  private final Class<? extends DataStorageFactory> dataStorageFactoryClass;

  /** Parameters to initialise the data storage for the storage observers. */
  private final ParameterBlock dataStorageParameters;

  /**
   * Parameters for the abstract model reader factory class (contains model
   * URI).
   */
  private final ParameterBlock absModelReaderFactoryParams;

  /** Additional parameters for the concrete model reader factory. */
  private final ParameterBlock modelReaderParams;

  /** Overall structure to configure execution. */
  private final ParameterBlock execParams;

  /** Reference to the master server. */
  private final IMasterServer masterServer;

  /** The class of the computation task stop policy factory. */
  private final Class<? extends ComputationTaskStopPolicyFactory> stopPolicyFactoryClass;

  /** The computation task stop policy parameters. */
  private final ParameterBlock runStopPolicyParameters;

  /** The setup mechanism to be used to setup the computation instance */
  private final IComputationTaskSetup setup;

  /**
   * 
   * Instantiates a new simulation run configuration.
   * 
   * @param expID
   *          the experiment id
   * @param configurationID
   *          configuration ID
   * @param computationTaskID
   *          the computation task id
   * @param modelParams
   *          the model parameters
   * @param dsFactoryClass
   *          class of the data storage factory that was used
   * @param dsParams
   *          parameters to set up the data storage properly
   * @param mrFactoryParams
   *          the mr factory params
   * @param mrParams
   *          the mr params
   * @param configurationNumber
   *          the sim conf num
   * @param simRunConfNum
   *          the sim run conf num
   * @param stopFactory
   *          the stop factory
   * @param stopParams
   *          the stop params
   * @param executionParameters
   *          the execution parameters
   * @param mServer
   *          the m server
   * @param mObservers
   *          the m observers
   * @param mInstrumenters
   *          the m instrumenters
   * @param sObservers
   *          the s observers
   * @param sInstrumenters
   *          the s instrumenters
   * 
   */
  SimulationRunConfiguration(IUniqueID expID, IUniqueID configurationID,
      Integer configurationNumber, ComputationTaskIDObject computationTaskID,
      Integer simRunConfNum, Map<String, ?> modelParams,
      Class<? extends DataStorageFactory> dsFactoryClass,
      ParameterBlock dsParams,
      Class<? extends ComputationTaskStopPolicyFactory> stopFactoryClass,
      ParameterBlock stopParams, ParameterBlock mrFactoryParams,
      ParameterBlock mrParams, ParameterBlock executionParameters,
      IMasterServer mServer, List<IObserver<? extends IObservable>> mObservers,
      List<IModelInstrumenter> mInstrumenters,
      List<IObserver<? extends IObservable>> sObservers,
      List<IComputationInstrumenter> sInstrumenters, IComputationTaskSetup setup) {
    experimentID = expID;
    this.configurationID = configurationID;
    this.configurationNumber = configurationNumber;
    taskID = computationTaskID;
    computationTaskConfigNumber = simRunConfNum;
    parameters = modelParams;
    dataStorageFactoryClass = dsFactoryClass;
    dataStorageParameters = dsParams;
    stopPolicyFactoryClass = stopFactoryClass;
    runStopPolicyParameters = stopParams;
    absModelReaderFactoryParams = mrFactoryParams;
    modelReaderParams = mrParams;
    execParams = executionParameters;
    masterServer = mServer;
    modelObservers = mObservers;
    modelInstrumenters = mInstrumenters;
    computationTaskObservers = sObservers;
    computationTaskInstrumenters = sInstrumenters;
    this.setup = setup;
  }

  @Override
  public IDataStorage createDataStorage() {

    // Check if data storage shall actually be used
    if (dataStorageFactoryClass != null && dataStorageParameters != null) {

      // Check that the simulation run ID is already known
      if (taskID == null) {
        throw new IllegalArgumentException("UID must not be null.");
      }

      // Instantiate factory
      DataStorageFactory dsFactory =
          SimSystem.getRegistry().instantiateFactory(dataStorageFactoryClass);

      // If data storage factory could be instantiated, use it to create a data
      // storage
      if (dsFactory != null) {
        IDataStorage dataStorage = dsFactory.create(dataStorageParameters, SimSystem.getRegistry().createContext());
        dataStorage.setExperimentID(experimentID);
        dataStorage.setConfigurationID(experimentID, configurationID);
        dataStorage.setComputationTaskID(experimentID, configurationID,
            taskID.getId());
        return dataStorage;
      }
    }

    return null;
  }

  /**
   * Instrument model.
   * 
   * @param model
   *          model to be instrumented
   * @param ds
   *          the data storage to be used, might be null
   */
  public void instrumentModel(IModel model, IDataStorage ds) {

    if (modelInstrumenters == null) {
      SimSystem
          .report(
              Level.WARNING,
              "Instrumentation and observation of models in distributed setups is not working properly so far.");
      return;
    }

    Mediator.create(model);

    for (IModelInstrumenter modelInstrumenter : modelInstrumenters) {
      modelInstrumenter.instrumentModel(model, this);
      if (modelInstrumenter.getInstantiatedObservers() != null) {
        modelObservers.addAll(modelInstrumenter.getInstantiatedObservers());
      }
    }

    for (IObserver observer : modelObservers) {
      if (observer instanceof IStoringObserver) {
        ((IStoringObserver) observer).setDataStorage(ds);
      }
    }
  }

  /**
   * Instrument simulation.
   * 
   * @param simulation
   *          simulation to be instrumented
   * @param ds
   *          the data storage to be used, might be null
   */
  public void instrumentSimulation(ISimulationRun simulation, IDataStorage ds) {

    if (computationTaskInstrumenters == null) {
      SimSystem
          .report(
              Level.WARNING,
              "Instrumentation and observation of computation tasks in distributed setups is not working properly so far.");
      return;
    }
    
    Mediator.create(simulation);

    for (IComputationInstrumenter simInstrumenter : computationTaskInstrumenters) {
      simInstrumenter.instrumentComputation(simulation);
      computationTaskObservers.addAll(simInstrumenter
          .getInstantiatedObservers());
    }

    for (IObserver observer : computationTaskObservers) {
      if (observer instanceof IStoringObserver) {
        ((IStoringObserver) observer).setDataStorage(ds);
      }
    }
  }

  @Override
  public ParameterBlock getAbsModelReaderFactoryParams() {
    return defensiveCopy(absModelReaderFactoryParams);
  }

  @Override
  public ParameterBlock getModelReaderParams() {
    return defensiveCopy(modelReaderParams);
  }

  @Override
  public ParameterBlock getExecParams() {
    return defensiveCopy(execParams);
  }

  @Override
  public Class<? extends DataStorageFactory> getDataStorageFactoryClass() {
    return dataStorageFactoryClass;
  }

  @Override
  public ParameterBlock getDataStorageParameters() {
    return defensiveCopy(dataStorageParameters);
  }

  @Override
  public IUniqueID getExperimentID() {
    return experimentID;
  }

  @Override
  public ComputationTaskIDObject getComputationTaskID() {
    return taskID;
  }

  @Override
  public Map<String, ?> getParameters() {
    return Collections.unmodifiableMap(parameters);
  }

  /**
   * Checks for data storage factory.
   * 
   * @return true, if there is one
   */
  public boolean hasDataStorageFactory() {
    return dataStorageFactoryClass != null;
  }

  @Override
  public boolean isInteractive() {
    return execParams.getSubBlockValue(ParameterUtils.INTERACTIVE, false);
  }

  @Override
  public boolean useMasterServer() {
    return masterServer != null;
  }

  @Override
  public IMasterServer getMasterServer() {
    return masterServer;
  }

  /**
   * Gets the simulation start time.
   * 
   * @return the simulation start time
   */
  public Double getSimStartTime() {
    return execParams.getSubBlockValue(ParameterUtils.SIM_START_TIME, 0.0);
  }

  @Override
  public Class<? extends ComputationTaskStopPolicyFactory> getStopPolicyFactoryClass() {
    return stopPolicyFactoryClass;
  }

  @Override
  public ParameterBlock getStopPolicyParameters() {
    return runStopPolicyParameters != null ? runStopPolicyParameters.getCopy()
        : null;
  }

  /**
   * Checks if is monitoring enabled.
   * 
   * @return true, if is monitoring enabled
   */
  public boolean isMonitoringEnabled() {
    return execParams
        .getSubBlockValue(ParameterUtils.MONITORING_ENABLED, false);
  }

  /**
   * Gets the inter step delay.
   * 
   * @return the inter step delay
   */
  public long getInterStepDelay() {
    return execParams.<Long> getSubBlockValue(ParameterUtils.INTER_STEP_DELAY);
  }

  @Override
  public boolean isStartPaused() {
    return execParams.<Boolean> getSubBlockValue(ParameterUtils.START_PAUSED);
  }

  /**
   * Checks if is silent.
   * 
   * @return true, if is silent
   */
  public boolean isSilent() {
    return execParams.getSubBlockValue(ParameterUtils.SILENT, false);
  }

  /**
   * Checks if is log time.
   * 
   * @return true, if is log time
   */
  public boolean isLogTime() {
    return execParams.getSubBlockValue(ParameterUtils.LOG_TIME, false);
  }

  /**
   * Gets the task configuration number.
   * 
   * @return the task configuration number
   */
  @Override
  public int getNumber() {
    return configurationNumber;
  }

  /**
   * Gets the computation task configuration number.
   * 
   * @return the computation task config number
   */
  public int getComputationTaskConfigNumber() {
    return computationTaskConfigNumber;
  }

  /**
   * Tests whether this configuration shall be executed with resilience support.
   * 
   * @return true if resilience support shall be enabled, otherwise false
   */
  public boolean isResilient() {
    return execParams.getSubBlockValue(ParameterUtils.RESILIENT, false);
  }

  /**
   * Gets the model instrumenters.
   * 
   * @return the model instrumenters
   */
  public List<IModelInstrumenter> getModelInstrumenters() {
    return Collections.unmodifiableList(modelInstrumenters);
  }

  @Override
  public List<IObserver<? extends IObservable>> getModelObservers() {
    return Collections.unmodifiableList(modelObservers);
  }

  /**
   * Gets the simulation instrumenters.
   * 
   * @return the simulation instrumenters
   */
  public List<IComputationInstrumenter> getSimulationInstrumenters() {
    return Collections.unmodifiableList(computationTaskInstrumenters);
  }

  @Override
  public List<IObserver<? extends IObservable>> getComputationTaskObservers() {
    return Collections.unmodifiableList(computationTaskObservers);
  }

  /**
   * Adds the model observer. Is marked deprecated and will be removed in the
   * future!
   * 
   * @param modelObserver
   *          the model observer
   */
  @Deprecated
  public void addModelObserver(IObserver modelObserver) {
    modelObservers.add(modelObserver);
  }

  /**
   * Gets the sim resource allocator.
   * 
   * @return the sim resource allocator
   */
  public ISimulationResourceAllocator getSimResourceAllocator() {
    ParameterBlock allocParams =
        execParams.getSubBlock(ParameterUtils.SIM_RESOURCE_ALLOCATION);
    SimulationResourceAllocatorFactory sraf =
        SimSystem.getRegistry().getFactory(
            AbstractSimulationResourceAllocatorFactory.class, allocParams);
    return sraf.create(allocParams, SimSystem.getRegistry().createContext());
  }

  @Override
  public void finished() {
    modelObservers.clear();
    modelInstrumenters.clear();
    computationTaskObservers.clear();
    computationTaskInstrumenters.clear();
    parameters.clear();
  }

  /**
   * Gets the task config id.
   * 
   * @return the taskConfigID
   */
  private IUniqueID getID() {
    return configurationID;
  }

  @Override
  public IComputationTaskSetup getSetup() {
    return setup;
  }

  /**
   * Returns a defensive copy of a parameter block, or null.
   * 
   * @param paramBlock
   *          the param block
   * 
   * @return the parameter block
   */
  private ParameterBlock defensiveCopy(ParameterBlock paramBlock) {
    if (paramBlock == null) {
      return null;
    }
    return paramBlock.getCopy();
  }

  @Override
  public final IUniqueID getConfigurationID() {
    return configurationID;
  }

}
