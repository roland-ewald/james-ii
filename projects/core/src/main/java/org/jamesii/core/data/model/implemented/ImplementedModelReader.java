/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.model.implemented;

import java.net.URI;
import java.util.Map;

import org.jamesii.core.data.model.IModelReader;
import org.jamesii.core.model.IModel;
import org.jamesii.core.model.InvalidModelException;
import org.jamesii.core.model.symbolic.ISymbolicModel;
import org.jamesii.core.util.Reflect;
import org.jamesii.core.util.exceptions.OperationNotSupportedException;

/**
 * 
 * {@link IModelReader} implementation for implemented models. Presupposes a
 * constructor of form <code>Classname(Map String,? parameters)</code>. These
 * should be interpreted by the constructor of the model read.
 * 
 * The host name of the {@link URI} is used to extract the fully qualified class
 * name, e.g., create a valid {@link URI} with
 * 
 * <code>new URI("java://package.to.my.Class")</code>
 * 
 * @author Roland Ewald
 * 
 *         04.06.2007
 * 
 */
public class ImplementedModelReader implements IModelReader {

  @Override
  public IModel read(URI ident, Map<String, ?> parameters) {
    return instantiateModel(ident, parameters);
  }

  /**
   * Instantiates the model using Java reflections.
   * 
   * @param source
   *          the URI where the model can be read from (can be a simple ident as
   *          well, ...)
   * @param parameters
   *          the parameters to be used for model creation
   * @return an instantiated, executable model.
   */
  protected <T> T instantiateModel(URI ident, Map<String, ?> parameters) {
    try {
      String modellLocation = ident.getAuthority();
      Class<?> modelClass = Class.forName(modellLocation);
      try {
        return Reflect.instantiate(modelClass, parameters);
      } catch (NoSuchMethodException ex) {
        // If the class is an inner class, try to instantiate the enclosing
        // class via default constructor
        if (modelClass.getEnclosingClass() != null) {
          return Reflect.instantiate(modelClass,
              Reflect.instantiate(modelClass.getEnclosingClass()), parameters);
        }
        throw new InvalidModelException(
            "Does model '"
                + modellLocation
                + "' implement a constructor with a Map<String, ?> as single argument? This is required.",
            ex);
      }
    } catch (Exception ex) {
      throw new InvalidModelException(
          "Error while trying to read implemented model '" + ident.getHost()
              + "'", ex);
    }
  }

  @Override
  public ISymbolicModel<?> read(URI ident) {
    throw new OperationNotSupportedException(
        "Does not support a symbolic model representation.");
  }

}
