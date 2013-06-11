/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.processor.messages;

import org.jamesii.core.base.Entity;

/**
 * The Class Message. Default implementation of the {@link IMessage} interface.
 * 
 * @param <S>
 *          the type of the sender
 */
public class Message<S> extends Entity implements IMessage<S> {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 7130539795467574053L;

  /** The sender. */
  private transient S sender;

  /**
   * Constructor initializes the message and sets the sender.
   * 
   * @param sender
   *          of the message
   */
  public <T extends S> Message(T sender) {
    super();
    this.sender = sender;
  }

  /**
   * Returns the sender of this message (which is always an IProcessor
   * descendant.
   * 
   * @return the sender of the message
   */
  @Override
  public S getSender() {
    return sender;
  }

  /**
   * Allows to set the sender of the message after the message has been created.
   * 
   * @param sender
   *          sets a new sender
   */
  @Override
  public void setSender(S sender) {
    this.sender = sender;
  }

}
