/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.model;

import java.net.URI;

import org.jamesii.core.model.IModel;
import org.jamesii.core.model.symbolic.ISymbolicModel;

/**
 * The interface IModelWriter has to be implemented by all classes which shall
 * be usable for storing a model to a target device.
 * 
 * @author Jan Himmelspach
 * 
 */
public interface IModelWriter {

  /**
   * Write the given model in the output stream.
   * 
   * @param model
   *          the model to be written
   * @param target
   *          the stream the model to be written to
   */
  void write(IModel model, URI target);

  /**
   * Writes the model to the given URI.
   * 
   * @param model
   *          the symbolic model to write
   * @param ident
   *          the URI to read the model from
   */
  void write(ISymbolicModel<?> model, URI ident);

}
