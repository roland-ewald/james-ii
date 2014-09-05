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
import org.jamesii.core.factories.Context;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.utils.dialogs.IFactoryParameterDialog;

/**
 * Super class of all factories that create dialogs for any factory. E.g., for
 * experiment reader/writer, model reader/writer etc.
 * 
 * @param <F>
 *          the type of the base factory
 * @author Roland Ewald
 * @param <X>
 * @param <AF>
 */
public abstract class FactoryParameterDialogFactory<X extends Factory<?>, F extends X, AF extends AbstractFactory<X>>
    extends Factory<IFactoryParameterDialog<F>> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 3599194317289978292L;

  /**
   * Creates factory dialog.
 * @param factoryDialogParameter
   *          the parameters for the selection of a proper dialog
 * @return dialog to create factory parameter
   */
  @Override
  public final IFactoryParameterDialog<F> create(
      ParameterBlock factoryDialogParameter, Context context) {
    // defensive copy of parameter block since it might be altered in this
    // method
    factoryDialogParameter = factoryDialogParameter.getCopy();

    List<? extends F> factories =
        factoryDialogParameter
            .getSubBlockValue(AbstractFactoryParameterDialogFactory.CONCRETE_FACTORIES);

    List<F> passFactories = new ArrayList<>();
    List<Class<? extends Factory>> suppFactories = getSupportedFactories();

    // Taking care of special case (support of *all* factories)
    if (suppFactories == null) {
      factoryDialogParameter.addSubBl(
          AbstractFactoryParameterDialogFactory.CONCRETE_FACTORIES,
          new ArrayList<>(factories));
      return createDialog(factoryDialogParameter);
    }

    for (F factory : factories) {
      for (Class<? extends Factory> sFactory : suppFactories) {
        if (sFactory.isAssignableFrom(factory.getClass())) {
          passFactories.add(factory);
          break;
        }
      }
    }

    factoryDialogParameter
        .addSubBl(AbstractFactoryParameterDialogFactory.CONCRETE_FACTORIES,
            passFactories);
    return createDialog(factoryDialogParameter);
  }

  /**
   * Creates a new FactoryParameterDialog object.
   * 
   * @param factoryDialogParameter
   *          the factory dialog parameter
   * 
   * @return the i factory parameter dialog< f>
   */
  protected abstract IFactoryParameterDialog<F> createDialog(
      ParameterBlock factoryDialogParameter);

  /**
   * Abstract factory class this dialog supports.
   * 
   * @return abstract factory class
   */
  public abstract Class<? extends AbstractFactory<X>> getSupportedAbstractFactory();

  /**
   * Get list of supported factories.
   * 
   * @return list of supported factories, null if all factories for this
   *         abstract factory are supported
   */
  public abstract List<Class<? extends Factory>> getSupportedFactories();

}
