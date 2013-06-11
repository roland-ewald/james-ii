/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.editable;

import java.util.List;

/**
 * 
 * This interface defines all parameter controlling functions that can be used
 * by external components (that e.g. add or delete something to/from the
 * parameter structure).
 * 
 * Created on June 30, 2004
 * 
 * @author Roland Ewald
 */
public interface IPropertyEditor {
  /**
   * @return model of the used parameter table
   */
  IEditorTableModel getTableModel();

  /**
   * Updates the parameter chooser view (e. g. a combo-box for a quick selection
   * of a parameter to be edited).
   */
  void refreshParameterChooser();

  /**
   * Updates the parameter table view - needs to be invoked after every
   * structural change.
   */
  void refreshParameterTable();

  /**
   * Shows new list of parameters in the parameter table
   * 
   * @param params
   *          - list of parameters
   */
  void showParameter(List<IEditable<?>> params);

  /**
   * Get editor for editable object.
   * 
   * @param editable
   *          the editable object
   * @return the corresponding editor
   */
  IEditor<?> getEditor(IEditable<?> editable);

}
