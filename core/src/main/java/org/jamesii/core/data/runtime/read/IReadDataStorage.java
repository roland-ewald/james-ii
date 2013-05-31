/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */

package org.jamesii.core.data.runtime.read;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.jamesii.core.util.misc.Quadruple;

/**
 * The Interface IReadDataStorage.
 * 
 * Commonly used variables
 * <table>
 * <tr>
 * <td>expId</td>
 * <td>You can use a data storage to store results from any number of
 * simulations, each one has an own id.</td>
 * </tr>
 * <tr>
 * <td>simId</td>
 * <td>Per experiment you can have any number of simulation runs, each one has
 * an own id. They should start at 0 for each experiment.</td>
 * </tr>
 * <tr>
 * <td>dataId</td>
 * <td>Identifies a simulation object</td>
 * </tr>
 * <tr>
 * <td>attribute</td>
 * <td>Each object identified by a dataId can have several attributes</td>
 * </tr>
 * 
 * </table>
 * 
 * @author Jan Himmelspach
 * @author Thomas Nösinger
 * @author Sebastian Lieske
 * @author Roland Ewald
 */
public interface IReadDataStorage<InternalID extends Serializable> extends
    IReadDataStorageSingleExperiment<InternalID>,
    IReadDataStorageSingleComputation {

  /**
   * Reads the data for a single attribute of a single object from the given
   * simulation run in the given experiment. Returns all values assigned to the
   * attribute stored in the data sink so far.
   * 
   * @param <D>
   * 
   * @param expid
   *          the expid of the experiment the data shall be retrieved from
   * @param configid
   *          the id of the configuration
   * @param taskid
   *          the simid of the simulation run the data shall be retrieved from
   * @param dataid
   *          the dataid of the object the data shall be retrieved from
   * @param attrib
   *          the attrib - the attribute the data shall be retrieved from
   * @return the map< double, list< d>>
   */
  <D> Map<Double, List<D>> readData(InternalID expid, InternalID configid,
      InternalID taskid, long dataid, String attrib);

  /**
   * Read all ids stored in the given simulation run of the given experiment.
   * 
   * @param expid
   *          ID of the experiment
   * @param configid
   *          the id of the configuration
   * @param taskid
   *          ID of the simulation run
   * @return a(n empty) list of data ids or null if experiment or simulation
   *         does not exist
   */
  List<Long> readDataIDs(InternalID expid, InternalID configid,
      InternalID taskid);

  /**
   * Read the list of attributes stored for the given dataid in simulation run
   * simid in experiment expid.
   * 
   * @param expid
   *          ID of the experiment
   * @param configid
   *          the id of the configuration
   * @param taskid
   *          ID of the simulation run
   * @param dataid
   *          ID of the data
   * @return a not empty list of attributes or null if experiment, simulation or
   *         dataid does not exist
   */
  List<String> readAttributes(InternalID expid, InternalID configid,
      InternalID taskid, long dataid);

  /**
   * Read the data in the interval [start, end[. End is exclusive, so it can be
   * used in a subsequent call as start value.
   * 
   * You can a combination of
   * {@link #countData(Serializable, Serializable, Serializable, Double, Double)}
   * and this method to try to read certain number of entries (e.g., for caching
   * purposes). I.e., you can try to read 10,000 entries by first finding the
   * time interval which comprises approx. 10,000 entries by successively
   * applying the countData method (and using a binary search). However, you
   * might end up with the need to read more than 10,000 entries at once: if
   * there are more than 10,000 entries for the next simulation time step.
   * 
   * @param <D>
   * 
   * @param expID
   *          ID of the experiment
   * @param configid
   *          the id of the configuration
   * @param taskID
   *          ID of the computation task
   * @param start
   *          the start
   * @param end
   *          the end
   * @return list of tuples (time, data_key, attrib_name, value)
   */
  <D> List<Quadruple<Double, Long, String, D>> readData(InternalID expID,
      InternalID configid, InternalID taskID, Double start, Double end);

  /**
   * Count the data in the interval [start, end[. End is exclusive, so it can be
   * used in a subsequent call as start value.
   * 
   * @param expID
   *          ID of the experiment
   * @param configid
   *          the id of the configuration
   * @param taskid
   *          the task id
   * @param start
   *          the start
   * @param end
   *          the end
   * @return the long
   */
  Long countData(InternalID expID, InternalID configid, InternalID taskid,
      Double start, Double end);

}
