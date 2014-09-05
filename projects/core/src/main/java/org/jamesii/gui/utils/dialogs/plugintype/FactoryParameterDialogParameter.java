/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.dialogs.plugintype;

import java.util.List;

import org.jamesii.SimSystem;
import org.jamesii.core.factories.Context;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Parameters to configure the dialog for a given factory.
 * 
 * @author Stefan Rybacki
 */
public final class FactoryParameterDialogParameter {

  /**
   * Create a {@link ParameterBlock} usable with
   * {@link FactoryParameterDialogFactory#create(ParameterBlock, Context)}.
   * 
   * @param <F>
   *          the general factory class (to ensure the provided list matches
   *          that class)
   * @param factoryClass
   *          general factory class
   * @param concrFactories
   *          class of the factory which shall be used
   * @return generated {@link ParameterBlock}
   */
  public static <F extends Factory<?>> ParameterBlock getParameterBlock(
      Class<F> factoryClass, List<? extends F> concrFactories) {
    if (concrFactories == null) {
      concrFactories = SimSystem.getRegistry().getFactories(factoryClass);
    }

    if (concrFactories == null) {
      throw new RuntimeException(
          "Was not able to find any factories for given factory class: "
              + factoryClass);
    }

    ParameterBlock block = new ParameterBlock();
    block.addSubBl(
        AbstractFactoryParameterDialogFactory.ABSTRACT_FACTORY_CLASS,
        SimSystem.getRegistry().getAbstractFactoryForFactory(factoryClass))
        .addSubBl(AbstractFactoryParameterDialogFactory.CONCRETE_FACTORIES,
            concrFactories);
    return block;
  }

  private FactoryParameterDialogParameter() {
  }

}
