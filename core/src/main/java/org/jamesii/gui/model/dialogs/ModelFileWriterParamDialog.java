/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.dialogs;

import javax.swing.JFileChooser;

import org.jamesii.core.data.model.ModelFileWriterFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.utils.dialogs.FileHandlingParamDialog;

/**
 * Simple save dialog that selects a file to store model to.
 * 
 * @author Stefan Rybacki
 */
public class ModelFileWriterParamDialog extends
    FileHandlingParamDialog<ModelFileWriterFactory> {

  /**
   * Default constructor.
   * 
   * @param parameter
   *          parameter for factory parameter dialog
   */
  public ModelFileWriterParamDialog(ParameterBlock parameter) {
    super(parameter, "org.jamesii.model.dialogs.model.save",
        JFileChooser.SAVE_DIALOG, true);
  }

}
