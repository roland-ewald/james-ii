/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore;


import java.util.Iterator;

import org.jamesii.core.model.IModel;

import model.devscore.ports.IPort;

/**
 * The Interface IBasicDEVSModel.
 * 
 * This interface is the base interface for all (coupled and atomic) DEVS models
 * based on this modeling formalism implementation. It provides methods required
 * by both types of DEVS models, like maintenance methods for working with
 * ports.<br>
 * Of relatively high importance is the {@link #getFullName()} method which has
 * to retrieve the names of all models from the root model up to this, to get a
 * unique identifier of this model in the overall model tree.
 * 
 * @author Jan Himmelspach
 */
public interface IBasicDEVSModel extends IModel {

  /**
   * Clear all messages from the incoming ports.
   */
  void clearInPorts();

  /**
   * Clear all messages from the outgoing ports.
   */
  void clearOutPorts();

  /**
   * Finalize.
   */
  void finalization();

  /**
   * Returns the fullname of a model. That's the name of all parents (each
   * separated by a dot) and finally the own one: a.b.c.myname
   * 
   * @return the fullname of the model
   * 
   */
  String getFullName();

  /**
   * Get the reference to an inport with this name.
   * 
   * @param name
   *          of the inport
   * 
   * @return reference to the inport if it exists otherwise null @
   */
  IPort getInPort(String name);

  /**
   * Returns an iterator for the in ports.
   * 
   * @return an in port iterator @
   */
  Iterator<IPort> getInPortIterator();

  /**
   * Get the level the model is located at.
   * 
   * @return the level
   */
  int getLevel();

  /**
   * Get the reference to an outport with this name.
   * 
   * @param name
   *          of the outport
   * 
   * @return reference to the outport if it exists otherwise null @
   */
  IPort getOutPort(String name);

  /**
   * Returns an outport iterator.
   * 
   * @return an outport iterator @
   */
  Iterator<IPort> getOutPortIterator();

  /**
   * Returns the parent of this model.
   * 
   * @return ICoupledModel parent @
   */
  IBasicCoupledModel getParent();

  /**
   * Checks whether external inputs exist.
   * 
   * @return true if there is an external input @
   */
  boolean hasExternalInput();

  /**
   * Checks if this model has the given port as one of its in ports.
   * 
   * @param port
   *          which existence should be examined
   * 
   * @return true if this model has the specified port as in port @
   */
  boolean hasInPort(IPort port);

  /**
   * Checks if this model has the given port as one of its out ports.
   * 
   * @param port
   *          which existence should be examined
   * 
   * @return true if this model has the specified port as out port @
   */
  boolean hasOutPort(IPort port);

  /**
   * Checks if this model has the given port.
   * 
   * @param port
   *          which existence should be examined
   * 
   * @return true if this model has the specified port (either as in or out
   *         port) @
   */
  boolean hasPort(IPort port);

  /**
   * Checks if this model has the given port.
   * 
   * @param port
   *          which existence should be examined
   * 
   * @return true if this model has the specified port (either as in or out
   *         port) @
   */
  boolean hasPort(String port);

  /**
   * Initialize.
   * 
   * @param time
   *          simulation start time @
   */
  void initialization(double time);

  /**
   * Read from a model's out port.
   * 
   * @param name
   *          the name
   * 
   * @return the object
   */
  Object readFromOutPort(String name);

  /**
   * Read an object from a specified port.
   * 
   * @param port
   *          which should be read from
   * 
   * @return object which is read @
   */
  Object readPort(IPort port);

  /**
   * Sets the parent model for this model.
   * 
   * @param parent
   *          @
   */
  void setParent(IBasicCoupledModel parent);

  /**
   * This method is called whenever certain values (as the fullName cache) have
   * to be updated (e.g. because the parent has been changed). A call to this
   * method should be allowed at any time.
   * 
   */
  void update();

  /**
   * Writes an object o to a specified port.
   * 
   * @param port
   *          where should be written at
   * @param o
   *          which should be written @
   */
  void writePort(IPort port, Object o);

  /**
   * Write to a model's in port.
   * 
   * @param name
   *          the name
   * @param obj
   *          @
   */
  void writeToInPort(String name, Object obj);

}
