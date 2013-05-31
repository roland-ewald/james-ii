/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.parametersetup.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.data.model.parameter.write.plugintype.AbstractModelParameterWriterFactory;
import org.jamesii.core.data.model.parameter.write.plugintype.ModelParameterFileWriterFactory;
import org.jamesii.core.data.model.parameter.write.plugintype.ModelParameterWriterFactory;
import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.utils.dialogs.IFactoryParameterDialog;
import org.jamesii.gui.utils.dialogs.plugintype.FactoryParameterDialogFactory;

/**
 * Factory for the parameter dialog for writing model paramters to a file.
 * 
 * @author Roland Ewald
 */
public class ModelParameterFileWriterParamDialogFactory
    extends
    FactoryParameterDialogFactory<ModelParameterWriterFactory, ModelParameterFileWriterFactory, AbstractModelParameterWriterFactory> {

  /** Serialisation ID. */
  private static final long serialVersionUID = 6542030316574316437L;

  @Override
  public IFactoryParameterDialog<ModelParameterFileWriterFactory> createDialog(
      ParameterBlock factoryDialogParameter) {
    return new ModelParameterFileWriterParamDialog(factoryDialogParameter);
  }

  @Override
  public Class<? extends AbstractFactory<ModelParameterWriterFactory>> getSupportedAbstractFactory() {
    return AbstractModelParameterWriterFactory.class;
  }

  @Override
  public List<Class<? extends Factory>> getSupportedFactories() {
    List<Class<? extends Factory>> supportedFactories = new ArrayList<>();
    supportedFactories.add(ModelParameterFileWriterFactory.class);
    return supportedFactories;
  }

}
