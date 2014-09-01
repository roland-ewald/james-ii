/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.objecteditor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.WindowConstants;

import org.jamesii.gui.utils.objecteditor.property.editor.EditingMode;
import org.jamesii.gui.utils.objecteditor.property.editor.IPropertyEditor;

/**
 * This is an internally used dialog implementation. The {@link ObjectEditor}
 * uses it indirectly through the {@link PropertyEditorTableCellEditorRenderer}
 * which uses this dialog to display external editors in a dialog rather than in
 * a popup windows if the {@link PropertyEditorDialog} specifies it.
 * 
 * @author Stefan Rybacki
 */
final class PropertyEditorDialog extends JDialog {
  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 6867612600672136876L;

  /** The Constant OK_OPTION. */
  public static final int OK_OPTION = 1;

  /** The Constant CANCEL_OPTION. */
  public static final int CANCEL_OPTION = 2;

  /** The ok flag. */
  private boolean ok = false;

  /**
   * Show property editor dialog with the specified editor.
   * 
   * @param parent
   *          the parent component used for placement of dialog
   * @param editor
   *          the editor to display in the dialog
   * @return {@value #OK_OPTION} or {@value #CANCEL_OPTION}
   */

  public static int showPropertyEditorDialog(Component parent,
      IPropertyEditor<?> editor) {

    PropertyEditorDialog dialog = new PropertyEditorDialog(editor);
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);

    return dialog.ok ? OK_OPTION : CANCEL_OPTION;
  }

  /**
   * Instantiates a new property editor dialog.
   * 
   * @param editor
   *          the editor to display in this dialog
   */
  private PropertyEditorDialog(final IPropertyEditor<?> editor) {
    super();
    setLayout(new BorderLayout());
    add(editor.getExternalComponent(), BorderLayout.CENTER);

    JButton okButton = new JButton("OK");
    JButton cancelButton = new JButton("Cancel");

    cancelButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        exit();
      }

    });

    okButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        ok = editor.finishEditing(EditingMode.EXTERNAL);
        if (ok) {
          exit();
        }
      }

    });

    Box hBox = Box.createHorizontalBox();
    hBox.add(Box.createHorizontalGlue());
    hBox.add(okButton);
    hBox.add(cancelButton);
    hBox.add(Box.createHorizontalGlue());

    add(hBox, BorderLayout.PAGE_END);

    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        exit();
      }
    });

    setModal(true);
    pack();
  }

  /**
   * helper method that closes and disposes this dialog.
   */
  private void exit() {
    setVisible(false);
    dispose();
  }

}
