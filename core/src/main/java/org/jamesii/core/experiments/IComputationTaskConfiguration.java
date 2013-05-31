/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.jamesii.core.data.storage.IDataStorage;
import org.jamesii.core.data.storage.plugintype.DataStorageFactory;
import org.jamesii.core.distributed.masterserver.IMasterServer;
import org.jamesii.core.experiments.tasks.ComputationTaskIDObject;
import org.jamesii.core.experiments.tasks.setup.IComputationTaskSetup;
import org.jamesii.core.experiments.tasks.stoppolicy.plugintype.ComputationTaskStopPolicyFactory;
import org.jamesii.core.observe.IObservable;
import org.jamesii.core.observe.IObserver;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.id.IUniqueID;

/**
 * The Interface IComputationTaskConfiguration. All configurations have to be
 * serializable so that they can be transferred to different machines.
 */
public interface IComputationTaskConfiguration extends Serializable {

  /**
   * Gets the number of this configuration. Each configuration should return a
   * unique number.
   * 
   * @return the configuration number of the task (>= 0 <= number of
   *         configurations to be executed)
   */
  int getNumber();

  /**
   * Checks whether to use master server.
   * 
   * @return true, if a master server shall be / is being used.
   */
  boolean useMasterServer();

  /**
   * Gets the master server.
   * 
   * @return the master server
   */
  IMasterServer getMasterServer();

  /**
   * Gets the experiment id.
   * 
   * @return the experiment id
   */
  IUniqueID getExperimentID();

  /**
   * Gets the configuration id.
   * 
   * @return the configuration id
   */
  IUniqueID getConfigurationID();

  /**
   * Gets the computation task id. This is the ID generated for the computation
   * task created based on this configuration. The number of the configuration
   * can be retrieved by using {@link #getNumber()}.
   * 
   * @return the computation task id.
   */
  ComputationTaskIDObject getComputationTaskID();

  /**
   * Gets the data storage parameters.
   * 
   * @return the data storage parameters
   */
  ParameterBlock getDataStorageParameters();

  /**
   * Gets the class of the data storage factory.
   * 
   * @return the data storage factory
   */
  Class<? extends DataStorageFactory> getDataStorageFactoryClass();

  /**
   * Tests whether this configuration shall be executed interactively.
   * 
   * @return true if interactive execution is demanded, otherwise false
   */
  boolean isInteractive();

  /**
   * Tests whether the configuration shall be started in paused mode. Thus it is
   * ready to run but needs an external trigger to start.
   * 
   * @return the start paused
   */
  boolean isStartPaused();

  /**
   * Gets the task setup. This setup will be used to create the instance of the
   * computation task.
   * 
   * @return the setup
   */
  IComputationTaskSetup getSetup();

  /**
   * Method that clears all references in case the simulation run has finished.
   */
  void finished();

  /**
   * Gets the simulation observers.
   * 
   * @return the simulation observers
   */
  List<IObserver<? extends IObservable>> getComputationTaskObservers();

  /**
   * Gets the model observers.
   * 
   * @return the model observers
   */
  List<IObserver<? extends IObservable>> getModelObservers();

  /**
   * Gets the computation's stop policy parameters. They will be used together
   * with the factory class returned by {@link #getStopPolicyFactoryClass()}.
   * 
   * @return parameters for the stop policy factory
   */
  ParameterBlock getStopPolicyParameters();

  /**
   * Gets the class of the stop policy factory. This class will be instantiated
   * and used to create an instance of the stopping policy to stop the
   * computation of the task. As parameters the parameters returned by
   * {@link #getStopPolicyParameters()} will be used.
   * 
   * @return the class of the stop policy factory
   */
  Class<? extends ComputationTaskStopPolicyFactory> getStopPolicyFactoryClass();

  /**
   * Gets the model reader params.
   * 
   * @return the model reader params
   */
  ParameterBlock getModelReaderParams();

  /**
   * Gets the abstract model reader factory params.
   * 
   * @return the abs model reader factory params
   */
  ParameterBlock getAbsModelReaderFactoryParams();

  /**
   * Gets the parameters.
   * 
   * @return the parameters
   */
  Map<String, ?> getParameters();

  /**
   * Gets the exec params.
   * 
   * @return the exec params
   */
  ParameterBlock getExecParams();

  /**
   * Creates the data storage, using the data storage factory and parameters.
   * 
   * @return the data storage, or null if no factory and parameters are given
   */
  IDataStorage createDataStorage();

}
