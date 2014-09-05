/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.parametersetup.plugintype;

import org.jamesii.core.factories.Context;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.factories.IParameterFilterFactory;
import org.jamesii.core.model.symbolic.ISymbolicModel;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Factory for windows that allow to setup parameters of models.
 * 
 * @author Jan Himmelspach
 */
public abstract class ModelParameterSetupWindowFactory extends
    Factory<ModelParameterWindow<? extends ISymbolicModel<?>>> implements
    IParameterFilterFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = 7978140893376388238L;

  /**
   * Creates a model parameter setup window.
 * @param params
   *          parameters that contain all information regarding the model
 * @return the model parameter window<? extends i symbolic model<?>>
   */
  @Override
  public abstract ModelParameterWindow<? extends ISymbolicModel<?>> create(
      ParameterBlock params, Context context);

}
