/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.model.parameter;

import java.net.URI;
import java.util.Map;

/**
 * The Interface IModelParameterReader.
 * 
 * Complex and detailed model initializations which might have been created
 * manually or which could be taken from specialized data bases (e.g., GIS data
 * bases) should be integratable in the simulation workflow. Thus a model
 * parameter reader should be able to read such a basic parameter combination
 * for the model at hand. <br/>
 * Consequently such a model parameter reader might be useful in single run
 * scenarios as well as in more complex experiments comprising thousands of
 * runs.
 * 
 * @author Jan Himmelspach
 */
public interface IModelParameterReader {

  /**
   * Read the parameter set from the given location and return a map containing
   * these.
   * 
   * @param location
   *          the location the parameters shall be read from
   * @return the map containing the parameters
   */
  Map<String, ?> read(URI location);

}
