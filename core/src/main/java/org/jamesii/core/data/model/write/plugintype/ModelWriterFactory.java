/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.model.write.plugintype;

import org.jamesii.core.data.IMIMETypeHandling;
import org.jamesii.core.data.IURIHandling;
import org.jamesii.core.data.model.IModelWriter;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.model.IModel;
import org.jamesii.core.model.symbolic.ISymbolicModel;

/**
 * Super class of all model writer factories.
 * 
 * @author Jan Himmelspach
 * 
 */
public abstract class ModelWriterFactory extends Factory<IModelWriter>
    implements IURIHandling, IMIMETypeHandling {

  /**
   * Serialisation ID.
   */
  private static final long serialVersionUID = -9070589108539376222L;

  /**
   * Returns true if the factory is able to handle such models.
   * 
   * @param model
   *          model which should be written
   * @return true, if model is supported
   */
  public abstract boolean supportsModel(IModel model);

  /**
   * Returns true if the factory is able to handle such symbolic models.
   * 
   * @param model
   *          symbolic model which should be written
   * @return true, if symbolic model is supported
   */
  public abstract boolean supportsModel(ISymbolicModel<?> model);

}
