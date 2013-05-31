/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.transmission.plugintype;

import java.io.File;

import org.jamesii.core.data.transmission.IObjectReceiver;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Base factory for object transmission factories. See {@link IObjectReceiver}
 * for details.
 * 
 * @author Simon Bartels
 * 
 */
public abstract class ObjectTransmissionFactory extends
    Factory<IObjectReceiver> {

  /**
   * The serial version UID.
   */
  private static final long serialVersionUID = -97455470611034300L;

  /**
   * Creates a new instance of {@link IObjectReceiver}.
   * 
   * @param object
   *          the object that shall be transferred
   * @return the object receiver
   */
  public abstract IObjectReceiver create(Object object);

  /**
   * In case the object is a file this method could be the better option.
   * (Depending on the underlying transfer system which may distinguish or not)
   * 
   * @param file
   *          the file pointing to the object
   * @return the object receiver
   */
  public abstract IObjectReceiver create(File file);

  @Override
  public IObjectReceiver create(ParameterBlock parameters) {
    if (parameters.getValue() instanceof File) {
      return create((File) parameters.getValue());
    }
    return create(parameters.getValue());
  }

}
