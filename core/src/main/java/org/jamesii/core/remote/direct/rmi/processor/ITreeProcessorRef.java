/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote.direct.rmi.processor;

import java.rmi.RemoteException;

import org.jamesii.core.processor.IProcessor;

/**
 * The Interface ITreeProcessorRef.
 */
public interface ITreeProcessorRef extends IProcessorRef {

  /**
   * REMOTE get parent.
   * 
   * @return the i processor
   * 
   * @throws RemoteException
   *           the remote exception
   */
  IProcessor remoteGetParent() throws RemoteException;

  /**
   * REMOTE set parent.
   * 
   * @param parent
   *          the parent
   * 
   * @throws RemoteException
   *           the remote exception
   */
  void remoteSetParent(IProcessor parent) throws RemoteException;

  /**
   * REMOTE add child.
   * 
   * @param proc
   *          the proc
   * 
   * @throws RemoteException
   *           the remote exception
   */
  void remoteAddChild(IProcessor proc) throws RemoteException;

  /**
   * REMOTE remove child.
   * 
   * @param proc
   *          the proc
   * 
   * @throws RemoteException
   *           the remote exception
   */
  void remoteRemoveChild(IProcessor proc) throws RemoteException;

}
