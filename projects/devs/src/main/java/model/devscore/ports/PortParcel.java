/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.ports;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * The class PortParcel is used on sending messages which contains port
 * allocations, i.e. ports on which values have been placed This wrapper is
 * needed for decreasing the number of remote accesses.
 * 
 * @author Jan Himmelspach
 */
public class PortParcel implements Serializable {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 2196210905128638963L;

  /**
   * This attribute contains the sending/receiving port
   */
  private IPort port;

  /**
   * This vector contains the values which are sent by or to be received by the
   * given port.
   */
  private List<Object> values;

  /**
   * Creats a new PortParcel, a parcel containing the involved port and the
   * values to be transmitted. This wrapper is used for limiting the network
   * traffic - so its possible to access the values on the target computer
   * without additional cross network accesses.
   * 
   * @param port
   *          which should be used
   * @param values
   *          that shall be sent
   */
  public PortParcel(IPort port, List<Object> values) {
    super();

    this.port = port;
    this.values = values;
  }

  /**
   * Return the port belonging to the values list
   * 
   * @return port which is associated to this PortParcel
   */
  public IPort getPort() {
    return port;
  }

  /**
   * Returns an Iterator, containing all values.
   * 
   * @return The value-containing Iterator.
   */
  public Iterator<Object> valueIterator() {
    return values.iterator();
  }
}
