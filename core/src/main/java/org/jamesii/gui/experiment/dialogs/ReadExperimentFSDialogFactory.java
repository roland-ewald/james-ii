/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.data.experiment.read.plugintype.AbstractExperimentReaderFactory;
import org.jamesii.core.data.experiment.read.plugintype.ExperimentFileReaderFactory;
import org.jamesii.core.data.experiment.read.plugintype.ExperimentReaderFactory;
import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.utils.dialogs.IFactoryParameterDialog;
import org.jamesii.gui.utils.dialogs.plugintype.FactoryParameterDialogFactory;

/**
 * Factory for {@link ReadExperimentFSDialog}.
 * 
 * @author Roland Ewald
 */
public class ReadExperimentFSDialogFactory
    extends
    FactoryParameterDialogFactory<ExperimentReaderFactory, ExperimentFileReaderFactory, AbstractExperimentReaderFactory> {

  /** Serialization ID. */
  private static final long serialVersionUID = 898509573241050820L;

  @Override
  public IFactoryParameterDialog<ExperimentFileReaderFactory> createDialog(
      ParameterBlock factoryDialogParameter) {
    return new ReadExperimentFSDialog(factoryDialogParameter);
  }

  @Override
  public Class<? extends AbstractFactory<ExperimentReaderFactory>> getSupportedAbstractFactory() {
    return AbstractExperimentReaderFactory.class;
  }

  @Override
  public List<Class<? extends Factory>> getSupportedFactories() {
    List<Class<? extends Factory>> supportedFactories = new ArrayList<>();
    supportedFactories.add(ExperimentFileReaderFactory.class);
    return supportedFactories;
  }

}
