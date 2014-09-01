/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.model.read.plugintype;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.data.IURIHandling;
import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.factories.FactoryCriterion;
import org.jamesii.core.model.IModel;
import org.jamesii.core.model.symbolic.ISymbolicModel;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;

/**
 * A factory for creating AbstractModelReaderWriter objects.
 */
public class AbstractModelReaderFactory extends
    AbstractFactory<ModelReaderFactory> {

  /** The serialization ID. */
  private static final long serialVersionUID = -3235573277930663367L;

  /** String identifier for passing the model itself. */
  public static final String MODEL = "model";

  /** String identifier for passing mime type to be used **/
  public static final String MIME_TYPE = "mimeType";

  /** String identifier for passing the model URI. */
  public static final String URI = IURIHandling.URI;

  /**
   * The Class URICriteria. This criteria filters model reader/writer factories
   * according an URI which has to be passed in a parameter classed "URI". In
   * addition a parameter named "model" can be passed as well.
   */
  private static class SupportCriteria extends
      FactoryCriterion<ModelReaderFactory> {

    @Override
    public List<ModelReaderFactory> filter(List<ModelReaderFactory> factories,
        ParameterBlock parameter) {

      List<ModelReaderFactory> filteredFactories = new ArrayList<>();

      boolean hasURI = ParameterBlocks.hasSubBlock(parameter, URI);
      boolean hasModel = ParameterBlocks.hasSubBlock(parameter, MODEL);
      boolean hasMimeType = ParameterBlocks.hasSubBlock(parameter, MIME_TYPE);

      for (int i = 0; i < factories.size(); i++) {

        ModelReaderFactory factory = factories.get(i);

        if (hasURI) {
          URI uri = ParameterBlocks.getSubBlockValue(parameter, URI);
          if ((uri == null)) {
            continue;
          }
          if (factory.supportsURI(uri)) {
            filteredFactories.add(factories.get(i));
            continue;
          }
        }

        if (hasModel
            && (ParameterBlocks.getSubBlockValue(parameter, MODEL) instanceof IModel)
            && (factory.supportsModel((IModel) ParameterBlocks
                .getSubBlockValue(parameter, MODEL)))) {
          filteredFactories.add(factories.get(i));
          continue;
        }

        if (hasModel
            && (ParameterBlocks.getSubBlockValue(parameter, MODEL) instanceof ISymbolicModel<?>)
            && factory.supportsModel((ISymbolicModel<?>) ParameterBlocks
                .getSubBlockValue(parameter, MODEL))) {
          filteredFactories.add(factories.get(i));
          continue;
        }

        if (hasMimeType
            && factory.supportsMIMEType((IMIMEType) parameter
                .getSubBlockValue(MIME_TYPE))) {
          filteredFactories.add(factories.get(i));
        }

      }

      return filteredFactories;
    }
  }

  /**
   * Instantiates a new abstract model reader writer factory.
   */
  public AbstractModelReaderFactory() {
    super();
    addCriteria(new SupportCriteria());
  }
}
