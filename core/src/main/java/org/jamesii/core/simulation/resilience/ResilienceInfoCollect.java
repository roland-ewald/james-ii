/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.simulation.resilience;

import java.rmi.RemoteException;
import java.util.List;

import org.jamesii.SimSystem;
import org.jamesii.core.distributed.masterserver.IMasterServer;
import org.jamesii.core.distributed.masterserver.MasterServer;
import org.jamesii.core.distributed.simulationserver.ISimulationHost;
import org.jamesii.core.experiments.tasks.ComputationTaskIDObject;
import org.jamesii.core.util.Semaphore;

/**
 * The class collects data for the resilience.
 * 
 * @author Thomas Noesinger
 * 
 */
public final class ResilienceInfoCollect implements Runnable {

  /**
   * The host where the simulation is executed.
   */
  private ISimulationHost host = null;

  /**
   * The resilience, which call this class.
   */
  private Resilience resilience = null;

  /**
   * The master server.
   */
  private IMasterServer server = null;

  /**
   * The simulation ID.
   */
  private ComputationTaskIDObject simulation = null;

  /** The hosts. */
  private List<ISimulationHost> hosts = null;

  /**
   * A reference to the Semaphore of the resilience.
   */
  private Semaphore wait = null;

  /**
   * The constructor of the class.
   * 
   * @param resilience
   *          the resilience
   * @param simulation
   *          the simulation
   * @param server
   *          the server
   * @param semaphore
   *          the semaphore
   * @param count
   *          the count
   * @param hosts
   *          the hosts
   */
  public ResilienceInfoCollect(Resilience resilience,
      ComputationTaskIDObject simulation, IMasterServer server,
      List<ISimulationHost> hosts, Semaphore semaphore, int count) {
    this.simulation = simulation;
    this.server = server;
    this.resilience = resilience;
    this.wait = semaphore;
    this.hosts = hosts;

    for (int i = 0; i < count; i++) {
      new Thread(this).start();
    }

  }

  /**
   * The method collects the necessary data of a host, which contains to the
   * given simulation.
   * 
   * @param hostnumber
   *          : the current hostnumber
   */
  private void collect(int hostnumber) {

    ResilienceSimulationInformation data =
        new ResilienceSimulationInformation(server, simulation, host);

    int value = hosts.size();

    if (data.setCurrentHostNumber(hostnumber)) {
      if (data.setOverallHostNumber(value)) {
        if (data.setFurtherInformation()) {
          // sends the data to the SimulationMasterServer
          ((MasterServer) server).storeResilienceData(data);
        }
      }
    }

  }

  @Override
  public void run() {
    if (!resilience.getFinished()) {

      int nextHost = resilience.getNextHost();

      while (nextHost != -1) {

        try {
          if (!(Boolean) server.executeRunnableCommand(simulation, "isPausing",
              null)) {
            return;
          }
        } catch (RemoteException e) {
          SimSystem.report(e);
        }

        host = hosts.get(nextHost);
        collect(nextHost);
        nextHost = resilience.getNextHost();
      }
    }

    resilience.infoCollectTerminates();
    if (resilience.getGoOn()) {
      wait.v();
    }
  }
}
