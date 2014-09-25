/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.converter;

import org.jamesii.core.factories.Context;
import org.jamesii.core.model.symbolic.convert.IConverter;
import org.jamesii.core.model.symbolic.convert.plugintype.AbstractConverterFactory;
import org.jamesii.core.model.symbolic.convert.plugintype.ConverterFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.plugins.annotations.Plugin;
import org.jamesii.model.carules.CARulesAntlrDocument;

/**
 * A factory for creating CARulesDocumentConverter objects.
 */
@Plugin(description = "Converter Factory for CA Rules documents.")
public class CARulesDocumentConverterFactory extends ConverterFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 6493642398837291656L;

  @Override
  public IConverter create(ParameterBlock params, Context context) {
    return new CARulesConverter();
  }

  @Override
  public int supportsParameters(ParameterBlock parameters) {
    boolean b1 =
        ((Class<?>) parameters.getSubBlockValue(AbstractConverterFactory.TYPE))
            .isAssignableFrom(CARulesAntlrDocument.class);
    return b1 ? 1 : 0;
  }

}
