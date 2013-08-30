/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.storage;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.jamesii.core.data.experiment.ExperimentInfo;
import org.jamesii.core.data.runtime.SystemInformation;
import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.util.id.IUniqueID;
import org.jamesii.core.util.info.JavaInfo;

/**
 * General data storage interface. All data storage types must implement this
 * interface.
 * 
 * Commonly used variables
 * <table>
 * <tr>
 * <td>expId</td>
 * <td>You can use a data storage to store results from any number of
 * simulations, each one has an own id. Is computed using the
 * {@link #setExperimentID(IUniqueID)} method.</td>
 * </tr>
 * <tr>
 * <td>confId</td>
 * <td>You can use a data storage to store results from any number of
 * configurations, each one has an own id. Is computed using the
 * {@link #setConfigurationID(IUniqueID, IUniqueID)} method.</td>
 * </tr>
 * <tr>
 * <td>compTaskId</td>
 * <td>Per experiment you can have any number of computation tasks, each one has
 * an own id. Is computed using the
 * {@link #setComputationTaskID(IUniqueID, IUniqueID, IUniqueID)} method.</td>
 * </tr>
 * 
 * </table>
 * 
 * The type of the InternalID to be passed here is used throughout the data
 * storage to identify experiments, configurations, and computation runs in the
 * data storage. Further on it used in many methods to identify what to read /
 * where to write to. Background: It is assumed (and guaranteed by the
 * corresponding classes of the framework) that each experiment, configuration,
 * and replication has a unique ID. This unique ID has to be used in
 * implementations of this interface to compute the corresponding internal ids.
 * Internal ids for configurations and computation tasks do not need to be
 * unique over all experiments, i.e., they might range per experiment /
 * configuration from 0 or 1 up to the number of configurations / computations
 * per configuration. However, in principal it would be ok as well to simply
 * return the unique id in implementations of the setters specified in here. The
 * internal ids had been introduced to provide a leaner access, thus to avoid
 * that a data storage has always to deal with the rather complex unique ids per
 * write / read operation.
 * 
 * @author Jan Himmelspach
 * @author Thomas Nösinger
 * @author Sebastian Lieske
 * @author Roland Ewald
 */
public interface IDataStorage<InternalID extends Serializable> {

  /**
   * Set the experiment UID - and return an id for the experiment used
   * internally.
   * 
   * Each data storage instance can only be used for a single experiment which
   * is identified by the unique id passed. If the UID is unknown to the data
   * storage it should create a corresponding entry internally.
   * 
   * @param id
   *          the experiment UID to be set
   * 
   * @return the internally used experiment id
   */
  InternalID setExperimentID(IUniqueID id);

  /**
   * Get the uid for a long experiment id.
   * 
   * @param id
   * @return
   */
  IUniqueID getExperimentUID(InternalID id);

  /**
   * Signal that the experiment with the expID has been completed. Data storages
   * might not accept any data transferred to the datastorage for the experiment
   * after this method has been called. Further on there might be methods
   * relying on the assumption that all the data of the experiment has been
   * stored.
   * 
   * @param expID
   *          the internal id used in the database to identify the experiment
   */
  void experimentDone(IUniqueID expID);

  /**
   * Get the number of experiments which have been executed using this data
   * sink.
   * 
   * @return the number of experiments
   */
  long getNumberOfExperiments();

  /**
   * Read the ids of the experiments which have been stored in the associated
   * data sink. If no experiments are deleted this will most likely be a list
   * containing number from 1 to n.
   * 
   * @return the list of experiment ids
   */
  List<InternalID> readExperimentIDs();

  /**
   * Write the experiment setup to the data sink.
   * @param experiment
   */
  void storeExperiment(BaseExperiment experiment);

  /**
   * Write experiment information.
   * 
   * @param expID
   *          the internal id used in the database to identify the experiment
   * @param info
   */
  void writeExperimentInformation(InternalID expID, ExperimentInfo info);

  /**
   * Gets the experiment information. An "experiment information" contains
   * important information about an experiment.
   * 
   * @param expid
   *          the if of the experiment the information shall be stored about
   * 
   * @return the experiment information
   */
  ExperimentInfo getExperimentInformation(InternalID expid);

  /**
   * Write meta information about the current experiment into the data sink.
   * 
   * @param expId
   *          the internal id used in the database to indentify the experiment
   * @param machineID
   * @param jamesVersion
   * @param java
   */
  void writeExperimentSystemInformation(IUniqueID expId, long machineID,
      String jamesVersion, JavaInfo java);

  /**
   * Gets the experiment system information.
   * 
   * @param expid
   *          the internal id used in the database to identify the experiment
   * 
   * @return the experiment system information
   */
  SystemInformation getExperimentSystemInformation(InternalID expid);

  /**
   * Set the configuration id. For each configuration an internal id can be
   * created. Further on calls to this method are used to relate configuration
   * and experiment.
   * 
   * @param expUID
   *          The uid of the experiment the configuration belongs to. If null is
   *          passed some data sources might require that
   *          {@link #setExperimentID(IUniqueID)} has been executed before.
   * @param configUID
   *          The unqiue id of a new or already existing configuration
   * @return
   */
  InternalID setConfigurationID(IUniqueID expUID, IUniqueID configUID);

  /**
   * Signal that the configuration has been completely computed (i.e., that all
   * (or the only) replication(s) have been computed. If this method is called
   * processes working on the data storage immediately afterwards might expect
   * that the data has been written.
   * 
   * @param configurationID
   */
  void configurationDone(IUniqueID configurationID);

  /**
   * Get the number of configurations computed in the experiment passed. Per
   * configuration any number of replications might have been computed.
   * 
   * @param expid
   *          the internal id used in the database to indentify the experiment
   * @return number of configurations computed
   */
  long getNumberOfConfigurations(InternalID expid);

  /**
   * Read the configuration ids.
   * 
   * @param expid
   *          the internal id used in the database to indetify the experiment
   * @return
   */
  List<InternalID> readConfigurationIDs(InternalID expid);

  /**
   * The method should write necessary information into the data storage.
   * 
   * @param expId
   *          the internal id used in the database to identify the experiment
   * @param configID
   *          the internal id used in the database to identify the configuration
   * @param paramMap
   * 
   * @param <D>
   */
  <D> void writeParameterMap(InternalID expID, InternalID configID,
      Map<Long, List<D>> paramMap);

  /**
   * Set the computation task ID.
   * 
   * A computation task is identified throughout the framework by a well defined
   * uid. This uid is used in the data storages as well - however, the data
   * storages may internally use a different unique id to represent the
   * computation task, but each data sink should be able to map a framework wide
   * computation task uid to the internal uid. <br>
   * If this method is called, and if the uid is unknown so far, the sink has to
   * create a new entry for the computation task identified by the uid. If the
   * uid is already known the existing data has to get active - thus we select
   * the passed run as the currently active one where read and write accesses
   * will go to (if not a different simid is specified). <br>
   * If the data sink caches data internally it should empty the cache BEFORE
   * the current computation task is exchanged. <br>
   * The method may return the internally used id, but it does not have to.
   * 
   * @param expUID
   *          The uid of the experiment the configuration belongs to. If null is
   *          passed some data sources might require that
   *          {@link #setExperimentID(IUniqueID)} has been executed before.
   * 
   * @param configUID
   *          The uid of the configuration the computation belongs to. If null
   *          is passed some data sources might require that
   *          {@link #setConfigurationID(IUniqueID, IUniqueID)} has been
   *          executed before.
   * @param uid
   *          the computation task UID to be used.
   * 
   * @return the ID used internally or null
   */
  InternalID setComputationTaskID(IUniqueID expUID, IUniqueID configUID,
      IUniqueID uid);

  /**
   * Get the uid for a long computation task id.
   * 
   * @param expId
   *          the internal id used in the database to identify the experiment
   * @param confId
   *          the internal id used in the database to identify the configuration
   * @param id
   * 
   * @return
   */
  IUniqueID getComputationTaskUID(InternalID expId, InternalID confId,
      InternalID id);
  
  /**
   * To signal that the computation of a single computation (often a replication
   * of a configuration in an experiment) has been done.
   * 
   * @param taskID
   */
  void computationTaskDone(IUniqueID taskID);

  /**
   * Return the number of simulation runs which have been executed within the
   * given experiment id. Consequently you can use the number up to the count to
   * access the different runs.
   * 
   * @param expid
   *          the internal id used in the database to identify the experiment
   * @param configid
   *          the internal id used in the database to identify the configuration
   * 
   * @return the number of simulation runs
   */
  long getNumberOfComputations(InternalID expid, InternalID configid);

  /**
   * Read the computation ids.
   * 
   * @param expid
   *          the internal id used in the database to identify the experiment
   * @param configid
   *          the internal id used in the database to identify the configuration
   * @return the list of ids used internally to represent the computations of
   *         the experiment and configuration passed
   */
  List<InternalID> readComputationIDs(InternalID expid, InternalID configid);
  
  // /**
  // * Write simulation run information.
  // * @param expID TODO
  // * @param info
  // */
  // void writeRunInformation(Long expID, Long simId, Object o);

  /**
   * The method should set the bufferSize. The bufferSize should indicate how
   * many values are collected before they are stored. If the bufferSize is
   * changed during the simulation, all collected data should be stored before.
   * 
   * @param bufferSize
   */
  void setBufferSize(long bufferSize);

  /**
   * The method should store the last written data at the end of a computation
   * (data which has not been stored before). *
   * 
   * @throws Exception
   *           on failure
   */
  void flushBuffers();

}
