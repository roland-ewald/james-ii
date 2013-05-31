/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.parametersetup.dialogs;

import javax.swing.JFileChooser;

import org.jamesii.core.data.model.parameter.read.plugintype.ModelParameterFileReaderFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.utils.dialogs.FileHandlingParamDialog;

/**
 * Dialog to open a model parameter file from the file system.
 * 
 * @author Stefan Rybacki
 */
public class OpenModelParameterFromFileSystem extends
    FileHandlingParamDialog<ModelParameterFileReaderFactory> {

  /**
   * The Constructor.
   * 
   * @param factoryDialogParameter
   *          the factory dialog parameter
   */
  public OpenModelParameterFromFileSystem(ParameterBlock factoryDialogParameter) {
    super(factoryDialogParameter, "org.jamesii.model.dialogs.paramter.open",
        JFileChooser.OPEN_DIALOG, false);
  }

}
