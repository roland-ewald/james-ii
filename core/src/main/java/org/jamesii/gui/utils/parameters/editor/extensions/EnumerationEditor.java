/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.editor.extensions;

import java.awt.Font;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.jamesii.core.util.IConstraint;
import org.jamesii.gui.utils.parameters.editable.Editable;
import org.jamesii.gui.utils.parameters.editable.IEditable;
import org.jamesii.gui.utils.parameters.editable.IEditor;
import org.jamesii.gui.utils.parameters.editable.IPropertyEditor;
import org.jamesii.gui.utils.parameters.editable.constraints.EnumerationConstraint;

/**
 * A GUI editor to select an option from an enumeration (of options).
 * 
 * Created on June 15, 2004
 * 
 * @param <V>
 *          type to be edited
 * 
 * @author Roland Ewald
 */
public class EnumerationEditor<V> extends JComboBox implements IEditor<V> {

  /** Serialisation ID. */
  static final long serialVersionUID = -900919614697417465L;

  /** The box to select the choice. */
  private DefaultComboBoxModel boxModel = new DefaultComboBoxModel();

  /** The editable parameter. */
  private IEditable<V> editable = null;

  /**
   * Default constructor.
   */
  public EnumerationEditor() {
    super();
    setModel(boxModel);
    this.setLightWeightPopupEnabled(false);

  }

  @Override
  public void configureEditor(IEditable<V> param, IPropertyEditor pdc) {
    editable = param;

    List<IConstraint<V>> constraints = param.getConstraints();

    boxModel.removeAllElements();

    // Ignore complex types
    if (!(param instanceof Editable<?>)) {
      return;
    }

    IEditable<V> p = param;

    Object originalValue = p.getValue();

    int enumIndex = -1;
    for (int i = 0; i < constraints.size(); i++) {
      if (constraints.get(i) instanceof EnumerationConstraint) {
        enumIndex = i;
        break;
      }
    }

    // There is no enumeration constraint
    if (enumIndex < 0) {
      return;
    }

    List<String> data =
        ((EnumerationConstraint) constraints.get(enumIndex)).getEnumeration();

    for (int i = 0; i < data.size(); i++) {
      String listEntry = data.get(i);
      boxModel.addElement(listEntry);
      if (originalValue != null && originalValue.equals(listEntry)) {
        this.setSelectedIndex(i);
      }
    }

  }

  @Override
  public JComponent getEmbeddedEditorComponent() {
    return this;
  }

  @Override
  public JComponent getSeparateEditorComponent() {
    return null;
  }

  @Override
  @SuppressWarnings("unchecked")
  public V getValue() {
    return (V) this.getSelectedItem();
  }

  @Override
  public void setEditing(boolean bSelect) {
    if (bSelect) {
      setFont(new Font("Helvetica", Font.BOLD, 12));
    } else {
      setFont(new Font("Helvetica", Font.PLAIN, 12));
    }
  }

  @Override
  public IEditable<V> getEditable() {
    return editable;
  }

}
