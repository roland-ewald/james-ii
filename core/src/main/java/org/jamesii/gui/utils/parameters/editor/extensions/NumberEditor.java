/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.editor.extensions;

import javax.swing.JTextField;

import org.jamesii.core.math.Calc;
import org.jamesii.gui.utils.parameters.editable.IEditable;
import org.jamesii.gui.utils.parameters.editable.IPropertyEditor;

/**
 * Editor for all kinds of numbers.
 * 
 * @param <N>
 *          the type of the number to be edited
 * @author Roland Ewald
 */
public class NumberEditor<N extends Number> extends
    SingleComponentEditor<N, JTextField> {

  /** Variable to be edited. */
  private IEditable<N> editableVar;

  @Override
  public void configureEditor(IEditable<N> var, IPropertyEditor peControl) {
    editableVar = var;
    setComponent(new JTextField(editableVar.getValue().toString()));
    super.configureEditor(var, peControl);
  }

  @Override
  public N getValue() {
    return Calc.parseNumber(editableVar.getValue(), getComponent().getText());
  }

}
