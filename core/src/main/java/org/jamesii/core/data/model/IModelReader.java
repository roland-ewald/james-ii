/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.model;

import java.net.URI;
import java.util.Map;

import org.jamesii.core.model.IModel;
import org.jamesii.core.model.symbolic.ISymbolicModel;

/**
 * Model reader. Basic interface which encapsulates model reading and
 * instantiation. Has to be implemented by all classes which shall be usable
 * automatically by the system for these purposes.
 * 
 * 
 * @author Jan Himmelspach
 */
public interface IModelReader {

  /**
   * Read and instantiate a model from the passed URI.
   * 
   * Please note: This method has to take into account the passed parameters for
   * the instantiation of the model. I.e., the model has to be created by using
   * the parameters passed. Among these there might be one parameter which links
   * a (sometimes) more complex model initialization read from an external
   * source by a
   * {@link org.jamesii.core.data.model.parameter.IModelParameterReader}.
   * Everything passed in there can be replaced by any other parameter (so this
   * forms as base which can still be modified).
   * 
   * @param source
   *          the URI where the model can be read from (can be a simple ident as
   *          well, ...)
   * @param parameters
   *          the parameters to be used for model creation
   * @return an instantiated, executable model.
   */
  IModel read(URI source, Map<String, ?> parameters);

  /**
   * Read the model from the given URI and return a symbolic model
   * representation. May return null if no symbolic model description is
   * supported (e.g., in case of compiled classes).
   * 
   * @param ident
   *          the URI to read the model from
   * 
   * @return an instance of a symbolic model
   */
  ISymbolicModel<?> read(URI ident);

}
