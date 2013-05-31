/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.objecteditor;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.jamesii.gui.utils.objecteditor.property.editor.AbstractPropertyEditor;
import org.jamesii.gui.utils.objecteditor.property.editor.EditingMode;
import org.jamesii.gui.utils.objecteditor.property.editor.IPropertyEditor;

/**
 * Internal class that implements {@link IPropertyEditor} but does nothing but
 * displaying the value to edit. This is used if there is no editor available
 * for the value but implementations are.
 * 
 * @author Stefan Rybacki
 * @param <V>
 *          the type to edit
 */
final class FakePropertyEditor<V> extends AbstractPropertyEditor<V> implements
    IPropertyEditor<V> {

  /**
   * The value.
   */
  private V value;

  @Override
  public void cancelEditing(EditingMode mode) {
  }

  @Override
  public boolean finishEditing(EditingMode mode) {
    return true;
  }

  @Override
  public JComponent getExternalComponent() {
    return null;
  }

  @Override
  public JComponent getInPlaceComponent() {
    return new JLabel(value != null ? value.toString() : "");
  }

  @Override
  public V getValue() {
    return value;
  }

  @Override
  public boolean isMasterEditor() {
    return false;
  }

  @Override
  public boolean supportsExternalEditing() {
    return false;
  }

  @Override
  public boolean supportsInPlaceEditing() {
    return true;
  }

  @Override
  public void setValue(V value) {
    this.value = value;
  }

}
