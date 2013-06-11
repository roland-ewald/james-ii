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
import org.jamesii.core.data.experimentsuite.write.plugintype.AbstractExperimentSuiteWriterFactory;
import org.jamesii.core.data.experimentsuite.write.plugintype.ExperimentSuiteFileWriterFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.gui.utils.dialogs.FileHandlingParamDialog;

/**
 * Dialog to write an experiment suite to a file.
 * 
 * @author Roland Ewald
 */

public class WriteExperimentSuiteFSDialog extends
    FileHandlingParamDialog<ExperimentSuiteFileWriterFactory> {

  /**
   * Instantiates a new write experiment suite fs dialog.
   * 
   * @param parameters
   */
  public WriteExperimentSuiteFSDialog(ParameterBlock parameters) {
    super(parameters, "org.jamesii.experimentsuite.dialogs.save",
        JFileChooser.SAVE_DIALOG, true);
  }

  @Override
  public Pair<ParameterBlock, ExperimentSuiteFileWriterFactory> getFactoryParameter(
      Window parentWindow) {
    Pair<ParameterBlock, ExperimentSuiteFileWriterFactory> result =
        super.getFactoryParameter(parentWindow);

    if (result == null) {
      return null;
    }

    ExperimentInfo info =
        new ExperimentInfo((URI) result.getFirstValue().getSubBlockValue(
            IURIHandling.URI), null);
    result.getFirstValue().addSubBl(
        AbstractExperimentSuiteWriterFactory.EXPERIMENT_INFO, info);
    return result;
  }
}
