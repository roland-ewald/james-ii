/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.parametersetup.dialogs;

import java.io.File;

import org.jamesii.gui.utils.dialogs.BrowseDialogFileComponent;

/**
 * Simple browse dialog entry for model parameter files.
 * 
 * @author Stefan Rybacki
 */
public class ModelParameterTableData extends BrowseDialogFileComponent {

  /**
   * Instantiates a new model parameter table data.
   * 
   * @param f
   *          the f
   */
  public ModelParameterTableData(File f) {
    super(f);
  }

}
