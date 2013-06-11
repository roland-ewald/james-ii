/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.objecteditor.property.editor;

import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 * Abstract implementation of {@link IPropertyEditor} that provides a basic
 * implementation of {@link #getPaintComponent(Object)} using
 * {@link #valueToPaint(Object)}.
 * 
 * @author Stefan Rybacki
 * @param <V>
 *          the value type
 */
public abstract class AbstractPropertyEditor<V> implements IPropertyEditor<V> {

  @Override
  public JComponent getPaintComponent(V value) {
    return new JLabel(valueToPaint(value));
  }

  /**
   * Value to paint. Override this method if you want to extract the text to
   * display by the {@link #getPaintComponent(Object)} method for the specified
   * value.
   * 
   * @param value
   *          the value
   * @return the string
   */
  protected String valueToPaint(V value) {
    if (value == null) {
      return "";
    }
    return value.toString();
  }

  @Override
  public boolean canHandleNull() {
    return true;
  }

  @Override
  public boolean asDialog() {
    return false;
  }
}
