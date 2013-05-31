/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.experimentsuite.write.plugintype;

import org.jamesii.core.data.experimentsuite.IExperimentSuiteWriter;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.factories.IParameterFilterFactory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Base factory for experiment suite writers.
 * 
 * @author Jan Himmelspach
 * 
 */
public abstract class ExperimentSuiteWriterFactory extends
    Factory<IExperimentSuiteWriter> implements IParameterFilterFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 8674908788843005008L;

  /**
   * Get experiment suite writer.
   * 
   * @param param
   *          parameters
   * @return experiment suite reader, or null if not supported
   */
  @Override
  public abstract IExperimentSuiteWriter create(ParameterBlock param);

}
