/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment.windows.edit;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.variables.ExperimentVariable;
import org.jamesii.core.experiments.variables.ExperimentVariables;
import org.jamesii.core.services.ServiceInfo;
import org.jamesii.gui.application.Contribution;
import org.jamesii.gui.application.action.AbstractAction;
import org.jamesii.gui.application.action.IAction;
import org.jamesii.gui.application.james.DefaultTreeView;
import org.jamesii.gui.application.resource.IconManager;
import org.jamesii.gui.application.resource.iconset.IconIdentifier;
import org.jamesii.gui.utils.AbstractTreeModel;
import org.jamesii.gui.utils.FilteredTreeModel;
import org.jamesii.gui.utils.SortedTreeModel;
import org.jamesii.gui.utils.TextFilter;

/**
 * Basic view that displays the experiment variables defined in an experiment.
 * 
 * @author Jan Himmelspach
 */
public class ExperimentVariablesView extends DefaultTreeView {
  /**
   * Creates the experiment variables tree.
   * 
   * @param root
   *          the root
   * @param variables
   *          the variables
   */
  public static void createExperimentVariablesTree(DefaultMutableTreeNode root,
      ExperimentVariables variables) {
    ExperimentVariables vars = variables;

    DefaultMutableTreeNode node = root;

    while (vars != null) {

      DefaultMutableTreeNode node2;
      node.add(node2 =
          new DefaultMutableTreeNode(String.format("<html><b>%s</b> </html>",
              vars.getName())));

      for (ExperimentVariable<?> var : vars.getVariables()) {
        node2.add(new DefaultMutableTreeNode(String.format(
            "<html><b>%s</b> </html>", var.getName())));
      }

      node = node2;
      vars = vars.getSubLevel();

    }
  }

  /**
   * A tree model wrapping the tree structure of experiment variables of an
   * experiment for use in a {@link JTree}.
   * 
   * @author Stefan Rybacki
   */
  private static class ExperimentVariablesTreeModel extends AbstractTreeModel {

    /** root node for tree model. */
    private ExperimentVariables root;

    /**
     * Creates a new instance.
     * 
     * @param variables
     *          the variables
     */
    public ExperimentVariablesTreeModel(ExperimentVariables variables) {
      super();
      root = variables;
      // TODO sr137: evaluate whether this is needed
      if (root == null) {
        root = new ExperimentVariables();
      }
    }

    @Override
    public Object getChild(Object parent, int index) {
      if (parent instanceof ExperimentVariables) {
        if (index >= 0
            && index < ((ExperimentVariables) parent).getVariables().size()) {
          return ((ExperimentVariables) parent).getVariables().get(index);
        }
        return ((ExperimentVariables) parent).getSubLevel();
      }
      return null;
    }

    @Override
    public int getChildCount(Object parent) {
      if (parent instanceof ExperimentVariables) {
        return ((ExperimentVariables) parent).getVariables().size()
            + (((ExperimentVariables) parent).getSubLevel() != null ? 1 : 0);
      }
      return 0;
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
      return 0;
    }

    @Override
    public Object getRoot() {
      return root;
    }

    @Override
    public boolean isLeaf(Object node) {
      return (node instanceof ExperimentVariables);
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
      throw new UnsupportedOperationException("Not supported by this TreeModel");
    }

  }

  /**
   * The Class NodeInfo.
   */
  public static class NodeInfo {

    /** The description. */
    private String description;

    /** The info. */
    private ServiceInfo info;

    /**
     * Instantiates a new node info.
     * 
     * @param d
     *          the d
     * @param si
     *          the si
     */
    public NodeInfo(String d, ServiceInfo si) {
      description = d;
      info = si;
    }

    @Override
    public String toString() {
      return description;
    }

  }

  /** Tree model able to filter another tree model. */
  private FilteredTreeModel<String> model;

  /**
   * Creates a new service view.
   * 
   * @param contribution
   *          the vies's contribution
   * @param experiment
   *          the experiment
   */
  public ExperimentVariablesView(ExperimentVariables experiment,
      Contribution contribution) {
    super("Experiment variables", new DefaultTreeModel(null), contribution,
        null);

    // experiment = new ExperimentVariables();
    // experiment.addVariable(new ExperimentVariable<Integer>("hugo ",
    // 2, new IncrementModifierInteger()));
    //
    // ExperimentVariables nextLevel = new ExperimentVariables();
    // nextLevel.addVariable(new ExperimentVariable<Integer>("Berta ",
    // 4, new IncrementModifierInteger()));
    // experiment.setSubLevel(nextLevel);
    //
    model =
        new FilteredTreeModel<>(new ExperimentVariablesTreeModel(experiment),
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
    setTreeCellRenderer(new DefaultTreeCellRenderer() {
      /**
       * Serialization ID
       */
      private static final long serialVersionUID = 8927589163156798558L;

      @Override
      public Component getTreeCellRendererComponent(JTree tree, Object value,
          boolean sel, boolean expanded, boolean leaf, int row, boolean focus) {
        if (value == getTreeModel().getRoot()) {
          return super.getTreeCellRendererComponent(tree,
              "<html><b>Variables</b></html>", sel, expanded, leaf, row, focus);
        }
        if (value instanceof ExperimentVariables) {
          return super.getTreeCellRendererComponent(tree,
              String.format("<html><b>%s</b></html>",
                  ((ExperimentVariables) value).getName()), sel, expanded,
              leaf, row, focus);
        }
        if (value instanceof ExperimentVariable<?>) {
          return super.getTreeCellRendererComponent(tree, String.format(
              "<html><b>%s</b></html>",
              ((ExperimentVariable<?>) value).getName()), sel, expanded, leaf,
              row, focus);
        }
        return super.getTreeCellRendererComponent(tree, value, sel, expanded,
            leaf, row, focus);
      }
    });
  }

  /**
   * Adds the event.
   * 
   * @param info
   *          the info
   */
  public void addEvent(ServiceInfo info) {

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

    return panel;
  }

  @Override
  public String getWindowID() {
    return "org.jamesii.view.experimentvariables";
  }

  /**
   * Gets the edits the actions.
   * 
   * @return the edits the actions
   */
  public List<IAction> getEditActions() {
    List<IAction> result = new ArrayList<>();

    // fetch the actions icons
    Icon addVariableIcon =
        IconManager.getIcon(IconIdentifier.NEW_SMALL, "Add variable");
    Icon addLevelIcon =
        IconManager.getIcon(IconIdentifier.NEXT_SMALL, "Add sub level");
    Icon addOptimizationIcon =
        IconManager.getIcon(IconIdentifier.WIZARD_SMALL, "Add optimization");
    Icon deleteIcon =
        IconManager.getIcon(IconIdentifier.DELETE_SMALL, "Delete entry");

    // add variable on the current level

    IAction addVariableAction =
        new AbstractAction("variables.variable.add", "Add variable",
            addVariableIcon, new String[] { "" }, null, null, this) {

          @Override
          public void execute() {
            // synchronized (this) {
            // }
          }
        };
    addVariableAction.setEnabled(true);

    // add sub level

    IAction addLevelAction =
        new AbstractAction("variables.sublevel.add", "Add sub level",
            addLevelIcon, new String[] { "" }, null, null, this) {

          @Override
          public void execute() {
            // synchronized (this) {
            // }
          }
        };
    addLevelAction.setEnabled(true);

    // add optimization

    IAction addOptimizationAction =
        new AbstractAction("variables.optimization.add", "Add optimization",
            addOptimizationIcon, new String[] { "" }, null, null, this) {

          @Override
          public void execute() {
            // synchronized (this) {
            // }
          }
        };
    addOptimizationAction.setEnabled(true);

    // remove variable / sub level / optimization

    IAction addORemoveAction =
        new AbstractAction("variables.delete", "Delete entry", deleteIcon,
            new String[] { "" }, null, null, this) {

          @Override
          public void execute() {
            // synchronized (this) {
            // }
          }
        };
    addORemoveAction.setEnabled(true);

    return result;
  }

  @Override
  protected IAction[] generateActions() {

    IAction[] inh = super.generateActions();

    List<IAction> actions = new ArrayList<>(Arrays.asList(inh));

    actions.addAll(getEditActions());

    return actions.toArray(new IAction[actions.size()]);
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(500, 600);
  }
}
