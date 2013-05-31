/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.model;

import org.jamesii.core.base.INamedEntity;

/**
 * IModel is the basic model interface for executable models. All models which
 * shall be executed within the framework have to implement this interface. All
 * extended model interfaces should use this interface (or one of its
 * descendants) as ancestor. <br/>
 * For editing, parameterization, and so on there is the
 * {@link org.jamesii.core.model.symbolic.ISymbolicModel} model interface. If
 * there is no difference between symbolic models and executable models the
 * IModel can be used as internal data structure of a symbolic model. <br/>
 * Instance creation of executable models is typically done by a model reading
 * mechanism (and thus any modelling language is fine). However, models can be
 * coded using Java as well.
 * 
 * 
 * @author Jan Himmelspach
 * 
 * @see org.jamesii.core.data.model.IModelReader
 * @see org.jamesii.core.simulationrun.ISimulationRun
 */
public interface IModel extends INamedEntity {

  /**
   * A model may have several restrictions during execution. With this method a
   * special access restriction object can be passed. This method will be called
   * by a simulator - a model has to check whether there is any restriction
   * before executing a restricted method.
   * 
   * @param accessRestriction
   */
  void setAccessRestriction(AccessRestriction accessRestriction);

  /**
   * Initializes the model.
   */
  void init();

  /**
   * Will be called at the end of a simulation run (after the run stopped).
   */
  void cleanUp();

}