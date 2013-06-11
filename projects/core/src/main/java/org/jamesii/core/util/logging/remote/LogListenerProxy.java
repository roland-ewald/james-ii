/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.logging.remote;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.jamesii.SimSystem;
import org.jamesii.core.hosts.system.IRemoteObserver;
import org.jamesii.core.util.logging.ILogListener;

/**
 * Proxy listener, which forwards the LogRecord to a remote listener.
 * 
 * @author Stefan Leye
 */
public class LogListenerProxy implements ILogListener {

  /** Reference to the listener on the window side. */
  private IRemoteObserver remote;

  /**
   * Instantiates a new log listener proxy.
   * 
   * @param remote
   *          the remote observer
   */
  public LogListenerProxy(IRemoteObserver remote) {
    super();
    this.remote = remote;
  }

  @Override
  public void publish(LogRecord record) {
    try {
      remote.update(record);
    } catch (RemoteException e) {
      String msg = "Forwarding the log record failed.";
      // avoid recursion!
      if (!record.getMessage().contains(msg)) {
        SimSystem.report(Level.WARNING, msg, e);
      }
    }
  }

  @Override
  public void flush() {
    // try {
    // remote.flush();
    // } catch (RemoteException e) {
    // e.printStackTrace();
    // }
  }

}
