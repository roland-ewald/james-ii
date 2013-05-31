/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote.direct.rmi.processor;

import java.rmi.RemoteException;

import org.jamesii.core.model.IModel;
import org.jamesii.core.processor.IProcessor;
import org.jamesii.core.processor.ProcessorState;
import org.jamesii.core.remote.direct.rmi.base.IEntityRef;

/**
 * The Interface IProcessorRef.
 * 
 * @author Jan Himmelspach
 */
public interface IProcessorRef extends IEntityRef {

  /**
   * Gets the proxy.
   * 
   * @return the proxy
   * 
   * @throws RemoteException
   *           the remote exception
   */
  IProcessor getProxy() throws RemoteException;

  /**
   * Every processor has to process the next simulation step if this method is
   * called.
   * 
   * @throws RemoteException
   */
  void remoteExecuteNextStep() throws RemoteException;

  /**
   * returns a pointer to the associcated model`s interface
   * 
   * @throws RemoteException
   * @return pointer to the associated interface
   */
  IModel remoteGetModel() throws RemoteException;

  /**
   * Returns the processor state of this model
   * 
   * @return the processorstate of this model
   * @throws RemoteException
   */
  ProcessorState remoteGetState() throws RemoteException;

  /**
   * REMOTe clean up.
   * 
   * @throws RemoteException
   *           the remote exception
   */
  void remoteCleanUp() throws RemoteException;

}
