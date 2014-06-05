/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.ports;


import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.jamesii.core.util.collection.ElementSet;

/**
 * The Class PortSet.
 * 
 * @author Jan Himmelspach
 */
public class PortSet extends ElementSet<IPort> implements IPortSet {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -2880077722062121434L;

  /** The ports to be maintained by this set. */
  private Map<String, IPort> helements = new HashMap<>(1);

  @Override
  public void addPort(IPort port) {
    getVelements().add(port);
    helements.put(port.getName(), port);
    setElementIterator(null);
    changed();
  }

  @Override
  public void clearPorts() {
    for (int i = 0; i < getVelements().size(); i++) {
      getVelements().get(i).clear();
    }
  }

  @Override
  public IPort getPort(String name) {
    return helements.get(name);
  }

  @Override
  public boolean hasPort(IPort port) {
    return helements.containsValue(port);
  }

  @Override
  public boolean hasPort(String name) {
    return helements.containsKey(name);
  }

  @Override
  public void removePort(IPort port) {

    if (!getVelements().remove(port)) {
      throw new NoSuchElementException("Port " + port.getName()
          + " could not be removed!");
    }
    helements.remove(port.getName());

    setElementIterator(null);
    changed();
  }
}
