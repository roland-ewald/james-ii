/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.hosts.database;

import java.io.Serializable;
import java.util.List;

import org.jamesii.core.data.experiment.ExperimentInfo;
import org.jamesii.core.data.storage.DataStorageException;

/**
 * The Interface representing database servers.
 */
public interface IDatabaseServer {

  /**
   * Batchwise denotes, whether the iven parameter may comprise more than one
   * data set to be stored. The handling is the taks of the DatabaseServer
   * object.
   * 
   * @param entries
   *          the entries
   * 
   * @throws DataStorageException
   *           the data storage exception
   */
  void updateBatchwise(Serializable entries);

  /**
   * Enquire.
   * 
   * @param query
   *          the query
   * 
   * @return the serializable
   * 
   * @throws DataStorageException
   *           the data storage exception
   */
  Serializable enquire(SimulationResultDataQuery query);

  /**
   * Write experiment information.
   * 
   * @param expid
   *          The ID of the experiment, you want the ExperimentItem to be
   *          associated with. <br>
   * @param info
   *          The ExperimentItem you want to store. <br>
   *          Please note, it is not recommended to let this parameter reference
   *          null, because there won't be anything to store. However, it will
   *          be not forbidden.
   * 
   * @throws DataStorageException
   *           the data storage exception
   */
  void writeExperimentInformation(long expid, ExperimentInfo info);

  /**
   * Gets the experiment information.
   * 
   * @param expid
   *          the expid
   * 
   * @return the experiment information
   */
  ExperimentInfo getExperimentInformation(long expid);

  /**
   * Read attributes.
   * 
   * @param expid
   *          the expid
   * @param simid
   *          the simid
   * @param dataid
   *          the dataid
   * 
   * @return the array list< string>
   */
  List<String> readAttributes(long expid, long simid, long dataid);

  /**
   * New simulation.
   * 
   * @return the long
   * 
   * @throws DataStorageException
   *           the data storage exception
   */
  long newSimulation();

  /**
   * New experiment.
   * 
   * @return the long
   * 
   * @throws DataStorageException
   *           the data storage exception
   */
  long newExperiment();

}
