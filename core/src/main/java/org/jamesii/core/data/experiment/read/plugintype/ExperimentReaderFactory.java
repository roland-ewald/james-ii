/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.experiment.read.plugintype;

import org.jamesii.core.data.experiment.IExperimentReader;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.factories.IParameterFilterFactory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Base factory for experiment readers.
 * 
 * @author Jan Himmelspach
 * 
 */
public abstract class ExperimentReaderFactory extends
    Factory<IExperimentReader> implements IParameterFilterFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = 4586760559938574923L;

  /**
   * Get experiment reader. An experiment reader reads an experiment description
   * from the data sink it has been designed for. Check the
   * {@link IExperimentReader} interface for more information about the
   * experiment readers.
   * 
   * @param param
   *          parameters
   * @return the reader
   */
  @Override
  public abstract IExperimentReader create(ParameterBlock param);

}
