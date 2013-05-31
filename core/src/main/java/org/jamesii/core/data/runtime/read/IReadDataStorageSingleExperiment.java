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

/**
 * The Interface IReadDataStorageSingleExperiment. The experiment needs to be
 * pre-selected.
 * 
 * Commonly used variables
 * <table>
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
public interface IReadDataStorageSingleExperiment<InternalID extends Serializable>
    extends IReadDataStorageSingleComputation {

  /**
   * Return the number of simulation runs. Consequently you can use the number
   * up to the count to access the different runs.
   * 
   * 
   * @return the number of simulation runs
   */
  // long getNumberOfSimulationRuns();
  /**
   * Read data.
   * 
   * @param configid
   *          the id of the configuration
   * @param simid
   *          the simid
   * @param dataid
   *          the dataid
   * @param attrib
   *          the attrib
   * 
   * @param <D>
   * 
   * @return the map< double, list< d>>
   */
  <D> Map<Double, List<D>> readData(InternalID configid, InternalID simid,
      long dataid, String attrib);

  /**
   * Read data.
   * 
   * @param configid
   *          the id of the configuration
   * @param simid
   *          the simid
   * @param dataid
   *          the dataid
   * @param attrib
   *          the attrib
   * @param time
   *          the time
   * 
   * @param <D>
   * 
   * @return the list< d>
   */
  <D> List<D> readData(InternalID configid, InternalID simid, long dataid,
      String attrib, double time);

  /**
   * Read data entries for the simulation object identified by the dataid from
   * the simulation run identified by the simid entirely, i.e., read all entries
   * identified by the dataid, independent from the attribute and time values.
   * You get back a map of the attributes stored for the object, and per
   * attribute a list of values together with the time stamps.
   * 
   * @param configid
   *          TODO
   * @param simid
   *          the simid
   * @param dataid
   *          the dataid
   * 
   * @param <D>
   * 
   * @return the map< string, map< double, list< d>>>
   */
  <D> Map<String, Map<Double, List<D>>> readDataEntirely(InternalID configid,
      InternalID simid, long dataid);

  /**
   * Read data entries for the simulation object identified by the dataid from
   * the simulation run identified by the simid for the given time stamp
   * entirely, i.e., read all entries identified by the (dataid, time) tuple,
   * independent from the attributes. You get back a map of the attributes
   * stored for the object, and per attribute a list of values together with the
   * time stamps.
   * 
   * @param configid
   *          the id of the configuration
   * @param simid
   *          the simid
   * @param dataid
   *          the dataid
   * @param time
   *          the time
   * 
   * @param <D>
   * 
   * @return the map< string, list< d>>
   */
  <D> Map<String, List<D>> readDataEntirely(InternalID configid,
      InternalID simid, long dataid, double time);

  /**
   * Read latest data. That's the latest (youngest) entry for the attribute of
   * the object identified by dataid stored for the simulation run identified by
   * the simid parameter.
   * 
   * @param configid
   *          the id of the configuration
   * @param simid
   *          the simid
   * @param dataid
   *          the dataid
   * @param attrib
   *          the attrib
   * 
   * @param <D>
   * 
   * @return the D
   */
  <D> D readLatestData(InternalID configid, InternalID simid, long dataid,
      String attrib);

  /**
   * Read latest data entirely. That's the latest (youngest) entries for all
   * attributes of the object identified by dataid stored for the simulation run
   * identified by the simid parameter.
   * 
   * @param configid
   *          TODO
   * @param simid
   *          the simid
   * @param dataid
   *          the dataid
   * 
   * @param <D>
   * 
   * @return the map< string, d>
   */
  <D> Map<String, D> readLatestDataEntirely(InternalID configid,
      InternalID simid, long dataid);

}
