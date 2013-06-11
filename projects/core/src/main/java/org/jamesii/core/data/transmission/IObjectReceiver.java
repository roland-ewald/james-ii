/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.transmission;

import org.jamesii.core.base.IEntity;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Wrapper for object transfers. The idea is that the transmitter instantiates
 * this object using parameters from the receiver. The receiver then starts the
 * transmission with {@link #executeCommand(ParameterBlock)} and will be
 * notified if the file transfer is complete or something has failed. The use
 * case here aims especially on the transmission of large objects.
 * 
 * @author Simon Bartels
 * 
 */
public interface IObjectReceiver extends IEntity {
  /**
   * Tries to interpret a command for the system behind the wrapper.
   * 
   * @param parameterBlock
   *          The parameter block holding the command and it's parameters.
   */
  void executeCommand(ParameterBlock parameterBlock);

  /**
   * Returns the reference to the object that has been received.
   * 
   * @param <T>
   *          type of the object
   * @return null if the object hasn't been completely received yet, otherwise
   *         the object
   */
  <T> T getObject();
}
