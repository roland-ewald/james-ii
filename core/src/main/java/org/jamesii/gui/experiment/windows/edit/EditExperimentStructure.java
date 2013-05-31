/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment.windows.edit;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.jamesii.SimSystem;
import org.jamesii.core.Registry;
import org.jamesii.core.base.INamedEntity;
import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.experiments.variables.ExperimentVariable;
import org.jamesii.core.experiments.variables.ExperimentVariables;
import org.jamesii.core.experiments.variables.modifier.QuantitativeVariableIncrementModifier;
import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.model.variables.IQuantitativeVariable;
import org.jamesii.core.model.variables.IVariable;
import org.jamesii.core.plugins.IPluginTypeData;
import org.jamesii.gui.utils.SimpleTreeCellRenderer;

/**
 * Class to edit the experiment structure.
 * 
 * CAUTION: This is not working properly yet.
 * 
 * @author Roland Ewald
 */
@SuppressWarnings("deprecation")
public class EditExperimentStructure extends EditExperimentPanel {

  /**
   * The Class ExperimentTreeSelectionListener.
   * 
   * @author Roland Ewald
   * 
   *         29.05.2007
   */
  final class ExperimentTreeSelectionListener implements TreeSelectionListener {

    @Override
    public void valueChanged(TreeSelectionEvent e) {
      TreePath path = e.getPath();
      Object target =
          ((DefaultMutableTreeNode) path.getLastPathComponent())
              .getUserObject();
      if (target instanceof ExperimentVariable<?>) {
        initConfigPanel((ExperimentVariable<?>) target); // TODO: Edit
      } else if (target instanceof ExperimentVariables) {
        // TODO: Edit ExpVariables
        initConfigPanel((ExperimentVariables) target);
      } else if (target instanceof BaseExperiment) {
        // TODO: Edit BaseExperiment
        initConfigPanel();
      } else {
        throw new RuntimeException("Wrong type in experiment tree!");
      }
    }
  }

  /**
   * Moves experiment variables up and down the tree.
   * 
   * @author Roland Ewald
   * 
   *         29.05.2007
   */
  final class MoveActionListener implements ActionListener {

    /** Ref to experiment variable to be moved. */
    private ExperimentVariable<?> experimentVariable = null;

    /** Ref to experiment variables to be moved. */
    private ExperimentVariables experimentVariables = null;

    /** Reference to tree model. */
    private DefaultTreeModel treeModel = null;

    /** True is movement is upwards. */
    private boolean upwards;

    /**
     * Default constructor.
     * 
     * @param expVar
     *          the experiment variable to be moved
     * @param upwardMoving
     *          true if variable should move upward
     */
    public MoveActionListener(ExperimentVariable<?> expVar, boolean upwardMoving) {
      this.experimentVariable = expVar;
      this.upwards = upwardMoving;
      init();
    }

    /**
     * Default constructor.
     * 
     * @param expVars
     *          the topmost experiment variables
     * @param upwardMoving
     *          true if variables should move upward
     */
    public MoveActionListener(ExperimentVariables expVars, boolean upwardMoving) {
      this.experimentVariables = expVars;
      this.upwards = upwardMoving;
      init();
    }

    /**
     * Action performed.
     * 
     * @param e
     *          the e
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
      if (experimentVariables != null) {
        if (upwards) {
          moveUp(experimentVariables);
        } else {
          moveDown(experimentVariables);
        }
      } else if (experimentVariable != null) {
        if (upwards) {
          moveUp(experimentVariable);
        } else {
          moveDown(experimentVariable);
        }
      }

    }

    /**
     * Initialization.
     */
    void init() {
      treeModel = (DefaultTreeModel) experimentTree.getModel();
    }

    /**
     * Moves variable down.
     * 
     * @param expVar
     *          experiment variable to be moved
     */
    void moveDown(ExperimentVariable<?> expVar) {
      DefaultMutableTreeNode node = treeNodeMap.get(expVar);
      ExperimentVariables oldExpVariables =
          (ExperimentVariables) ((DefaultMutableTreeNode) node.getParent())
              .getUserObject();

      // Cannot move downwards; there is no lower level
      if (oldExpVariables.getSubLevel() == null) {
        return;
      }

      ExperimentVariables newExpVariables = oldExpVariables.getSubLevel();
      oldExpVariables.removeVariable(expVar);
      newExpVariables.addVariable(expVar);
      toNewParent(node, treeNodeMap.get(newExpVariables));
      expandTree(node);
    }

    /**
     * Moves experiment variables one down.
     * 
     * @param expVars
     *          the experiment variables
     */
    void moveDown(ExperimentVariables expVars) {
      if (expVars.getSubLevel() == null) {
        return;
      }
      moveUp(expVars.getSubLevel());
    }

    /**
     * Moves variable up.
     * 
     * @param expVar
     *          experiment variable to be moved
     */
    void moveUp(ExperimentVariable<?> expVar) {

      DefaultMutableTreeNode node = treeNodeMap.get(expVar);
      DefaultMutableTreeNode parentNode =
          (DefaultMutableTreeNode) node.getParent().getParent();
      ExperimentVariables oldExpVariables =
          (ExperimentVariables) ((DefaultMutableTreeNode) node.getParent())
              .getUserObject();

      // Cannot move more upwards than topmost exp vars level
      if (parentNode.getUserObject() == getExperiment()) {
        return;
      }

      ExperimentVariables newExpVariables =
          (ExperimentVariables) parentNode.getUserObject();

      oldExpVariables.removeVariable(expVar);
      newExpVariables.addVariable(expVar);
      toNewParent(node, treeNodeMap.get(newExpVariables));
      experimentTree.revalidate();
    }

    /**
     * Moves experiment variables upwards.
     * 
     * @param expVars
     *          the experiment variables
     */
    void moveUp(ExperimentVariables expVars) {
      DefaultMutableTreeNode node = treeNodeMap.get(expVars);
      DefaultMutableTreeNode parentNode =
          (DefaultMutableTreeNode) node.getParent();

      boolean hasSubLevel = expVars.getSubLevel() != null;

      // Cannot move more upwards than topmost exp vars level
      if (parentNode.getUserObject() == getExperiment()) {
        return;
      }

      ExperimentVariables exchangeExpVariables =
          (ExperimentVariables) parentNode.getUserObject();
      DefaultMutableTreeNode parentParentNode =
          (DefaultMutableTreeNode) parentNode.getParent();
      Object parentParentObject = parentParentNode.getUserObject();

      if (parentParentObject instanceof BaseExperiment) {
        ((BaseExperiment) parentParentObject).setExperimentVariables(expVars);
      } else {
        ((ExperimentVariables) parentParentObject).setSubLevel(expVars);
      }

      exchangeExpVariables.setSubLevel(expVars.getSubLevel());
      expVars.setSubLevel(exchangeExpVariables);

      DefaultMutableTreeNode lastLeaf = null;
      if (hasSubLevel) {
        lastLeaf = node.getLastLeaf();
        treeModel.removeNodeFromParent(lastLeaf);
      }
      treeModel.removeNodeFromParent(node);
      treeModel.removeNodeFromParent(parentNode);

      treeModel.insertNodeInto(node, parentParentNode,
          getNextFreePos(parentParentNode));
      treeModel.insertNodeInto(parentNode, node, getNextFreePos(node));
      if (lastLeaf != null) {
        treeModel.insertNodeInto(lastLeaf, parentNode,
            getNextFreePos(parentNode));
      }
      expandTree((DefaultMutableTreeNode) (parentNode.getChildCount() == 0 ? parentNode
          : parentNode.getFirstChild()));

    }

    /**
     * Assigns node to new parent.
     * 
     * @param node
     *          the node
     * @param newParent
     *          the new parent
     */
    void toNewParent(DefaultMutableTreeNode node,
        DefaultMutableTreeNode newParent) {
      treeModel.removeNodeFromParent(node);
      int freePos = getNextFreePos(newParent);
      if (freePos > 0) {
        freePos--;
      }
      treeModel.insertNodeInto(node, newParent, freePos);
    }
  }

  /**
   * The Class RenameActionListener.
   * 
   * @author Roland Ewald
   * 
   *         29.05.2007
   */
  final class RenameActionListener implements ActionListener {

    /** Entity to be renamed. */
    private INamedEntity entity;

    /** Text field that holds new name. */
    private JTextField textField;

    /**
     * Instantiates a new rename action listener.
     * 
     * @param ne
     *          the ne
     * @param tf
     *          the tf
     */
    public RenameActionListener(INamedEntity ne, JTextField tf) {
      this.entity = ne;
      this.textField = tf;
    }

    /**
     * Action performed.
     * 
     * @param e
     *          the e
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
      entity.setName(textField.getText());
      experimentTree.revalidate();
    }
  }

  /** Serialisation ID. */
  private static final long serialVersionUID = -4688091183410979306L;

  /** Button to add a new level to the experiment. */
  private JButton addSubLevelButton = new JButton("Add Level");

  /** Panel to edit an experiment variable. */
  private JPanel editVariablePanel = new JPanel();

  /** Tree to display the experiment's structure. */
  private JTree experimentTree;

  /** Name of the selected component. */
  private JLabel nameOfSelectedComponent = new JLabel();

  /** Map from elements of experiment definition to tree nodes. */
  private Map<Object, DefaultMutableTreeNode> treeNodeMap = new HashMap<>();

  /** Scroll pane for tree. */
  private JScrollPane treeScrollPane = new JScrollPane();

  /** Panel to configure variable. */
  private JPanel variableConfigPanel = new JPanel();

  {
    addSubLevelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        addAdditionalExpLevel();
      }
    });
  }

  {
    variableConfigPanel.setLayout(new BorderLayout());
  }

  {
    editVariablePanel.setLayout(new BorderLayout());
  }

  /**
   * Default constructor.
   * 
   * @param exp
   *          reference to experiment
   */
  public EditExperimentStructure(BaseExperiment exp) {
    super(exp);
    this.setLayout(new BorderLayout());

    initTree();
    treeScrollPane.getViewport().add(experimentTree);

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(addSubLevelButton);

    JSplitPane treeEditPane = new JSplitPane();
    treeEditPane.add(treeScrollPane, JSplitPane.LEFT);
    treeEditPane.add(variableConfigPanel, JSplitPane.RIGHT);

    variableConfigPanel.add(nameOfSelectedComponent, BorderLayout.NORTH);
    variableConfigPanel.add(editVariablePanel, BorderLayout.CENTER);

    this.add(treeEditPane, BorderLayout.CENTER);
    this.add(buttonPanel, BorderLayout.SOUTH);
  }

  /**
   * Add an additional instance of {@link ExperimentVariables} to the
   * {@link BaseExperiment}.
   */
  void addAdditionalExpLevel() {

    ExperimentVariables parentLevel = getExperiment().getExperimentVariables();

    if (parentLevel == null) {
      ExperimentVariables expVars = new ExperimentVariables();
      getExperiment().setExperimentVariables(expVars);
      DefaultMutableTreeNode root = treeNodeMap.get(getExperiment());
      DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(expVars);
      treeNodeMap.put(expVars, newChild);
      ((DefaultTreeModel) experimentTree.getModel()).insertNodeInto(newChild,
          root, getNextFreePos(root));
      return;
    }

    while (parentLevel.getSubLevel() != null) {
      parentLevel = parentLevel.getSubLevel();
    }

    ExperimentVariables expVars = new ExperimentVariables();
    parentLevel.setSubLevel(expVars);
    DefaultMutableTreeNode parentNode = treeNodeMap.get(parentLevel);
    DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(expVars);
    treeNodeMap.put(expVars, newChild);
    ((DefaultTreeModel) experimentTree.getModel()).insertNodeInto(newChild,
        parentNode, getNextFreePos(parentNode));
  }

  /**
   * Expands tree along the path to node.
   * 
   * @param node
   *          the node
   */
  void expandTree(DefaultMutableTreeNode node) {
    experimentTree.expandPath(getPathTo(null, null, node));
    experimentTree.revalidate();
  }

  /**
   * Creates panel to edit the name of an entity.
   * 
   * @param namedEntity
   *          the named entity
   * @param isEditable
   *          the is editable
   * 
   * @return the name editing panel
   */
  JPanel getNameEditingPanel(INamedEntity namedEntity, boolean isEditable) {
    JPanel editBExpPanel = new JPanel();

    isEditable = isEditable & !(namedEntity instanceof BaseExperiment);

    editBExpPanel.add(new JLabel("Name:"));
    JTextField textField = new JTextField(namedEntity.getName());
    textField.setEditable(isEditable);
    textField.setColumns(20);
    editBExpPanel.add(textField);

    if (!isEditable) {
      textField.setEnabled(false);
      return editBExpPanel;
    }

    JButton okButton = new JButton("OK");
    editBExpPanel.add(okButton);
    okButton
        .addActionListener(new RenameActionListener(namedEntity, textField));
    return editBExpPanel;
  }

  /**
   * Calculate insertion position for experiment variables.
   * 
   * @param parentNode
   *          the parent node
   * 
   * @return the next free pos
   */
  int getNextFreePos(DefaultMutableTreeNode parentNode) {
    return parentNode.getChildCount();
  }

  /**
   * Retrieves path to a certain node.
   * 
   * @param path
   *          the path
   * @param node
   *          the node
   * @param finalNode
   *          the final node
   * 
   * @return the path to
   */
  TreePath getPathTo(TreePath path, DefaultMutableTreeNode node,
      DefaultMutableTreeNode finalNode) {

    if (node != null && node.getChildCount() > 0) {
      path = path.pathByAddingChild(node);
    }

    if (node == null) {
      node = treeNodeMap.get(getExperiment());
    }

    if (path == null) {
      path = new TreePath(node);
    }

    Enumeration<?> children = node.children();
    while (children.hasMoreElements()) {
      DefaultMutableTreeNode child =
          (DefaultMutableTreeNode) children.nextElement();
      if (child.equals(finalNode)) {
        return path;
      }
      if (child.getUserObject() instanceof ExperimentVariables) {
        return getPathTo(path, child, finalNode);
      }
    }

    return path;
  }

  /**
   * Sets up configuration Panel to edit BaseExperiment's properties.
   */
  void initConfigPanel() {
    initConfigPanels();
    JPanel editBExpPanel = getNameEditingPanel(getExperiment(), true);
    editVariablePanel.add(editBExpPanel, BorderLayout.NORTH);
    refreshConfigPanels();
  }

  /**
   * TODO: Merge this with existing stuff! Initialises configuration panel for a
   * single experiment variable.
   * 
   * @param experimentVariable
   *          the experiment variable´
   * @param <V>
   *          the type of the variable to be edited
   */
  @SuppressWarnings("unchecked")
  // This needs to be refactored anyway
  <V> void initConfigPanel(ExperimentVariable<V> experimentVariable) {

    initConfigPanels();

    JPanel editBExpPanel = getNameEditingPanel(experimentVariable, false);
    editVariablePanel.add(editBExpPanel, BorderLayout.NORTH);

    V value = experimentVariable.getValue();

    JPanel editPanel = null;
    if (value instanceof String) {
      // TODO: do something
    } else if (value instanceof IQuantitativeVariable) {
      editPanel =
          setupQuantitativeVariable((ExperimentVariable<IQuantitativeVariable<?>>) experimentVariable);
    }

    if (editPanel != null) {
      editVariablePanel.add(editPanel, BorderLayout.CENTER);
    }

    JPanel buttonPanel = new JPanel();
    JButton up = new JButton("up");
    up.addActionListener(new MoveActionListener(experimentVariable, true));
    JButton down = new JButton("down");
    down.addActionListener(new MoveActionListener(experimentVariable, false));
    buttonPanel.add(up);
    buttonPanel.add(down);
    variableConfigPanel.add(buttonPanel, BorderLayout.SOUTH);

    refreshConfigPanels();
  }

  /**
   * Initialises panel for {@link ExperimentVariables} configuration.
   * 
   * @param experimentVariables
   *          the experiment variables
   */
  void initConfigPanel(ExperimentVariables experimentVariables) {
    initConfigPanels();
    JPanel editBExpPanel = getNameEditingPanel(experimentVariables, true);
    editVariablePanel.add(editBExpPanel, BorderLayout.NORTH);

    // TODO: Generate UI for adding new variables or removing old ones...

    JPanel buttonPanel = new JPanel();
    JButton up = new JButton("up");
    up.addActionListener(new MoveActionListener(experimentVariables, true));
    JButton down = new JButton("down");
    down.addActionListener(new MoveActionListener(experimentVariables, false));

    buttonPanel.add(up);
    buttonPanel.add(down);
    editVariablePanel.add(buttonPanel, BorderLayout.SOUTH);

    refreshConfigPanels();
  }

  /**
   * Inits the config panels.
   */
  void initConfigPanels() {
    variableConfigPanel.removeAll();
    variableConfigPanel.add(editVariablePanel, BorderLayout.CENTER);
    editVariablePanel.removeAll();
  }

  /**
   * Initialise {@link JTree}.
   */
  protected final void initTree() {

    DefaultMutableTreeNode oldTreeNode =
        new DefaultMutableTreeNode(getExperiment());
    DefaultMutableTreeNode root = oldTreeNode;
    treeNodeMap.put(getExperiment(), root);
    ExperimentVariables expVars = getExperiment().getExperimentVariables();

    while (expVars != null) {
      DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(expVars);
      oldTreeNode.add(treeNode);
      treeNodeMap.put(expVars, treeNode);

      for (ExperimentVariable<?> var : expVars.getVariables()) {
        DefaultMutableTreeNode leaf = new DefaultMutableTreeNode(var, false);
        treeNode.add(leaf);
        treeNodeMap.put(var, leaf);
      }

      expVars = expVars.getSubLevel();
      oldTreeNode = treeNode;
    }

    experimentTree = new JTree(root);

    // Configure displayed names
    experimentTree.setCellRenderer(new SimpleTreeCellRenderer());

    // Configure selection
    DefaultTreeSelectionModel selModel = new DefaultTreeSelectionModel();
    selModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    selModel.addTreeSelectionListener(new ExperimentTreeSelectionListener());
    experimentTree.setSelectionModel(selModel);

  }

  /**
   * Refresh config panels.
   */
  void refreshConfigPanels() {
    variableConfigPanel.revalidate();
    variableConfigPanel.repaint();
  }

  // Auxiliary classes

  /**
   * Create the edit panel for a plugin based variable. The panel allows the
   * selection of the plugin type and the plugins to be used / not to be used
   * for the parameter. If a plugin shall be used but is not installed a warning
   * should be issued during execution. The list of plugins to be used should be
   * stored - thus restarting the experiment on a different system with an
   * alternative list of installed plugins will be mostly the same - maybe we
   * should include another flag named "generic" here: if checked the list of
   * plugins will be automatically retrieved and only matched to a negative list
   * defined here.
   * 
   * @param expVar
   *          the exp var
   * 
   * @return the j panel
   */
  JPanel setupPluginVariable(IVariable<?> expVar) {
    JPanel editPluginVarPanel = new JPanel();

    Registry registry = SimSystem.getRegistry();

    JPanel pluginTypePanel = new JPanel();
    JLabel pluginTypeLabel = new JLabel("Variable based on plugin type: ");
    JComboBox pluginCombo = new JComboBox();

    for (Class<? extends Factory<?>> c : registry.getKnownFactoryClasses()) {
      Class<? extends AbstractFactory<?>> af =
          registry.getAbstractFactoryForBaseFactory(c);
      if (af != null) {
        IPluginTypeData ptd = registry.getPluginType(af);
        // namePTDmapping.put(ptd.getIds().getName(), ptd);
        pluginCombo.addItem(ptd.getId().getName());
      } else {
        pluginCombo.addItem("Error! No AbstractFactory found for " + c);
      }

    }
    pluginTypePanel.add(pluginTypeLabel);
    pluginTypePanel.add(pluginCombo);

    JPanel pluginPanel = new JPanel();
    JLabel pluginLabel = new JLabel("Select the plugins (not) to be used");
    JList pluginList = new JList();
    JCheckBox pluginCheckBox = new JCheckBox("Selected are to be used", true);

    pluginPanel.add(pluginLabel);
    pluginPanel.add(pluginCheckBox);
    pluginPanel.add(pluginList);

    JPanel buttonPanel = new JPanel();
    JButton saveButton = new JButton("Save");
    buttonPanel.add(saveButton);

    editVariablePanel.add(pluginTypePanel, BorderLayout.NORTH);
    editVariablePanel.add(pluginPanel, BorderLayout.CENTER);
    editVariablePanel.add(buttonPanel, BorderLayout.SOUTH);

    return editPluginVarPanel;
  }

  //
  // /**
  // *
  // * @author Roland Ewald
  // *
  // * 29.05.2007
  // *
  // * @param <V>
  // */
  // final class QuantVarActionListener<V extends Comparable<V>> implements
  // ActionListener {
  //
  // /**
  // * Quantitative variable modifier
  // */
  // QuantitativeVariableIncrementModifier<V, IQuantitativeVariable<V>>
  // modifier;
  //
  // JTextField textFieldLBound;
  //
  // JTextField textFieldUBound;
  //
  // JTextField textFieldInc;
  //
  // /**
  // * Default constructor
  // *
  // * @param mod
  // * @param tfLBound
  // * @param tfUBound
  // * @param tfInc
  // */
  // public QuantVarActionListener(
  // QuantitativeVariableIncrementModifier<V, IQuantitativeVariable<V>> mod,
  // JTextField tfLBound, JTextField tfUBound, JTextField tfInc) {
  // modifier = mod;
  // textFieldLBound = tfLBound;
  // textFieldInc = tfInc;
  // textFieldUBound = tfUBound;
  // }
  //
  // /**
  // * @see
  // java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
  // */
  // public void actionPerformed(ActionEvent e) {
  // modifier.setStartValue(parseText(textFieldLBound.getText()));
  // modifier.setIncrementBy(parseText(textFieldInc.getText()));
  // modifier.setStopValue(parseText(textFieldUBound.getText()));
  // }
  //
  // /**
  // * This is the ugly part where generics don't help anymore :/
  // *
  // * @param text
  // * @return
  // */
  // V parseText(String text) {
  //
  // // V val = modifier.getCurrentValue().getValue();
  // //
  // // if (val instanceof Double)
  // // return (V) new Double(Double.parseDouble(text));
  // // else if (val instanceof Integer)
  // // return (V) new Integer(Integer.parseInt(text));
  //
  // return null;
  // }
  //
  // }

  /**
   * Setup quantitative variable.
   * 
   * @param expVar
   *          the exp var
   * 
   * @return the j panel
   */
  @SuppressWarnings("unchecked")
  JPanel setupQuantitativeVariable(
      ExperimentVariable<IQuantitativeVariable<?>> expVar) {

    QuantitativeVariableIncrementModifier<? extends Comparable<?>, ? extends IQuantitativeVariable<?>> modifier =
        (QuantitativeVariableIncrementModifier<? extends Comparable<?>, ? extends IQuantitativeVariable<?>>) expVar
            .getModifier();

    JPanel editQuantVarPanel = new JPanel();

    JPanel lowerBoundPanel = new JPanel();
    JLabel lowerBoundLabel = new JLabel("Lower bound:");
    JTextField lowerBoundText =
        new JTextField(modifier.getStartValue().toString());
    lowerBoundText.setColumns(8);
    lowerBoundPanel.add(lowerBoundLabel);
    lowerBoundPanel.add(lowerBoundText);

    JPanel incPanel = new JPanel();
    JLabel incLabel = new JLabel("Increment:");
    JTextField incText = new JTextField(modifier.getIncrementBy().toString());
    incText.setColumns(8);
    incPanel.add(incLabel);
    incPanel.add(incText);

    JPanel upperBoundPanel = new JPanel();
    JLabel upperBoundLabel = new JLabel("Upper bound:");
    JTextField upperBoundText =
        new JTextField(modifier.getStopValue().toString());
    upperBoundText.setColumns(8);
    upperBoundPanel.add(upperBoundLabel);
    upperBoundPanel.add(upperBoundText);

    JPanel buttonPanel = new JPanel();
    JButton saveButton = new JButton("Save");
    buttonPanel.add(saveButton);
    // saveButton.addActionListener(new QuantVarActionListener(modifier,
    // lowerBoundText, upperBoundText, incText));

    editQuantVarPanel.add(lowerBoundPanel);
    editQuantVarPanel.add(incPanel);
    editQuantVarPanel.add(upperBoundPanel);
    editVariablePanel.add(buttonPanel, BorderLayout.SOUTH);

    return editQuantVarPanel;
  }

  @Override
  public void closeDialog() {
    // TODO Auto-generated method stub

  }

  @Override
  public String getName() {
    return "Experimental Structure";
  }
}
