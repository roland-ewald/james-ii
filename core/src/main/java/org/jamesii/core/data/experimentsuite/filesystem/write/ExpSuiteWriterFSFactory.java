/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.experimentsuite.filesystem.write;

import org.jamesii.core.data.experiment.filesystem.ExperimentFileReaderWriter;
import org.jamesii.core.data.experimentsuite.IExperimentSuiteWriter;
import org.jamesii.core.data.experimentsuite.filesystem.ExperimentSuiteFileReaderWriter;
import org.jamesii.core.data.experimentsuite.write.plugintype.ExperimentSuiteFileWriterFactory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Factory for {@link ExperimentFileReaderWriter}. Returns instances of readers
 * and writers of experiments from and to the file system
 * {@link ExperimentFileReaderWriter}.
 * 
 * @author Roland Ewald
 */
public class ExpSuiteWriterFSFactory extends ExperimentSuiteFileWriterFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1916962014455010412L;

  /**
   * Instantiates a new experiment reader writer fs factory.
   */
  public ExpSuiteWriterFSFactory() {
    super();
  }

  @Override
  public IExperimentSuiteWriter create(ParameterBlock param) {
    return new ExperimentSuiteFileReaderWriter();
  }

  @Override
  public String getDescription() {
    return "Experiment Suites";
  }

  @Override
  public String getFileEnding() {
    return "exps";
  }

}
