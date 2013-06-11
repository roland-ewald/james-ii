/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.model.parameter.read.plugintype;

import org.jamesii.core.data.IURIHandling;
import org.jamesii.core.data.model.parameter.IModelParameterReader;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.model.IModel;
import org.jamesii.core.model.symbolic.ISymbolicModel;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Super class of all model parameter reader factories.
 * 
 * @author Jan Himmelspach
 */
public abstract class ModelParameterReaderFactory extends
    Factory<IModelParameterReader> implements IURIHandling {

  /**
   * Serialization ID.
   */
  private static final long serialVersionUID = -9070589108539376222L;

  /**
   * Create model reader.
   * 
   * @param params
   *          parameters, may be null
   * @return model reader
   */
  @Override
  public abstract IModelParameterReader create(ParameterBlock params);

  /**
   * Returns true if the factory is able to handle such models.
   * 
   * @param modelClass
   *          the class of the model that should be handled
   * @return true, if model is supported
   */
  public abstract boolean supportsModel(Class<?> modelClass);

  /**
   * Returns true if the factory is able to handle such models.
   * 
   * @param model
   *          model which should be handled
   * @return true, if model is supported
   */
  public abstract boolean supportsModel(IModel model);

  /**
   * Returns true if the factory is able to handle such symbolic models.
   * 
   * @param model
   *          symbolic model which should be handled
   * @return true, if symbolic model is supported
   */
  public abstract boolean supportsModel(ISymbolicModel<?> model);
}
