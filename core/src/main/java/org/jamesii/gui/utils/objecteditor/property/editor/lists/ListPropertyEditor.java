/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.objecteditor.property.editor.lists;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.jamesii.SimSystem;
import org.jamesii.gui.utils.objecteditor.property.editor.AbstractPropertyEditor;
import org.jamesii.gui.utils.objecteditor.property.editor.EditingMode;
import org.jamesii.gui.utils.parameters.factories.ListParameters;
import org.jamesii.gui.utils.parameters.list.Entry;

/**
 * Editor for list of factory values. It provides an external editor for the
 * list.
 * 
 * @author Jan Himmelspach
 */
public class ListPropertyEditor extends AbstractPropertyEditor<ListParameters> {

  private ListParameters value;

  private JLabel listLabel = null;

  /**
   * The list editor.
   */
  private ListEditorComponent listEditor;

  @Override
  public void cancelEditing(EditingMode mode) {
  }

  @Override
  public boolean finishEditing(EditingMode mode) {
    switch (mode) {
    case EXTERNAL:
      value.setList(listEditor.getResult());
      // updateLabel();
      break;
    }
    return true;
  }

  /**
   * Update the {@link #listLabel} text.
   * 
   * @return the instance of the list label, new instance if {@link #listLabel}
   *         had been null before
   */
  private JLabel updateLabel(ListParameters v) {

    if (listLabel == null) {
      listLabel = new JLabel();
    }

    StringBuilder label = new StringBuilder("[");

    int i = 0;
    for (Entry f : v.getList()) {
      if (i > 0) {
        label.append(", ");
      }

      label.append(SimSystem.getRegistry().getFactory(f.getFactoryName())
          .getReadableName());
      i++;
    }

    label.append("]");

    listLabel.setText(label.toString());
    return listLabel;
  }

  @Override
  public JComponent getPaintComponent(ListParameters v) {

    if (v == null) {
      return new JLabel("NULL");
    }

    // if (v == value) {
    // return updateLabel();
    // }

    // return new JLabel("List of " + v.getBaseFactory());
    return updateLabel(v);
  }

  @Override
  public JComponent getExternalComponent() {
    listEditor =
        new ListEditorComponent(value.getList(), value.getBaseFactory());
    return listEditor;
  }

  @Override
  public JComponent getInPlaceComponent() {
    return null;
  }

  @Override
  public boolean isMasterEditor() {
    return true;
  }

  @Override
  public boolean supportsExternalEditing() {
    return true;
  }

  @Override
  public boolean supportsInPlaceEditing() {
    return false;
  }

  @Override
  public void setValue(ListParameters value) {
    this.value = value;
  }

  @Override
  public ListParameters getValue() {
    return value;
  }

  @Override
  public boolean asDialog() {
    return true;
  }

}
