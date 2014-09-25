/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.grid.plugintype;

import java.util.Iterator;
import java.util.List;

import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.factories.FactoryCriterion;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.plugins.annotations.PluginType;

/**
 * An abstract factory for creating grid object factories.
 * 
 * @author Jan Himmelspach
 */
@PluginType(
    description = "Grid realization plugins for the CA (rule based) formalism.")
public class AbstractGridFactory extends AbstractFactory<BaseGridFactory> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -9180871893400183684L;

  /**
   * The Constant DIMENSION.
   */
  public static final String DIMENSION = "DIMENSION";

  /**
   * This criteria selects factories according to the dimensions they support.
   * 
   * @author Jan Himmelspach
   * 
   */
  private static class DimensionCriteria extends
      FactoryCriterion<BaseGridFactory> {

    @Override
    public List<BaseGridFactory> filter(List<BaseGridFactory> factories,
        ParameterBlock parameter) {

      Integer dim = parameter.getSubBlockValue(DIMENSION);

      // iterate over all available factories
      Iterator<BaseGridFactory> iPF = factories.iterator();

      while (iPF.hasNext()) {
        BaseGridFactory pf = iPF.next();
        boolean supports = false;
        if (pf.getDimension() == dim) {
          supports = true;
        }

        if (!supports) {
          iPF.remove();
        }

      }

      return factories;
    }
  }

  /**
   * Create a new instance of the AbstractProcessorFactory Adds several criteria
   * for filtering the list of available factories
   * 
   */
  public AbstractGridFactory() {
    super();

    // criterias used for filtering the list of available grid factories
    addCriteria(new DimensionCriteria());
  }

}
