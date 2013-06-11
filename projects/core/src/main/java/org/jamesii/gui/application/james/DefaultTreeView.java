/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.james;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.jamesii.gui.application.AbstractWindow;
import org.jamesii.gui.application.Contribution;
import org.jamesii.gui.application.action.AbstractAction;
import org.jamesii.gui.application.action.IAction;
import org.jamesii.gui.syntaxeditor.JamesUndoManager;

/**
 * Base class that can be used to provide tree like views for plugging into the
 * JAMES II GUI. Just provide a tree model, a contribution, an icon and override
 * methods like {@link #generateActions()} accordingly to provide custom
 * actions.
 * 
 * @author Stefan Rybacki
 * 
 */
public class DefaultTreeView extends AbstractWindow {
  /**
   * the swing tree
   */
  private JTree tree;

  /**
   * Creates a tree view for use in JAMES II GUI
   * 
   * @param title
   *          the view title
   * @param model
   *          the tree model to use
   * @param contribution
   *          the view contribution
   * @param icon
   *          the view's icon
   */
  public DefaultTreeView(String title, TreeModel model,
      Contribution contribution, Icon icon) {
    super(title, icon, contribution);
    tree = new JTree();
    setTreeModel(model);
  }

  /**
   * Sets a custom tree cell render that is used to render the tree.
   * 
   * @param renderer
   *          the render to use
   */
  protected final void setTreeCellRenderer(TreeCellRenderer renderer) {
    tree.setCellRenderer(renderer);
  }

  /**
   * Sets a custom tree cell editor that is used to edit the tree.
   * 
   * @param editor
   *          the editor to use
   */
  protected final void setTreeCellEditor(TreeCellEditor editor) {
    tree.setCellEditor(editor);
  }

  /**
   * sets the tree model
   * 
   * @param model
   *          the model
   */
  protected final void setTreeModel(TreeModel model) {
    tree.setModel(model);
  }

  @Override
  protected JComponent createContent() {
    return new JScrollPane(tree);
  }

  @Override
  public JamesUndoManager getUndoManager() {
    return null;
  }

  @Override
  public boolean isUndoRedoSupported() {
    return false;
  }

  /**
   * Adds a tree selection listener to the tree
   * 
   * @param l
   *          the listener to add
   */
  public final synchronized void addTreeSelectionListener(
      TreeSelectionListener l) {
    tree.addTreeSelectionListener(l);
  }

  /**
   * Adds the popup menu.
   * 
   * @param popup
   *          the popup
   */
  public final synchronized void addPopupMenu(JPopupMenu popup) {
    tree.add(popup);
  }

  /**
   * Removes a previously registered tree selection listener
   * 
   * @param l
   *          the listener to remove
   */
  public final synchronized void removeTreeSelectionListener(
      TreeSelectionListener l) {
    tree.removeTreeSelectionListener(l);
  }

  /**
   * @return the currently selected tree node
   */
  public final synchronized Object getSelectedTreeNode() {
    TreePath selectionPath = tree.getSelectionPath();
    if (selectionPath == null) {
      return null;
    }
    return tree.getSelectionPath().getLastPathComponent();
  }

  /**
   * @return the currently used tree model
   */
  public final TreeModel getTreeModel() {
    return tree.getModel();
  }

  /**
   * Sets the flag, denoting whether the underlying tree is editable.
   * 
   * @param flag
   */
  public void setEditable(boolean flag) {
    this.tree.setEditable(flag);
  }

  @Override
  protected IAction[] generateActions() {
    return new IAction[] { new AbstractAction("tree.expandAll", "Expand All",
        new String[] { "" }, this) {

      @Override
      public void execute() {
        for (int i = 0; i < tree.getRowCount(); i++) {
          tree.expandRow(i);
        }
      }
    } };
  }

  /**
   * Get the underlying tree.
   * 
   * @return the tree
   */
  public JTree getTree() {
    return tree;
  }

}
