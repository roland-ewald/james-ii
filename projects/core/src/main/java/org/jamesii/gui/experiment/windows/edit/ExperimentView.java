/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment.windows.edit;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.services.ServiceInfo;
import org.jamesii.gui.application.Contribution;
import org.jamesii.gui.application.action.IAction;
import org.jamesii.gui.application.james.DefaultTreeView;
import org.jamesii.gui.utils.FilteredTreeModel;
import org.jamesii.gui.utils.SortedTreeModel;
import org.jamesii.gui.utils.TextFilter;

// TODO: Auto-generated Javadoc
/**
 * Basic view that displays the experiment variables defined in an experiment.
 * 
 * @author Jan Himmelspach
 */
public class ExperimentView extends DefaultTreeView {

  /**
   * A tree model wrapping the tree structure of experiment variables of an
   * experiment for use in a {@link javax.swing.JTree}.
   * 
   * @author Jan Himmelspach
   */
  private static class ExperimentTreeModel extends DefaultTreeModel {

    /** root node for tree model. */
    private DefaultMutableTreeNode root;

    /**
     * Creates a new instance.
     * 
     * @param experiment
     *          the experiment
     */
    public ExperimentTreeModel(BaseExperiment experiment) {
      super(new DefaultMutableTreeNode("<html><b>Experiment</b></html>"), true);

      DefaultMutableTreeNode node2;

      root = (DefaultMutableTreeNode) getRoot();

      // the model
      root.add(node2 = new DefaultMutableTreeNode("Model"));

      node2.add(new DefaultMutableTreeNode(String.format(
          "<html><b>Source: %s</b> </html>", experiment.getModelLocation())));

      // the model variables
      root.add(node2 = new DefaultMutableTreeNode("Model parameters"));

      ExperimentVariablesView.createExperimentVariablesTree(node2,
          experiment.getExperimentVariables());

      // the simulation parameters
      root.add(node2 = new DefaultMutableTreeNode("Simulation parameters"));

      // the execution
      root.add(node2 = new DefaultMutableTreeNode("Simulation runner"));

      node2
          .add(new DefaultMutableTreeNode(String.format(
              "<html><b>Source: %s</b> </html>",
              experiment.getTaskRunnerFactory())));

      // replications
      root.add(node2 = new DefaultMutableTreeNode("Replications"));

      node2.add(new DefaultMutableTreeNode(String.format(
          "<html><b>Rep.-crit.: %s</b> </html>",
          experiment.getReplicationCriterionFactory())));

      // data sink
      root.add(node2 = new DefaultMutableTreeNode("Data sink"));

      node2.add(new DefaultMutableTreeNode(
          String.format("<html><b>Source: %s</b> </html>",
              experiment.getDataStorageFactory())));

      root.add(node2 = new DefaultMutableTreeNode("Model instrumentation"));

      node2.add(new DefaultMutableTreeNode(String.format(
          "<html><b>Instrumenter: %s</b> </html>", experiment.getModelInstrumenterFactory().getFactoryInstance().getName())));

      // for (IModelInstrumenter m : experiment.getModelInstrumenters()) {
      // node2.add(new DefaultMutableTreeNode(String
      // .format("<html><b>Instrumenter: %s</b> </html>",
      // m.getClass().getName())));
      // }

      root.add(node2 = new DefaultMutableTreeNode("Simulation instrumentation"));

      node2.add(new DefaultMutableTreeNode(String.format(
          "<html><b>Instrumenter: %s</b> </html>", experiment.getComputationInstrumenterFactory().getFactoryInstance().getName())));

      // for (ISimulationInstrumenter s :
      // experiment.getSimulationInstrumenters()) {
      // node2.add(new DefaultMutableTreeNode(String
      // .format("<html><b>Instrumenter: %s</b> </html>",
      // s.getClass().getName())));
      // }

    }

    /** Serialization ID. */
    private static final long serialVersionUID = -7755537722696968072L;

  }

  /**
   * The Class UserMutableTreeNode.
   */
  public static class UserMutableTreeNode extends DefaultMutableTreeNode {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2790422525151647711L;

    /**
     * Instantiates a new user mutable tree node.
     * 
     * @param userObject
     *          the user object
     */
    public UserMutableTreeNode(Object userObject) {
      super(userObject);
    }

    @Override
    public Object getUserObject() {
      return userObject;
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
      setInfo(si);
    }

    @Override
    public String toString() {
      return description;
    }

    /**
     * @return the info
     */
    public ServiceInfo getInfo() {
      return info;
    }

    /**
     * @param info
     *          the info to set
     */
    public void setInfo(ServiceInfo info) {
      this.info = info;
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
  public ExperimentView(BaseExperiment experiment, Contribution contribution) {
    super("Experiment view", new DefaultTreeModel(null), contribution, null);

    model =
        new FilteredTreeModel<>(new ExperimentTreeModel(experiment),
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

  @Override
  protected IAction[] generateActions() {

    IAction[] inh = super.generateActions();

    List<IAction> actions = new ArrayList<>(Arrays.asList(inh));

    // Icon logIcon = null;
    // try {
    // logIcon = ApplicationResourceManager
    // .getResource(IconIdentifier.FOLDER_SMALL);
    // } catch (ResourceLoadingException e2) {
    // SimSystem
    // .report(Level.WARNING, null, "Couldn't load icon (%s)",
    // new Object[] { e2.getMessage() });
    // }
    //
    // logAction = new AbstractAction("log.show", "Show Log",
    // logIcon, new String[] { "" }, null, null) {
    //
    // @Override
    // public void execute() {
    // synchronized (this) {
    // NodeInfo o = selectedNodeInfo;
    //
    // if (o == null)
    // return;
    //
    // IService s = null;
    // String a = "rmi://" + o.info.hostAddress.getHostName()
    // + ":10992/" + o.info.name;
    // try {
    //
    // s = (IService) Naming.lookup(a);
    // } catch (Exception e) {
    // Entity.report("Error on connecting to " + a);
    // SimSystem.report(e);
    // }
    //
    // if (s instanceof IMSSystemHost) {
    // IMSSystemHost server = (IMSSystemHost) s;
    //
    // BasicLogView logView;
    // try {
    // logView = new BasicLogView(server.getName(), null,
    // Contribution.EDITOR, null);
    //
    // IRemoteLogListener listener = new RemoteLogListener(
    // logView);
    // server.registerLogListener(listener, true);
    // getWindowManager().addWindow(logView);
    //
    // } catch (RemoteException e) {
    // SimSystem.report(e);
    // }
    // }
    //
    // }
    // }
    // };
    // logAction.setEnabled(false);
    //
    // actions.add(logAction);
    return actions.toArray(new IAction[actions.size()]);
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(500, 600);
  }

}
