/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.editor.extensions;

import javax.swing.JCheckBox;

import org.jamesii.gui.utils.parameters.editable.IEditable;
import org.jamesii.gui.utils.parameters.editable.IPropertyEditor;

/**
 * Editor for {@link Boolean}, ie a simple check box.
 * 
 * @author Roland Ewald
 */
public class BooleanEditor extends SingleComponentEditor<Boolean, JCheckBox> {

  @Override
  public void configureEditor(IEditable<Boolean> variable,
      IPropertyEditor peControl) {
    setComponent(new JCheckBox());
    getComponent().setSelected(variable.getValue());
    super.configureEditor(variable, peControl);
  }

  @Override
  public Boolean getValue() {
    return getComponent().isSelected();
  }

}
