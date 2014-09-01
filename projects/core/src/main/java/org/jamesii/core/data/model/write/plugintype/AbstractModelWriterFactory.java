/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.model.write.plugintype;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.data.model.read.plugintype.AbstractModelReaderFactory;
import org.jamesii.core.data.model.read.plugintype.IMIMEType;
import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.factories.FactoryCriterion;
import org.jamesii.core.model.IModel;
import org.jamesii.core.model.symbolic.ISymbolicModel;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;

/**
 * A factory for creating AbstractModelReaderWriter objects.
 */
public class AbstractModelWriterFactory extends
    AbstractFactory<ModelWriterFactory> {

  /** The serialization ID. */
  private static final long serialVersionUID = -3235573277930663367L;

  /** The model itself. */
  public static final String MODEL = AbstractModelReaderFactory.MODEL;

  /**
   * String identifier for passing the mime type to be used.
   */
  public static final String MIME_TYPE = AbstractModelReaderFactory.MIME_TYPE;

  public static final String URI = AbstractModelReaderFactory.URI;

  /**
   * The Class URICriteria. This criteria filters model reader/writer factories
   * according an URI which has to be passed in a parameter classed "URI". In
   * addition a parameter named "model" can be passed as well.
   */
  private static class URICriteria extends FactoryCriterion<ModelWriterFactory> {

    @Override
    public List<ModelWriterFactory> filter(List<ModelWriterFactory> factories,
        ParameterBlock parameter) {

      List<ModelWriterFactory> filteredFactories = new ArrayList<>();

      boolean hasURI = ParameterBlocks.hasSubBlock(parameter, URI);
      boolean hasModel = ParameterBlocks.hasSubBlock(parameter, MODEL);
      boolean hasMimeType = ParameterBlocks.hasSubBlock(parameter, MIME_TYPE);

      for (int i = 0; i < factories.size(); i++) {

        ModelWriterFactory factory = factories.get(i);
        if ((hasURI && factory.supportsURI((URI) ParameterBlocks
            .getSubBlockValue(parameter, URI)))) {
          filteredFactories.add(factories.get(i));
          continue;
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
          filteredFactories.add(0, factories.get(i));
        }

      }

      return filteredFactories;
    }
  }

  private static class SortByMimeTypeCriteria extends
      FactoryCriterion<ModelWriterFactory> {

    @Override
    public List<ModelWriterFactory> filter(List<ModelWriterFactory> factories,
        ParameterBlock parameter) {

      List<ModelWriterFactory> filteredFactories = new ArrayList<>();

      boolean hasModel = ParameterBlocks.hasSubBlock(parameter, MODEL);

      for (int i = 0; i < factories.size(); i++) {

        ModelWriterFactory factory = factories.get(i);

        if (hasModel
            && (ParameterBlocks.getSubBlockValue(parameter, MODEL) instanceof ISymbolicModel<?>)
            && factory.supportsModel((ISymbolicModel<?>) ParameterBlocks
                .getSubBlockValue(parameter, MODEL))
            && (factory.supportsMIMEType(((ISymbolicModel<?>) ParameterBlocks
                .getSubBlockValue(parameter, MODEL)).getSourceMimeType()))) {
          filteredFactories.add(0, factory);
        } else {
          filteredFactories.add(factory);
        }

      }

      return filteredFactories;
    }
  }

  /**
   * Instantiates a new abstract model reader writer factory.
   */
  public AbstractModelWriterFactory() {
    super();
    addCriteria(new URICriteria());
    addCriteria(new SortByMimeTypeCriteria());
  }
}
