/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.workflow.experiment.parameterexploration.propertyeditor;

import javax.swing.JComponent;

import org.jamesii.core.experiments.variables.modifier.IVariableModifier;
import org.jamesii.gui.utils.objecteditor.property.editor.AbstractPropertyEditor;
import org.jamesii.gui.utils.objecteditor.property.editor.EditingMode;

/**
 * @author Stefan Rybacki
 * 
 */
public class VariableModifierPropertyEditor extends
    AbstractPropertyEditor<IVariableModifier<?>> {
  private IVariableModifier<?> value;

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
    return getPaintComponent(value);
  }

  @Override
  public IVariableModifier<?> getValue() {
    return value;
  }

  @Override
  public boolean isMasterEditor() {
    return false;
  }

  @Override
  public void setValue(IVariableModifier value) {
    this.value = value;
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
  protected String valueToPaint(IVariableModifier<?> value) {
    return value.getClass().getSimpleName();
  }

  @Override
  public boolean canHandleNull() {
    return false;
  }

}
