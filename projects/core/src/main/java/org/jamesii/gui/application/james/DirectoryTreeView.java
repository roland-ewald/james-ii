/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.james;

import java.awt.Component;
import java.awt.Cursor;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.jamesii.SimSystem;
import org.jamesii.gui.application.Contribution;
import org.jamesii.gui.application.IProgressListener;
import org.jamesii.gui.application.action.AbstractAction;
import org.jamesii.gui.application.action.ActionSet;
import org.jamesii.gui.application.action.IAction;
import org.jamesii.gui.application.resource.IconManager;
import org.jamesii.gui.application.resource.iconset.IconIdentifier;

/**
 * View for JAMES II GUI that provides access to the directory structure for a
 * given directory or a given set of directories.
 * 
 * @author Stefan Rybacki
 * 
 */
public class DirectoryTreeView extends DefaultTreeView implements
    IProgressListener {

  /**
   * {@link javax.swing.tree.TreeCellRenderer} that renders the nodes of the
   * {@link DirectoryTreeView} using the system icons of the shown {@link File}
   * item if available.
   * 
   * @author Stefan Rybacki
   * 
   */
  private static class FileTreeNodeRenderer extends DefaultTreeCellRenderer {
    /**
     * Serialization ID
     */
    private static final long serialVersionUID = -6572171692214206999L;

    /**
     * default renderer as delegate to ensure correct used for L&F switches
     */
    private DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();

    /**
     * file system view used to retrieve the icons for given {@link File}
     * objects
     */
    private final FileSystemView fileSystemView = FileSystemView
        .getFileSystemView();

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
        boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus1) {

      DefaultTreeCellRenderer r =
          (DefaultTreeCellRenderer) renderer.getTreeCellRendererComponent(tree,
              value, sel, expanded, leaf, row, hasFocus1);

      if ((value instanceof DefaultMutableTreeNode)
          && (((DefaultMutableTreeNode) value).getUserObject() instanceof File)) {
        r.setIcon(fileSystemView
            .getSystemIcon((File) ((DefaultMutableTreeNode) value)
                .getUserObject()));
        r.setText(fileSystemView
            .getSystemDisplayName((File) ((DefaultMutableTreeNode) value)
                .getUserObject()));
      } else {
        r.setIcon(null);
      }

      return r;
    }

    @Override
    public void updateUI() {
      renderer = new DefaultTreeCellRenderer();
    }
  }

  /**
   * list of custom actions
   */
  private final List<IAction> actions = new ArrayList<>();

  /**
   * view's icon
   */
  private Icon icon;

  /**
   * Creates a new view for directory structures using the given title,
   * contribution and a list of directories that should be accessible through
   * this view.
   * 
   * @param title
   *          the title
   * @param contribution
   *          the view's contribution
   * @param directories
   *          an array of directories
   */
  public DirectoryTreeView(String title, Contribution contribution,
      File... directories) {
    super(title, null, contribution, null);

    if (directories == null || directories.length == 0) {
      throw new IllegalArgumentException(
          "Directories can't be null or an empty list!");
    }

    icon = IconManager.getIcon(IconIdentifier.FOLDER_SMALL, "Choose");

    actions.add(new ActionSet("org.jamesii.choose", null, "Choose", null, null,
        icon, this));

    for (int i = 0; i < directories.length; i++) {
      final File dir = directories[i];
      actions.add(new AbstractAction(directories[i].getName(), FileSystemView
          .getFileSystemView().getSystemDisplayName(dir), null, null,
          FileSystemView.getFileSystemView().getSystemIcon(dir),
          new String[] { "org.jamesii.choose" }, null, null, this) {
        @Override
        public void execute() {
          DirectoryTreeView.this.setTreeModel(new DirectoryTreeModel(dir,
              DirectoryTreeView.this));
        }
      });
    }

    setTreeCellRenderer(new FileTreeNodeRenderer());
  }

  /**
   * Creates a new view for directory structures using the given title,
   * contribution and a list of directories that should be accessible through
   * this view.
   * 
   * @param title
   *          the title
   * @param contribution
   *          the view's contribution
   * @param directories
   *          a list of directories
   */
  public DirectoryTreeView(String title, Contribution contribution,
      List<File> directories) {
    this(title, contribution, directories.toArray(new File[directories.size()]));
  }

  @Override
  public Icon getWindowIcon() {
    return icon;
  }

  @Override
  protected IAction[] generateActions() {
    return actions.toArray(new IAction[actions.size()]);
  }

  @Override
  public boolean canClose() {
    SimSystem.report(Level.INFO, null,
        String.format("%s can not be closed!", getTitle()), null);
    return false;
  }

  @Override
  public String getWindowID() {
    return "org.jamesii.view.directory";
  }

  @Override
  public void finished(Object source) {
    getContent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
  }

  @Override
  public void progress(Object sender, float progress) {
    // nothing to do
  }

  @Override
  public void started(Object source) {
    getContent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
  }

  @Override
  public void taskInfo(Object source, String info) {
    // nothing to do
  }

}
