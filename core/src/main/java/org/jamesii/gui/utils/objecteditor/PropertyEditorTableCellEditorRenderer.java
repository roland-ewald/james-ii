/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.objecteditor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.AbstractCellEditor;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jamesii.gui.application.resource.IconManager;
import org.jamesii.gui.application.resource.iconset.IconIdentifier;
import org.jamesii.gui.decoration.Decorator;
import org.jamesii.gui.decoration.IDecoration;
import org.jamesii.gui.decoration.ValidatorDecoration;
import org.jamesii.gui.utils.FlatButton;
import org.jamesii.gui.utils.objecteditor.property.editor.EditingMode;
import org.jamesii.gui.utils.objecteditor.property.editor.IPropertyEditor;

// TODO: Auto-generated Javadoc
/**
 * Class for internal use which provides a wrapper around an
 * {@link IPropertyEditor} to fulfill {@link TableCellEditor} and
 * {@link TableCellRenderer} interfaces. It also adds the implementation
 * providing ability as well as manages the internal and external editors given
 * for a specific class.
 * 
 * @author Stefan Rybacki
 * @param <T>
 *          the type to edit
 */
final class PropertyEditorTableCellEditorRenderer<T> extends AbstractCellEditor
    implements TableCellEditor, TableCellRenderer {

  /**
   * 
   * LICENCE: JAMESLIC
   * 
   * @author Stefan Rybacki
   * 
   */
  private final class ExternalButtonPopupListener implements ActionListener {
    /**
     * 
     */
    private final int row;

    /**
     * 
     */
    private final int column;

    /**
     * 
     */
    private final JTable table;

    /**
     * 
     */
    private final FlatButton externalButton;

    /**
     * @param row
     * @param column
     * @param table
     * @param externalButton
     */
    private ExternalButtonPopupListener(int row, int column, JTable table,
        FlatButton externalButton) {
      this.row = row;
      this.column = column;
      this.table = table;
      this.externalButton = externalButton;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      if (popup != null) {
        if (!editor.finishEditing(mode)) {
          return;
        }
        popup.hide();
        popup = null;
        // reactivate in place component if present
        if (inplace != null) {
          inplace.setEnabled(oldEnabled);
        }
        fireEditingStopped();
        SwingUtilities.invokeLater(new Runnable() {

          @Override
          public void run() {
            // start editing again
            table.editCellAt(row, column);
          }

        });
        return;
      }

      // disable in place component if present
      if (inplace != null) {
        inplace.setEnabled(false);
      }

      mode = EditingMode.EXTERNAL;
      JPanel popupPanel = new JPanel(new BorderLayout());
      popupPanel.add(editor.getExternalComponent());

      popup =
          PopupFactory.getSharedInstance().getPopup(
              externalButton,
              popupPanel,
              externalButton.getLocationOnScreen().x
                  + externalButton.getWidth(),
              externalButton.getLocationOnScreen().y);
      popup.show();
    }
  }

  /**
   * 
   * LICENCE: JAMESLIC
   * 
   * @author Stefan Rybacki
   * 
   */
  private final class ExternalButtonWithDialogListener implements
      ActionListener {
    /**
     * 
     */
    private final int row;

    /**
     * 
     */
    private final JTable table;

    /**
     * 
     */
    private final int column;

    /**
     * @param row
     * @param table
     * @param column
     */
    private ExternalButtonWithDialogListener(int row, JTable table, int column) {
      this.row = row;
      this.table = table;
      this.column = column;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      // disable in place component if present
      if (inplace != null) {
        inplace.setEnabled(false);
      }

      mode = EditingMode.EXTERNAL;

      PropertyEditorDialog.showPropertyEditorDialog(table, editor);

      // reactivate in place component if present
      if (inplace != null) {
        inplace.setEnabled(oldEnabled);
      }
      fireEditingStopped();
      SwingUtilities.invokeLater(new Runnable() {

        @Override
        public void run() {
          // start editing again
          table.editCellAt(row, column);
        }

      });
    }
  }

  /**
   * Serialization ID.
   */
  private static final long serialVersionUID = -2641484467224718036L;

  /**
   * The wrapped editor. (only one for now, TODO sr137: add the ability to have
   * more than one editor)
   */
  private IPropertyEditor<T> editor;

  /**
   * The available implementations.
   */
  private Map<String, T> implementations;

  /**
   * The current editing mode.
   */
  private EditingMode mode = EditingMode.INPLACE;

  /**
   * The popup for external editors.
   */
  private Popup popup = null;

  /**
   * The inplace component wrapper.
   */
  private JComponent inplace = null;

  /**
   * The old enabled flag.
   */
  private boolean oldEnabled = false;

  /**
   * The editable flag. false for read only properties
   */
  private boolean editable;

  /**
   * The validator used to validate the editors value and it is therefore
   * responsible for enabling/disabling the error icon if any.
   */
  private final PropertyEditorTableCellEditorRendererValidator validator =
      new PropertyEditorTableCellEditorRendererValidator();

  /**
   * The error icon.
   */
  private Image errorIcon;

  /**
   * Instantiates a new property editor table cell editor renderer.
   * 
   * @param e
   *          the editor
   * @param implementations
   *          the available implementations if any
   * @param editable
   *          the editable flag
   * @param errorIcon
   *          the error icon if any
   */
  public PropertyEditorTableCellEditorRenderer(IPropertyEditor<T> e,
      Map<String, T> implementations, boolean editable, Image errorIcon) {
    editor = e;
    this.errorIcon = errorIcon;
    setEditable(editable);
    setImplementations(implementations);
  }

  /**
   * Sets the editable flag.
   * 
   * @param editable
   *          the new editable state
   */
  public void setEditable(boolean editable) {
    this.editable = editable;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Component getTableCellEditorComponent(final JTable table,
      final Object v, boolean isSelected, final int row, final int column) {

    validator.setValid(true);
    editor.setValue((T) v);
    if (!editable) {
      return getTableCellRendererComponent(table, v, isSelected, true, row,
          column);
    }

    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.setFocusable(false);

    if (isSelected) {
      panel.setBackground(table.getSelectionBackground());
      panel.setForeground(table.getSelectionForeground());
    } else {
      panel.setBackground(table.getBackground());
      panel.setForeground(table.getForeground());
    }

    if ((v != null || editor.canHandleNull())
        && editor.supportsInPlaceEditing()) {
      mode = EditingMode.INPLACE;
      inplace = editor.getInPlaceComponent();
      oldEnabled = inplace.isEnabled();
      panel.add(inplace, BorderLayout.CENTER);
    }

    Box hBox = Box.createHorizontalBox();
    hBox.setOpaque(false);

    if (implementations != null && !implementations.isEmpty()) {
      FlatButton impButton =
          new FlatButton(new ImplementationSelectionAction<>(implementations,
              this, editor));
      hBox.add(impButton);
    }

    if ((v != null || editor.canHandleNull())
        && editor.supportsExternalEditing()) {

      final FlatButton externalButton = new FlatButton();

      if (editor.asDialog()) {
        externalButton.addActionListener(new ExternalButtonWithDialogListener(
            row, table, column));
        externalButton.setText("...");
      } else {
        externalButton.addActionListener(new ExternalButtonPopupListener(row,
            column, table, externalButton));

        Icon icon = IconManager.getIcon(IconIdentifier.RIGHT_SMALL, ">");
        externalButton.setIcon(icon);
      }
      hBox.add(externalButton);

      // in case there is now inplace editor use the paintcomponent if
      // there is
      // any
      if (!editor.supportsInPlaceEditing()) {
        panel.add(editor.getPaintComponent((T) v), BorderLayout.CENTER);
      }
    }

    panel.add(hBox, BorderLayout.LINE_END);

    IDecoration deco =
        new ValidatorDecoration(validator, errorIcon,
            ValidatorDecoration.SOUTH_EAST);
    Decorator decorator = new Decorator(panel, deco);
    decorator.setFocusable(false);

    return decorator;
  }

  @Override
  public boolean shouldSelectCell(EventObject anEvent) {
    return true;
  }

  @Override
  public void cancelCellEditing() {
    if (popup != null) {
      popup.hide();
      popup = null;
    }

    editor.cancelEditing(mode);
    validator.setValid(true);
    fireEditingCanceled();
  }

  @Override
  public boolean stopCellEditing() {
    // TODO sr137: in case finish Editing does not return true show
    // invalid icon
    // in editor
    boolean res = editor.finishEditing(mode) || !editable;
    if (res) {
      fireEditingStopped();
      if (popup != null) {
        popup.hide();
        popup = null;
      }
    }

    validator.setValid(res);
    return res;
  }

  @Override
  public Object getCellEditorValue() {
    return editor.getValue();
  }

  @SuppressWarnings("unchecked")
  @Override
  public Component getTableCellRendererComponent(final JTable table,
      final Object v, final boolean isSelected, boolean hasFocus, int row,
      int column) {
    JComponent c = editor.getPaintComponent((T) v);
    if (c != null) {
      c.setOpaque(true);
      if (isSelected) {
        c.setBackground(table.getSelectionBackground());
        c.setForeground(table.getSelectionForeground());
      } else {
        c.setBackground(table.getBackground());
        c.setForeground(table.getForeground());
      }

      return c;
    }

    JLabel l = new JLabel(v == null ? "" : v.toString());
    l.setOpaque(true);

    if (isSelected) {
      l.setBackground(table.getSelectionBackground());
      l.setForeground(table.getSelectionForeground());
    } else {
      l.setBackground(table.getBackground());
      l.setForeground(table.getForeground());
    }
    l.setFont(table.getFont());

    return l;
  }

  @Override
  public boolean isCellEditable(EventObject e) {
    if (e instanceof MouseEvent) {
      return ((MouseEvent) e).getClickCount() >= 1;
    }
    return super.isCellEditable(e);
  }

  /**
   * Sets the available implementations.
   * 
   * @param list
   *          the list
   */
  public void setImplementations(Map<String, T> list) {
    implementations = new TreeMap<>(list);
  }

}
