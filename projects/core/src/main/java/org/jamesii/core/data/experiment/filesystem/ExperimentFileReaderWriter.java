/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.experiment.filesystem;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.jamesii.core.data.experiment.ExperimentInfo;
import org.jamesii.core.data.experiment.IExperimentReader;
import org.jamesii.core.data.experiment.IExperimentWriter;
import org.jamesii.core.data.experiment.read.plugintype.AbstractExperimentReaderFactory;
import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;
import org.jamesii.core.util.misc.Files;

/**
 * This is a simple experiment reader/writer for file I/O. It only uses the
 * identification URI given in {@link ExperimentInfo}. This reader/writer uses
 * the built-in system to decode/code Java Beans to XML (see
 * {@link java.beans.XMLEncoder}, {@link java.beans.XMLDecoder}).
 * 
 * @author Roland Ewald
 */
public class ExperimentFileReaderWriter implements IExperimentReader,
    IExperimentWriter {

  @Override
  public List<ExperimentInfo> getAvailableExperiments(List<URI> datasources) {
    return null;
  }

  @Override
  public BaseExperiment readExperiment(ParameterBlock param) throws IOException {
    ExperimentInfo expInfo =
        param.getSubBlockValue(AbstractExperimentReaderFactory.EXPERIMENT_INFO);
    return (BaseExperiment) Files.load(Files.getFileFromURI(expInfo.getIdent())
        .getAbsolutePath());
  }

  @Override
  public void writeExperiment(ParameterBlock param, BaseExperiment experiment)
      throws IOException {
    ExperimentInfo expInfo =
        ParameterBlocks.getSubBlockValue(param,
            AbstractExperimentReaderFactory.EXPERIMENT_INFO);
    Files.save(experiment, Files.getFileFromURI(expInfo.getIdent())
        .getAbsolutePath());
  }

}
