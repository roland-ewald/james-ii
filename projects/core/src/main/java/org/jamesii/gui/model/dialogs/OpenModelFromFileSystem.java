/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.dialogs;

import javax.swing.JFileChooser;

import org.jamesii.core.data.model.ModelFileReaderFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.utils.dialogs.FileHandlingParamDialog;

/**
 * Dialog to select a model file from the file system.
 * 
 * @author Stefan Rybacki
 */
public class OpenModelFromFileSystem extends
    FileHandlingParamDialog<ModelFileReaderFactory> {

  /**
   * The Constructor.
   * 
   * @param parameters
   */
  public OpenModelFromFileSystem(ParameterBlock parameters) {
    super(parameters, "org.jamesii.model.dialogs.open",
        JFileChooser.OPEN_DIALOG, false);
  }

}
