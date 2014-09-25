/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.editor.ca.wolfram;

import org.jamesii.core.model.symbolic.ISymbolicModel;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.plugins.annotations.Plugin;
import org.jamesii.gui.model.ISymbolicModelWindowManager;
import org.jamesii.gui.model.windows.plugintype.AbstractModelWindowFactory;
import org.jamesii.gui.model.windows.plugintype.ModelWindow;
import org.jamesii.gui.model.windows.plugintype.ModelWindowFactory;
import org.jamesii.model.carules.symbolic.ISymbolicCAModel;
import org.jamesii.model.carules.symbolic.ISymbolicCAModelInformation;

/**
 * Factory for creating the CA editor.
 * 
 * @author Mathias Süß
 */
@Plugin(description = "Graphical editor for Wolfram rule Cellular Automata")
public class CAWolframRuleEditorFactory extends ModelWindowFactory {

  /** Serial version ID. */
  private static final long serialVersionUID = 4697119696490411997L;

  @Override
  public ModelWindow<? extends ISymbolicModel<?>> create(ParameterBlock params,
      ISymbolicModelWindowManager mlManager) {
    return new CAWolframRuleEditorWindow(
        (ISymbolicCAModel) params
            .getSubBlockValue(AbstractModelWindowFactory.MODEL),
        mlManager);
  }

  @Override
  public int supportsParameters(ParameterBlock params) {
    Object model = params.getSubBlockValue(AbstractModelWindowFactory.MODEL);
    if (params.getSubBlockValue(AbstractModelWindowFactory.MODEL) instanceof ISymbolicCAModel) {
      ISymbolicCAModel scam = (ISymbolicCAModel) model;

      if (scam == null || scam.getAsDataStructure() == null) {
        return 0;
      }

      ISymbolicCAModelInformation modelInfo = scam.getAsDataStructure();

      if (modelInfo.isWolfram()) {
        return 1;
      }
    }

    return 0;
  }

}
