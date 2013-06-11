/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.objecteditor.property.editor;

import javax.swing.JComponent;

import org.jamesii.gui.utils.objecteditor.ObjectEditorComponent;

/**
 * Interface for any property editor usable for {@link ObjectEditorComponent}.
 * This interface enables to provide internal, external or both editors for
 * editing property values in {@link ObjectEditorComponent}.
 * 
 * @author Stefan Rybacki
 * @param <V>
 *          the value type to edit
 */
public interface IPropertyEditor<V> {

  /**
   * Checks if this is a master editor. If <code>true</code> no sub properties
   * should be editable outside of this editor.
   * 
   * @return true, if is master editor
   */
  // FIXME sr137: incorporate this into ObjectEditorModel
  boolean isMasterEditor();

  /**
   * Supports in place editing.
   * 
   * @return true, if in place editing is supported means whether a one line
   *         editing component is returned by {@link #getInPlaceComponent()}
   */
  boolean supportsInPlaceEditing();

  /**
   * Supports extra editing.
   * 
   * @return true, if an extra component for editing is available via
   *         {@link #getExternalComponent()}
   */
  boolean supportsExternalEditing();

  /**
   * If {@link #supportsExternalEditing()} is true and this returns
   * <code>true</code> the external component is shown as dialog rather than as
   * popup.
   * 
   * @return true, if external component should be shown as dialog rather than
   *         as popup
   */
  boolean asDialog();

  /**
   * Gets the in place component if {@link #supportsInPlaceEditing()} returns
   * true. It can return null if not supported but has to return false in
   * {@link #supportsInPlaceEditing()}
   * 
   * @return the in place component
   */
  JComponent getInPlaceComponent();

  /**
   * Gets the extra component if {@link #supportsExternalEditing()} returns
   * true.
   * 
   * @return the extra component
   */
  JComponent getExternalComponent();

  /**
   * Paints the specified value on using the returned component.
   * 
   * @param value
   *          the value to paint
   * @return component used for painting the value
   */
  JComponent getPaintComponent(V value);

  /**
   * Sets the value right before editing. This value should be reflected in the
   * components that are returned by {@link #getExternalComponent()} and
   * {@link #getInPlaceComponent()}
   * 
   * @param value
   *          the value to edit
   */
  void setValue(V value);

  /**
   * Gets the value after editing.
   * 
   * @return the edited value
   */
  V getValue();

  /**
   * Called if editing should be canceled this means {@link #getValue()} has to
   * return the value as it was before editing.
   * 
   * @param mode
   *          specifying which editor component was used to edit the value
   *          inplace or external
   */
  void cancelEditing(EditingMode mode);

  /**
   * Finishes editing, this means the editor should check whether the edited
   * value is correct and fully edited so that it can be used for further
   * processing. If this is not the case the editor can return false if it can't
   * finish editing due to e.g. validation problems with the edited value. If
   * returned true the editing was successful and {@link #getValue()} should
   * return the new edited value.
   * 
   * @param mode
   *          specifying which editor component was used to edit
   * @return true, if successful
   */
  boolean finishEditing(EditingMode mode);

  /**
   * Specifies whether the editor can handle null values. This is useful in case
   * the editor edits a String, Integer or Double which is null but can easily
   * be created by the editor.
   * 
   * @return true, if editor can handle null values
   */
  boolean canHandleNull();
}
