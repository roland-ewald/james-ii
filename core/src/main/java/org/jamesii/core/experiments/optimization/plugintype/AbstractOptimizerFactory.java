/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization.plugintype;

import java.util.Iterator;
import java.util.List;

import org.jamesii.core.experiments.optimization.parameter.Configuration;
import org.jamesii.core.experiments.optimization.parameter.OptimizationProblemDefinition;
import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.factories.FactoryCriterion;
import org.jamesii.core.model.variables.IQuantitativeVariable;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * The abstract factory for the optimizer algorithms.
 * 
 * @author Jan Himmelspach
 */
public class AbstractOptimizerFactory extends AbstractFactory<OptimizerFactory> {

  /**
   * The Class SupportOrderedSetCriterion.
   */
  private class SupportOrderedSetCriterion extends
      FactoryCriterion<OptimizerFactory> {

    @Override
    public List<OptimizerFactory> filter(List<OptimizerFactory> factories,
        ParameterBlock parameter) {
      // iterate over all available factories
      Iterator<OptimizerFactory> iOF = factories.iterator();

      while (iOF.hasNext()) {
        OptimizerFactory of = iOF.next();
        boolean ok = false;
        // IOrderedSet is needed of optimizer and supported by definition
        if (of.needsOrderedSet()) {
          Configuration c = parameter.getSubBlockValue("factors");
          if (c.factorsInstanceof(IQuantitativeVariable.class)) {
            ok = true;
          }
        } else {
          ok = true;
        }
        if (!ok) {
          iOF.remove();
        }
      }
      return factories;
    }

  }

  /**
   * The Class SupportPredefinedConfigurationsCriteria.
   */
  private class SupportPredefinedConfigurationsCriteria extends
      FactoryCriterion<OptimizerFactory> {

    @Override
    public List<OptimizerFactory> filter(List<OptimizerFactory> factories,
        ParameterBlock parameter) {
      // iterate over all available factories
      Iterator<OptimizerFactory> iOF = factories.iterator();

      while (iOF.hasNext()) {
        OptimizerFactory of = iOF.next();
        boolean ok = false;
        // nedd configurations is needed of optimizer and supported by
        // definition
        ok = of.supportsPredefinedConfigurations(parameter);

        if (!ok) {
          iOF.remove();
        }
      }
      return factories;
    }

  }

  private class SupportsMultipleObjectiveCriteria extends
      FactoryCriterion<OptimizerFactory> {

    @Override
    public List<OptimizerFactory> filter(List<OptimizerFactory> factories,
        ParameterBlock paramBlock) {
      // iterate over all available factories
      Iterator<OptimizerFactory> iOF = factories.iterator();
      while (iOF.hasNext()) {
        OptimizerFactory of = iOF.next();
        boolean ok = of.supportsMultipleCriteria();

        if (!ok && paramBlock.getValue() != null
            && paramBlock.getValue() instanceof OptimizationProblemDefinition) {
          ok =
              ((OptimizationProblemDefinition) paramBlock.getValue())
                  .isSingleObjective();
        }

        if (!ok) {
          iOF.remove();
        }
      }
      return factories;
    }

  }

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 8274206153504839650L;

  /**
   * Instantiates a new abstract optimizer factory.
   */
  public AbstractOptimizerFactory() {
    super();
    addCriteria(new SupportOrderedSetCriterion());
    addCriteria(new SupportPredefinedConfigurationsCriteria());
    addCriteria(new SupportsMultipleObjectiveCriteria());
  }

}
