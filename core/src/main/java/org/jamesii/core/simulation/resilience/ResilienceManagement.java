/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.simulation.resilience;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.base.Entity;
import org.jamesii.core.data.resilience.IDataResilience;
import org.jamesii.core.data.resilience.recover.AbstractRecoverModelFactory;
import org.jamesii.core.data.resilience.recover.RecoverModel;
import org.jamesii.core.data.resilience.recover.RecoverModelFactory;
import org.jamesii.core.distributed.masterserver.IMasterServer;
import org.jamesii.core.distributed.simulationserver.ISimulationHost;
import org.jamesii.core.experiments.tasks.ComputationTaskIDObject;
import org.jamesii.core.model.IModel;
import org.jamesii.core.simulationrun.ISimulationRun;

/**
 * Data structures and resilience management methods.
 * 
 * @author Jan Himmelspach
 */
public class ResilienceManagement extends Entity {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -4215145307770152794L;

  /** Object of the storage for the resilience. */
  private ResilienceStorageConnection resilienceCollect = null;

  /** A reference to the resilience. */
  private final Map<ComputationTaskIDObject, Resilience> usedResilience =
      new HashMap<>();

  /**
   * Shut down.
   */
  public void shutDown() {
    for (Map.Entry<ComputationTaskIDObject, Resilience> e : usedResilience
        .entrySet()) {
      e.getValue().setStop();
    }
    if (resilienceCollect != null) {
      resilienceCollect.shutDown();
    }
  }

  /**
   * Checks for resilience.
   * 
   * @param simulation
   *          the simulation
   * 
   * @return true, if checks for resilience
   */
  public boolean hasResilience(ISimulationRun simulation) {
    return usedResilience.containsKey(simulation.getUniqueIdentifier());
  }

  /**
   * Gets the resilience.
   * 
   * @param simulation
   *          the simulation
   * 
   * @return the resilience
   */
  public Resilience getResilience(ISimulationRun simulation) {
    return usedResilience.get(simulation.getUniqueIdentifier());
  }

  /**
   * Extract resilience.
   * 
   * @param simulation
   *          the simulation
   * 
   * @return the resilience
   */
  public Resilience extractResilience(ISimulationRun simulation) {
    Resilience result = usedResilience.get(simulation.getUniqueIdentifier());
    usedResilience.remove(simulation.getUniqueIdentifier());
    return result;
  }

  /**
   * Checks for check point.
   * 
   * @param simulation
   *          the simulation
   * 
   * @return true, if checks for check point
   */
  public boolean hasCheckPoint(ISimulationRun simulation) {
    return resilienceCollect.checkIfCheckpointIsAvailable(simulation);
  }

  /**
   * Gets the data storage.
   * 
   * @return the data storage
   */
  public IDataResilience getDataStorage() {
    return resilienceCollect.getDataStorage();
  }

  /**
   * Adds the simulation.
   * 
   * @param server
   *          the server
   * @param compTaskID
   *          the simulation
   * @param hosts
   *          the hosts
   */
  public void addSimulation(IMasterServer server,
      ComputationTaskIDObject compTaskID, List<ISimulationHost> hosts) {
    Resilience res = new Resilience(server, compTaskID, hosts, 0, 10000);
    usedResilience.put(compTaskID, res);
    changed(compTaskID);
  }

  /**
   * Store data.
   * 
   * @param data
   *          the data
   */
  public void storeData(ResilienceSimulationInformation data) {
    if (data == null) {
      SimSystem.report(Level.SEVERE, "No data given.");
    } else {
      resilienceCollect.setResilienceInformation(data);
    }
  }

  /**
   * Retrieve model.
   * 
   * @param serverName
   *          the server name
   * @param simUID
   *          the sim uid
   * 
   * @return the i model
   */
  public IModel retrieveModel(String serverName, long simUID) {
    RecoverModelFactory factory =
        SimSystem.getRegistry().getFactory(AbstractRecoverModelFactory.class,
            null);

    RecoverModel recoverModel = (RecoverModel) factory.createRecoverModel(null);
    return recoverModel.recoverModel(getDataStorage(), serverName, simUID);
  }

  /**
   * Cancel resilience.
   * 
   * @param simulation
   *          the simulation
   */
  public void cancelResilience(ISimulationRun simulation) {
    if (!hasResilience(simulation)) {
      return;
    }
    Resilience simRes = extractResilience(simulation);
    simRes.setStop();
    changed(simulation);
  }

  /**
   * Creates the resilience storage connection.
   * 
   * @param server
   *          the server
   */
  public void createResilienceStorageConnection(IMasterServer server) {
    resilienceCollect = new ResilienceStorageConnection(server);
  }

}
