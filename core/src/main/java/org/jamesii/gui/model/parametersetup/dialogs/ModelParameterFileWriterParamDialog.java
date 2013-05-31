/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.parametersetup.dialogs;

import javax.swing.JFileChooser;

import org.jamesii.core.data.model.parameter.write.plugintype.ModelParameterFileWriterFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.utils.dialogs.FileHandlingParamDialog;

/**
 * Simple dialog instance that selectes a file to store model parameters to.
 * 
 * @author Stefan Rybacki
 */
public class ModelParameterFileWriterParamDialog extends
    FileHandlingParamDialog<ModelParameterFileWriterFactory> {

  /**
   * Instantiates a new model parameter file writer param dialog.
   * 
   * @param parameter
   *          the parameter
   */
  public ModelParameterFileWriterParamDialog(ParameterBlock parameter) {
    super(parameter, "org.jamesii.model.dialogs.param.save",
        JFileChooser.SAVE_DIALOG, true);
  }

}
