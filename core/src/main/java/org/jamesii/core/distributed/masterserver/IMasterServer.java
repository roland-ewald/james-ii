/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.masterserver;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;

import org.jamesii.core.distributed.IControlSimulationRun;
import org.jamesii.core.distributed.computationserver.IJob;
import org.jamesii.core.distributed.partition.Partition;
import org.jamesii.core.experiments.IComputationTaskConfiguration;
import org.jamesii.core.experiments.taskrunner.IRemoteComputationTaskRunner;
import org.jamesii.core.experiments.taskrunner.InitializedComputationTask;
import org.jamesii.core.experiments.tasks.ComputationTaskIDObject;
import org.jamesii.core.hosts.system.IMSSystemHost;
import org.jamesii.core.services.IService;
import org.jamesii.core.services.ServiceInfo;
import org.jamesii.core.util.id.IUniqueID;

/**
 * The Interface IMasterServer. A master server is responsible for the
 * management of distributed resources. Any distributed resource (aka service)
 * which shall be usable by the class implementing this interface needs to get
 * registered by the {@link #register} method.
 * 
 * @author Jan Himmelspach
 * @version 1.0
 */
public interface IMasterServer extends IMSSystemHost, IControlSimulationRun {

  /**
   * Register a service.
   * 
   * @param service
   *          the service
   * 
   * @throws RemoteException
   *           the remote exception
   */
  void register(IService service) throws RemoteException;

  /**
   * Unregister the given service.
   * 
   * @param service
   *          the service
   * 
   * @throws RemoteException
   *           the remote exception
   */
  void unregister(IService service) throws RemoteException;

  /**
   * Creates and initializes a simulation run.
   * 
   * @param runConfig
   *          simulation run configuration to be initialized
   * @param simRunner
   *          the sim runner used remotely
   * 
   * @return an initializedsimulation run ({@link InitializedComputationTask}),
   *         i.e., several attributes which define the run
   * 
   * @throws RemoteException
   *           the remote exception
   */
  InitializedComputationTask executeSimulationConfiguration(
      IComputationTaskConfiguration runConfig,
      IRemoteComputationTaskRunner simRunner) throws RemoteException;

  /**
   * Initializes a given job on a computation server.
   * 
   * @param <V>
   *          the type parameter of the job which shall be initialized - will be
   *          auto determined
   * 
   * @param job
   *          the job to be executed
   * @return the job's unique id
   * @throws RemoteException
   *           the remote exception
   */
  <V> IUniqueID initializeJob(IJob<V> job) throws RemoteException;

  /**
   * Executes a given job on a computation server.
   * 
   * @param <V>
   *          type of the return value
   * @param id
   *          the job's id
   * @param data
   * @return the job's result
   * @throws RemoteException
   *           the remote exception
   */
  <V> V executeJob(IUniqueID id, Serializable data) throws RemoteException;

  /**
   * Finishes a given job on a computation server.
   * 
   * @param id
   *          the job's id
   * @throws RemoteException
   *           the remote exception
   */
  void finalizeJob(IUniqueID id) throws RemoteException;

  // /**
  // * Creates a simulation run.
  // *
  // *
  // *
  // * @param model the model
  // * @param simConfig the sim config
  // *
  // * @return the simulation run
  // *
  // * @throws RemoteException the remote exception
  // */
  // InitializedSimulation createSimulation(IModel model,
  // SimulationConfiguration simConfig) throws RemoteException;

  /**
   * Get the number of services of the given type.
   * 
   * @param serviceType
   *          the service type
   * 
   * @return the number of registered simulation services
   * 
   * @throws RemoteException
   *           the remote exception
   */
  int getNumberOfRegisteredServices(Class<?> serviceType)
      throws RemoteException;

  /**
   * Gets the registered services.
   * 
   * @return the registered services
   * 
   * @throws RemoteException
   *           the remote exception
   */
  List<ServiceInfo> getRegisteredServices() throws RemoteException;

  /**
   * Gets the registered service types.
   * 
   * @return the registered service types
   * 
   * @throws RemoteException
   *           the remote exception
   */
  List<Class<?>> getRegisteredServiceTypes() throws RemoteException;

  /**
   * Get a property of the simulation run. For properties see
   * {@link org.jamesii.core.simulationrun.ISimulationRun#getProperty(String)}.
   * 
   * @param <D>
   *          the data type of the value returned
   * @param simulationID
   * 
   * @param property
   *          the property
   * 
   * @return the simulation run property value
   * 
   * @throws RemoteException
   *           the remote exception
   */
  <D> D getSimulationRunProperty(ComputationTaskIDObject simulationID,
      String property) throws RemoteException;

  /**
   * Gets the partition.
   * 
   * @param simulationID
   *          the simulation id
   * 
   * @return the partition
   * 
   * @throws RemoteException
   *           the remote exception
   */
  Partition getPartition(ComputationTaskIDObject simulationID)
      throws RemoteException;

  /**
   * Execute a simulation - start, run, and clean up.
   * 
   * @param simRunner
   *          reference to simulation runner (for resilience)
   * @param simulationId
   *          the simulation id
   * 
   * @throws RemoteException
   *           the remote exception
   */
  void execute(ComputationTaskIDObject simulationId,
      IRemoteComputationTaskRunner simRunner) throws RemoteException;

  /**
   * Stop.
   * 
   * @param simulationID
   *          the simulation id
   * 
   * @throws RemoteException
   *           the remote exception
   */
  void stop(ComputationTaskIDObject simulationID) throws RemoteException;

  /**
   * Shut down.
   * 
   * @throws RemoteException
   *           the remote exception
   */
  void shutDown() throws RemoteException;
}
