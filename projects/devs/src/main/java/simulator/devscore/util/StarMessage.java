/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package simulator.devscore.util;

import org.jamesii.core.processor.IProcessor;
import org.jamesii.core.processor.messages.Message;

/**
 * A StarMessage is sent from the RootCoordinator to its imminent children. It
 * contains the sender of the message and the actual simulation time.
 * 
 * @author Jan Himmelspach
 */
public class StarMessage extends Message<IProcessor> {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -2848581289827080371L;

  /** The time. */
  private double time;

  /**
   * Creates a new StarMessage.
   * 
   * @param sender
   *          of the StarMessage
   * @param time
   *          actual simulation time
   */
  public StarMessage(IProcessor sender, double time) {
    super(sender);
    this.time = time;
  }

  /**
   * Get the time of the star message.
   * 
   * @return the time within this message
   */
  public double getTime() {
    return time;
  }
}
