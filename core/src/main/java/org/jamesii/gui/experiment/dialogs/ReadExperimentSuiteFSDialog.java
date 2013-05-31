/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment.dialogs;

import java.awt.Window;
import java.net.URI;

import javax.swing.JFileChooser;

import org.jamesii.core.data.IURIHandling;
import org.jamesii.core.data.experiment.ExperimentInfo;
import org.jamesii.core.data.experimentsuite.read.plugintype.AbstractExperimentSuiteReaderFactory;
import org.jamesii.core.data.experimentsuite.read.plugintype.ExperimentSuiteFileReaderFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.gui.utils.dialogs.FileHandlingParamDialog;

/**
 * Dialog to open an experiment suite from file.
 * 
 * @author Roland Ewald
 */
public class ReadExperimentSuiteFSDialog extends
    FileHandlingParamDialog<ExperimentSuiteFileReaderFactory> {

  /**
   * Instantiates a new read experiment suite fs dialog.
   * 
   * @param parameters
   */
  public ReadExperimentSuiteFSDialog(ParameterBlock parameters) {
    super(parameters, "org.jamesii.experimentsuite.dialogs.open",
        JFileChooser.OPEN_DIALOG, false);
  }

  @Override
  public Pair<ParameterBlock, ExperimentSuiteFileReaderFactory> getFactoryParameter(
      Window parentWindow) {
    Pair<ParameterBlock, ExperimentSuiteFileReaderFactory> result =
        super.getFactoryParameter(parentWindow);

    if (result == null) {
      return null;
    }

    ExperimentInfo info =
        new ExperimentInfo((URI) result.getFirstValue().getSubBlockValue(
            IURIHandling.URI), null);
    result.getFirstValue().addSubBl(
        AbstractExperimentSuiteReaderFactory.EXPERIMENT_INFO, info);
    return result;
  }

}
