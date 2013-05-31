/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.experiment.write.plugintype;

import org.jamesii.core.data.experiment.IExperimentWriter;
import org.jamesii.core.data.experimentsuite.IExperimentSuiteWriter;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.factories.IParameterFilterFactory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Base factory for experiment writers.
 * 
 * @author Jan Himmelspach
 * 
 */
public abstract class ExperimentWriterFactory extends
    Factory<IExperimentWriter> implements IParameterFilterFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -7606719993616690842L;

  /**
   * Get an experiment writer. An experiment writer stores an experiment
   * description to the data sink the writer created by this factory has been
   * designed for. There are no format restrictions for experiment definitions.
   * Check the {@link IExperimentWriter} interface for more details.
   * 
   * @param param
   *          parameters
   * @return the writer
   */
  @Override
  public abstract IExperimentWriter create(ParameterBlock param);

  /**
   * Used to determine whether this factory also provides experiment suite
   * readers or writers.
   * 
   * @return true if this is supported, otherwise false.
   */
  public boolean supportsExperimentSuites() {
    return false;
  }

  /**
   * Get experiment suite writer.
   * 
   * @param param
   *          parameters
   * @return experiment suite writer, or null if not supported
   */
  public IExperimentSuiteWriter getSuiteWriter(ParameterBlock param) {
    return null;
  }

}
