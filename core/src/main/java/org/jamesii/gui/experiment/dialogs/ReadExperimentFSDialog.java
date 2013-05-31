/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment.dialogs;

import java.awt.Window;
import java.net.URI;

import javax.swing.JFileChooser;

import org.jamesii.core.data.IURIHandling;
import org.jamesii.core.data.experiment.ExperimentInfo;
import org.jamesii.core.data.experiment.read.plugintype.AbstractExperimentReaderFactory;
import org.jamesii.core.data.experiment.read.plugintype.ExperimentFileReaderFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.gui.utils.dialogs.FileHandlingParamDialog;

/**
 * Dialog to read an experiment from the file system.
 * 
 * @author Roland Ewald
 */
public class ReadExperimentFSDialog extends
    FileHandlingParamDialog<ExperimentFileReaderFactory> {

  /**
   * Instantiates a new read experiment fs dialog.
   * 
   * @param parameters
   */
  public ReadExperimentFSDialog(ParameterBlock parameters) {
    super(parameters, "org.jamesii.experiment.dialogs.open",
        JFileChooser.OPEN_DIALOG, false);
  }

  @Override
  public Pair<ParameterBlock, ExperimentFileReaderFactory> getFactoryParameter(
      Window parentWindow) {
    Pair<ParameterBlock, ExperimentFileReaderFactory> result =
        super.getFactoryParameter(parentWindow);

    if (result == null) {
      return null;
    }

    ExperimentInfo info =
        new ExperimentInfo((URI) result.getFirstValue().getSubBlockValue(
            IURIHandling.URI), null);
    result.getFirstValue().addSubBl(
        AbstractExperimentReaderFactory.EXPERIMENT_INFO, info);
    return result;
  }

}
