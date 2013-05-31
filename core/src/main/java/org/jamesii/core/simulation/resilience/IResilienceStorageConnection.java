/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.simulation.resilience;

import java.util.List;

import org.jamesii.core.data.resilience.IDataResilience;
import org.jamesii.core.simulationrun.ISimulationRun;

/**
 * General interface for data collection for the resilience.
 * 
 * @author Thomas Noesinger
 */
public interface IResilienceStorageConnection {

  /**
   * Check if checkpoint is available.
   * 
   * @param simulation
   *          the simulation
   * 
   * @return true, if successful
   */
  boolean checkIfCheckpointIsAvailable(ISimulationRun simulation);

  /**
   * Gets the resilience information.
   * 
   * @param simulation
   *          the simulation
   * 
   * @return the resilience information
   */
  List<ResilienceCheckpointInformation> getResilienceInformation(
      ISimulationRun simulation);

  /**
   * Sets the resilience information.
   * 
   * @param data
   *          the new resilience information
   */
  void setResilienceInformation(ResilienceSimulationInformation data);

  /**
   * Sets the data storage.
   * 
   * @param dataStorage
   *          the new data storage
   */
  void setDataStorage(IDataResilience dataStorage);

  /**
   * Gets the data storage.
   * 
   * @return the data storage
   */
  IDataResilience getDataStorage();

  /**
   * Shut down.
   */
  void shutDown();

  /**
   * Validate checkpoint.
   * 
   * @param simulation
   *          the simulation
   * @param time
   *          the time
   * 
   * @return true, if successful
   */
  boolean validateCheckpoint(ISimulationRun simulation, double time);
}
