/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.computationserver;

import java.io.Serializable;
import java.rmi.RemoteException;

import org.jamesii.core.services.IService;
import org.jamesii.core.util.id.IUniqueID;

/**
 * Interface for computation server.
 * 
 * @author Stefan Leye
 */
public interface IComputationServer extends IService {

  /**
   * Register this simulation server at a master server. The address of the
   * master server to be registered at has to be given in a RMI compatible
   * style.
   * 
   * @param serverAdress
   *          the server adress
   * 
   * @throws RemoteException
   *           the remote exception
   */
  void register(String serverAdress) throws RemoteException;

  /**
   * Initialize the passed job on the server.
   * 
   * @param job
   * @param id
   * @throws RemoteException
   */
  <V> void initializeJob(IJob<V> job, IUniqueID id) throws RemoteException;

  /**
   * Finalize the job on the server.
   * 
   * @param id
   * @throws RemoteException
   */
  void finalizeJob(IUniqueID id) throws RemoteException;

  /**
   * Execute the job.
   * 
   * @param id
   * @param data
   * @return
   * @throws RemoteException
   */
  <V> V executeJob(IUniqueID id, Serializable data) throws RemoteException;

}
