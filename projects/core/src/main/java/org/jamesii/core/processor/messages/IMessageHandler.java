/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.processor.messages;

import org.jamesii.core.processor.IThreadedProcessor;
import org.jamesii.core.util.Hook;

/**
 * The IMessageHandler interface must be implemented by the processors which
 * have to react on incoming messages.
 * 
 * The receiveMessage method should be implemented in a non blocking way (e.g.,
 * by using a special queue (e.g., the {@link MessageQueue}).
 * 
 * Most often this interface will be used together with the
 * {@link IThreadedProcessor} interface.
 * 
 * Message hooks can be used to add functionality not foreseen at the moment of
 * creation of the simulation algorithm or just to observer the message
 * receiving as such (e.g., for load - balancing). An implementing class should
 * provide this functionality to make sure that any algorithm relying in hooks
 * will work for the implementor of this interface as well. The hooks have to be
 * executed before the message is processed/stored or whatever.
 * 
 * @author Jan Himmelspach
 * @param <M>
 *          the type of the sender of the messages to be handled (thus it is the
 *          {@link IMessage} generic parameter
 * 
 */
public interface IMessageHandler<M> {

  /**
   * Install message hook. The installed message hook(s) have to be executed
   * whenever a message is received, and before the message is executed.
   * 
   * 
   * @param hook
   *          the hook to be installed
   */
  void installMessageHook(Hook<IMessage<M>> hook);

  /**
   * Classes which implement this method guarantee that they are able to receive
   * messages. Should be implemented in a non blocking way! Thus it should
   * return control to the caller ASAP. Any installed message hook (see
   * {@link #installMessageHook(Hook)}) has to be executed before this message
   * starts processing the message.
   * 
   * @param msg
   *          which is received
   */
  void receiveMessage(IMessage<M> msg);

}
