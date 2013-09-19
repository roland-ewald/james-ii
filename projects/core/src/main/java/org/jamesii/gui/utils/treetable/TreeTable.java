/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.treetable;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 * A {@link JTable} which can display hierarchically organised tabular data.
 * 
 * @author Johannes Rössel
 * @author Stefan Rybacki
 */
public class TreeTable extends JTable {

  /** Serialisation ID. */
  private static final long serialVersionUID = 6267267630235402552L;

  /**
   * The {@link TreeTableViewModel} for this tree table. The view model handles
   * collapsing and expanding of rows.
   */
  private TreeTableViewModel ttvm;

  /**
   * A mouse listener for expanding/collapsing nodes by using double-clicking.
   */
  private final transient MouseListener clickMouseListener =
      new MouseAdapter() {
        // @Override
        // public void mouseClicked(MouseEvent e) {
        //
        // if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() ==
        // 1) {
        // int rowIndex = rowAtPoint(e.getPoint());
        // Point position = e.getPoint();
        // if (!ttvm.isLeaf(ttvm.getNode(rowIndex))
        // && position.getX() - 12 * ttvm.getDepth(rowIndex) + 2 <= 10) {
        // ttvm.setExpanded(ttvm.getNode(rowIndex), !ttvm
        // .isExpanded(ttvm.getNode(rowIndex)));
        // System.out.println("1");
        // e.consume();
        // }
        // }
        // }

        @Override
        public void mousePressed(MouseEvent e) {
          if (e.getButton() == MouseEvent.BUTTON1) {

            int rowIndex = rowAtPoint(e.getPoint());
            if (rowIndex < 0) {
              return;
            }
            Point position = e.getPoint();
            int depth = ttvm.getDepth(rowIndex);
            if (!ttvm.isLeaf(ttvm.getNode(rowIndex))
                && TreeTableCellRenderer.isClickedOnIcon(depth, position.x,
                    position.y)) {
              ttvm.setExpanded(ttvm.getNode(rowIndex),
                  !ttvm.isExpanded(ttvm.getNode(rowIndex)));
              e.consume();
            }
          }
        };
      };

  public TreeTable() {
    this(new DummyTreeTableModel());
  }

  
  
  
  /**
   * initializes a new instance of the {@link TreeTable} class with the
   * specified {@link ITreeTableModel} as data source.
   * 
   * @param dm
   *          The {@link ITreeTableModel} for the contents of the
   *          {@link TreeTable}.
   */
  public TreeTable(ITreeTableModel dm) {
    super();

    setTreeTableModel(dm);

    setTerminateEditingOnFocusLost(true);

    this.addMouseListener(clickMouseListener);
    this.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
          e.consume();
        }
      }
    });

    this.addKeyListener(new KeyAdapter() {
      @Override
      public void keyTyped(KeyEvent e) {
        if ((e.getKeyChar() == '+' || e.getKeyChar() == '-')
            && e.getModifiers() == 0) {
          e.consume();
          int rowIndex = getSelectedRow();
          if (rowIndex >= 0 && !ttvm.isLeaf(ttvm.getNode(rowIndex))) {
            ttvm.setExpanded(ttvm.getNode(rowIndex), e.getKeyChar() == '+');
          }
        }
      }
    });
  }

  @Override
  protected TableModel createDefaultDataModel() {
    return null;
  }

  @Override
  public final TableCellRenderer getCellRenderer(int row, int column) {
    if (column == 0) {
      return new TreeTableCellRenderer(getCellRendererFor(row, column));
    }

    return getCellRendererFor(row, column);
  }

  /**
   * Gets the cell renderer for the specified cell. Override this if you want to
   * provide custom cell renderers.
   * 
   * @param row
   *          the row
   * @param column
   *          the column
   * @return the cell renderer for the specified cell
   */
  protected TableCellRenderer getCellRendererFor(int row, int column) {
    return super.getCellRenderer(row, column);
  }

  @Override
  public void setModel(TableModel dataModel) {
    if (dataModel == null) {
      return;
    }
    throw new UnsupportedOperationException(
        "setModel not supported use setTreeTableModel instead");
  }

  /**
   * Sets the tree table model.
   * 
   * @param model
   *          the new tree table model to set
   */
  public void setTreeTableModel(ITreeTableModel model) {
    ttvm = new TreeTableViewModel(model);
    super.setModel(ttvm);
  }

  @Override
  public TableModel getModel() {
    return ttvm;
  }

  /**
   * Returns the currently used {@link TreeTableViewModel}. This is needed by
   * the cell renderer, for example, as the view model keeps track of collapsed
   * and expanded rows.
   * 
   * @return The {@link TreeTableViewModel} currently used by this
   *         {@link TreeTable}.
   */
  public ITreeTableModel getTreeTableModel() {
    return ttvm.getModel();
  }

  @Override
  public Dimension getPreferredScrollableViewportSize() {
    return new Dimension(200, Math.min(ttvm.getRowCount(), 8) * getRowHeight());
  }

  /**
   * Gets a row's view index for a given node.
   * 
   * @param node
   *          the node
   * @return The associated index in the visible table, or −1 if there is no
   *         such view index.
   */
  public int getViewIndex(Object node) {
    return ttvm.getViewIndex(node);
  }

  /**
   * Gets the node at the specified row index.
   * 
   * @param rowIndex
   *          the row index
   * @return the node at the specified row
   */
  public Object getNode(int rowIndex) {
    return ttvm.getNode(rowIndex);
  }

  /**
   * Sets whether the root is visible.
   * 
   * @param visible
   *          if {@code true} root will be visible
   */
  public void setRootVisible(boolean visible) {
    ttvm.setRootVisible(visible);
  }

  /**
   * Checks if is root visible.
   * 
   * @return <code>true</code>, if is root visible
   */
  public boolean isRootVisible() {
    return ttvm.isRootVisible();
  }

  /**
   * Retrieves the current setting that determines what happens when the table
   * cell editor loses focus. If {@code true} then the editor commits its value
   * as soon as focus shifts to another control. Otherwise the editor stays
   * active unless focus shifts to another table cell.
   * 
   * @return A value determining whether any focus change should end editing a
   *         table cell.
   */
  public boolean getTerminateEditingOnFocusLost() {
    Object value = getClientProperty("terminateEditOnFocusLost");
    return value != null && value instanceof Boolean && (Boolean) value;
  }

  /**
   * Sets the behaviour for losing focus to other controls when currently
   * editing a table cell. If {@code true} then any change of focus—also to
   * controls outside the table—will cause the editor to commit its current
   * value.
   * <p>
   * By default this is set to {@code true}.
   * 
   * @param value
   *          The new behaviour.
   */
  public void setTerminateEditingOnFocusLost(boolean value) {
    putClientProperty("terminateEditOnFocusLost", value);
  }

  /**
   * Small class for implementing the {@link ITreeTableModel} in a nice tree
   * fashion.
   * 
   * @author Johannes Rössel
   */
  @SuppressWarnings("all")
  private static class TreeNode {
    private Object firstColumn;

    private Object secondColumn;

    private List<TreeNode> children;

    public TreeNode(Object firstColumn, Object secondColumn,
        List<TreeNode> children) {
      this.setFirstColumn(firstColumn);
      this.setSecondColumn(secondColumn);
      this.setChildren(children);
    }

    public void setFirstColumn(Object firstColumn) {
      this.firstColumn = firstColumn;
    }

    public Object getFirstColumn() {
      return firstColumn;
    }

    public void setSecondColumn(Object secondColumn) {
      this.secondColumn = secondColumn;
    }

    public Object getSecondColumn() {
      return secondColumn;
    }

    public void setChildren(List<TreeNode> children) {
      this.children = children;
    }

    public List<TreeNode> getChildren() {
      return children;
    }
  }
}
