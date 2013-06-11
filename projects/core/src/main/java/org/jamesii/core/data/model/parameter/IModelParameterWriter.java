/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.model.parameter;

import java.net.URI;

/**
 * The Interface IModelParameterWriter.
 * 
 * Writes a set of model parameters to an (extra) data sink. Sometimes it might
 * be useful to allow complex (graphical or textual) in detail initialization
 * for model runs. If that's needed there is an interest of the user to store
 * this initialization so that it can be reused later on.
 * 
 * @author Jan Himmelspach
 */
public interface IModelParameterWriter {

  /**
   * Write the parameters combination to the given location.
   * 
   * @param location
   *          the location the parameters shall be written to
   * @param parameters
   *          the parameters to be written
   */
  void write(URI location, Object parameters);

}
