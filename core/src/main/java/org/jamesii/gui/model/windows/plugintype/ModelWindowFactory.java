/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.windows.plugintype;

import org.jamesii.core.factories.Factory;
import org.jamesii.core.factories.IParameterFilterFactory;
import org.jamesii.core.model.symbolic.ISymbolicModel;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.model.ISymbolicModelWindowManager;

/**
 * Factory for windows that display models.
 * 
 */
public abstract class ModelWindowFactory extends
    Factory<ModelWindow<? extends ISymbolicModel<?>>> implements
    IParameterFilterFactory {

  /**
   * Serialisation ID.
   */
  private static final long serialVersionUID = -8558242481683467756L;

  /**
   * Creates a model window.
   * 
   * @param params
   *          parameters that contain all information regarding the model
   * @param mwManager
   *          manager to propagate changes to
   * 
   * @return the model window<? extends i symbolic model<?>>
   */
  public abstract ModelWindow<? extends ISymbolicModel<?>> create(
      ParameterBlock params, ISymbolicModelWindowManager mwManager);

  @Override
  public ModelWindow<? extends ISymbolicModel<?>> create(
      ParameterBlock parameters) {
    return create(parameters,
        (ISymbolicModelWindowManager) parameters.getSubBlock("MW_MANAGER"));
  }

}
