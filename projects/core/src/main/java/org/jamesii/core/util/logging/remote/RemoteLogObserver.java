/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.logging.remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.jamesii.SimSystem;
import org.jamesii.core.hosts.system.IObserverRegisterer;
import org.jamesii.core.hosts.system.IRemoteObserver;
import org.jamesii.gui.application.logging.BasicLogView;

/**
 * Listener for remote logs.
 * 
 * @author Stefan Leye
 */
public class RemoteLogObserver extends UnicastRemoteObject implements
    IRemoteObserver {

  /** The serialization ID. */
  private static final long serialVersionUID = 600561147486429192L;

  /** The log view which shall show the log. */
  private transient BasicLogView logView;

  /**
   * The publish history flag, if true the (available) history of log entries
   * will be published as well.
   */
  private boolean publishHistory;

  /**
   * Instantiates a new remote listener.
   * 
   * @param logView
   *          the log view
   * @param publishHistory
   *          the publish history
   * 
   * @throws RemoteException
   *           the remote exception
   */
  public RemoteLogObserver(BasicLogView logView, boolean publishHistory)
      throws RemoteException {
    super();
    this.logView = logView;
    this.publishHistory = publishHistory;
  }

  @Override
  public IObserverRegisterer getRegisterer() throws RemoteException {

    return new LogObserverRegisterer(publishHistory);
  }

  @Override
  public void update(Object hint) throws RemoteException {
    if (hint instanceof LogRecord) {
      logView.publish((LogRecord) hint);
    } else {
      SimSystem.report(Level.WARNING, "Warning! Update of remote logger called, without log!");
    }
  }

  @Override
  public Class<?> getRegistererClass() throws RemoteException {
    return LogObserverRegisterer.class;
  }

}
