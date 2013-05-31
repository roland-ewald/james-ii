/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.parametersetup.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.data.model.parameter.read.plugintype.AbstractModelParameterReaderFactory;
import org.jamesii.core.data.model.parameter.read.plugintype.ModelParameterFileReaderFactory;
import org.jamesii.core.data.model.parameter.read.plugintype.ModelParameterReaderFactory;
import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.utils.dialogs.IFactoryParameterDialog;
import org.jamesii.gui.utils.dialogs.plugintype.FactoryParameterDialogFactory;

/**
 * Factory for the parameter dialog for loading model parameters from a file.
 * 
 * @author Stefan Rybacki
 */
public class OpenModelParameterFromFileSystemFactory
    extends
    FactoryParameterDialogFactory<ModelParameterReaderFactory, ModelParameterFileReaderFactory, AbstractModelParameterReaderFactory> {

  /** Serialisation ID. */
  private static final long serialVersionUID = 6542030416574313437L;

  @Override
  public IFactoryParameterDialog<ModelParameterFileReaderFactory> createDialog(
      ParameterBlock factoryDialogParameter) {
    return new OpenModelParameterFromFileSystem(factoryDialogParameter);
  }

  @Override
  public Class<? extends AbstractFactory<ModelParameterReaderFactory>> getSupportedAbstractFactory() {
    return AbstractModelParameterReaderFactory.class;
  }

  @Override
  public List<Class<? extends Factory>> getSupportedFactories() {
    List<Class<? extends Factory>> supportedFactories = new ArrayList<>();
    supportedFactories.add(ModelParameterFileReaderFactory.class);
    return supportedFactories;
  }

}
