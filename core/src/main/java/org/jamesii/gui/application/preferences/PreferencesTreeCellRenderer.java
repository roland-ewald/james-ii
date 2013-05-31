/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.preferences;

import java.awt.Color;
import java.awt.Component;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.jamesii.gui.base.URLTreeNode;

/**
 * A special renderer for internal use in the preferences tree of the
 * {@link PreferencesDialog}. It shows the hierarchical structure of the
 * preferences pages as well as checks validity of each and shows invalid
 * branches in red color.
 * 
 * @author Stefan Rybacki
 */
class PreferencesTreeCellRenderer extends DefaultTreeCellRenderer {
  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -6448022378214613706L;

  /**
   * internally used renderer that is delegated to
   */
  private DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();

  @Override
  public Component getTreeCellRendererComponent(JTree tree, Object value,
      boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus1) {

    DefaultTreeCellRenderer r =
        (DefaultTreeCellRenderer) renderer.getTreeCellRendererComponent(tree,
            value, sel, expanded, leaf, row, hasFocus1);
    // if not valid mark entry not valid
    if (!isValid(value)) {
      r.setForeground(Color.red);
    }
    return r;
  }

  /**
   * Helper function that recursively checks the validity of a node and its
   * children.
   * 
   * @param value
   *          the node to inspect
   * @return true if the node and all its children are valid
   */
  private boolean isValid(Object value) {
    // check whether value is an URLTreeNode with a List of IPreferencesPages
    boolean valid = true;
    if (value instanceof URLTreeNode<?>) {
      // check children for validity first
      for (int i = 0; i < ((URLTreeNode<?>) value).getChildCount(); i++) {
        valid = valid && isValid(((URLTreeNode<?>) value).getChildAt(i));
        if (!valid) {
          break;
        }
      }

      if (!valid) {
        return valid;
      }

      // check whether attached object is a list
      if (((URLTreeNode<?>) value).getAttachedObject() instanceof List<?>) {
        List<?> list = (List<?>) ((URLTreeNode<?>) value).getAttachedObject();
        // now check whether list has any items and if those items of type
        // IPreferencesPage check
        // the entire list for validity
        if (list.size() > 0 && (list.get(0) instanceof IPreferencesPage)) {
          for (Object o : list) {
            IPreferencesPage page = (IPreferencesPage) o;
            valid = valid && page.isValid();
          }
        }
      }
    }
    return valid;
  }

  @Override
  public void updateUI() {
    renderer = new DefaultTreeCellRenderer();
  }

}
