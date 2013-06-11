/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.runtime.write;

import org.jamesii.core.base.IEntity;

/**
 * The Interface IWriteDataStorageSingleComputation. Write methods for a fixed
 * computation task in a fixed experiment. This means experiment and run have to
 * be pre-selected (known by the implementing class)! <br>
 * The data schema available through this interface is very general, but it has
 * to be usable for any m&s paradigm including distributed simulation
 * executions. Due to the generality you need to find your own mapping of your
 * trajectory data to this interface. But therefore you get the possibility to
 * use any data sink implementing this interface. By this interface it is not
 * defined how the data is written into the actual data sink - this is
 * completely left over to the implementation.
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
public interface IWriteDataStorageSingleComputation {

  /**
   * Write data. Write the data value of the attribute of the object identified
   * by the dataId for the timestamp given.
   * 
   * 
   * 
   * The dataID can be used to identify simulation objects, like individuals,
   * species or whatever remains in a simulation for a period of time and of
   * which you'd like to record data. Basically you could then ignore the
   * attribute and just store the corresponding java object by using this
   * method. However, this may hamper the post processing. Because of this data
   * sinks may provide internally better means to treat the basic data types,
   * and thus you should use the attribute's to separately store the single
   * attribute values of a simulation object.<br/>
   * You are not fixed to this interpretation of dataID and attrib at all. You
   * can just map your data schema to these variables. E.g., you could even
   * store hierarchies, dependencies, etc by exploiting the attrib. For example,
   * you could use an attrib "path" which then gets as value s.th. like dataId +
   * "." + dataID + "." ...
   * 
   * @param <D>
   * @param dataID
   *          - object identification
   * @param attrib
   *          - attribute of the object the new (time, data) tuple shall be
   *          stored of
   * @param time
   *          - the time value of the data change to be recorded (MUST not be
   *          +/- Inf or NaN!, use Double.MAX_VALUE intead of +Inf)
   * @param data
   *          - the value to be recorded
   */
  <D> void writeData(long dataID, String attrib, double time, D data);

  /**
   * Write data. Write the data value of the attribute of the object identified
   * by the data entity for the timestamp given. The methods will use internally
   * the data.getId method to obtain a dataId.
   * 
   * @param attrib
   *          - attribute to be written
   * @param time
   *          - the time value of the data change to be recorded (MUST not be
   *          +/- Inf or NaN!, use Double.MAX_VALUE intead of +Inf)
   * @param data
   *          - the entity which shall be stored by using the attribute
   */
  void writeData(String attrib, double time, IEntity data);

}
