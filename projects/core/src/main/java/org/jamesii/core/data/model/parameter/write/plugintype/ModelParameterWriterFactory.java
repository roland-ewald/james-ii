/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.model.parameter.write.plugintype;

import java.net.URI;

import org.jamesii.core.data.model.parameter.IModelParameterWriter;
import org.jamesii.core.factories.Context;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.model.IModel;
import org.jamesii.core.model.symbolic.ISymbolicModel;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Super class of all model parameter writer factories.
 * 
 * @author Jan Himmelspach
 * 
 */
public abstract class ModelParameterWriterFactory extends
    Factory<IModelParameterWriter> {

  /**
   * Serialization ID.
   */
  private static final long serialVersionUID = -9070589108539376242L;

  /**
   * Create model writer.
 * @param params
   *          parameters, may be null
 * @return model writer
   */
  @Override
  public abstract IModelParameterWriter create(ParameterBlock params, Context context);

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

  /**
   * Returns true if the factory is able to handle models type and data source
   * as given by the URI.
   * 
   * @param uri
   *          the URI to be handled
   * @return true, if URI is supported
   */
  public abstract boolean supportsURI(URI uri);

}
