/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization.parameter;

import java.io.Serializable;
import java.util.Map;

import org.jamesii.core.model.variables.BaseVariable;
import org.jamesii.core.observe.IObservable;
import org.jamesii.core.observe.IObserver;

/**
 * Interface for observers to be used for the observation of optimisation.
 * variables
 * 
 * @author Roland Ewald Date: 18.05.2007
 */
public interface IResponseObserver<E extends IObservable> extends IObserver<E>,
    Serializable {

  /**
   * Gets response list.
   * 
   * @return map with the response variables
   */
  Map<String, BaseVariable<?>> getResponseList();

}
