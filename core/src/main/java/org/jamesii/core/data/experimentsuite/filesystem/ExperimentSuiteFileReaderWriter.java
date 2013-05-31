/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.experimentsuite.filesystem;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.jamesii.core.data.experiment.ExperimentInfo;
import org.jamesii.core.data.experiment.read.plugintype.AbstractExperimentReaderFactory;
import org.jamesii.core.data.experimentsuite.ExperimentSuiteInfo;
import org.jamesii.core.data.experimentsuite.IExperimentSuiteReader;
import org.jamesii.core.data.experimentsuite.IExperimentSuiteWriter;
import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.experiments.ExperimentSuite;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;
import org.jamesii.core.util.misc.Files;

/**
 * This is a simple experiment reader/writer for file I/O. It only uses the
 * identification URI given in {@link ExperimentInfo}. This reader/writer uses
 * the built-in system to decode/code Java Beans to XML (see {@link XMLEncoder},
 * {@link XMLDecoder}).
 * 
 * @author Roland Ewald
 */
public class ExperimentSuiteFileReaderWriter implements IExperimentSuiteReader,
    IExperimentSuiteWriter {

  @Override
  public List<ExperimentSuiteInfo> getAvailableExperimentSuites(
      List<URI> locationList) {
    return null;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <E extends BaseExperiment> ExperimentSuite<E> readExperimentSuite(
      Class<E> expType, ParameterBlock param) throws IOException {
    ExperimentInfo expInfo =
        ParameterBlocks.getSubBlockValue(param,
            AbstractExperimentReaderFactory.EXPERIMENT_INFO);
    return (ExperimentSuite<E>) Files.load(Files.getFileFromURI(
        expInfo.getIdent()).getAbsolutePath());
  }

  @Override
  public <E extends BaseExperiment> void writeExperimentSuite(
      ParameterBlock param, ExperimentSuite<E> experimentSuite)
      throws IOException {
    ExperimentInfo expInfo =
        ParameterBlocks.getSubBlockValue(param,
            AbstractExperimentReaderFactory.EXPERIMENT_INFO);
    Files.save(experimentSuite, Files.getFileFromURI(expInfo.getIdent())
        .getAbsolutePath());
  }

}
