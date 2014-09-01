/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.logging.remote;

import org.jamesii.SimSystem;
import org.jamesii.core.hosts.system.IMSSystemHost;
import org.jamesii.core.hosts.system.IObserverRegisterer;
import org.jamesii.core.hosts.system.IRemoteObserver;
import org.jamesii.core.util.logging.ApplicationLogger;

/**
 * The Class LogObserverRegisterer. Registers at the remote site (a ms system
 * host) a Remote log observer listening to the application log.
 */
public class LogObserverRegisterer implements IObserverRegisterer {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 5595522550781912605L;

  /** The publish history. */
  private boolean publishHistory = true;

  /**
   * Instantiates a new log observer registerer.
   * 
   * @param publishHistory
   *          publish the history
   */
  public LogObserverRegisterer(boolean publishHistory) {
    super();
    this.publishHistory = publishHistory;
  }

  @Override
  public boolean register(IMSSystemHost host, IRemoteObserver remote) {
    try {
      ApplicationLogger.addLogListener(new LogListenerProxy(remote),
          publishHistory);
      return true;
    } catch (Exception e) {
      SimSystem.report(e);
      return false;
    }
  }
}
