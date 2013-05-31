/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.taskrunner;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.jamesii.core.experiments.ComputationTaskRuntimeInformation;

/**
 * Remote interface for computation task runners that need notification of the
 * master server (regarding computation task status, crash, etc.).
 * 
 * @author Roland Ewald
 * @author Thomas Nösinger
 * 
 */
public interface IRemoteComputationTaskRunner extends Remote {

  /**
   * Restarts the computation task with the uid passed.
   * 
   * @param taskUID
   *          the unique ID of the computation task
   * @throws RemoteException
   *           if starting fails
   */
  void restartComputationTask(long taskUID) throws RemoteException;

  /**
   * Recovers a failed computation task.
   * 
   * @param taskUID
   *          the ID of the computaton task
   * @param runtimeInfo
   *          the corresponding runtime information
   * @throws RemoteException
   *           if recovering fails
   */
  void recoverComputationTask(long taskUID,
      ComputationTaskRuntimeInformation runtimeInfo) throws RemoteException;

}
