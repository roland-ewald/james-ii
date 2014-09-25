/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.parameter;

import org.jamesii.core.data.model.parameter.IModelParameterReader;
import org.jamesii.core.data.model.parameter.read.plugintype.ModelParameterFileReaderFactory;
import org.jamesii.core.factories.Context;
import org.jamesii.core.model.IModel;
import org.jamesii.core.model.symbolic.ISymbolicModel;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.plugins.annotations.Plugin;
import org.jamesii.model.carules.CARulesModel;
import org.jamesii.model.carules.symbolic.ISymbolicCAModel;

/**
 * The Class CAModelParameterReaderFactory.
 * 
 * @author Jan Himmelspach
 */
@Plugin(description = "Reader for CA model parameters.")
public class CAModelParameterFileReaderFactory extends
    ModelParameterFileReaderFactory {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 6193249743262345127L;

  @Override
  public IModelParameterReader create(ParameterBlock params, Context context) {
    return new CAModelParameterFileReader();
  }

  @Override
  public boolean supportsModel(IModel model) {
    return (model != null && CARulesModel.class.equals(model.getClass()));
  }

  @Override
  public boolean supportsModel(ISymbolicModel<?> model) {
    if (model == null) {
      return false;
    }
    return model instanceof ISymbolicCAModel
        && ((ISymbolicCAModel) model).getAsDataStructure().getDimensions() == 2;
  }

  @Override
  public boolean supportsModel(Class<?> modelClass) {
    return false;
  }

  @Override
  public String getDescription() {
    return "Parameter File Reader for Cellular Automata";
  }

  @Override
  public String getFileEnding() {
    return "cap";
  }

}
