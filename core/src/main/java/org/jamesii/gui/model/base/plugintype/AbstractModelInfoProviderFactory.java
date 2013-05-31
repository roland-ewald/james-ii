/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.base.plugintype;

import java.util.List;

import org.jamesii.core.factories.AbstractFilteringFactory;
import org.jamesii.core.factories.FactoryCriterion;
import org.jamesii.core.model.symbolic.convert.IDocument;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Abstract factory for all model information provider
 * 
 * @author Stefan Rybacki
 */
public class AbstractModelInfoProviderFactory extends
    AbstractFilteringFactory<ModelInfoProviderFactory> {

  /**
   * Serialization ID.
   */
  private static final long serialVersionUID = -8626706310150199524L;

  /**
   * The document that is used to display the model, type: {@link IDocument}.
   */
  public static final String DOCUMENT_CLASS = "document";

  /**
   * Instantiates a new abstract model info provider factory.
   */
  public AbstractModelInfoProviderFactory() {
    addCriteria(new SupportsParameterCriteria());
  }

  /**
   * The Class SupportsParameterCriteria.
   */
  private static class SupportsParameterCriteria extends
      FactoryCriterion<ModelInfoProviderFactory> {

    @Override
    public List<ModelInfoProviderFactory> filter(
        List<ModelInfoProviderFactory> factories, ParameterBlock parameter) {
      for (int i = factories.size() - 1; i >= 0; i--) {
        if (factories.get(i).supportsParameters(parameter) == 0) {
          factories.remove(i);
        }
      }

      return factories;
    }

  }
}
