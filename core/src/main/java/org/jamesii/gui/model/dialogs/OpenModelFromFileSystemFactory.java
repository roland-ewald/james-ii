/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.data.model.ModelFileReaderFactory;
import org.jamesii.core.data.model.read.plugintype.AbstractModelReaderFactory;
import org.jamesii.core.data.model.read.plugintype.ModelReaderFactory;
import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.utils.dialogs.IFactoryParameterDialog;
import org.jamesii.gui.utils.dialogs.plugintype.FactoryParameterDialogFactory;

/**
 * Factory for the parameter dialog for loading a model from a file.
 * 
 * @author Stefan Rybacki
 */
public class OpenModelFromFileSystemFactory
    extends
    FactoryParameterDialogFactory<ModelReaderFactory, ModelFileReaderFactory, AbstractModelReaderFactory> {

  /** Serialisation ID. */
  private static final long serialVersionUID = 6542030316574313437L;

  @Override
  public IFactoryParameterDialog<ModelFileReaderFactory> createDialog(
      ParameterBlock factoryDialogParameter) {
    return new OpenModelFromFileSystem(factoryDialogParameter);
  }

  @Override
  public Class<? extends AbstractFactory<ModelReaderFactory>> getSupportedAbstractFactory() {
    return AbstractModelReaderFactory.class;
  }

  @Override
  public List<Class<? extends Factory>> getSupportedFactories() {
    List<Class<? extends Factory>> supportedFactories = new ArrayList<>();
    supportedFactories.add(ModelFileReaderFactory.class);
    return supportedFactories;
  }

}
