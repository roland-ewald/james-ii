/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.experimentsuite.read.plugintype;

import org.jamesii.core.data.experimentsuite.IExperimentSuiteReader;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.factories.IParameterFilterFactory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Base factory for experiment suite readers.
 * 
 * @author Jan Himmelspach
 * 
 */
public abstract class ExperimentSuiteReaderFactory extends
    Factory<IExperimentSuiteReader> implements IParameterFilterFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -855342061062218051L;

  /**
   * Get experiment suite reader.
   * 
   * @param param
   *          parameters
   * @return experiment suite reader, or null if not supported
   */
  @Override
  public abstract IExperimentSuiteReader create(ParameterBlock param);

}
