/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.server;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.rmi.RemoteException;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.tree.DefaultTreeModel;

import org.jamesii.SimSystem;
import org.jamesii.core.hosts.system.IMSSystemHost;
import org.jamesii.gui.application.Contribution;
import org.jamesii.gui.application.action.IAction;
import org.jamesii.gui.application.james.DefaultTreeView;
import org.jamesii.gui.server.view.util.NodeInfo;
import org.jamesii.gui.server.view.util.UserMutableTreeNode;
import org.jamesii.gui.service.view.IServiceView;
import org.jamesii.gui.utils.FilteredTreeModel;
import org.jamesii.gui.utils.SortedTreeModel;
import org.jamesii.gui.utils.TextFilter;

/**
 * Basic class for Views displaying information about servers.
 * 
 * @author Stefan Leye
 * 
 */
public abstract class ServerView extends DefaultTreeView implements
    IServiceView {

  /** Tree model able to filter another tree model. */
  private FilteredTreeModel<String> model;

  /** The server. */
  private IMSSystemHost server;

  /** The selected node info. */
  private NodeInfo<?> selectedNodeInfo;

  /**
   * Creates a new service view.
   * 
   * @param contribution
   *          the vies's contribution
   * @param server
   *          the server
   * @param title
   *          the title
   */
  public ServerView(String title, IMSSystemHost server,
      Contribution contribution) {
    super(title, new DefaultTreeModel(null), contribution, null);
    this.server = server;
    try {
      this.setTitle(server.getName() + ":" + getTitle());
    } catch (RemoteException e) {
      // just ignore ...
    }
    model = createModel();

    addTreeSelectionListener(new TreeSelectionListener() {
      @Override
      public void valueChanged(TreeSelectionEvent e) {
        // notify window about action change
        selectedNodeInfo = updateSelectedNode();
        IAction[] oldActions = getActions();
        fireActionsChanged(oldActions);
      }

    });
    setTreeModel(new SortedTreeModel(model));
  }

  /**
   * Creates the model.
   * 
   * @return the filtered tree model< string>
   */
  protected abstract FilteredTreeModel<String> createModel();

  /**
   * Gets the model.
   * 
   * @return the model
   */
  public FilteredTreeModel<String> getModel() {
    return model;
  }

  @Override
  public JComponent createContent() {
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

  /**
   * Gets the server for this view.
   * 
   * @return the server
   */
  public IMSSystemHost getServer() {
    return server;
  }

  /**
   * Returns the info object of the currently selected node.
   * 
   * @return the selected node info
   */
  public NodeInfo<?> getSelectedNodeInfo() {
    return selectedNodeInfo;
  }

  /**
   * Sets the selected node info.
   * 
   * @param selectedNodeInfo
   *          the selected node info
   */
  @SuppressWarnings("unchecked")
  public void setSelectedNodeInfo(NodeInfo selectedNodeInfo) {
    this.selectedNodeInfo = selectedNodeInfo;
  }

  /**
   * Method that returns the node info for the currently selected server node.
   * 
   * @return the node info for selected server or null if no server is selected
   */
  @SuppressWarnings("unchecked")
  public NodeInfo<?> updateSelectedNode() {
    Object n = getSelectedTreeNode();
    if (n instanceof UserMutableTreeNode) {
      Object[] userObjectPath = ((UserMutableTreeNode) n).getUserObjectPath();
      // try to find a NodeInfo node on path to currently selected
      // node
      for (Object o : userObjectPath) {
        if (o instanceof NodeInfo) {
          return (NodeInfo<?>) o;
        }
      }
    }
    return null;
  }

  @Override
  public String getWindowID() {
    return "org.jamesii.view.service";
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(500, 600);
  }
}
