/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.ports;

import org.jamesii.core.util.collection.IElementSet;

/**
 * The Interface IPortSet. A set of ports - i.e.., either of input or output
 * ports of a model.
 */
public interface IPortSet extends IElementSet<IPort> {

  /**
   * Adds the port.
   * 
   * @param port
   *          the port
   */
  void addPort(IPort port);

  /**
   * Clear ports.
   */
  void clearPorts();

  /**
   * Gets the port.
   * 
   * @param name
   *          the name
   * 
   * @return the port
   */
  IPort getPort(String name);

  /**
   * Checks for port.
   * 
   * @param port
   *          the port
   * 
   * @return true, if successful
   */
  boolean hasPort(IPort port);

  /**
   * Checks for port.
   * 
   * @param name
   *          the name
   * 
   * @return true, if successful
   */
  boolean hasPort(String name);

  /**
   * Removes the port.
   * 
   * @param port
   *          the port
   */
  void removePort(IPort port);

}
