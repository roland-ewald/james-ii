/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.transmission;

import org.jamesii.core.base.Entity;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * An object receiver which transfers nothing. Necessary for the
 * {@link LazyResultCollector}.
 * 
 * @author Simon Bartels
 * 
 */
public class LazyObjectReceiver extends Entity implements IObjectReceiver {

  /**
   * The serial version UID.
   */
  private static final long serialVersionUID = -4274832355169984413L;

  /**
   * Just notifies all observers that the result is ready.
   */
  @Override
  public void executeCommand(ParameterBlock parameterBlock) {
    changed();
  }

  /**
   * Returns a message saying that nothing has been transmitted.
   */
  @SuppressWarnings("unchecked")
  @Override
  public <T> T getObject() {
    return (T) "You've triggered the lazy object receiver. There's no result!";
  }

}
