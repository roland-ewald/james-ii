/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.data.experimentsuite.write.plugintype.AbstractExperimentSuiteWriterFactory;
import org.jamesii.core.data.experimentsuite.write.plugintype.ExperimentSuiteFileWriterFactory;
import org.jamesii.core.data.experimentsuite.write.plugintype.ExperimentSuiteWriterFactory;
import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.utils.dialogs.IFactoryParameterDialog;
import org.jamesii.gui.utils.dialogs.plugintype.FactoryParameterDialogFactory;

/**
 * Factory for {@link WriteExperimentSuiteFSDialog}.
 * 
 * @author Roland Ewald
 */
public class WriteExperimentSuiteFSDialogFactory
    extends
    FactoryParameterDialogFactory<ExperimentSuiteWriterFactory, ExperimentSuiteFileWriterFactory, AbstractExperimentSuiteWriterFactory> {

  /** Serialization ID. */
  private static final long serialVersionUID = 898509573241050820L;

  @Override
  public IFactoryParameterDialog<ExperimentSuiteFileWriterFactory> createDialog(
      ParameterBlock factoryDialogParameter) {
    return new WriteExperimentSuiteFSDialog(factoryDialogParameter);
  }

  @Override
  public Class<? extends AbstractFactory<ExperimentSuiteWriterFactory>> getSupportedAbstractFactory() {
    return AbstractExperimentSuiteWriterFactory.class;
  }

  @Override
  public List<Class<? extends Factory>> getSupportedFactories() {
    List<Class<? extends Factory>> supportedFactories = new ArrayList<>();
    supportedFactories.add(ExperimentSuiteFileWriterFactory.class);
    return supportedFactories;
  }

}
