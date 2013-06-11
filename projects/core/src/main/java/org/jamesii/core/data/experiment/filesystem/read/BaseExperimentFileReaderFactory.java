/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.experiment.filesystem.read;

import org.jamesii.core.data.experiment.IExperimentReader;
import org.jamesii.core.data.experiment.filesystem.ExperimentFileReaderWriter;
import org.jamesii.core.data.experiment.read.plugintype.ExperimentFileReaderFactory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Factory for {@link ExperimentFileReaderWriter}. Returns instances of readers
 * and writers of experiments from and to the file system
 * {@link ExperimentFileReaderWriter}.
 * 
 * @author Roland Ewald
 */
public class BaseExperimentFileReaderFactory extends
    ExperimentFileReaderFactory {

  /** Serialization ID. */
  private static final long serialVersionUID = 7049357986167436766L;

  /**
   * Instantiates a new experiment reader writer fs factory.
   */
  public BaseExperimentFileReaderFactory() {
    super();
  }

  @Override
  public IExperimentReader create(ParameterBlock param) {
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
