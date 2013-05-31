/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.transmission;

import java.io.File;

import org.jamesii.core.data.transmission.plugintype.ObjectTransmissionFactory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Factory for creating {@link LazyObjectReceiver}s. See there for details.
 * 
 * @author Simon Bartels
 * 
 */
public class LazyObjectReceiverFactory extends ObjectTransmissionFactory {

  /**
   * The serial version UID.
   */
  private static final long serialVersionUID = 2106547162516335374L;

  @Override
  public IObjectReceiver create(Object object) {
    return new LazyObjectReceiver();
  }

  @Override
  public IObjectReceiver create(File file) {
    return new LazyObjectReceiver();
  }

  @Override
  public IObjectReceiver create(ParameterBlock parameters) {
    if (parameters.hasSubBlock("OBJECT")) {
      return create(parameters.getSubBlockValue("OBJECT"));
    }
    return create((File) parameters.getSubBlockValue("FILE"));
  }

}
