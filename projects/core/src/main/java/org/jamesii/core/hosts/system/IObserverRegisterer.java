/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.hosts.system;

import java.io.Serializable;

/**
 * Interface for the registerer of a remote observer. In general the remote
 * observer is located on a remote host end sends the registerer to the host to
 * be observed, where it executes the registration process.
 * 
 * @author Stefan Leye
 */

public interface IObserverRegisterer extends Serializable {

  /**
   * Registers a remote observer a the local host where the registerer is
   * situated.
   * 
   * @param host
   *          the host
   * @param remote
   *          the remote observer to be used
   * 
   * @return true, if successful
   */
  boolean register(IMSSystemHost host, IRemoteObserver remote);

}
