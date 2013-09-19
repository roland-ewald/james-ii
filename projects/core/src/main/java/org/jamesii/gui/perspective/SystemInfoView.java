/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.perspective;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.jamesii.SimSystem;
import org.jamesii.core.util.info.JavaInfo;
import org.jamesii.gui.application.Contribution;
import org.jamesii.gui.application.james.DefaultTreeView;
import org.jamesii.gui.utils.FilteredTreeModel;
import org.jamesii.gui.utils.SortedTreeModel;
import org.jamesii.gui.utils.TextFilter;

// TODO: Auto-generated Javadoc
/**
 * Basic view that displays information about the in JAMES II installed plugins.
 * 
 * @author Stefan Rybacki
 */
public class SystemInfoView extends DefaultTreeView {

  /**
   * A tree model providing the plugins as tree like structure for use in a
   * {@link javax.swing.JTree}.
   * 
   * @author Stefan Rybacki
   */
  private static class SystemInfoTreeModel extends DefaultTreeModel {

    /** Root node for tree model. */
    private DefaultMutableTreeNode root;

    /**
     * Creates a new instance.
     * 
     * @param info
     *          the info
     * @param system
     *          the system
     * 
     * */
    public SystemInfoTreeModel(JavaInfo info, String system) {
      super(new DefaultMutableTreeNode(String.format(
          "<html><b>System info of %s</b></html>", system)), true);
      root = (DefaultMutableTreeNode) getRoot();

      for (java.lang.reflect.Method m : JavaInfo.class.getDeclaredMethods()) {

        String mN = m.getName();

        // ignore everything besides the getters
        if (mN.startsWith("get")) {

          try {
            root.add(new DefaultMutableTreeNode(String.format(
                "<html><b>%s</b> <i> %s</i></html>", mN.substring(3),
                m.invoke(info, (Object[]) null))));
          } catch (IllegalArgumentException | IllegalAccessException
              | InvocationTargetException e) {
            SimSystem.report(e);
          }

        }

      }

    }

    /** Serialization ID. */
    private static final long serialVersionUID = -7755537722696968072L;

  }

  /** Tree model able to filter another tree model. */
  private FilteredTreeModel<String> model;

  /**
   * Creates a new service view.
   * 
   * @param contribution
   *          the vies's contribution
   * @param info
   *          the info
   * @param systemName
   *          the system name
   */
  public SystemInfoView(JavaInfo info, String systemName,
      Contribution contribution) {
    super("System information Inspector", new DefaultTreeModel(null),
        contribution, null);

    setTitle("System Information");

    model =
        new FilteredTreeModel<>(new SystemInfoTreeModel(info, systemName),
            new TextFilter());
    // addTreeSelectionListener(new TreeSelectionListener() {
    // @Override
    // public void valueChanged(TreeSelectionEvent e) {
    // selectedNodeInfo = getSelectedNode();
    // logAction.setEnabled(selectedNodeInfo != null);
    // }
    //
    // });
    setTreeModel(new SortedTreeModel(model));
  }

  @Override
  protected JComponent createContent() {
    JPanel panel = new JPanel(new BorderLayout());

    JTextField filterTextField = new JTextField(30);

    Box filterTextBox = Box.createHorizontalBox();

    filterTextField.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void changedUpdate(DocumentEvent e) {
        try {
          ((TextFilter) model.getFilter()).setFilterValue(e.getDocument()
              .getText(0, e.getDocument().getLength()).toLowerCase());
        } catch (BadLocationException e1) {
          SimSystem.report(e1);
        }
      }

      @Override
      public void insertUpdate(DocumentEvent e) {
        changedUpdate(e);
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        changedUpdate(e);
      }
    });

    filterTextBox.add(Box.createHorizontalStrut(10));
    filterTextBox.add(new JLabel("Filter tree by text:"));
    filterTextBox.add(Box.createHorizontalStrut(5));
    filterTextBox.add(filterTextField);
    filterTextBox.add(Box.createHorizontalStrut(10));

    panel.add(filterTextBox, BorderLayout.NORTH);
    panel.add(super.createContent(), BorderLayout.CENTER);

    // JPopupMenu popup = new JPopupMenu();
    // popup.add(new JMenuItem("Show log") {
    //
    // /** The Constant serialVersionUID. */
    // private static final long serialVersionUID =
    // 711178352547639814L;
    //
    //
    //
    // });
    //
    // addPopupMenu (popup);

    return panel;
  }

  // /**
  // * Method that returns the node info for the currently selected server node
  // *
  // * @return the node info for selected server or null if no server is
  // selected
  // */
  // public NodeInfo getSelectedNode() {
  // Object n = getSelectedTreeNode();
  // if (n instanceof UserMutableTreeNode) {
  // Object[] userObjectPath = ((UserMutableTreeNode) n).getUserObjectPath();
  // // try to find a NodeInfo node on path to currently selected
  // // node
  // for (Object o : userObjectPath) {
  // if (o instanceof NodeInfo)
  // return (NodeInfo) o;
  // }
  // }
  // return null;
  // }

  @Override
  public String getWindowID() {
    return "org.jamesii.view.systeminfo";
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(500, 600);
  }
}
