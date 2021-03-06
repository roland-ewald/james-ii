/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.editor.ca.infoprovider;

import org.jamesii.core.factories.Context;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.plugins.annotations.Plugin;
import org.jamesii.gui.model.base.IModelInfoProvider;
import org.jamesii.gui.model.base.plugintype.AbstractModelInfoProviderFactory;
import org.jamesii.gui.model.base.plugintype.ModelInfoProviderFactory;
import org.jamesii.model.carules.CARulesAntlrDocument;

/**
 * A factory for creating CA rules state highlight information providers.
 */
@Plugin(description = "State Highlight Provider for CA rules based models")
public class CAStateHighlightProviderFactory extends ModelInfoProviderFactory {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 2053711500779068622L;

  /**
   * Creates state highlight information provider.
 * @param params
   *          the parameters
 * @return information provider (null if no provider can be provided for given
   *         parameters)
   */
  @Override
  public IModelInfoProvider create(ParameterBlock params, Context context) {
    return new CAStateHighlightProvider();
  }

  @Override
  public int supportsParameters(ParameterBlock params) {
    Object document =
        params
            .getSubBlockValue(AbstractModelInfoProviderFactory.DOCUMENT_CLASS);
    return (document != null && CARulesAntlrDocument.class.equals(document)) ? 1
        : 0;
  }

}
