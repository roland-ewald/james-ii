/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization.parameter.instrumenter;

import java.util.Map;

import org.jamesii.core.experiments.optimization.parameter.Configuration;
import org.jamesii.core.model.variables.BaseVariable;

// TODO: Auto-generated Javadoc
/**
 * The Interface IParResponseObsSimInstrumenter.
 */
public interface IParResponseObsSimInstrumenter extends
    IResponseObsSimInstrumenter {

  /**
   * Retrieves the responses from the given configuration.
   * 
   * @param _configuration
   *          the _configuration
   * 
   * @return the observed responses
   */
  Map<? extends String, ? extends BaseVariable<?>> getObservedResponses(
      Configuration _configuration);

}
