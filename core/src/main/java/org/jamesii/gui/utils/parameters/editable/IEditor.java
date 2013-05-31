/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.editable;

import javax.swing.JComponent;

/**
 * 
 * A general GUI editor interface for parameter editing.
 * 
 * Date: 23.05.2004
 * 
 * @param <V>
 *          type of the object to be edited
 * 
 * @author Roland Ewald
 */
public interface IEditor<V> {

  /**
   * Configures the editor.
   * 
   * @param variable
   *          the parameter to be edited
   * @param peController
   *          the property editor dialogue
   */
  void configureEditor(IEditable<V> variable, IPropertyEditor peController);

  /**
   * Get component that edits the parameter and can be displayed within the
   * parameter table.
   * 
   * @return component with the embedded editor's GUI, null if not supported
   */
  JComponent getEmbeddedEditorComponent();

  /**
   * Get component to be displayed within an extra panel.
   * 
   * @return component with the separate editor's GUI, null if not supported
   */
  JComponent getSeparateEditorComponent();

  /**
   * Current value of edited object.
   * 
   * @return current value of edited object
   */
  V getValue();

  /**
   * Set editing status for the editor, true if it is currently selected and the
   * user may edit.
   * 
   * @param editing
   *          the editing status
   */
  void setEditing(boolean editing);

  /**
   * Get the editable for which the editor was configured.
   * 
   * @return the editable
   */
  IEditable<V> getEditable();

}
