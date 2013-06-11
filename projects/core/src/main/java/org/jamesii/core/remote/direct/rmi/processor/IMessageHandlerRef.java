/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote.direct.rmi.processor;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.jamesii.core.processor.messages.IMessage;

/**
 * The Interface IMessageHandlerRef.
 * 
 * @author Jan Himmelspach
 * @param <M>
 *          type of the message
 */
public interface IMessageHandlerRef<M> extends Remote {

  /**
   * Classes which implement this method guarantee that they are able to receive
   * messages.
   * 
   * @param msg
   *          which is received
   * @param sender
   *          the sender
   * 
   * @throws RemoteException
   *           the remote exception
   */
  void remoteReceiveMessage(IMessage<M> msg, String sender)
      throws RemoteException;

}
