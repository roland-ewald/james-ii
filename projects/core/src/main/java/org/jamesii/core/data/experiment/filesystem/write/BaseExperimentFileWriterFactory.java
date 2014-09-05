/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.experiment.filesystem.write;

import org.jamesii.core.data.experiment.IExperimentWriter;
import org.jamesii.core.data.experiment.filesystem.ExperimentFileReaderWriter;
import org.jamesii.core.data.experiment.write.plugintype.ExperimentFileWriterFactory;
import org.jamesii.core.factories.Context;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Factory for {@link ExperimentFileReaderWriter}. Returns instances of readers
 * and writers of experiments from and to the file system
 * {@link ExperimentFileReaderWriter}.
 * 
 * @author Roland Ewald
 */
public class BaseExperimentFileWriterFactory extends
    ExperimentFileWriterFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -6102995724647109211L;

  /**
   * Instantiates a new experiment reader writer fs factory.
   */
  public BaseExperimentFileWriterFactory() {
    super();
  }

  @Override
  public IExperimentWriter create(ParameterBlock param, Context context) {
    return new ExperimentFileReaderWriter();
  }

  @Override
  public String getDescription() {
    return "Base Experiments";
  }

  @Override
  public String getFileEnding() {
    return "exp";
  }

}
