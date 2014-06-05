/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.observe;


import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import model.devscore.ports.Port;

import org.jamesii.core.observe.Observer;

/**
 * The Class PortObserver.
 * 
 * @author Björn Paul
 */
public class PortObserver extends Observer<Port> {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 1452461271382022751L;

  /** The port items. */
  private Map<Port, IntegerObject> portItems =
      new HashMap<>();

  /**
   * This method is called when information about an Port which was previously
   * requested using an asynchronous interface becomes available.
   * 
   * @param port
   *          the port
   */
  public void addPort(Port port) {
    IntegerObject counter = new IntegerObject();
    portItems.put(port, counter);
  }

  /**
   * This method is called when information about an Port which was previously
   * requested using an asynchronous interface becomes available.
   * 
   * @param port
   *          the port
   * 
   * @return the message counter
   */
  public int getMessageCounter(Port port) {
    return (portItems.get(port).getAccessCounter());
  }

  /**
   * This method is called when information about an Port which was previously
   * requested using an asynchronous interface becomes available.
   * 
   * @return the port items iterator
   */
  public Iterator<IntegerObject> getPortItemsIterator() {
    Collection<IntegerObject> c = portItems.values();
    return c.iterator();
  }

  /**
   * This method is called when information about an Port which was previously
   * requested using an asynchronous interface becomes available.
   * 
   * @param port
   *          the port
   */
  public void removePort(Port port) {
    portItems.remove(port);
  }

  @Override
  public void update(Port port) {
    // accessCounter++
    portItems.get(port).addOne();

  }

}
