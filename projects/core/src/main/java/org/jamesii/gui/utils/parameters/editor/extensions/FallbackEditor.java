/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.editor.extensions;

import javax.swing.JLabel;

import org.jamesii.gui.utils.parameters.editable.IEditable;
import org.jamesii.gui.utils.parameters.editable.IPropertyEditor;

/**
 * The fall-back editor works for every type of variable. Drawback is, of
 * course, that editing is actually not permitted. Only a string representation
 * will be displayed.
 * 
 * @author Roland Ewald
 */
public class FallbackEditor extends SingleComponentEditor<Object, JLabel> {

  @Override
  public void configureEditor(IEditable<Object> var, IPropertyEditor peControl) {
    setComponent(new JLabel(var.getValue() != null ? var.getValue().toString()
        : ""));
    super.configureEditor(var, peControl);
  }

  @Override
  public Object getValue() {
    return getEditable().getValue();
  }

}
