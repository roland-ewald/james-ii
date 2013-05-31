/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote.direct.rmi.processor;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The Interface IThreadedProcessorRef.
 * 
 * @author Bjoern Paul
 */
public interface IThreadedProcessorRef extends Remote {

  /**
   * Start the thread of an processor
   * 
   * @throws RemoteException
   */
  void remoteStartThread() throws RemoteException;

}
