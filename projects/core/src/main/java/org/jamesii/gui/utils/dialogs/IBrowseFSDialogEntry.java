/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.dialogs;

import java.io.File;

/**
 * Interface for models and experiments which are used by various BrowseFSDialog
 * versions. You can find a predefined default model here :
 * {@link BrowseDialogFileComponent}.
 * */
public interface IBrowseFSDialogEntry {

  /**
   * Column names for the table
   * 
   * @return Array of column names
   */
  String[] getColumnNames();

  /**
   * Returns the Value at x-Position for the table.
   * 
   * @param xPosition
   *          Element at the x-Position to load.
   * 
   * @return Value at x-Position.
   */
  Object getValue(int xPosition);

  /**
   * Returns the current File.
   * 
   * @return The File of the Model.
   */
  File getFile();
}
