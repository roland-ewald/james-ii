/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization.parameter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.jamesii.core.model.variables.BaseVariable;
import org.jamesii.core.observe.IObservable;
import org.jamesii.core.observe.Observer;

/**
 * This observer is only for the response variables of the model. After each
 * update() it is called and stores the changes to a list of responses. The list
 * of responses is given by the problem definition. Remember using unique
 * variable names.
 * 
 * @author Arvid Schwecke
 */
public class SimpleResponseObserver extends Observer implements
    IResponseObserver, Serializable {

  /** Generated serialVersionUID. */
  private static final long serialVersionUID = -3377982577879351601L;

  /** List of responses. */
  private Map<String, BaseVariable<?>> responses = new HashMap<>();

  @Override
  public Map<String, BaseVariable<?>> getResponseList() {
    return responses;
  }

  @Override
  public void update(IObservable entity) {
    if (entity instanceof BaseVariable<?>) {
      BaseVariable<?> response = (BaseVariable<?>) entity;
      responses.put(response.getName(), response);
    }
  }

}
