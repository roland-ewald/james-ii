/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.model;

import java.io.Serializable;

/**
 * The Class ModelInformation wraps a model. It is used in the context of
 * distributed simulation runs.
 * 
 * @see org.jamesii.core.simulation.distributed
 * 
 * @author Jan Himmelspach
 */
public class ModelInformation implements Serializable {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 4550422021528293973L;

  /** The local model. */
  private IModel local;

  /**
   * Instantiates a new model information for a "null" model.
   */
  public ModelInformation() {
    super();
    local = null;
  }

  /**
   * Instantiates a new model information for the passed model.
   * 
   * @param model
   *          the model
   */
  public ModelInformation(IModel model) {
    super();
    local = model;
  }

  /**
   * Gets the local model.
   * 
   * @return the local model, might be null if no model has been set so far
   */
  public IModel getLocal() {
    return local;
  }

  /**
   * Sets the (local) model.
   * 
   * @param model
   *          the model to be set
   */
  public void setModel(IModel model) {
    local = model;
  }

}
