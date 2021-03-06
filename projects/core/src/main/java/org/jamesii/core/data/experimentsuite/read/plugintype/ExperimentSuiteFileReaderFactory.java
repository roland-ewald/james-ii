/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.experimentsuite.read.plugintype;

import java.io.File;
import java.net.URI;

import org.jamesii.core.data.IFileHandling;
import org.jamesii.core.data.experiment.ExperimentInfo;
import org.jamesii.core.data.experiment.read.plugintype.AbstractExperimentReaderFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;
import org.jamesii.core.util.misc.Files;

/**
 * Super class of all experiment suite reader factories that create file
 * readers.
 * 
 * @author Stefan Rybacki
 */
public abstract class ExperimentSuiteFileReaderFactory extends
    ExperimentSuiteReaderFactory implements IFileHandling {

  /**
   * Serialization ID.
   */
  private static final long serialVersionUID = -9070589108539376252L;

  @Override
  public boolean supportsURI(URI uri) {
    if (uri.getScheme().equals("file-" + getFileEnding())) {
      return true;
    }
    return false;
  }

  @Override
  public int supportsParameters(ParameterBlock params) {
    ExperimentInfo expInfo =
        ParameterBlocks.getSubBlockValue(params,
            AbstractExperimentReaderFactory.EXPERIMENT_INFO);
    try {
      if (expInfo != null
          && expInfo.getIdent() != null
          && supportsURI(Files.getURIFromFile(new File(expInfo.getIdent()
              .toString()))) && expInfo.getDataBase() == null) {
        return 5;
      }
    } catch (Exception e) {
    }
    return 0;
  }

}
