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
import org.jamesii.core.data.experiment.write.plugintype.AbstractExperimentWriterFactory;
import org.jamesii.core.data.experiment.write.plugintype.ExperimentFileWriterFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.gui.utils.dialogs.FileHandlingParamDialog;

/**
 * Dialog to write an experiment to the file system.
 * 
 * @author Roland Ewald
 */
public class WriteExperimentFSDialog extends
    FileHandlingParamDialog<ExperimentFileWriterFactory> {

  /**
   * Default constructor.
   * 
   * @param parameters
   */
  public WriteExperimentFSDialog(ParameterBlock parameters) {
    super(parameters, "org.jamesii.experiment.dialogs.save",
        JFileChooser.SAVE_DIALOG, true);
  }

  @Override
  public Pair<ParameterBlock, ExperimentFileWriterFactory> getFactoryParameter(
      Window parentWindow) {
    Pair<ParameterBlock, ExperimentFileWriterFactory> result =
        super.getFactoryParameter(parentWindow);

    if (result == null) {
      return null;
    }

    ExperimentInfo info =
        new ExperimentInfo((URI) result.getFirstValue().getSubBlockValue(
            IURIHandling.URI), null);
    result.getFirstValue().addSubBl(
        AbstractExperimentWriterFactory.EXPERIMENT_INFO, info);
    return result;
  }
}
