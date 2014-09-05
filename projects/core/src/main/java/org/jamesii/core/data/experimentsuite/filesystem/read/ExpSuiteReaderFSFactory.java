/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.experimentsuite.filesystem.read;

import org.jamesii.core.data.experiment.ExperimentInfo;
import org.jamesii.core.data.experiment.read.plugintype.AbstractExperimentReaderFactory;
import org.jamesii.core.data.experimentsuite.IExperimentSuiteReader;
import org.jamesii.core.data.experimentsuite.filesystem.ExperimentSuiteFileReaderWriter;
import org.jamesii.core.data.experimentsuite.read.plugintype.ExperimentSuiteFileReaderFactory;
import org.jamesii.core.factories.Context;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;

/**
 * Factory for
 * {@link org.jamesii.core.data.experiment.filesystem.ExperimentFileReaderWriter}
 * . Returns instances of readers and writers of experiments from and to the
 * file system
 * {@link org.jamesii.core.data.experiment.filesystem.ExperimentFileReaderWriter}
 * .
 * 
 * @author Roland Ewald
 */
public class ExpSuiteReaderFSFactory extends ExperimentSuiteFileReaderFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -5951243738606293258L;

  /**
   * Instantiates a new experiment reader writer fs factory.
   */
  public ExpSuiteReaderFSFactory() {
    super();
  }

  @Override
  public IExperimentSuiteReader create(ParameterBlock param, Context context) {
    return new ExperimentSuiteFileReaderWriter();
  }

  @Override
  public int supportsParameters(ParameterBlock params) {
    ExperimentInfo expInfo =
        ParameterBlocks.getSubBlockValue(params,
            AbstractExperimentReaderFactory.EXPERIMENT_INFO);
    if (expInfo != null && expInfo.getIdent() != null
        && expInfo.getIdent().toString().endsWith(".exp")
        && expInfo.getDataBase() == null) {
      return 5;
    }
    return 0;
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
