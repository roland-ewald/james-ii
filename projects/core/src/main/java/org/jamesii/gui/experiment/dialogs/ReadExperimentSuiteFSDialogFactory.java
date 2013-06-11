/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.data.experimentsuite.read.plugintype.AbstractExperimentSuiteReaderFactory;
import org.jamesii.core.data.experimentsuite.read.plugintype.ExperimentSuiteFileReaderFactory;
import org.jamesii.core.data.experimentsuite.read.plugintype.ExperimentSuiteReaderFactory;
import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.utils.dialogs.IFactoryParameterDialog;
import org.jamesii.gui.utils.dialogs.plugintype.FactoryParameterDialogFactory;

/**
 * Factory for {@link ReadExperimentSuiteFSDialog}.
 * 
 * @author Roland Ewald
 */
public class ReadExperimentSuiteFSDialogFactory
    extends
    FactoryParameterDialogFactory<ExperimentSuiteReaderFactory, ExperimentSuiteFileReaderFactory, AbstractExperimentSuiteReaderFactory> {

  /** Serialization ID. */
  private static final long serialVersionUID = 898509573241050820L;

  @Override
  public IFactoryParameterDialog<ExperimentSuiteFileReaderFactory> createDialog(
      ParameterBlock factoryDialogParameter) {
    return new ReadExperimentSuiteFSDialog(factoryDialogParameter);
  }

  @Override
  public Class<? extends AbstractFactory<ExperimentSuiteReaderFactory>> getSupportedAbstractFactory() {
    return AbstractExperimentSuiteReaderFactory.class;
  }

  @Override
  public List<Class<? extends Factory>> getSupportedFactories() {
    List<Class<? extends Factory>> supportedFactories = new ArrayList<>();
    supportedFactories.add(ExperimentSuiteFileReaderFactory.class);
    return supportedFactories;
  }

}
