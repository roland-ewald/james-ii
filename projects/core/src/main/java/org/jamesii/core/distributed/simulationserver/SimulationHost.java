/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.simulationserver;

/**
 * Title: CoSA: SimulationHost Description: Copyright: Copyright (c) 2003
 * Company: University of Rostock, Faculty of Computer Science Modeling and
 * Simulation group
 * 
 * @author Jan Himmelspach
 * @version 1.0
 */

import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.distributed.partition.Partition;
import org.jamesii.core.experiments.SimulationRunConfiguration;
import org.jamesii.core.experiments.tasks.ComputationTaskIDObject;
import org.jamesii.core.hosts.system.MSSystemHost;
import org.jamesii.core.model.IModel;
import org.jamesii.core.observe.IMediator;
import org.jamesii.core.simulation.distributed.NeighbourInformation;
import org.jamesii.core.simulationrun.ISimulationRun;
import org.jamesii.core.simulationrun.SimulationRun;
import org.jamesii.core.util.exceptions.OperationNotSupportedException;

/**
 * The Class SimulationHost.
 */
public class SimulationHost extends MSSystemHost implements ISimulationHost {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -7474231023840820625L;

  /** The manager managing the simulation runs. */
  private final ComputationTaskManagement taskManager =
      new ComputationTaskManagement();

  /**
   * Instantiates a new simulation host.
   * 
   * @throws RemoteException
   *           the remote exception
   */
  public SimulationHost() throws RemoteException {
    super();
  }

  /**
   * Creates the simulation.
   * 
   * @param model
   *          the model
   * 
   * @return the i simulation run
   * 
   * @throws RemoteException
   *           the remote exception
   */
  public ISimulationRun createSimulation(IModel model) throws RemoteException {
    // System.out.println("ups_remote");
    return this.createSimulation(model, 0, Double.POSITIVE_INFINITY);
  }

  /**
   * Creates the simulation.
   * 
   * @param model
   *          the model
   * @param startTime
   *          the start time
   * @param endTime
   *          the end time
   * 
   * @return the i simulation run
   * 
   * @throws RemoteException
   *           the remote exception
   */
  public ISimulationRun createSimulation(IModel model, double startTime,
      double endTime) throws RemoteException {
    throw new OperationNotSupportedException(
        "SimulationHost, no parameters are forwarded!!!! Config needed");
  }

  @Override
  public ISimulationRun createSimulation(IModel model,
      SimulationRunConfiguration simConfig) throws RemoteException {

    try {
      IModel localModel = model;
      SimulationRun simulation =
          new SimulationRun("sim", localModel, simConfig, null);
      
      SimSystem.report(Level.INFO, "Created computation task for "+model);
      
      taskManager.addComputationTask(simulation, simConfig);
      return simulation;
    } catch (Exception e) {
      SimSystem.report(e);
    }
    return null;
  }

  /**
   * Creates the simulation.
   * 
   * @param part
   *          the part
   * @param neighbourInformation
   *          the neighbour information
   * @param config
   *          the config
   * 
   * @return the simulation run
   */
  public NeighbourInformation createSimulation(Partition part,
      NeighbourInformation neighbourInformation,
      SimulationRunConfiguration config) {

    // model.setModel(null);
    // System.out.println(neighbourModelInformation);
    SimulationRun simulation =
        new SimulationRun(part, "Sim", new ComputationTaskIDObject(),
            neighbourInformation, config);

    SimSystem.report(Level.INFO, "Created computation task for "+part.getModel());
    
    taskManager.addComputationTask(simulation, config);
    // TODO(re027): This would be unnecessary, if the simulation's reference to
    // parts would not be set to null

    return neighbourInformation;
  }

  @Override
  public String getName() {
    return "unnamed simulation host";
  }

  /**
   * Gets the simulation by name.
   * 
   * @param name
   *          the name
   * 
   * @return the simulation by name
   */
  public synchronized ISimulationRun getSimulationByName(String name) {
    return taskManager.getSimulationByName(name);
  }

  /**
   * Gets the simulation by uid.
   * 
   * @param uid
   *          the uid
   * 
   * @return the simulation by uid
   */
  public synchronized ISimulationRun getSimulationByUID(
      ComputationTaskIDObject uid) {
    return (ISimulationRun) taskManager.getComputationTaskByUid(uid);
  }

  /**
   * Removes the simulation.
   * 
   * @param simulation
   *          the simulation
   */
  public synchronized void removeSimulation(ISimulationRun simulation) {
    simulation.stopProcessor();
    taskManager.removeComputationTask(simulation);
    SimSystem.report(Level.WARNING,
        "Stopped simulation (" + simulation.getName() + ")");
  }

  @Override
  public Class<?> getServiceType() {
    return SimulationHost.class;
  }

  @Override
  public int getMaxNumberOfConcurrentJobs() throws RemoteException {
    // Runtime r = Runtime.getRuntime();
    // return r.availableProcessors()-1;
    return 0; // means that we can only execute 1 job at a time
  }

  @Override
  public String getServiceName() throws RemoteException {
    return "Simulation computation server";
  }

  @Override
  public void setManagementMediator(IMediator mediator) throws RemoteException {
    taskManager.setMediator(mediator);
  }

  @Override
  public List<ComputationTaskIDObject> getRunningSimulations()
      throws RemoteException {
    return taskManager.getRunningComputationTasks();
  }

  /**
   * @return the taskManager
   */
  public ComputationTaskManagement getTaskManager() {
    return taskManager;
  }

}
