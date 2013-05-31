/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.objecteditor.property.editor;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComponent;

/**
 * Editor for {@link Boolean} properties.
 * 
 * @author Stefan Rybacki
 */
public class BooleanPropertyEditor extends AbstractPropertyEditor<Boolean> {

  /**
   * The value to edit.
   */
  private boolean value;

  /**
   * The check box.
   */
  private final JCheckBox box = new JCheckBox();
  {
    box.setOpaque(false);
  }

  @Override
  public void cancelEditing(EditingMode mode) {
  }

  @Override
  public boolean finishEditing(EditingMode mode) {
    value = box.isSelected();
    return true;
  }

  @Override
  public JComponent getExternalComponent() {
    return null;
  }

  @Override
  public JComponent getInPlaceComponent() {
    box.setSelected(value);
    Box hBox = Box.createHorizontalBox();
    hBox.add(box);
    hBox.add(Box.createHorizontalGlue());
    return hBox;
  }

  @Override
  public Boolean getValue() {
    return Boolean.valueOf(value);
  }

  @Override
  public boolean isMasterEditor() {
    return true;
  }

  @Override
  public void setValue(Boolean value) {
    if (value == null) {
      this.value = false;
    } else {
      this.value = value.booleanValue();
    }
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
  public JComponent getPaintComponent(Boolean v) {
    JCheckBox b = new JCheckBox();
    b.setSelected(v.booleanValue());
    b.setEnabled(false);
    return b;
  }

}
