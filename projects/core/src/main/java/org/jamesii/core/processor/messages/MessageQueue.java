/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.processor.messages;

import java.io.Serializable;

import org.jamesii.core.util.collection.Fifo;

/**
 * Class FifoMessageBuffer implements a first-in-first-out queue storing
 * messages. The queue's length dynamically increases and decreases. There must
 * be producers and consumers in order to put a message in or get a message out.
 * This buffer synchronizes producers and consumers in that way, that a producer
 * can write to the end of the queue if no other writer is active -- otherwise
 * or if a reader is active it will be blocked. A consumer can only read (and
 * thereby empty) the buffer, if something is in it -- otherwise the consumer is
 * blocked, till the buffer is not empty and no producer is active.
 * 
 * @param <T>
 * 
 * @see org.jamesii.core.processor.messages.Message T, the type of the sender
 *      used in the messages stored in the queues
 * 
 */
public final class MessageQueue<T> implements Serializable {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -29089168598146023L;

  // --- INTERNAL STRUCTURES ---------------------------------------------

  /** This first in first out queue is the condition variable. */
  private Fifo<IMessage<T>> fifo = new Fifo<>();

  /**
   * used for the wake up of the thread who wait for the queue ,when some one
   * want to force the end of this certain thread.
   */
  private boolean finish = false;

  /** The is waiting. */
  private boolean isWaiting = false;

  // --- BLOCKING SERVICE METHODS ----------------------------------------

  /**
   * Gets and deletes a message from the queue. If there is no message to get,
   * the consumer (calling this method) is blocked until a producer puts a
   * message.
   * 
   * @return the first message which is in the queue
   */
  public synchronized IMessage<T> getMessage() {
    while (fifo.isEmpty()) {
      try {
        wait();
      } catch (InterruptedException e) {
      }
    }
    IMessage<T> message = fifo.out();
    notifyAll();
    return message;
  }

  /**
   * check whether the queue is empty.
   * 
   * @return true, if checks if is empty
   */
  public synchronized boolean isEmpty() {
    return fifo.isEmpty();
  }

  /**
   * Wait for a message and return if a message arrived.
   */
  public synchronized void waitForMessage() {

    while (fifo.isEmpty() && !finish) {
      this.isWaiting = true;
      try {
        wait();
      } catch (InterruptedException e) {
      }
    }
    this.isWaiting = false;
    if (!finish) {
      return;
    }
    notifyAll();

  }

  /**
   * Puts a message into the queue. The producer (calling this method) is
   * blocked as long as an other producer or consumer is active.
   * 
   * @param message
   *          which should be put into the queue
   */
  public synchronized void putMessage(IMessage<T> message) {

    fifo.add(message);

    notifyAll();
  }

  /**
   * Show top message.
   * 
   * @return the i message<?>
   */
  public synchronized IMessage<T> showTopMessage() {

    return fifo.get();
  }

  /**
   * Show size.
   * 
   * @return the int
   */
  public synchronized int size() {
    return fifo.size();
  }

  /**
   * used for the wakeup of the thread who wait for the queue ,when some one
   * want to force the end of this certain thread.
   */
  public synchronized void fairwell() {
    finish = true;
    if (isWaiting) {
      notifyAll();
    }
  }
}
