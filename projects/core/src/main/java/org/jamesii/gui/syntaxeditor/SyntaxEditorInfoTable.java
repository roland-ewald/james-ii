/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.syntaxeditor;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

import javax.swing.JPopupMenu;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;

import org.jamesii.gui.utils.IconAwareCellRenderer;
import org.jamesii.gui.utils.treetable.GroupingTreeTableModel;
import org.jamesii.gui.utils.treetable.ITreeTableModel;
import org.jamesii.gui.utils.treetable.TreeTable;

/**
 * @author Stefan Rybacki
 */
class SyntaxEditorInfoTable extends TreeTable {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 2674638965818780852L;

  private InfoProviderTableModel tableModel;

  @SuppressWarnings("unchecked")
  public SyntaxEditorInfoTable(final JTextComponent editor) {
    super(null);

    final GroupingTreeTableModel ttModel =
        new GroupingTreeTableModel(tableModel =
            new InfoProviderTableModel(Collections.EMPTY_LIST), 0) {
          @Override
          protected Object getValueForGroup(Object groupingObject,
              int columnIndex) {
            if (columnIndex == 1) {
              return String.format("(%d) Elements",
                  this.getElementCountInGroup(groupingObject));
            }
            return super.getValueForGroup(groupingObject, columnIndex);
          }
        };
    super.setTreeTableModel(ttModel);

    // set dimensions of table columns
    getColumnModel().getColumn(0).setWidth(45);
    getColumnModel().getColumn(0).setMaxWidth(45);
    getColumnModel().getColumn(0).setMinWidth(45);
    getColumnModel().getColumn(0).setCellRenderer(new IconAwareCellRenderer());
    getTableHeader().setReorderingAllowed(false);

    // react on double click on table item -> select area in editor
    // specified by token
    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
          int row = ttModel.getRowForNode(getNode(getSelectedRow()));
          if (row >= 0) {
            ILexerToken token = tableModel.getTokenAt(row);
            editor.select(token.getStart(), token.getEnd());
            editor.requestFocus();
          }
          e.consume();
        }
      }
    });

    // react on left click on specific information entries with
    // information actions
    addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        mouseReleased(e);
      }

      @Override
      public void mouseReleased(MouseEvent event) {
        if (event.isPopupTrigger()) {
          // get token at triggered location
          int row = ttModel.getRowForNode(getNode(getSelectedRow()));
          if (row >= 0) {
            ILexerToken token = tableModel.getTokenAt(row);
            // get provider for token
            IInfoProvider provider = tableModel.getInfoProviderForToken(token);

            List<ITokenAction> actions = provider.getActionsForToken(token);

            if (actions != null && actions.size() > 0) {
              // TODO sr137: cache menu
              JPopupMenu menu = new JPopupMenu();
              for (final ITokenAction a : actions) {
                menu.add(new javax.swing.AbstractAction(a.getDescription()) {

                  /**
                   * Serialization ID
                   */
                  private static final long serialVersionUID =
                      -873403286157925245L;

                  @Override
                  public void actionPerformed(ActionEvent aEvent) {
                    Reader input = new DocumentReader(editor.getDocument());
                    StringWriter output = new StringWriter();
                    if (a.run(input, output)) {
                      // replace text in editor
                      int caretPosition = editor.getCaretPosition();
                      editor.setText(output.toString());
                      editor.setCaretPosition(caretPosition);
                    }
                  }
                });
              }

              // show selected token
              editor.select(token.getStart(), token.getEnd());
              editor.requestFocus();
              menu.show(event.getComponent(), event.getX(), event.getY());
            }
          }

          event.consume();
        }
      }
    });
  }

  public void addInfoProvider(IInfoProvider p) {
    tableModel.addInfoProvider(p);
  }

  public void removeInfoProvider(IInfoProvider p) {
    tableModel.removeInfoProvider(p);
  }

  @Override
  public void setTreeTableModel(ITreeTableModel model) {
    if (model == null) {
      return;
    }
    throw new UnsupportedOperationException("You can't change the model");
  }

  public TableModel getTableModel() {
    return tableModel;
  }
}
