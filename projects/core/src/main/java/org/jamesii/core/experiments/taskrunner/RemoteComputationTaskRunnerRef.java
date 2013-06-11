/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.taskrunner;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.jamesii.core.experiments.ComputationTaskRuntimeInformation;

/**
 * Reference by which a {@link ITaskRunner} may be accessed remotely.
 * 
 * @author Roland Ewald
 */
public class RemoteComputationTaskRunnerRef extends UnicastRemoteObject
    implements IRemoteComputationTaskRunner {

  /** Serialisation ID. */
  private static final long serialVersionUID = 4675707800125179513L;

  /** Reference to wrapped local runner. */
  private ITaskRunner taskRunner;

  /**
   * Instantiates a new remote computation task runner reference for the passed
   * local task runner.
   * 
   * @param taskRunner
   *          the local task runner
   * 
   * @throws RemoteException
   *           the remote exception
   */
  public RemoteComputationTaskRunnerRef(ITaskRunner taskRunner)
      throws RemoteException {
    super();
    this.taskRunner = taskRunner;
  }

  @Override
  public void recoverComputationTask(long taskUID,
      ComputationTaskRuntimeInformation runtimeInfo) throws RemoteException {
    taskRunner.recoverTask(taskUID, runtimeInfo);
  }

  @Override
  public void restartComputationTask(long taskUID) throws RemoteException {
    taskRunner.restartTask(taskUID);
  }

}
