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
 * The DoneMessage is send from a child processor to its parent processor to
 * indicate that its processing is finished and to send its next tonie.
 * 
 * @author Jan Himmelspach
 */
public class DoneMessage extends Message<IProcessor> {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 3691693510222691525L;

  /** The next tonie. */
  private double nextTonie;

  /**
   * Constructs a new DoneMessage.
   * 
   * @param sender
   *          the processor which is sending this message
   * @param nextTonie
   *          the time of next event
   */
  public DoneMessage(IProcessor sender, double nextTonie) {
    super(sender);
    this.setNextTonie(nextTonie);
  }

  /**
   * Instantiates a new done message for sender and a Double *object* denoting
   * the tonie. Event queues return a null tonie if they are empty, in this case
   * the tonie is +inf.
   * 
   * @param sender
   *          the sender
   * @param nextTonie
   *          the next tonie
   */
  public DoneMessage(IProcessor sender, Double nextTonie) {
    super(sender);
    this.setNextTonie(nextTonie == null ? Double.POSITIVE_INFINITY : nextTonie);
  }

  /**
   * Get the tonie transferred via this done message.
   * @return
   */
  public double getNextTonie() {
    return nextTonie;
  }

  /**
   * Set the tonie to be transferred via this done message.
   * @param nextTonie
   */
  public final void setNextTonie(double nextTonie) {
    this.nextTonie = nextTonie;
  }

}
