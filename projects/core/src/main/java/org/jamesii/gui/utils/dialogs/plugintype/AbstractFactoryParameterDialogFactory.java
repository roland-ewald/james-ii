/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.dialogs.plugintype;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.factories.FactoryCriterion;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Abstract factory to manage factory dialogs.
 * 
 * @author Roland Ewald
 */
public class AbstractFactoryParameterDialogFactory extends
    AbstractFactory<FactoryParameterDialogFactory<?, ?, ?>> {

  /**
   * Serialisation ID.
   */
  private static final long serialVersionUID = -1176359242086820885L;

  /**
   * The base factory.
   */
  public static final String ABSTRACT_FACTORY_CLASS = "factoryClass";

  /**
   * The concrete factory.
   */
  public static final String CONCRETE_FACTORIES = "concreteFactories";

  /**
   * Default constructor.
   */
  public AbstractFactoryParameterDialogFactory() {
    super();
    addCriteria(new FactoryClassCriteria());
  }
}

/**
 * Removes all dialogs that are not available for the given factory.
 * 
 * 
 * @author Roland Ewald
 * 
 */
class FactoryClassCriteria extends
    FactoryCriterion<FactoryParameterDialogFactory<?, ?, ?>> {

  @Override
  public List<FactoryParameterDialogFactory<?, ?, ?>> filter(
      List<FactoryParameterDialogFactory<?, ?, ?>> factories,
      ParameterBlock parameter) {

    ArrayList<FactoryParameterDialogFactory<?, ?, ?>> filteredFactories =
        new ArrayList<>();

    Class<? extends AbstractFactory<?>> targetAFClass =
        parameter
            .getSubBlockValue(AbstractFactoryParameterDialogFactory.ABSTRACT_FACTORY_CLASS);
    List<? extends Factory> targetFClasses =
        parameter
            .getSubBlockValue(AbstractFactoryParameterDialogFactory.CONCRETE_FACTORIES);

    for (FactoryParameterDialogFactory<?, ?, ?> factory : factories) {
      Class<? extends AbstractFactory<?>> supportedAFactory =
          factory.getSupportedAbstractFactory();
      List<Class<? extends Factory>> supportedFactories =
          factory.getSupportedFactories();

      if (!targetAFClass.equals(supportedAFactory)) {
        continue;
      }

      // All factory implementations are supported if null is returned
      if (supportedFactories == null || targetFClasses == null
          || targetFClasses.isEmpty()) {
        filteredFactories.add(factory);
        continue;
      }

      for (Class<? extends Factory> supportedFactory : supportedFactories) {
        // If factory or a superclass is directly supported, add it.
        boolean added = false;
        for (Factory targetFClass : targetFClasses) {
          if (supportedFactory.isAssignableFrom(targetFClass.getClass())) {
            filteredFactories.add(factory);
            added = true;
            break;
          }
        }
        if (added) {
          break;
        }
      }
    }

    return filteredFactories;
  }
}
