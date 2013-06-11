/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.data.model.ModelFileWriterFactory;
import org.jamesii.core.data.model.write.plugintype.AbstractModelWriterFactory;
import org.jamesii.core.data.model.write.plugintype.ModelWriterFactory;
import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.utils.dialogs.IFactoryParameterDialog;
import org.jamesii.gui.utils.dialogs.plugintype.FactoryParameterDialogFactory;

/**
 * Factory for the parameter dialog for writing a model to a file.
 * 
 * @author Roland Ewald
 */
public class ModelFileWriterParamDialogFactory
    extends
    FactoryParameterDialogFactory<ModelWriterFactory, ModelFileWriterFactory, AbstractModelWriterFactory> {

  /** Serialisation ID. */
  private static final long serialVersionUID = 6542030316574316437L;

  @Override
  public IFactoryParameterDialog<ModelFileWriterFactory> createDialog(
      ParameterBlock factoryDialogParameter) {
    ModelFileWriterParamDialog dialog =
        new ModelFileWriterParamDialog(factoryDialogParameter);
    return dialog;
  }

  @Override
  public Class<? extends AbstractFactory<ModelWriterFactory>> getSupportedAbstractFactory() {
    return AbstractModelWriterFactory.class;
  }

  @Override
  public List<Class<? extends Factory>> getSupportedFactories() {
    List<Class<? extends Factory>> supportedFactories = new ArrayList<>();
    supportedFactories.add(ModelFileWriterFactory.class);
    return supportedFactories;
  }

}
