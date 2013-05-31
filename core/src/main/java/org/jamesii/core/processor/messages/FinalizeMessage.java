/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.processor.messages;

import org.jamesii.core.processor.IProcessor;

/**
 * This message can be send to the
 * processors if the simulation has been stopped (e.g. for cleaning up)
 *  * 
 * @author Jan Himmelspach
 * @version 1.0
 */
public class FinalizeMessage extends Message {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -682589770117315122L;

  /**
   * Instantiates a new finalize message.
   * 
   * @param sender
   *          the sender
   */
  public FinalizeMessage(IProcessor<?> sender) {
    super(sender);
  }

}
