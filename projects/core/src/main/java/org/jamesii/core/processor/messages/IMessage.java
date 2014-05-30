/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.processor.messages;

import java.io.Serializable;

/**
 * The Interface IMessage.
 * 
 * Basic interface for messages (between simulation algorithms instances) in the
 * framework. By using the generic parameter this interface can be used for any
 * message. There is a default implementation of this interface as well
 * {@link Message}. The interface {@link IMessageHandler} provides basic
 * handling methods, i.e., message receiving, and the ability to install message
 * hooks for doing something whenever a message is received.
 * 
 * @param <S>
 *          the type of the sender
 * @author Jan Himmelspach
 */
public interface IMessage<S> extends Serializable {

  /**
   * Gets the sender. Has to be set by setSender of by using a constructor
   * before.
   * 
   * @return the sender if the message
   */
  S getSender();

  /**
   * Sets the sender.
   * 
   * @param sender
   *          the sender of the message
   */
  void setSender(S sender);

}
