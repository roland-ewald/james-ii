/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.editor.extensions;

import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JTextField;

import org.jamesii.gui.utils.parameters.editable.IEditable;
import org.jamesii.gui.utils.parameters.editable.IPropertyEditor;

/**
 * Parameter editor for strings.
 * 
 * Created on July 8, 2004
 * 
 * @author Roland Ewald
 */
public class StringEditor extends SingleComponentEditor<String, JTextField> {

  /** Serialisation ID. */
  static final long serialVersionUID = -6985203747000519747L;

  @Override
  public void configureEditor(IEditable<String> var, IPropertyEditor peControl) {

    setComponent(new JTextField(var.getValue()));

    // If parameter is complex, generate a description of it's attributes to
    // describe it's value
    if (var.isComplex()) {

      StringBuilder parameterDescription = new StringBuilder();
      Map<String, IEditable<?>> attributes = var.getAllAttributes();

      for (Entry<String, IEditable<?>> entry : attributes.entrySet()) {
        String elementName = entry.getKey();
        IEditable<?> value = entry.getValue();

        parameterDescription.append(parameterDescription);
        parameterDescription.append(elementName);
        parameterDescription.append(":");
        parameterDescription.append(value.getValue());
        parameterDescription.append(" ");
      }

      getComponent().setText(parameterDescription.toString());
      getComponent().setEditable(false);
    }

    super.configureEditor(var, peControl);
  }

  @Override
  public String getValue() {
    return getComponent().getText();
  }

}
