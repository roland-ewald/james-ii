/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.simulationserver;

import java.rmi.RemoteException;
import java.util.List;

import org.jamesii.core.experiments.SimulationRunConfiguration;
import org.jamesii.core.experiments.tasks.ComputationTaskIDObject;
import org.jamesii.core.hosts.system.IMSSystemHost;
import org.jamesii.core.hosts.system.IMSSystemHostInformation;
import org.jamesii.core.model.IModel;
import org.jamesii.core.simulationrun.ISimulationRun;

/**
 * The Interface ISimulationHost.
 * 
 * FIXME: What is the difference to {@link ISimulationServer}?
 * 
 * @author Jan Himmelspach
 * @version 1.0
 */
public interface ISimulationHost extends IMSSystemHost,
    IMSSystemHostInformation {

  /**
   * Creates the simulation.
   * 
   * @param model
   *          the instantiated model
   * @param simConfig
   *          the simulation configuration
   * 
   * @return the i simulation run
   * 
   * @throws RemoteException
   *           the remote exception
   */
  ISimulationRun createSimulation(IModel model,
      SimulationRunConfiguration simConfig) throws RemoteException;

  /**
   * Get a list of information about running simulations.
   * 
   * @return list with simulationInfoObjects
   * 
   * @throws RemoteException
   *           the remote exception
   */
  List<ComputationTaskIDObject> getRunningSimulations() throws RemoteException;
}
