/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.simulationserver;

import java.rmi.RemoteException;
import java.util.List;

import org.jamesii.core.distributed.IControlSimulationRun;
import org.jamesii.core.distributed.masterserver.IMasterServer;
import org.jamesii.core.distributed.partition.Partition;
import org.jamesii.core.experiments.RunInformation;
import org.jamesii.core.experiments.SimulationRunConfiguration;
import org.jamesii.core.experiments.tasks.ComputationTaskIDObject;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.processor.IProcessor;
import org.jamesii.core.remote.hostcentral.rmi.IRemoteCommunicationCenter;
import org.jamesii.core.services.TriggerableByName;
import org.jamesii.core.simulation.distributed.NeighbourInformation;
import org.jamesii.core.simulation.resilience.ResilienceSimulationInformation;
import org.jamesii.core.simulationrun.SimulationRun;
import org.jamesii.core.util.id.IUniqueID;

/**
 * Any server implementing this interface provides the service of simulation run
 * execution.
 * 
 * @author Jan Himmelspach
 */
public interface ISimulationServer extends ISimulationHost,
    IControlSimulationRun {

  /**
   * Initialize a simulation run.
   * 
   * @param config
   *          the simulation run configuration, i.e., model source and
   *          parameters to be used.
   * @param simName
   *          the name to be used for the simulation run
   * @param bookedResources
   *          the number of additional resources to be used, null if none are
   *          available
   * 
   * @return the run information
   * 
   * @throws RemoteException
   *           the remote exception
   * 
   * @see org.jamesii.core.simulationrun.SimulationRun
   */
  RunInformation initializeSimulationRun(SimulationRunConfiguration config,
      String simName, List<ISimulationHost> bookedResources)
      throws RemoteException;

  /**
   * Gets the load.
   * 
   * @return the load
   * 
   * @throws RemoteException
   *           the remote exception
   */
  double getLoad() throws RemoteException;

  /**
   * Server abort. Used to signal an abort of the server.
   * 
   * @throws RemoteException
   *           the remote exception
   */
  void serverAbort() throws RemoteException;

  /**
   * Stop processor
   * 
   * @param uid
   *          , the uid
   * 
   * @throws RemoteException
   *           the remote exception
   */
  @TriggerableByName(parameterDescription = { "simulation's unique ID" })
  void stopProc(ComputationTaskIDObject uid) throws RemoteException;

  /**
   * Gets the partition used as base for a simulation run.
   * 
   * @param uid
   *          , the uid of the simulation run
   * 
   * @return the partition
   * 
   * @throws RemoteException
   *           the remote exception
   */
  Partition getPartition(ComputationTaskIDObject uid) throws RemoteException;

  /**
   * Stop <b>ALL</b> simulation runs.
   * 
   * @throws RemoteException
   *           the remote exception
   */
  void stopSimulationRuns() throws RemoteException;

  /**
   * Start a simulation run.
   * 
   * @param uid
   *          the uid of the simulation run to be started
   * 
   * @throws RemoteException
   *           the remote exception
   */
  void startSimulationRun(ComputationTaskIDObject uid) throws RemoteException;

  /**
   * Gets the server log.
   * 
   * @return the server log
   * 
   * @throws RemoteException
   *           the remote exception
   */
  List<String> getServerLog() throws RemoteException;

  /**
   * Checks if the server is is busy, i.e., most often if it is already
   * computing a simulation run.
   * 
   * @return true, if is busy
   * 
   * @throws RemoteException
   *           the remote exception
   */
  boolean isBusy() throws RemoteException;

  /**
   * Gets the master server. Each simulation server needs to be registered at a
   * master server. This master server serves the simulation run jobs to this
   * server.
   * 
   * @return the server
   * 
   * @throws RemoteException
   *           the remote exception
   */
  IMasterServer getMasterServer() throws RemoteException;

  /**
   * Unregister from the master server. Should be executed by the server as soon
   * as it closes down.
   * 
   * @throws RemoteException
   *           the remote exception
   */
  void unregister() throws RemoteException;

  /**
   * Checks if is registered at a master server.
   * 
   * @return true, if is registered
   * 
   * @throws RemoteException
   *           the remote exception
   */
  boolean isRegistered() throws RemoteException;

  /**
   * Gets the master server address.
   * 
   * @return the master server address
   * 
   * @throws RemoteException
   *           the remote exception
   */
  String getMasterServerAdress() throws RemoteException;

  /**
   * Finalize. Clean up.
   * 
   * @throws RemoteException
   *           the remote exception
   */
  void shutDown() throws RemoteException;

  /**
   * Register this simulation server at a master server. The address of the
   * master server to be registered at has to be given in a RMI compatible
   * style.
   * 
   * @param serverAdress
   *          the server adress
   * 
   * @throws RemoteException
   *           the remote exception
   */
  void register(String serverAdress) throws RemoteException;

  /**
   * Initiates the migration of a processor (Support for load balancing).
   * 
   * @param modelFullName
   *          the model full name
   * @param targetHost
   *          the target host
   * 
   * @return true, if migrate processor
   * 
   * @throws RemoteException
   *           the remote exception
   */
  boolean migrateProcessor(String modelFullName, ISimulationHost targetHost)
      throws RemoteException;

  /**
   * Receives a new simulation part (Support for load balancing).
   * 
   * @param simulation
   *          the simulation
   * @param p
   *          the p
   * 
   * @return true, if receive simulation part
   * 
   * @throws RemoteException
   *           the remote exception
   */
  boolean receiveSimulationPart(SimulationRun simulation, IProcessor p)
      throws RemoteException;

  /**
   * Manages the storage of the given data for the resilience.
   * 
   * @param data
   *          the data
   * 
   * @throws RemoteException
   *           the remote exception
   */
  void storeResilienceData(ResilienceSimulationInformation data)
      throws RemoteException;

  /**
   * Creates the simulation.
   * 
   * @param partition
   *          the partition
   * @param neighbourInformation
   *          (the information about models and processors in the neighbourhood,
   *          these are models to which links/connections from a model exist)
   * @param config
   *          the simulation configuration
   * 
   * @return the neighbour information
   * 
   * @throws RemoteException
   *           the remote exception
   */
  NeighbourInformation createSimulation(Partition partition,
      NeighbourInformation neighbourInformation,
      SimulationRunConfiguration config) throws RemoteException;

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
   * Gets the remote communication center.
   * 
   * @param uniqueID
   *          the unique id
   * @param params
   *          the params
   * 
   * @return the remote communication center
   * @throws RemoteException
   */
  IRemoteCommunicationCenter getRemoteCommunicationCenter(IUniqueID uniqueID,
      ParameterBlock params) throws RemoteException;

}
