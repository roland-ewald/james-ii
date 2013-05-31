/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote.direct.rmi.model;

import java.rmi.RemoteException;

import org.jamesii.core.model.AccessRestriction;
import org.jamesii.core.model.IModel;
import org.jamesii.core.remote.direct.rmi.base.INamedEntityRef;

/**
 * The Interface IModelRef.
 */
public interface IModelRef extends INamedEntityRef {

  /**
   * REMOTE get proxy. Get a proxy of the remote model to be used in the local
   * context.
   * 
   * @return the model proxy
   * 
   * @throws RemoteException
   *           the remote exception
   */
  IModel remoteGetProxy() throws RemoteException;

  /**
   * REMOTE set access restriction. Set the access restriction to the remote
   * model.
   * 
   * @param accessRestriction
   *          the access restriction
   * 
   * @throws RemoteException
   *           the remote exception
   */
  void remoteSetAccessRestriction(AccessRestriction accessRestriction)
      throws RemoteException;

  /**
   * REMOTE clean up. Call the cleanUp method of the remote model.
   * 
   * @throws RemoteException
   *           the remote exception
   */
  void remoteCleanUp() throws RemoteException;

  /**
   * REMOTE initialization of the model.
   * 
   * @throws RemoteException
   *           the remote exception
   */
  void remoteInit() throws RemoteException;

}
