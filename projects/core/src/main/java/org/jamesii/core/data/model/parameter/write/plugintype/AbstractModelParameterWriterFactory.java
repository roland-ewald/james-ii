/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.model.parameter.write.plugintype;

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
 * A factory for creating AbstractModelParameterWriter objects.
 * 
 * @author Jan Himmelspach
 */
public class AbstractModelParameterWriterFactory extends
    AbstractFactory<ModelParameterWriterFactory> {

  /** The serialization ID. */
  private static final long serialVersionUID = -3235573277930663367L;

  /** The parameter themself. */
  public static final String PARAMETER = "parameter";

  /**
   * Identification string which should be used in model readers to identify the
   * block which contain such a basic initialization.<br/>
   * Anything in this block can be overwritten by explicit parameters.
   */
  public static final String INITIALIZATION = "parameter:initialization";

  /** the model itself */
  public static final String MODEL = "model";

  /**
   * The Class URICriteria. This criteria filters model reader/writer factories
   * according an URI which has to be passed in a parameter classed "URI". In
   * addition a parameter named "model" can be passed as well.
   */
  private static class URICriteria extends
      FactoryCriterion<ModelParameterWriterFactory> {

    @Override
    public List<ModelParameterWriterFactory> filter(
        List<ModelParameterWriterFactory> factories, ParameterBlock parameter) {

      List<ModelParameterWriterFactory> filteredFactories = new ArrayList<>();

      boolean hasURI = ParameterBlocks.hasSubBlock(parameter, IURIHandling.URI);
      boolean hasModel = ParameterBlocks.hasSubBlock(parameter, MODEL);
      for (int i = 0; i < factories.size(); i++) {

        try {

          ModelParameterWriterFactory factory = factories.get(i);
          if ((hasURI && factory.supportsURI((URI) ParameterBlocks
              .getSubBlockValue(parameter, IURIHandling.URI)))) {
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

        } catch (Exception e) {
          // Exceptions here can be, e.g., abstract method errors if there are
          // incomplete classes
          report(e);
        }
      }

      return filteredFactories;
    }
  }

  /**
   * Instantiates a new abstract model reader writer factory.
   */
  public AbstractModelParameterWriterFactory() {
    super();
    addCriteria(new URICriteria());
  }
}
