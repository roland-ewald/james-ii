/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.objecteditor.property.editor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jamesii.core.util.misc.IDirectory;
import org.jamesii.gui.utils.FileChooser;
import org.jamesii.gui.utils.FlatButton;
import org.jamesii.gui.utils.parameters.factories.converter.Directory;

/**
 * Editor for {@link IDirectory} values. It provides an inplace editor that also
 * allows to select an existing directory using {@link FileChooser}.
 * 
 * @author Stefan Rybacki
 */
public class DirectoryPropertyEditor extends AbstractPropertyEditor<IDirectory>
    implements ActionListener {

  /**
   * The value.
   */
  private IDirectory value;

  /**
   * The chooser.
   */
  private FileChooser chooser = new FileChooser(
      "org.jamesii.core.utils.directory.editor");
  {
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
  }

  private JTextField textField;

  @Override
  public void cancelEditing(EditingMode mode) {
  }

  @Override
  public boolean finishEditing(EditingMode mode) {
    switch (mode) {
    case INPLACE:
      if (textField != null) {
        try {
          if (value == null) {
            value = new Directory(textField.getText());
          } else {
            value.setDir(new File(textField.getText()));
          }
        } catch (Exception e) {
          return false;
        }
      }
      break;
    }
    return true;
  }

  @Override
  public JComponent getExternalComponent() {
    return null;
  }

  @Override
  public JComponent getInPlaceComponent() {
    JPanel panel = new JPanel(new BorderLayout());
    textField = new JTextField(valueToPaint(value));
    textField.setBorder(null);

    panel.setOpaque(false);
    panel.add(textField, BorderLayout.CENTER);

    FlatButton button = new FlatButton("...");
    panel.add(button, BorderLayout.LINE_END);

    button.addActionListener(this);

    return panel;
  }

  @Override
  public IDirectory getValue() {
    return value;
  }

  @Override
  public boolean isMasterEditor() {
    return true;
  }

  @Override
  protected String valueToPaint(IDirectory value) {
    return value == null ? "" : value.asFile().getPath();
  }

  @Override
  public void setValue(IDirectory value) {
    this.value = value;
    if (textField != null) {
      textField.setText(valueToPaint(value));
    }
    if (value != null) {
      chooser.setSelectedFile(value.asFile());
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
  public void actionPerformed(ActionEvent e) {
    if (chooser.showOpenDialog(textField) == JFileChooser.APPROVE_OPTION) {
      textField.setText(chooser.getSelectedFile().getPath());
    }
  }

}
