/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.objecteditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.TableCellEditor;

import org.jamesii.gui.application.resource.IconManager;
import org.jamesii.gui.application.resource.iconset.IconIdentifier;
import org.jamesii.gui.utils.BasicUtilities;
import org.jamesii.gui.utils.objecteditor.property.editor.IPropertyEditor;

/**
 * Internal helper class that provides an action which pops up a menu of
 * available implementations. This is used in the property editors cell of
 * {@link ObjectEditorComponent}.
 * 
 * @author Stefan Rybacki
 * @param <T>
 *          the type of the provided implementations
 */
final class ImplementationSelectionAction<T> extends AbstractAction {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 7616351948744813809L;

  /**
   * The menu containing the available implementations.
   */
  private JPopupMenu menu;

  /**
   * Instantiates a new implementation selection action.
   * 
   * @param imps
   *          the available implementations
   * @param tce
   *          the table cell editor that is notified after selecting an item
   *          from popup menu
   * @param editor
   *          the editor the selected value is written to
   */
  public ImplementationSelectionAction(Map<String, T> imps,
      final TableCellEditor tce, final IPropertyEditor<T> editor) {
    super();

    // first try to load icon from laf
    Icon icon = UIManager.getIcon("Table.descendingSortIcon");
    try {
      // testing whether icon can be painted properly
      icon = new ImageIcon(BasicUtilities.iconToImage(icon));
    } catch (Exception e) {
      icon = null;
    }

    // first fallback
    if (icon == null) {
      icon = IconManager.getIcon(IconIdentifier.DOWN_SMALL, "v");
    }

    if (icon != null) { // NOSONAR (false positive)
      putValue(Action.SMALL_ICON, icon);
    } else {
      // second fallback
      putValue(Action.NAME, "v");
    }
    putValue(Action.SHORT_DESCRIPTION, "Available Implementations");
    // create popup menu

    // FIXME sr137: show also tooltips for implementations
    menu = new JPopupMenu("Implementations");
    for (final Entry<String, T> entry : imps.entrySet()) {
      // build popup menu with implementations
      JMenuItem item = new JMenuItem(entry.getKey());
      item.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          editor.setValue(entry.getValue());
          tce.stopCellEditing();
        }

      });
      menu.add(item);
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    SwingUtilities.updateComponentTreeUI(menu);
    menu.show(((JComponent) e.getSource()), 0,
        ((JComponent) e.getSource()).getHeight());
  }

}
