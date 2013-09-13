/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.instrumentation.model;

import java.io.Serializable;

import org.jamesii.core.experiments.IComputationTaskConfiguration;
import org.jamesii.core.experiments.instrumentation.IInstrumenter;
import org.jamesii.core.model.IModel;

/**
 * This interface defines model instrumenters. A model instrumenter is used to
 * add observers to specific parts of a model. It is called after model creation
 * and before the model is processed.
 * 
 * @author Roland Ewald Date: 15.06.2007
 * 
 */
public interface IModelInstrumenter extends IInstrumenter, Serializable {

  /**
   * Called to instrument model instance.
   * 
   * @param model
   *          model to be instrumented
   * @param simConfig
   *          the simulation configuration
   */
  void instrumentModel(IModel model, IComputationTaskConfiguration simConfig);

}
