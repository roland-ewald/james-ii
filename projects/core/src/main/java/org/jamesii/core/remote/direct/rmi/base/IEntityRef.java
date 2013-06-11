/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote.direct.rmi.base;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.jamesii.core.observe.IObserver;

/**
 * The Interface IEntityRef is the counterpart of the
 * {@link org.jamesii.core.base.IEntity} interface. It is a remotely accessible
 * interface (by using Java RMI) and it is implemented by the the
 * {@link EntityRef} class.
 * 
 * @author Jan Himmelspach
 */
public interface IEntityRef extends Remote {

  /**
   * Execute the method with the passed method name on the local object and
   * return the result.
   * 
   * @param methodName
   *          the name of method to be executed
   * @param parameters
   *          the parameters to be used for the execution
   * 
   * @return the result of the method execution, maybe null
   * 
   * @throws RemoteException
   *           the remote exception
   */
  Object remoteExecuteMethod(String methodName, Object[] parameters)
      throws RemoteException;

  /**
   * Get class name of the "hidden" class.
   * 
   * @return the string
   * 
   * @throws RemoteException
   *           the remote exception
   * 
   * 
   */
  String remoteGetClassName() throws RemoteException;

  /**
   * Get the complete info string of the hidden instance.
   * 
   * @return the string
   * 
   * @throws RemoteException
   *           the remote exception
   * @see org.jamesii.core.base.IEntity#getCompleteInfoString()
   */
  String remoteGetCompleteInfoString() throws RemoteException;

  /**
   * Get the uid.
   * 
   * @return the long
   * 
   * @throws RemoteException
   *           the remote exception
   * @see org.jamesii.core.base.IEntity#getSimpleId()
   */
  long remoteGetUid() throws RemoteException;

  /**
   * Register an observer.
   * 
   * @param observer
   *          the observer
   * 
   * @throws RemoteException
   *           the remote exception
   * 
   */
  void remoteRegisterObserver(IObserver observer) throws RemoteException;

  /**
   * Unregister an observer.
   * 
   * @param observer
   *          the observer
   * 
   * @throws RemoteException
   *           the remote exception
   */
  void remoteUnregisterObserver(IObserver observer) throws RemoteException;

}
