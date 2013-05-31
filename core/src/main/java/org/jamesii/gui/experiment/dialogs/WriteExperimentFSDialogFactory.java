/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.data.experiment.write.plugintype.AbstractExperimentWriterFactory;
import org.jamesii.core.data.experiment.write.plugintype.ExperimentFileWriterFactory;
import org.jamesii.core.data.experiment.write.plugintype.ExperimentWriterFactory;
import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.utils.dialogs.IFactoryParameterDialog;
import org.jamesii.gui.utils.dialogs.plugintype.FactoryParameterDialogFactory;

/**
 * Factory for {@link WriteExperimentFSDialog}.
 * 
 * @author Roland Ewald
 */
public class WriteExperimentFSDialogFactory
    extends
    FactoryParameterDialogFactory<ExperimentWriterFactory, ExperimentFileWriterFactory, AbstractExperimentWriterFactory> {

  /** Serialization ID. */
  private static final long serialVersionUID = 898509573241050820L;

  @Override
  public IFactoryParameterDialog<ExperimentFileWriterFactory> createDialog(
      ParameterBlock factoryDialogParameter) {
    return new WriteExperimentFSDialog(factoryDialogParameter);
  }

  @Override
  public Class<? extends AbstractFactory<ExperimentWriterFactory>> getSupportedAbstractFactory() {
    return AbstractExperimentWriterFactory.class;
  }

  @Override
  public List<Class<? extends Factory>> getSupportedFactories() {
    List<Class<? extends Factory>> supportedFactories = new ArrayList<>();
    supportedFactories.add(ExperimentFileWriterFactory.class);
    return supportedFactories;
  }

}
