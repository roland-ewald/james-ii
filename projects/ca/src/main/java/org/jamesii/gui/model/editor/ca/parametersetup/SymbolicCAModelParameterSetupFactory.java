/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.editor.ca.parametersetup;

import org.jamesii.core.factories.Context;
import org.jamesii.core.model.symbolic.ISymbolicModel;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;
import org.jamesii.core.plugins.annotations.Plugin;
import org.jamesii.gui.model.parametersetup.plugintype.ModelParameterSetupWindowFactory;
import org.jamesii.gui.model.parametersetup.plugintype.ModelParameterWindow;
import org.jamesii.gui.model.windows.plugintype.AbstractModelWindowFactory;
import org.jamesii.model.carules.symbolic.ISymbolicCAModel;

/**
 * Factory that creates an editor for setting up parameters of a
 * {@link ISymbolicCAModel}
 * 
 * @author Stefan Rybacki
 * 
 */
@Plugin
public class SymbolicCAModelParameterSetupFactory extends
    ModelParameterSetupWindowFactory {

  /** Serialization ID */
  private static final long serialVersionUID = -3352454430233220843L;

  @Override
  public ModelParameterWindow<? extends ISymbolicModel<?>> create(
      ParameterBlock params, Context context) {
    Object model =
        ParameterBlocks.getSubBlockValue(params,
            AbstractModelWindowFactory.MODEL);
    if (model != null && model instanceof ISymbolicCAModel) {
      return new CAModelParameterWindow((ISymbolicCAModel) model);
    }
    return null;
  }

  @Override
  public int supportsParameters(ParameterBlock params) {
    // check for model of type SymbolicCAModel
    Object model =
        ParameterBlocks.getSubBlockValue(params,
            AbstractModelWindowFactory.MODEL);
    if (model instanceof ISymbolicCAModel) {
      return 1;
    }
    return 0;
  }

}
