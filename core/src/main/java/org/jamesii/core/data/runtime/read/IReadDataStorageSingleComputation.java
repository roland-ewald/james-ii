/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.runtime.read;

import java.util.List;
import java.util.Map;

/**
 * The Interface IReadRunDataStorage. Read methods for a fixed simulation run in
 * an experiment. Experiment and run have to be pre-selected.
 * 
 * Explanation of some commonly used variables:
 * <table>
 * <tr>
 * <tr>
 * <td>dataid</td>
 * <td>Identifies a simulation object, e.g., an agent, a molecule, ...</td>
 * </tr>
 * <tr>
 * <td>attribute</td>
 * <td>Each object identified by a dataId can have several attributes, e.g., an
 * agent could have various state variables (amount of money, education, ...), a
 * molecule could have a position and a movement vector</td>
 * </tr>
 * 
 * </table>
 * 
 * @author Jan Himmelspach
 * @author Thomas Nösinger
 * @author Sebastian Lieske
 * @author Roland Ewald
 */
public interface IReadDataStorageSingleComputation {

  /**
   * Read data.
   * 
   * @param <D>
   * 
   * @param dataid
   *          the dataid
   * @param attrib
   *          the attrib
   * 
   * @return the map< double, list< d>>
   */
  <D> Map<Double, List<D>> readData(long dataid, String attrib);

  /**
   * Read all stored data having the id dataid, attribute and simulation time in
   * the data sink. As there can be more than one entry per time point for each
   * of this you get back a list. However, often this list will only contain a
   * single entry.
   * 
   * <b>Note:</b> Depending on the database used the order of entries read back
   * might differ from the writing order!
   * 
   * @param <D>
   * 
   * @param dataid
   *          the dataid
   * @param attrib
   *          the attrib
   * @param time
   *          the time
   * 
   * @return the list< d>
   */
  <D> List<D> readData(long dataid, String attrib, double time);

  /**
   * Read data entries for the simulation object identified by the dataid
   * entirely, i.e., read all entries identified by the dataid, independent from
   * the attribute and time values. You get back a map of the attributes stored
   * for the object, and per attribute a list of values together with the time
   * stamps.
   * 
   * <b>Note:</b> Depending on the database used the order of entries read back
   * might differ from the writing order!
   * 
   * @param <D>
   * 
   * @param dataid
   *          the dataid
   * 
   * @return the map< string, map< double, list< d>>>
   */
  <D> Map<String, Map<Double, List<D>>> readDataEntirely(long dataid);

  /**
   * Read data entries for the simulation object identified by the dataid for
   * the given time stamp entirely, i.e., read all entries identified by the
   * (dataid, time) tuple, independent from the attributes. You get back a map
   * of the attributes stored for the object, and per attribute a list of
   * values.
   * 
   * <b>Note:</b> Depending on the database used the order of entries read back
   * might differ from the writing order!
   * 
   * @param <D>
   * 
   * @param dataid
   *          the dataid
   * @param time
   *          the time
   * 
   * @return the map< string, list< d>>
   */
  <D> Map<String, List<D>> readDataEntirely(long dataid, double time);

  /**
   * Read latest data. Reads the latest (youngest) entry of the attribute given
   * of the object identified by the dataid.
   * 
   * <b>Note:</b> Depending on the database used the order of entries read back
   * might differ from the writing order! This means here that if there is more
   * than one entry with the youngest timestamp for the dataid, attrib
   * combination you will get back one of these. Not necessarily the one written
   * last.
   * 
   * @param <D>
   * 
   * @param dataid
   *          the dataid
   * @param attrib
   *          the attrib
   * 
   * @return the d
   */
  <D> D readLatestData(long dataid, String attrib);

  /**
   * Read latest data entirely.
   * 
   * <b>Note:</b> Depending on the database used the order of entries read back
   * might differ from the writing order! This means here that if there is more
   * than one entry with the youngest timestamp for the dataid combination you
   * will get back one of these. Not necessarily the one written last. You might
   * even end up with a mixture of attribute values from subsequent writes of
   * dataid, timestamp combinations.
   * 
   * @param <D>
   * 
   * @param dataid
   *          the dataid
   * 
   * @return the map< string, d>
   */
  <D> Map<String, D> readLatestDataEntirely(long dataid);

}
