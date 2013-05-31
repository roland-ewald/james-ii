/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.runtime;

import org.jamesii.core.data.storage.IDataStorage;

/**
 * A connection handler manages the connection to a data storage. Besides
 * opening and closing the connection a handler is responsible for the
 * computation of a proper next experiment / and or simulation id.
 * 
 * @author Jan Himmelspach *
 */
public interface IConnectionHandler {

  /**
   * Compute a new id for a new experiment. Should be called if a new experiment
   * shall be executed. Depending on the datasource several initializations for
   * being able to store a new experiment will be done - not calling this method
   * may result in corrupted data.
   * 
   * @return experimentID
   */
  long newExperiment();

  /**
   * The method returns the next simulationID. This method should be called
   * before a new simulation run is executed. Simulation run data collected
   * without a call to this method between the runs will carry the same simid -
   * and thus you'll not be able to distinguish between the runs.
   * 
   * @param expID
   *          the experiment ID for which a new simulation shall be recorded
   * @return SimID
   */
  long newSimulation(long expID);

  /**
   * Gets a fresh instance of a data storage.
   * 
   * @return the data storage
   */
  IDataStorage dataStorage();

  /**
   * Checks whether the connection is available.
   * 
   * @return true if the connection is available, false otherwise
   */
  boolean isConnected();

}
