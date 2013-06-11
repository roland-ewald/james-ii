/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.editor.extensions;

import java.awt.Font;

import javax.swing.JComponent;

import org.jamesii.gui.utils.parameters.editable.IEditable;
import org.jamesii.gui.utils.parameters.editable.IEditor;
import org.jamesii.gui.utils.parameters.editable.IPropertyEditor;

/**
 * Editor that uses a single Swing-Component to let the user display and edit a
 * variable.
 * 
 * @param <X>
 *          the type to be edited
 * @param <Y>
 *          the type of swing component to be used
 * 
 * @author Roland Ewald
 */
public abstract class SingleComponentEditor<X, Y extends JComponent> implements
    IEditor<X> {

  /** Component to display and edit the value to be edited. */
  private Y component = null;

  /** The editable. */
  private IEditable<X> editable = null;

  /** Property editor controller. */
  private IPropertyEditor peController = null;

  @Override
  public void configureEditor(IEditable<X> variable, IPropertyEditor peControl) {

    if (getComponent() == null) {
      throw new RuntimeException(
          "My component is null. The editor in "
              + this.getClass()
              + " should create a component in this.component and *then* call super.configureEditor(...)");
    }

    editable = variable;
    getComponent().setToolTipText(variable.getDocumentation());
    peController = peControl;
  }

  @Override
  public IEditable<X> getEditable() {
    return editable;
  }

  @Override
  public JComponent getEmbeddedEditorComponent() {
    return getComponent();
  }

  @Override
  public JComponent getSeparateEditorComponent() {
    return null;
  }

  @Override
  public void setEditing(boolean editing) {
    if (editing) {
      getComponent().setFont(new Font("Helvetica", Font.BOLD, 12));
    } else {
      getComponent().setFont(new Font("Helvetica", Font.PLAIN, 12));
    }
  }

  /**
   * @return the component
   */
  protected final Y getComponent() {
    return component;
  }

  /**
   * @param component
   *          the component to set
   */
  protected final void setComponent(Y component) {
    this.component = component;
  }

}
