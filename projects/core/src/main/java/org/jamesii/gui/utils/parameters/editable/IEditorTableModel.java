/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.editable;

/**
 * Table model for the editor table. This is thought to be a table where complex
 * parameter types can be 'opened' and then their sub-parameters are added as
 * lines to the table. Indentation should show the hierarchical relations.
 * Imnplementations of this interface basically defines something like an object
 * inspector.
 * 
 * @author Roland Ewald
 */
public interface IEditorTableModel {

  /**
   * Close sub paramater in the table.
   * 
   * @param parameter
   *          the parameter
   */
  void closeSubParam(IEditable<?> parameter);

  /**
   * Gets the hierarchy level of parameter.
   * 
   * @param p
   *          the p
   * 
   * @return the parameter's level in the hierarchy
   */
  int getParamLevel(IEditable<?> p);

  /**
   * Checks if parameter is currently open.
   * 
   * @param p
   *          the p
   * 
   * @return true, if is parameter opened
   */
  boolean isParamOpened(IEditable<?> p);

  /**
   * Open parameter.
   * 
   * @param selectedParam
   *          the index of the selected parameter
   */
  void openParam(int selectedParam);

  /**
   * Attempts to 'open' a parameter (display all sub-elements and attributes).
   * 
   * @param selectedParam
   *          index of parameter to be opened
   * @param openNode
   *          true if it should not be opened, otherwise false
   */
  void openParam(int selectedParam, boolean openNode);

  /**
   * Opens a sub-parameter recursively.
   * 
   * @param parameter
   *          the parameter to be opened
   * @param startRow
   *          the start row
   * @param level
   *          the level
   */
  void openSubParam(IEditable<?> parameter, int startRow, int level);

  /**
   * Recalculate table length: the number of lines.
   * 
   * @param parameter
   *          the parameter
   * 
   * @return the length of the table (=number of lines)
   */
  int recalculateLength(IEditable<?> parameter);

  /**
   * Refresh structure.
   */
  void refreshStructure();
}
